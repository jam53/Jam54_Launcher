package com.jam54.jam54_launcher.SaveLoad;

import com.google.gson.Gson;

import java.util.Base64;
import java.util.prefs.Preferences;

/**
 * This class is used to save/load data within the launcher
 */
public final class SaveLoadManager
{
    private static Jam54LauncherData data;

    static
    {
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
        String saveFile = Preferences.userRoot().get("Jam54LauncherSaveFile", null);

        if (saveFile != null) //Check if the save file exists, before trying to use it to overwrite the default values
        {
            data = new Gson().fromJson(decode(saveFile), Jam54LauncherData.class); //Load the user's save file and use it to overwrite the default values. Leave new default values that aren't present in the user's save file untouched
        }

        saveToDisk(); //Normally we would just save the exact same json that's already saved to the disk. The only exception to this is when there are new default values (i.e. a there was an update, that added additional data/variables to the save file)
        //, then we will actually write new stuff to the save file
    }

    /**
     * This method saves the variables inside the "data" object as a JSON to the disk.
     */
    public static void saveToDisk()
    {
        Preferences.userRoot().put("Jam54LauncherSaveFile", encode(new Gson().toJson(data)));
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
