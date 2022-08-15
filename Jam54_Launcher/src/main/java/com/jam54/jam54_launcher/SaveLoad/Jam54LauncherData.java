package com.jam54.jam54_launcher.SaveLoad;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is a "data class" that holds all the data/variables that need to be persistent between different sessions
 * + it sets the default value for these variables
 */
public class Jam54LauncherData
{
    //region data/variables
    private String dataPath = Paths.get(System.getenv("LOCALAPPDATA"), "Jam54Launcher").toString();
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
    //endregion
}
