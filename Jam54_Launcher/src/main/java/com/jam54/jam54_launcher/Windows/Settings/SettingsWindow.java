package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * This class is used to create the settings window, where the user can change the language, installation path, etc.
 */
public class SettingsWindow extends HBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    public SettingsWindow()
    {
        Button settings_window = new Button("Settings window");
//        settings_window.setOnAction(e -> model.setGamesWindowSelected());
        this.getChildren().add(settings_window);
    }

    @Override
    public void invalidated(Observable observable)
    {

    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
    }
}
