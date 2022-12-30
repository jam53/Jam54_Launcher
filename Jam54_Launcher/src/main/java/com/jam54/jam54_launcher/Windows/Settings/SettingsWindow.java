package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Jam54LauncherModel;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class is used to create the settings window, where the user can change the language, installation path, etc.
 */
public class SettingsWindow extends HBox
{
    private Jam54LauncherModel model;

    private VBox rightSide;

    public SettingsWindow()
    {
        VBox leftBar = new VBox();

        VBox leftBarTop = new VBox(); //Contains the title (Settings) + the options inside the settings menu
        VBox leftBarBottom = new VBox(); //Contains the version number + social media icons

        rightSide = new InstallationLocationWindow(); //We don't use the `setRightSide` method here. Although we would prefer to use it for consistency's sake. But when we do that we get an error since in that method we clear the `rightSide` first. But at this point the `rightSide` variable isn't assigned yet, so we get a nullpointer exception

        Label title = new Label("%Settings");
        Label other = new Label("%Other");

        Button installationLocation = new Button("%Installation Location");
        Button language = new Button("%Language");

        installationLocation.setOnAction( e -> setRightSide(new InstallationLocationWindow()));
        language.setOnAction( e -> setRightSide(new LanguageWindow()));

        leftBarTop.getChildren().addAll(title, other, installationLocation, language);

        Label version = new Label("%Couldn't retrieve the launcher's version");

        Properties properties = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("Jam54LauncherConfig.properties"))
        {
            properties.load(in);
            version = new Label(properties.getProperty("version"));
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorLoadingJam54LauncherConfig"));
            errorMessage.show();
        }

        HBox socials = new HBox();

        Button discord = new Button("d");
        Button youtube = new Button("y");
        Button playstore = new Button("p");
        Button website = new Button("w");

        socials.getChildren().addAll(discord, youtube, playstore, website);

        leftBarBottom.getChildren().addAll(version, socials);

        leftBar.getChildren().addAll(leftBarTop, leftBarBottom);


        //TODO
        //Dit object hoeft normaal geen listener/viewer te zijn. Het past alleen maar dingen aan in het model (taal, install location, etc)
        //Het switchen tussen de 2 tabjes "Installation Location" en "Language"
        //Niet zo fancy doen zoals Games/programs, Settings & ApplicationWindow switching
        //Maar gewoon met setVisible & setManaged
        //TODO
        //Ook nog de social buttons laten werken


        this.getChildren().addAll(leftBar, rightSide);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }

    /**
     * Replace the content of the `rightSide` VBox
     */
    private void setRightSide(VBox vbox)
    {
        rightSide.getChildren().clear();
        rightSide.getChildren().addAll(vbox.getChildren());
    }
}
