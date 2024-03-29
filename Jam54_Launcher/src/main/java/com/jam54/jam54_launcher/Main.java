package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * This is the entry point to our application.
 *
 * Because of the scaling that can be set in Windows settings, the Jam54Launcher may appear very large and even overflow the screen. In order to circumvent this we can dynamically calculate the scale that should be used for this application and override the value from Windows settings.
 * This needs to be done before any JavaFX stuff has been initialized however, otherwise the scaling won't be applied. That's why we set the correct scaling here, and only then call the {@link MainApplication} which launches the actual application. Because {@link MainApplication} extends the {@link javafx.application.Application} class, setting the scaling in {@link MainApplication} would already be too late, even if we did it in the {@link MainApplication#run(String[])} method rather than the {@link MainApplication#start(Stage)} method.
 */
public class Main
{
    public static void main(String[] args)
    {
        if (args.length == 1)
        {// If we get passed an CLI argument, this should be the appId of an application we want to launch.
            int appId = Integer.parseInt(args[0]);
            Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), appId + "");

            try
            {
                String entryPoint = FileUtils.readFileToString(Path.of(appInstallationPath.toString(), "EntryPoint.txt").toFile(), StandardCharsets.UTF_8);

                String fullPathToApp = entryPoint.contains(":") ? entryPoint : Path.of(appInstallationPath.toString(), entryPoint).toString(); //If the entrypoint contains a ":" character. Then it means it isn't a path to a file, but rather an URL to a website or an URL to send a mail. In such cases we don't want to use Path.of() but just the raw string

                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + fullPathToApp + '"');
                processBuilder.start();
                System.exit(0);
            }
            catch (IOException e)
            {
                boolean launchJam54Launcher = 0 == JOptionPane.showConfirmDialog(null,
                        SaveLoadManager.getTranslation("ApplicationCouldNotBeLaunched"),
                        "",
                        JOptionPane.YES_NO_OPTION); //We cant use JavaFX to show a notification to the user, since the JavaFX thread hasn't been initialized yet. We also don't want to write the logic to launch an application after JavaFX is initialized, since that would add a significant delay.

                if (launchJam54Launcher)
                {
                    launchGUI(args);
                }
                else
                {
                    System.exit(0);
                }
            }
        }
        else
        {
            launchGUI(args);
        }
    }

    private static void launchGUI(String[] args)
    {
        float userMonitorHeight = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getDevice().getDisplayMode().getHeight();
        float userMonitorWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration().getDevice().getDisplayMode().getWidth();

        int scaleToApply = (int)(((userMonitorWidth * 0.70) / MainApplication.WINDOW_WIDTH) * 100);

        if ((scaleToApply/100f) * MainApplication.WINDOW_HEIGHT > userMonitorHeight * 0.8) //If the user is on an ultra wide display
        {
            scaleToApply = (int)(((userMonitorHeight * 0.70) / MainApplication.WINDOW_HEIGHT) * 100);
        }

        System.setProperty("glass.win.uiScale", scaleToApply + "%");

        MainApplication.run(args);
    }
}