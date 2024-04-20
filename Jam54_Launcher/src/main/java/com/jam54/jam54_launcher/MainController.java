package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.Route;
import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.Windows.GamesPrograms.GamesProgramsWindow;
import com.jam54.jam54_launcher.Windows.LeftBar;
import com.jam54.jam54_launcher.Windows.Settings.SettingsWindow;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MainController implements InvalidationListener
{
    private Jam54LauncherModel model;
    private final GamesProgramsWindow gamesProgramsWindow;
    private final SettingsWindow settingsWindow;
    private final ApplicationWindow applicationWindow;
    private final LeftBar leftBar;

    @FXML
    private HBox updateAvailable_Button;
    @FXML
    private BorderPane borderPane;

    public MainController()
    {
        gamesProgramsWindow = new GamesProgramsWindow();
        settingsWindow = new SettingsWindow();
        applicationWindow = new ApplicationWindow();
        leftBar = new LeftBar();
    }

    public void initialize()
    {
        borderPane.setLeft(leftBar);

        model.navigateToWindow(Route.HOME); //When the program opens, we want to show the HOME AKA games/programs window
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);

        gamesProgramsWindow.setModel(model);
        settingsWindow.setModel(model);
        applicationWindow.setModel(model);
        leftBar.setModel(model);
    }

    /**
     * After an update is installed. A button at the top of the screen will become visible that restarts the program.
     * If the user clicks on that button, this function will be called.
     */
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

        if (model.getSelectedWindow() == Route.SETTINGS)
        {
            borderPane.setCenter(settingsWindow);
        }
        else if (model.getSelectedWindow() == Route.APPLICATION)
        {
            borderPane.setCenter(applicationWindow);
        }
        else
        {
            borderPane.setCenter(gamesProgramsWindow);
        }
    }
}