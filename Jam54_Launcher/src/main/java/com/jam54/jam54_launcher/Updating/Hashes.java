package com.jam54.jam54_launcher.Updating;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * This class is used to calculate hashes
 */
public class Hashes
{
    public static final int CHUNK_SIZE = 1024 * 1024; // 1MB chunk size used for chunking files

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
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntCalculateHash") + e.getMessage());
            errorMessage.show();
        }

        return SaveLoadManager.getTranslation("CouldntCalculateHash");
    }

    /**
     * Given a path to a file, this function splits the file into 1MB chunks and calculates the hash of each 1MB chunk.
     * @return A HashMap with key: The start byte position in the original file to which the 1MB chunk belongs, value: The hash of the 1MB chunk.
     */
    public HashMap<Integer, String> calculateChunkHashes(Path input)
    {
        ConcurrentHashMap<Integer, String> hashMap = new ConcurrentHashMap(); //k: Startbyte, v: hash

        byte[] fileBytes;
        try
        {
            fileBytes = FileUtils.readFileToByteArray(input.toFile());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        ExecutorService vExecutorService = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < fileBytes.length; i += CHUNK_SIZE)
        {
            final int startIndex = i;
            final int endIndex = Math.min(i + CHUNK_SIZE, fileBytes.length);

            vExecutorService.submit(() -> {
                byte[] chunk = Arrays.copyOfRange(fileBytes, startIndex, endIndex);
                String sha256 = DigestUtils.sha256Hex(chunk);
                hashMap.put(startIndex, sha256);
            });
        }

        try
        {
            vExecutorService.shutdown();
            boolean completed = vExecutorService.awaitTermination(1, TimeUnit.HOURS);

            if (!completed)
            {
                System.out.println("Not all threads completed");
            }
        }
        catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }

        return new HashMap<>(hashMap);
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
     * This function splits a given file into 1MB chunks and saves the chunks as separate files in the provided chunksFolder path. The filename of each file is the hash of that 1MB chunk file.
     * The function also creates a new file with ".hashes" appended to the end of the filename of fileToChunk and places the file next to the fileToChunk file.
     */
    public void writeFileChunksToDisk(Path fileToChunk, Path chunksFolder)
    {
        HashMap<Integer, String> fileChunkHashes = calculateChunkHashes(fileToChunk);

        byte[] fileBytes;
        try
        {
            fileBytes = FileUtils.readFileToByteArray(fileToChunk.toFile());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


        //Hoeven we niet echt te optimaliseren met virtual threads en multithreading, want we gaan dit alleen maar uitvoeren tijdens development.
        for (int i = 0; i < fileBytes.length; i += CHUNK_SIZE)
        {
            final int startIndex = i;
            final int endIndex = Math.min(i + CHUNK_SIZE, fileBytes.length);

            byte[] chunk = Arrays.copyOfRange(fileBytes, startIndex, endIndex);

            try
            {
                FileUtils.writeByteArrayToFile(
                        Path.of(chunksFolder.toFile().getAbsolutePath(), fileChunkHashes.get(startIndex)).toFile(),
                        chunk,
                        false
                        );
            }
            catch (IOException e)
            {//Since we will only ever use this during development/when publishing files, we don't need to create a fancy GUI error message window
                throw new RuntimeException(e);
            }
        }

        StringBuilder linesToWrite = new StringBuilder();

        for (Map.Entry<Integer, String> fileChunk : fileChunkHashes.entrySet())
        {
            linesToWrite.append(fileChunk.getValue()).append("|").append(fileChunk.getKey()).append("\n");
        }

        try
        {
            FileUtils.writeStringToFile(new File(fileToChunk.toAbsolutePath() + ".hashes"), linesToWrite.toString(), StandardCharsets.UTF_8);
        } catch (IOException e)
        {//Since we will only ever use this during development/when publishing files, we don't need to create a fancy GUI error message window
            throw new RuntimeException(e);
        }
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
     * Given a list of paths, for each one of the paths, every file in the path or in subdirectories of the path will be split into 1MB chunks where the filename is the hash of the 1MB chunk. These 1MB chunks then get placed in a folder called `Chunks` in the root of the path.
     *
     * Furthermore, a `filename.extension.hashes` file will be created for every file in the path or any subdirectories. This `filename.extension.hashes` file contains the hashes + startPositionOf1MBChunkInOriginalFile for all the 1MB chunks that are associated with the `filename.extension` file.
     * @param paths A directory
     */
    public void createAndCalculateChunkHashesTXTFiles(ArrayList<Path> paths)
    {
        for (Path path : paths)
        {
            if (path.toFile().exists())
            {
                Iterator<File> it = FileUtils.iterateFiles(path.toFile(), null, true);
                while (it.hasNext())
                {
                    Path file = it.next().toPath();
                    writeFileChunksToDisk(file, Path.of(path.toAbsolutePath().toString(), "/Chunks"));
                }
            }
        }
    }
}
