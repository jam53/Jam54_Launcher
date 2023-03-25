package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * This class is used to create the buttons on the Games/Programs windows
 * It shows the name and logo from the app. It also shows whether or not the app is installed (a colored vs grey image)
 * It shows if there is a new update available + it allows to open a little context menu
 */
public class ApplicationButton extends VBox
{
    private Jam54LauncherModel model;
    private ApplicationInfo info;
    private Button button;

    public ApplicationButton(ApplicationInfo info)
    {
        this.info = info;
        button = new Button(info.name());
        button.setOnAction(this::selectApplicationWindow);

        this.getChildren().add(button);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }

    private void selectApplicationWindow(ActionEvent event)
    {
        //This fixes a bug where, if you opened an application that hadn't been installed yet during this session of the Jam54Launcher. Proceeded to install it, went back to the games/programs window and opened the app that was just installed again, that it would appear to be uninstalled. Since the application info that is in this class wouldnt but updated
        model.getAllApplications().forEach(applicationInfo -> {
            if (applicationInfo.id() == info.id())
            {
                info = applicationInfo;
            }
        });
        model.setOpenedApplication(info);

        model.setApplicationWindowSelected(true);
        model.setSettingsWindowSelected(false);
        //We don't set the Games/Programs windows' values to false. This is because only one of them would have been true anyway.
        //And if we go "back to library" from the Application Window. This will allow us to reopen the last page we were on. Either Games or Programs
    }

    /**
     * This function returns true, if this is a button for a game
     */
    public boolean isGame()
    {
        return info.isGame();
    }
}
