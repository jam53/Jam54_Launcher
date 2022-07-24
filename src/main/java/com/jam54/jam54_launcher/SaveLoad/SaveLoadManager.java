package com.jam54.jam54_launcher.SaveLoad;

import com.google.gson.Gson;
import com.jam54.jam54_launcher.ErrorMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * This class is used to save/load data within the launcher
 */
public final class SaveLoadManager
{
    private static final Path savePath;
    private static Jam54LauncherData data;

    static
    {
        savePath = Paths.get(SystemUtils.getUserHome().getAbsolutePath(), ".Jam54Launcher", "Jam54LauncherData.dat");

        data = new Jam54LauncherData();
        /* Our Jam54LauncherData object holds all the variables + instantiated with their default values.
         * Later we will overwrite this data object with the user's save file.
         * --- If the user opens the Jam54Launcher for the first time, we will only have the default values and nothing will be overwritten
         * If the user updates to a new version, a part of the default values will be overwritten, and new variables that weren't there in the previous release, will have their default value
        */

        loadSaveFileFromDisk();
    }

    /**
     * This method loads the save file from the disk. And overwrites the default values inside the "data" object
     */
    private static void loadSaveFileFromDisk()
    {
        if (savePath.toFile().isFile()) //Check if the save file exists, before trying to use it to overwrite the default values
        {
            try
            {
                String fileContents = FileUtils.readFileToString(savePath.toFile(), StandardCharsets.UTF_8);
                data = new Gson().fromJson(decode(fileContents), Jam54LauncherData.class); //Load the user's save file and use it to overwrite the default values. Leave new default values that aren't present in the user's save file untouched
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, "%The program encountered an error while trying to load data from the disk. " + e);
                errorMessage.show();
            }
        }

        saveToDisk(); //Normally we would just save the exact same json that's already saved to the disk. The only exception to this is when there are new default values (i.e. a there was an update, that added additional data/variables to the save file)
        //, then we will actually write new stuff to the save file
    }

    /**
     * This method saves the variables inside the "data" object as a JSON to the disk.
     */
    public static void saveToDisk()
    {
        try
        {
            FileUtils.writeStringToFile(savePath.toFile(), encode(new Gson().toJson(data)), StandardCharsets.UTF_8);
            Files.setAttribute(savePath.getParent(), "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, "%The program encountered an error while trying to save data to the disk. " + e);
            errorMessage.show();
        }
    }

    /**
     * This method encodes a given string to Base64
     */
    private static String encode(String string)
    {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    /**
     * This method decodes Base64 to a string
     */
    private static String decode(String string)
    {
        return new String(Base64.getDecoder().decode(string));
    }

    /**
     * This method returns an instance of our data class.
     *
     * If we want to load for example the "version" variable stored inside the data class, we can do so by using this `getData` method as follows:
     * SaveLoadManager.getData().getVersion();
     *
     * If we want to save/update the value of the "version" variable stored inside the data class, we can do so by using this `getData` method as follows:
     * SaveLoadManager.getData().setVersion("1.0.0");
     */
    public static Jam54LauncherData getData()
    {
        return data;
    }
}
