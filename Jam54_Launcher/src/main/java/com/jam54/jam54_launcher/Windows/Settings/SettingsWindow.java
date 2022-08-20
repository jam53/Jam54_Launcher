package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 * This class is used to create the settings window, where the user can change the language, installation path, etc.
 */
public class SettingsWindow extends HBox
{
    private Jam54LauncherModel model;
    public SettingsWindow()
    {
        //TODO
        //Dit object hoeft normaal geen listener/viewer te zijn. Het past alleen maar dingen aan in het model (taal, install location, etc)
        //Het switchen tussen de 2 tabjes "Installation Location" en "Language"
        //Niet zo fancy doen zoals Games/programs, Settings & ApplicationWindow switching
        //Maar gewoon met setVisible & setManaged
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }
}
