package com.jam54.jam54_launcher.Windows.Application;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class holds all the data needed to display a certain application. This can either be a game/program
 */
public record ApplicationInfo(int id, String name, Image image, boolean updateAvailable, String version, HashMap<String, String> descriptions, ArrayList<Platforms> platforms, long releaseDate, long lastUpdate, boolean isGame)
{
}
