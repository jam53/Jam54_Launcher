package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Animations.ToggleButtonNotGradientColor;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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
        this.getStyleClass().add("settingsWindow");

        VBox leftBar = new VBox();
        leftBar.getStyleClass().add("settingsWindowLeftBar");

        VBox leftBarTop = new VBox(); //Contains the title (Settings) + the options inside the settings menu
        leftBarTop.getStyleClass().add("settingsWindowLeftBarTop");
        VBox leftBarBottom = new VBox(); //Contains the version number + social media icons
        leftBarBottom.getStyleClass().add("settingsWindowLeftBarBottom");

        rightSide = new InstallationLocationWindow(); //We don't use the `setRightSide` method here. Although we would prefer to use it for consistency's sake. But when we do that we get an error since in that method we clear the `rightSide` first. But at this point the `rightSide` variable isn't assigned yet, so we get a nullpointer exception

        Text title = new Text(SaveLoadManager.getTranslation("Settings"));
        VBox titleHolder = new VBox(title);
        titleHolder.setId("settingsWindowTitleHolder");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selected

        ToggleButton installationLocation = new ToggleButton();
        installationLocation.setSkin(new ToggleButtonNotGradientColor(installationLocation, LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-bg-selected"), LoadCSSStyles.getCSSColor("-hollow-button-clicked")));
        Text installationLocationText = new Text(SaveLoadManager.getTranslation("InstallationLocation"));
        installationLocation.setGraphic(installationLocationText);

        ToggleButton appearance = new ToggleButton();
        appearance.setSkin(new ToggleButtonNotGradientColor(appearance, LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-bg-selected"), LoadCSSStyles.getCSSColor("-hollow-button-clicked")));
        Text languageText = new Text(SaveLoadManager.getTranslation("Appearance"));
        appearance.setGraphic(languageText);

        installationLocation.setOnAction( e ->
        {
            InstallationLocationWindow installationLocationWindow = new InstallationLocationWindow();
            installationLocationWindow.setModel(model);
            setRightSide(installationLocationWindow);
        });
        appearance.setOnAction( e -> setRightSide(new AppearanceWindow(model.getSupportedLanguages())));

        installationLocation.setToggleGroup(toggleGroup);
        appearance.setToggleGroup(toggleGroup);

        toggleGroup.selectToggle(installationLocation);

        leftBarTop.getChildren().addAll(titleHolder, installationLocation, appearance);

        Text version = new Text(SaveLoadManager.getTranslation("CouldntRetrieveLauncherVersion"));

        Properties properties = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("Jam54LauncherConfig.properties"))
        {
            properties.load(in);
            version = new Text(properties.getProperty("version"));
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorLoadingJam54LauncherConfig"));
            errorMessage.show();
        }
        HBox versionHolder = new HBox(version);

        HBox socials = new HBox();
        socials.getStyleClass().add("socials");

        Button youtube = new Button();
        Button discord = new Button();
        Button github = new Button();
        Button mail = new Button();

        youtube.setId("youtubeButton");
        discord.setId("discordButton");
        github.setId("githubButton");
        mail.setId("mailButton");

        youtube.setOnAction(e ->
        {
            try
            {
                (new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + "https://youtube.com/c/jam54" + '"')).start();
            } catch (IOException ex)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntOpenWebsite") + " " + ex.getMessage());
                errorMessage.show();
            }
        });
        discord.setOnAction(e ->
        {
            try
            {
                (new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + "https://discord.gg/z6wEvv7" + '"')).start();
            } catch (IOException ex)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntOpenWebsite") + " " + ex.getMessage());
                errorMessage.show();
            }
        });
        github.setOnAction(e ->
        {
            try
            {
                (new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + "https://github.com/jam53" + '"')).start();
            } catch (IOException ex)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntOpenWebsite") + " " + ex.getMessage());
                errorMessage.show();
            }
        });
        mail.setOnAction(e ->
        {
            try
            {
                (new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + "mailto:info@jam54.com" + '"')).start();
            } catch (IOException ex)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntOpenWebsite") + " " + ex.getMessage());
                errorMessage.show();
            }
        });

        socials.getChildren().addAll(youtube, discord, github, mail);

        leftBarBottom.getChildren().addAll(versionHolder, socials);

        leftBar.getChildren().addAll(leftBarTop, leftBarBottom);

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
