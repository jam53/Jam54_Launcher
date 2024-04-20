package com.jam54.jam54_launcher.Windows;

import com.jam54.jam54_launcher.Animations.ToggleButtonNotGradientColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.Route;
import com.jam54.jam54_launcher.LoadCSSStyles;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * This class is used to create the bar at the left. Which holds the home, settings, ... button toggles
 */
public class LeftBar extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;

    private final ToggleGroup toggleGroup;
    private final ToggleButton homeToggle;
    private final ToggleButton availableAppUpdatesToggle;
    private final ToggleButton settingsToggle;

    public LeftBar()
    {
        this.getStyleClass().add("leftBar");

        toggleGroup = new ToggleGroup(); //This ToggleGroup holds all of the buttons/toggles in the left bar
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selected

        homeToggle = new ToggleButton();
        availableAppUpdatesToggle = new ToggleButton();
        settingsToggle = new ToggleButton();

        homeToggle.setOnAction(this::selectHomeToggle);
        settingsToggle.setOnAction(this::selectSettingsToggle);

        homeToggle.setToggleGroup(toggleGroup);
        settingsToggle.setToggleGroup(toggleGroup);

        homeToggle.getStyleClass().add("homeToggle");
        settingsToggle.getStyleClass().add("settingsToggle");

        homeToggle.setSkin(new ToggleButtonNotGradientColor(homeToggle, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-bg-selected")));
        settingsToggle.setSkin(new ToggleButtonNotGradientColor(settingsToggle, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-bg-selected")));


        toggleGroup.selectToggle(homeToggle);

        this.getChildren().addAll(homeToggle, settingsToggle);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void invalidated(Observable observable)
    {
        if (model.getSelectedWindow() == Route.SETTINGS)
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
        model.navigateToWindow(Route.HOME);
    }

    private void selectSettingsToggle(ActionEvent actionEvent)
    {
        model.navigateToWindow(Route.SETTINGS);
    }
}
