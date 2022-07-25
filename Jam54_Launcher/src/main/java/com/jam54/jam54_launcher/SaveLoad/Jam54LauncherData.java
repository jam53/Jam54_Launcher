package com.jam54.jam54_launcher.SaveLoad;

/**
 * This is a "data class" that holds all the data/variables that need to be persistent between different sessions
 * + it sets the default value for these variables
 */
public class Jam54LauncherData
{
    //region data/variables
    private String version = "0.1.0";
    //endregion

    private void fireInvalidationEvent()
    {
        SaveLoadManager.saveToDisk();
    }

    //region getters and setters
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
        fireInvalidationEvent();
    }
    //endregion
}
