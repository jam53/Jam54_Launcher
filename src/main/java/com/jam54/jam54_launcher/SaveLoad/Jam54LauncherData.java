package com.jam54.jam54_launcher.SaveLoad;

/**
 * This is a "data class" that holds all the data/variables that need to be persistent between different sessions
 * + it sets the default value for these variables
 */
public class Jam54LauncherData
{
    private String version = "0.1.0";

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
        fireInvalidationEvent();
    }

    private void fireInvalidationEvent()
    {
        SaveLoadManager.saveToDisk();
    }
}
