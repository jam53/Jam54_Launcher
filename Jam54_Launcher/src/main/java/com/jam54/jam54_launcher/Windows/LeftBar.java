package com.jam54.jam54_launcher.Windows;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * This class is used to create the bar at the left. Which holds the home, settings, ... button toggles
 */
public class LeftBar extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;

    private ToggleGroup toggleGroup;
    private ToggleButton homeToggle;
    private ToggleButton settingsToggle;

    public LeftBar()
    {
        this.getStyleClass().add("leftBar");

        toggleGroup = new ToggleGroup(); //This ToggleGroup holds all of the buttons/toggles in the left bar
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selcted

        homeToggle = new ToggleButton();
        settingsToggle = new ToggleButton();

        homeToggle.setOnAction(this::selectHomeToggle);
        settingsToggle.setOnAction(this::selectSettingsToggle);

        homeToggle.setToggleGroup(toggleGroup);
        settingsToggle.setToggleGroup(toggleGroup);

        homeToggle.setGraphic(new ImageView(new Image("/com/jam54/jam54_launcher/img/Home.png", 50, 50, true, true)));
        settingsToggle.setGraphic(new ImageView(new Image("/com/jam54/jam54_launcher/img/Settings.png", 30, 30, true, true)));

        toggleGroup.selectToggle(homeToggle);

        this.getChildren().addAll(homeToggle, settingsToggle);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }

    @Override
    public void invalidated(Observable observable)
    {
        if (model.isSettingsWindowSelected())
        {
            toggleGroup.selectToggle(settingsToggle);
        }
        else
        {
            toggleGroup.selectToggle(homeToggle);
        }
    }

    private void selectHomeToggle(ActionEvent actionEvent)
    {
        model.setGamesWindowSelected(true);

        model.setProgramsWindowSelected(false);
        model.setSettingsWindowSelected(false);
        model.setApplicationWindowSelected(false);
    }

    private void selectSettingsToggle(ActionEvent actionEvent)
    {
        model.setSettingsWindowSelected(true);

        model.setGamesWindowSelected(false);
        model.setProgramsWindowSelected(false);
        model.setApplicationWindowSelected(false);
    }
}
