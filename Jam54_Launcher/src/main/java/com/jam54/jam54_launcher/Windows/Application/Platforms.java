package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;

public enum Platforms
{
    ANDROID, WEB, WINDOWS;

    @Override
    public String toString()
    {
        return switch (this)
        {
            case ANDROID -> SaveLoadManager.getTranslation("Android");
            case WEB -> SaveLoadManager.getTranslation("Web");
            case WINDOWS -> SaveLoadManager.getTranslation("Windows");
        };
    }
}
