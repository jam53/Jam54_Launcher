package com.jam54.jam54_launcher.Updating;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Jam54LauncherModel;
import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is used to check if there are new updates available for the Jam54Launcher, and if there are it starts the update process.
 */
public class LauncherUpdater
{
    private Jam54LauncherModel model;
    private static URL VERSION_URL; //This contains the url to a file in which the latest version of the launcher is stored
    private static URL JAM54LAUNCHER_URL; //This contains the url to the latest version of the Jam54_Launcher.jar

    public LauncherUpdater(Jam54LauncherModel model)
    {
        this.model = model;

        try
        {
            VERSION_URL = new URL("https://github.com/jamhorn/Jam54Launcher/releases/latest/download/version.txt");
            JAM54LAUNCHER_URL = new URL("https://github.com/jamhorn/Jam54Launcher/releases/latest/download/Jam54_Launcher.jar");
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void checkForUpdates()
    {
        String versionInCloud = "";
        try
        {
            Path tempFile = Files.createTempFile("version", ".txt");
            FileUtils.copyURLToFile(VERSION_URL, tempFile.toFile(), 10000, 10000);
            versionInCloud = FileUtils.readFileToString(tempFile.toFile(), StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, "%Failed to check for updates, please ensure you hava a stable network connection.");
            errorMessage.show();
        }


        Path newJarLocation;
        try
        { //This returns the path to a new file called "Jam54_Launcher_New.jar", which will be located inside the folder in which the currently running jar is located, in this case where Jam54_Launcher.jar
            newJarLocation = Paths.get(LauncherUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().resolve("Jam54_Launcher_New.jar");
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }


        if (Files.exists(newJarLocation))
        {//This means the update was downloaded the last time the launcher was open. But that it hasn't been installed yet.
            model.setNewVersionDownloaded(false);
            update();
        }
        else if (!versionInCloud.equals(SaveLoadManager.getData().getVersion()) && !versionInCloud.equals(""))
        {//If true, this means there is a new version available
            try
            {
                FileUtils.copyURLToFile(JAM54LAUNCHER_URL, newJarLocation.toFile(), 10000, 10000); //Download the new version of the launcher
                model.setNewVersionDownloaded(true);
            } catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, "%Failed to download a new version of the Jam54 Launcher, please ensure you have a stable network connection.");
                errorMessage.show();
            }
        }
        else
        {
            model.setNewVersionDownloaded(false);
        }
    }

    public void update()
    {
        try
        {
            Process process = Runtime.getRuntime().exec("java -jar Updater.jar");
            Platform.exit();
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, "%Failed to start the Updater");
            errorMessage.show();
        }
    }
}
