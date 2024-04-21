package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.Route;
import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.Windows.AvailableAppUpdates.AvailableAppUpdatesWindow;
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
    private final AvailableAppUpdatesWindow availableAppUpdatesWindow;
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
        availableAppUpdatesWindow = new AvailableAppUpdatesWindow();
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
        availableAppUpdatesWindow.setModel(model);
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

        switch (model.getSelectedWindow())
        {
            case AVAILABLE_APP_UPDATES -> borderPane.setCenter(availableAppUpdatesWindow);
            case SETTINGS -> borderPane.setCenter(settingsWindow);
            case APPLICATION -> borderPane.setCenter(applicationWindow);
            case null, default -> borderPane.setCenter(gamesProgramsWindow);
        }
    }
}