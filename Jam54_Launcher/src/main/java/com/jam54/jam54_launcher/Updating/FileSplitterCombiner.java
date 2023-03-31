package com.jam54.jam54_launcher.Updating;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is used to split and combine binary files
 */
public class FileSplitterCombiner
{
    /**
     * Given a *root* folder, this function will go through all of the files (including subdirectories),
     * and split all files that are larger than *maxSizeMBs* into parts smaller than or equal to *maxSizeMBs*.
     *
     * After a given file has been split, the original file wont be deleted. This is because in OneDrive we still want to have all of the original
     * files for the application, should we want to run it without having the launcher for example to merge the splitted files again.
     * When uploading the binaries to GitHub (or another site that imposes filesize limits), we can just omit the original unsplitted files that are too large.
     *
     * The function will also place a "Split.txt" file in the *root* folder, where each line contains the path starting from the *root*
     * to the original file.
     *
     * If there are no files larger than maxSizeMBs, the file "Split.txt" will still be created, but will be empty
     */
    public void splitFilesLargerThan(int maxSizeMBs, Path root)
    {
        int partSize = maxSizeMBs * 1024 * 1024; //partSize is in bytes

        StringBuilder linesToWrite = new StringBuilder();

        Iterator<File> it = FileUtils.iterateFiles(root.toFile(), null, true);
        while(it.hasNext())
        {
            File inputFile = it.next();

            if (inputFile.length() > partSize)
            {
                linesToWrite.append(root.relativize(inputFile.toPath())).append("\n");

                try (FileInputStream fis = new FileInputStream(inputFile))
                {
                    byte[] buffer = new byte[partSize];
                    int bytesRead;
                    int partNumber = 1;

                    while ((bytesRead = fis.read(buffer)) != -1)
                    {
                        String partFileName = Path.of(root.toString(), root.relativize(inputFile.toPath()) + ".part" + partNumber).toString();
                        try (FileOutputStream fos = new FileOutputStream(partFileName))
                        {
                            fos.write(buffer, 0, bytesRead);
                        }
                        partNumber++;
                    }

                    inputFile.delete();
                } catch (IOException e)
                {
                    throw new RuntimeException(e); //This method will only ever be run by us, the developer, so no need for a fancy error message
                }
            }
        }

        try
        {
            FileUtils.writeStringToFile(Path.of(root.toString(), "Split.txt").toFile(), linesToWrite.toString(), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e); //This method will only ever be run by us, the developer, so no need for a fancy error message
        }
    }

    /**
     * Given a *root* folder, this function will look for a file called "Split.txt" in the root of the *root* folder
     * It will then loop over this file line by line, and for every path in the file it will combine the split files again into a single file
     *
     * It wont however, delete the splitted files. Since with the next updates those will have to be redownloaded, since they would be missing.
     * This makes it so that for all the files that have been split, both the merged and the splitted files will be stored; thus requiring double the diskspace.
     * Another thing to note, in the same situation where during an update, the splitted/merged files haven't been changed. The delta update system
     * that I wrote will remove the merged file. Since that merged file's hash isn't in "Hashes.txt" and only the splitted files' hashes are.
     * But since we didn't delete the splitted files, those wont be redownloaded, and we can reuse them. So we would only have to recombine them into
     * the merged file.
     */
    public void combineSplitFiles(Path root)
    {
        try
        {
            for (String line : FileUtils.readLines(Path.of(root.toString(), "Split.txt").toFile(), StandardCharsets.UTF_8))
            {
                File outputFile = new File(line);

                int partNumber = 1;

                try (FileOutputStream fos = new FileOutputStream(Path.of(root.toString(), outputFile.toString()).toFile()))
                {
                    while (true)
                    {
                        File partFile = Path.of(root.toString(), outputFile + ".part" + partNumber).toFile();

                        if (!partFile.exists())
                        {
                            break;
                        }

                        try (FileInputStream fis = new FileInputStream(partFile))
                        {
                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = fis.read(buffer)) != -1)
                            {
                                fos.write(buffer, 0, bytesRead);
                            }
                        }

                        partNumber++;
                    }
                }
                catch (IOException e)
                {
                    ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorMergeSplitFiles") + " " + e.getMessage());
                    errorMessage.show();
                }
            }
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorReadingSplitTXT") + " " + e.getMessage());
            errorMessage.show();
        }
    }
}
