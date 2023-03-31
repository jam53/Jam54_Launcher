package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Windows.GamesPrograms.OptionsWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class is used to create the buttons on the Games/Programs windows
 * It shows the name and logo from the app. It also shows whether or not the app is installed (a colored vs grey image)
 * It shows if there is a new update available + it allows to open a little context menu
 */
public class ApplicationButton extends VBox
{
    private Jam54LauncherModel model;
    private ApplicationInfo info;

    private Popup popup;

    public ApplicationButton(ApplicationInfo info)
    {
        this.getStyleClass().add("applicationButton");
        this.info = info;

        //region image with rounded corners at the top
        //Just setting a radius through css didn't work. So basically we create 2 images, which we will then lay on top of each other using a stackpane.
        //For the upperHalf image, we create a rectangle and sest the Arc which creates a rectangle with rounded corners. We then use this rectangle to clip the upperHalf image
        //Since the images are going to be layered on top of each other. The lowerHalf image has to be a bit shorter than the upperHalf image. Otherwise we will just overlay the rounded corners of the upperHalf image with the unrounded corners of the lowerHalf image
        ImageView imageUpperHalf = new ImageView(info.image());
        imageUpperHalf.setFitWidth(188);
        imageUpperHalf.setFitHeight(188);
        ImageView imageLowerHalf = new ImageView(info.image());
        imageLowerHalf.setFitWidth(188);
        imageLowerHalf.setFitHeight(188);

        Rectangle clipRounded = new Rectangle();
        clipRounded.setWidth(188);
        clipRounded.setHeight(94);
        clipRounded.setArcHeight(7);
        clipRounded.setArcWidth(7);

        Rectangle clip = new Rectangle(0, 20, 188, 168);
        imageUpperHalf.setClip(clipRounded);
        imageLowerHalf.setClip(clip);

        StackPane finalImage = new StackPane();
        finalImage.getChildren().addAll(imageLowerHalf, imageUpperHalf);
        //endregion

        HBox bottom = new HBox();
        VBox textHolder = new VBox();
        HBox optionsHolder = new HBox();

        Text title = new Text(info.name());
        title.setWrappingWidth(130);

        Text status = new Text();
        ImageView statusImage;

        HBox statusHolder = new HBox();
        statusHolder.setId("statusHolder");

        if (info.version() == null)
        {
            status.setText(SaveLoadManager.getTranslation("Download"));
            statusImage = new ImageView(new Image(Main.class.getResource("img/icons/Download_ButtonIconUnselected.png").toString()));

            statusImage.setFitHeight(18);
            statusImage.setFitWidth(18);
        }
        else if (info.updateAvailable())
        {
            status.setText(SaveLoadManager.getTranslation("Update"));
            statusImage = new ImageView(new Image(Main.class.getResource("img/icons/Update_ButtonIconUnselected.png").toString()));

            statusImage.setFitHeight(18);
            statusImage.setFitWidth(18);
        }
        else
        {
            status.setText(SaveLoadManager.getTranslation("Launch"));
            statusImage = new ImageView(new Image(Main.class.getResource("img/icons/Play_ButtonIconUnselected.png").toString()));

            statusImage.setFitHeight(14.72);
            statusImage.setFitWidth(12.75);
        }
        statusHolder.getChildren().addAll(statusImage, status);

        textHolder.getChildren().addAll(title, statusHolder);

        Button optionsButton = new Button();
        optionsButton.setOnMouseClicked(this::openOptionsWindow);
        optionsButton.setSkin(new ButtonColor(optionsButton, Color.web("#242424"), Color.web("#595959"), Color.web("#595959")));

        optionsHolder.getChildren().add(optionsButton);

        bottom.getChildren().addAll(textHolder, optionsHolder);
        this.getChildren().addAll(finalImage, bottom);

        this.setOnMouseClicked(this::selectApplicationWindow);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
    }

    public void selectApplicationWindow(MouseEvent event)
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

    /**
     * Opens a optionsWindow at the location of the button
     */
    private void openOptionsWindow(MouseEvent event)
    {
        OptionsWindow optionsWindow = new OptionsWindow(info, this, model);
        optionsWindow.getStylesheets().add(Main.class.getResource("css/mainDark.css").toString());

        popup = new Popup();
        popup.getContent().add(optionsWindow);
        popup.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY());

        popup.setAutoHide(true); // Make the Popup automatically hide when it loses focus
    }

    /**
     * Closes the popup window which contains the optionsWindow
     */
    public void closeOptionsWindow()
    {
        popup.hide();
    }
}
