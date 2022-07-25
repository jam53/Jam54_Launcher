package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class MainController implements InvalidationListener
{
    private Jam54LauncherModel model;

    @FXML
    private HBox updateAvailable_Button;

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
    }
    @FXML
    private void restartProgram(ActionEvent event)
    {
        LauncherUpdater launcherUpdater = new LauncherUpdater(model);
        launcherUpdater.update();
    }

    @Override
    public void invalidated(Observable observable)
    {
        updateAvailable_Button.setVisible(model.isNewVersionDownloaded());
    }
}