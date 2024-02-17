package com.jam54.jam54_launcher;

import javafx.stage.Stage;

import java.awt.*;

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