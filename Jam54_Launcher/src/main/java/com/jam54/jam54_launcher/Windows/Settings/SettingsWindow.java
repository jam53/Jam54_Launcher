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

    private Label titleRight;
    private VBox holderRight;

    public SettingsWindow()
    {
        VBox leftBar = new VBox();

        VBox leftBarTop = new VBox(); //Contains the title (Settings) + the options inside the settings menu
        VBox leftBarBottom = new VBox(); //Contains the version number + social media icons

        VBox rightSide = new VBox();

        Label title = new Label("%Settings");
        Label other = new Label("%Other");
        Button installationLocation = new Button("%Installation Location");
        Button language = new Button("%Language");

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

        titleRight = new Label();
        holderRight = new VBox();

        rightSide.getChildren().addAll(titleRight, holderRight);



        //TODO
        //Dit object hoeft normaal geen listener/viewer te zijn. Het past alleen maar dingen aan in het model (taal, install location, etc)
        //Het switchen tussen de 2 tabjes "Installation Location" en "Language"
        //Niet zo fancy doen zoals Games/programs, Settings & ApplicationWindow switching
        //Maar gewoon met setVisible & setManaged


        this.getChildren().addAll(leftBar, rightSide);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }
}
