package com.jam54.jam54_launcher.Updating;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.nothome.delta.Delta;
import com.nothome.delta.GDiffWriter;
import javafx.application.Platform;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class is used to calculate hashes
 */
public class Hashes
{
    /**
     * Given a path, this function calculates the SHA-256 hash and returns it as a string
     */
    public String calculateHash(Path input)
    {
        try
        {
            byte[] fileBytes = FileUtils.readFileToByteArray(input.toFile());
            return DigestUtils.sha256Hex(fileBytes);
        }
        catch (IOException e)
        {
            Platform.runLater(() ->
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntCalculateHash") + e.getMessage());
                errorMessage.show();
            });
        }

        return SaveLoadManager.getTranslation("CouldntCalculateHash");
    }

    /**
     * This function takes a root directory, and calculates the hashes for all of the files in the root & subfolders
     * @return Returns a hashmap with the path to the file (starting in the root folder, so not the full path, and doesn't include the root folder) and a string with the hash
     */
    public HashMap<String, Path> calculateHashesForFilesInDirectory(Path root)
    {
        HashMap<String, Path> hashedFiles = new HashMap<>();

        if (root.toFile().exists())
        {
            Iterator<File> it = FileUtils.iterateFiles(root.toFile(), null, true);
            while (it.hasNext())
            {
                Path file = it.next().toPath();
                hashedFiles.put(calculateHash(file), root.relativize(file)); //The relativize function makes it so that the path to the file starts in the `root` directory
            }
        }

        return hashedFiles;
    }

    /**
     * This function computes the delta between all of the files in the previousVersion directory and the currentVersion directory.
     *
     * In order to this, it recursively iterates over all of the files in the currentVersion directory. For each file it then computes the delta with the corresponding file in the previousVersion directory.
     * If a file is not present in the previousVersion directory, this file is skipped and wont be added to the delta zip. Since in {@code InstallApp} where we handle installs/updates we first download all files that are missing in the new version.
     * If a file is not present in the currentVersion directory, this file is also skipped. This file will be deleted in {@code InstallApp}.
     * If a file is both present in the previousVersion and currentVersion directory, then the delta is computed between the two versions of the file and added to the zip.
     *
     * @param previousVersion path to the directory of the previous version of the app
     * @param currentVersion path to the directory of the current version of the app
     */
    private void writeDeltaZipToDisk(Path previousVersion, Path currentVersion, ZipOutputStream zipOutputStream) throws IOException
    {
        NoCloseOutputStream outputStream = new NoCloseOutputStream(zipOutputStream);

        try (Stream<Path> currentFiles = Files.walk(Paths.get(currentVersion.toString())).filter(Files::isRegularFile).filter(path -> !path.getParent().getFileName().toString().equals("Deltas")))
        {
            Delta delta = new Delta();
            currentFiles.forEach(currentFile ->
            {
                try
                {
                    Path relativePathToFile = currentVersion.relativize(currentFile);
                    Path previousFile = Path.of(previousVersion.toString(), relativePathToFile.toString());

                    if (previousFile.toFile().exists())
                    {//If the file also existed in the previous version of the app
                        zipOutputStream.putNextEntry(new ZipEntry(relativePathToFile + ".gdiff"));
                        delta.compute(previousFile.toFile(), currentFile.toFile(), new GDiffWriter(outputStream));
                        zipOutputStream.closeEntry();
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }

        outputStream.manualClose();
    }

    /**
     * Given a list of Paths, this function will calculate the hashes for all the files in each of the paths (and their subdirectories).
     * And place a "Hashes.txt" file in the root of every path from the list, containing the hashes for all the files in that directory.
     */
    public void calculateHashesTXTFiles(ArrayList<Path> paths)
    {
        for (Path path : paths)
        {
            StringBuilder linesToWrite = new StringBuilder();

            for (Map.Entry<String, Path> hashedFile : calculateHashesForFilesInDirectory(path).entrySet())
            {
                linesToWrite.append(hashedFile.getKey()).append("|").append(hashedFile.getValue()).append("\n");
            }

            try
            {
                FileUtils.writeStringToFile(Path.of(path.toString(), "Hashes.txt").toFile(), linesToWrite.toString(), StandardCharsets.UTF_8);
            }
            catch (IOException e)
            {//Since we will only ever use this during development/when publishing files, we don't need to create a fancy GUI error message window
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Given a path to a directory containing previous versions of an app and the path to the folder that contains the current version of the app, this function will create deltas between each of the subfolders in the PreviousVersions directory against the files in the currentVersion directory.
     *
     * The created deltas will be placed in a folder called `Deltas` in the root of the currentVersion directory. This folder then contains a zipfile for each one of the subfolders in the PreviousVersions directory.
     * Each of these zipfiles follows the same naming convention:
     *      "a.b.c-x.y.z.zip"
     * Where:
     *      - a.b.c is the previous version of the app
     *      - x.y.z is the current version of the app
     *
     * The created zipfile contains the exact same file and folder structure of the currentVersion directory. However, every filename has ".gdiff" added to it.
     *
     * Inside this `Deltas` directory a file called "Sizes.properties" is created. This Java Properties file contains the size of each of the delta zips in bytes as the value and the name of the zipfile as the key
     *
     * @param folderContainingPreviousVersions This should be the path to the directory, that contains subfolders which in turn contain previous versions of the app. Each subfolder should have the name of the version of the app it contains, i.e. the name of the subfolder should be "a.b.c"
     * @param currentVersion This should be the path to the directory that contains the current version of our app
     * @param currentVersionNumber The current version of the app i.e. "x.y.z"
     */
    public void createAndCalculateDeltaFiles(Path folderContainingPreviousVersions, Path currentVersion, String currentVersionNumber)
    {
        try
        {
            Properties sizesProperties = new Properties();

            for (File path : folderContainingPreviousVersions.toFile().listFiles(File::isDirectory))
            {
                Path outputZip = Path.of(
                        currentVersion.toString(),
                        "Deltas",
                        path.getName() + '-' + currentVersionNumber + ".zip"
                );
                Files.createDirectories(outputZip.getParent());
                outputZip.toFile().createNewFile();
                ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputZip.toFile())));

                writeDeltaZipToDisk(path.toPath(), currentVersion, zipOutputStream);

                zipOutputStream.close();

                sizesProperties.put(outputZip.getFileName().toString(), "" + Files.size(outputZip)); //We need to convert the value to a String, otherwise the store() method we use to write the Properties to disk throws an error
            }

            sizesProperties.store(new FileOutputStream(Path.of(currentVersion.toString(), "Deltas", "Sizes.properties").toString()), null);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
