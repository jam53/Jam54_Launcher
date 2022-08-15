package com.jam54.jam54_launcher.SaveLoad;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * This is a "data class" that holds all the data/variables that need to be persistent between different sessions
 * + it sets the default value for these variables
 */
public class Jam54LauncherData
{
    //region data/variables
    private String dataPath = Paths.get(System.getenv("LOCALAPPDATA"), "Jam54Launcher").toString(); //A path where the launcher can place the downloaded apps
    private String locale = "en"; //The locale/language that should be used when displaying the application
    //endregion

    private void fireInvalidationEvent()
    {
        SaveLoadManager.saveToDisk();
    }

    //region getters and setters
    /**
     * This function returns a Path, where the launcher can place the downloaded apps
     */
    public Path getDataPath()
    {
        return Paths.get(dataPath);
    }

    /**
     * This function sets a Path, where the launcher can place the downloaded apps
     */
    public void setDataPath(Path savePath)
    {
        this.dataPath = savePath.toString();
        fireInvalidationEvent();
    }

    /**
     * Returns the locale that should be used to display the application in
     */
    public Locale getLocale()
    {
        return new Locale(locale);
    }

    /**
     * Specify which locale/language should be used when displaying the application
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale.getLanguage();
        fireInvalidationEvent();
    }
    //endregion
}
