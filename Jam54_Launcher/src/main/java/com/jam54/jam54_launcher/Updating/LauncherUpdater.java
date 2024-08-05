package com.jam54.jam54_launcher.Updating;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class is used to check if there are new updates available for the Jam54Launcher, and if there are it starts the update process.
 */
public class LauncherUpdater
{
    private Jam54LauncherModel model;
    private final Properties properties; //This properties variable, contains config/pref info about the launcher. E.g. the version number, the download url for a new version, ...

    public LauncherUpdater(Jam54LauncherModel model)
    {
        this.model = model;
        properties = new Properties();

        try (InputStream in = Main.class.getResourceAsStream("Jam54LauncherConfig.properties"))
        {
            properties.load(in);
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorLoadingJam54LauncherConfig"));
            errorMessage.show();
        }
    }

    public void checkForUpdates()
    {
        String versionInCloud = "";
        try
        {
            Path tempFile = Files.createTempFile("version", ".txt");
            tempFile.toFile().deleteOnExit();
            DownloadFile.saveUrlToFile(new URI(properties.getProperty("versionUrl")).toURL(), tempFile, 10000, 10000, 10);
            versionInCloud = FileUtils.readFileToString(tempFile.toFile(), StandardCharsets.UTF_8);
        }
        catch (IOException | URISyntaxException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedCheckingUpdates"));
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
        else if (!versionInCloud.equals(properties.getProperty("version")) && !versionInCloud.equals(""))
        {//If true, this means there is a new version available
            new Thread(() -> {
                try
                {
                    DownloadFile.saveUrlToFile(new URI(properties.getProperty("Jam54LauncherUrl")).toURL(), newJarLocation, 10000, 10000, 10); //Download the new version of the launcher
                    Platform.runLater(() -> model.setNewVersionDownloaded(true));
                }
                catch (IOException | URISyntaxException e)
                {
                    Platform.runLater(() -> {
                        ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedDownloadingNewJam54LauncherVersion"));
                        errorMessage.show();
                    });
                }
            }).start();
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
            String updaterFolder = Paths.get(new File(LauncherUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath()).normalize().toString(); //Gets the path to the folder that contains the Updater.exe executable
            String updaterExecutable = Paths.get(updaterFolder, "Updater.exe").normalize().toString(); //Get the path to the Updater.exe executable

            final ProcessBuilder pb = new ProcessBuilder(updaterExecutable);
            pb.directory(new File(updaterFolder));
            final Process p = pb.start();

            Platform.exit();
        }
        catch (IOException | URISyntaxException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedToStartUpdater"));
            errorMessage.show();
        }
    }
}
