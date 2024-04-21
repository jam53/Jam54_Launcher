package com.jam54.jam54_launcher.Windows;

import com.jam54.jam54_launcher.Animations.ToggleButtonNotGradientColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.Route;
import com.jam54.jam54_launcher.LoadCSSStyles;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
    private final Circle updatesAvailableNotification;

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
        availableAppUpdatesToggle.setOnAction(this::selectAvailableAppUpdatesToggle);
        settingsToggle.setOnAction(this::selectSettingsToggle);

        homeToggle.setToggleGroup(toggleGroup);
        availableAppUpdatesToggle.setToggleGroup(toggleGroup);
        settingsToggle.setToggleGroup(toggleGroup);

        homeToggle.getStyleClass().add("homeToggle");
        availableAppUpdatesToggle.getStyleClass().add("availableAppUpdatesToggle");
        settingsToggle.getStyleClass().add("settingsToggle");

        homeToggle.setSkin(new ToggleButtonNotGradientColor(homeToggle, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-bg-selected")));
        availableAppUpdatesToggle.setSkin(new ToggleButtonNotGradientColor(availableAppUpdatesToggle, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-bg-selected")));
        settingsToggle.setSkin(new ToggleButtonNotGradientColor(settingsToggle, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-bg-selected")));

        StackPane availableAppUpdates_StackPane = new StackPane();
        updatesAvailableNotification = new Circle(0, 0, 5); //Color of circle will be set in the `invalidated()` function
        StackPane.setAlignment(updatesAvailableNotification, Pos.TOP_RIGHT);
        StackPane.setMargin(updatesAvailableNotification, new Insets(0, 7, 0, 0));
        availableAppUpdates_StackPane.getChildren().addAll(availableAppUpdatesToggle, updatesAvailableNotification);

        toggleGroup.selectToggle(homeToggle);

        this.getChildren().addAll(homeToggle, availableAppUpdates_StackPane, settingsToggle);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
    }

    @Override
    public void invalidated(Observable observable)
    {
        switch (model.getSelectedWindow())
        {
            case AVAILABLE_APP_UPDATES -> toggleGroup.selectToggle(availableAppUpdatesToggle);
            case SETTINGS -> toggleGroup.selectToggle(settingsToggle);
            case null, default -> toggleGroup.selectToggle(homeToggle);
        }

        updatesAvailableNotification.setFill(model.getAllApplications().stream().anyMatch(applicationInfo -> applicationInfo.updateAvailable() && applicationInfo.version() != null) ? Color.RED : Color.TRANSPARENT);
    }

    private void selectHomeToggle(ActionEvent actionEvent)
    {
        model.navigateToWindow(Route.HOME);
    }

    private void selectAvailableAppUpdatesToggle(ActionEvent actionEvent)
    {
        model.navigateToWindow(Route.AVAILABLE_APP_UPDATES);
    }

    private void selectSettingsToggle(ActionEvent actionEvent)
    {
        model.navigateToWindow(Route.SETTINGS);
    }
}
