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

    public ApplicationInfo getInfo()
    {
        return info;
    }

    public void setInfo(ApplicationInfo info)
    {
        this.info = info;
    }

    private void selectApplicationWindow(ActionEvent event)
    {
        model.setOpenedApplication(info);

        model.setApplicationWindowSelected(true);
        model.setSettingsWindowSelected(false);
        //We don't set the Games/Programs windows' values to false. This is because only one of them would have been true anyway.
        //And if we go "back to library" from the Application Window. This will allow us to reopen the last page we were on. Either Games or Programs
    }
}