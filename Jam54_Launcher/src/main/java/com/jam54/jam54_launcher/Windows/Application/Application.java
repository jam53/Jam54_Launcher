package com.jam54.jam54_launcher.Windows.Application;

import javafx.scene.image.Image;

import java.util.ArrayList;

/**
 * This class holds all the data needed to display a certain application. This can either be a game/program
 */
public record Application(String name, Image image, boolean installed, boolean updateAvailable, String version, String description, ArrayList<Platforms> platforms, long releaseDate, long latestUpdate)
{
}
