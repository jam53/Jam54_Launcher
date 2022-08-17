package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.Windows.GamesPrograms.GamesProgramsWindow;
import com.jam54.jam54_launcher.Windows.Settings.SettingsWindow;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController implements InvalidationListener
{
    private Jam54LauncherModel model;
    private final GamesProgramsWindow gamesProgramsWindow;
    private final SettingsWindow settingsWindow;
    private final ApplicationWindow applicationWindow;

    @FXML
    private HBox updateAvailable_Button;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox leftBar;

    public MainController()
    {
        gamesProgramsWindow = new GamesProgramsWindow();
        settingsWindow = new SettingsWindow();
        applicationWindow = new ApplicationWindow();
    }

    public void initialize()
    {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        ToggleButton homeButton = new ToggleButton();
        ToggleButton settingsButton = new ToggleButton();

        homeButton.setToggleGroup(toggleGroup);
        settingsButton.setToggleGroup(toggleGroup);
        toggleGroup.selectToggle(homeButton);

        homeButton.getStyleClass().add("leftBarToggle");
        settingsButton.getStyleClass().add("leftBarToggle");

        homeButton.setGraphic(new ImageView(new Image("/com/jam54/jam54_launcher/img/Home.png", 50, 50, true, true)));
        settingsButton.setGraphic(new ImageView(new Image("/com/jam54/jam54_launcher/img/Settings.png", 30, 30, true, true)));

        leftBar.getChildren().addAll(homeButton, settingsButton);


        borderPane.setCenter(gamesProgramsWindow); //When the program opens, we want to show the games/programs window
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);

        gamesProgramsWindow.setModel(model);
        settingsWindow.setModel(model);
        applicationWindow.setModel(model);
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

        if (model.isSettingsWindowSelected())
        {
            borderPane.setCenter(settingsWindow);
        }
        else if (model.isApplicationWindowSelected())
        {
            borderPane.setCenter(applicationWindow);
        }
        else
        {
            borderPane.setCenter(gamesProgramsWindow);
        }
    }
}