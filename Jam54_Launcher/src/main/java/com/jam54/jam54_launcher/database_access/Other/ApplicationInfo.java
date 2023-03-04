package com.jam54.jam54_launcher.database_access.Other;

import com.jam54.jam54_launcher.Windows.Application.Platforms;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class holds all the data needed to display a certain application. This can either be a game/program
 */
public record ApplicationInfo(int id, String name, Image image, boolean updateAvailable, String availableVersion, String version, HashMap<Locale, String> descriptions, ArrayList<Platforms> platforms, long releaseDate, long lastUpdate, boolean isGame)
{
}
