package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ApplicationWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    public ApplicationWindow()
    {


        this.getChildren().add(new Button("Application window"));
    }

    @Override
    public void invalidated(Observable observable)
    {

    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }
}
