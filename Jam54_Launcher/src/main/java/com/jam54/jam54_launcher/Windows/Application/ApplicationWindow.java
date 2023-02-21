package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * This window displays more detail about a specific app.
 * It shows a description, the name of the app, when the latest update was, ...
 */
public class ApplicationWindow extends VBox
{
    private Jam54LauncherModel model;
    public ApplicationWindow()
    {
        Button button = new Button("%Back To Library");
        button.setOnAction(this::backToLibrary);
        this.getChildren().add(button);
    }

    private void backToLibrary(ActionEvent event)
    {
        model.setApplicationWindowSelected(false);
        model.setSettingsWindowSelected(false);
        //We don't set on of the Games/Programs windows' values to true. This is because one of them is already true. (Since we didn't set it to false when opening the ApplicationWindow)
        //And if we go "back to library" from the Application Window. This will allow us to reopen the last page we were on. Either Games or Programs
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }
}
