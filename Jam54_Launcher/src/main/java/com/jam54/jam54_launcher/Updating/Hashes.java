package com.jam54.jam54_launcher.Updating;

import com.google.gson.Gson;
import com.jam54.jam54_launcher.ErrorMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

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
            ErrorMessage errorMessage = new ErrorMessage(false, "%Couldn't calculate hash" + e.getMessage());
            errorMessage.show();
        }

        return "Couldn't calculate hash";
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
    }
}
