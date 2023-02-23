package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * This window displays more detail about a specific app.
 * It shows a description, the name of the app, when the latest update was, ...
 */
public class ApplicationWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;

    private final HBox topBar;
    private final VBox leftSide;
    private final VBox rightSide;

    private final Label title;
    private final Button backToLibrary;

    private final ImageView imageView;
    private final HBox latestUpdate_HBox;
    private final Label latestUpdate_Label;
    private final Label latestUpdateDate;

    private final HBox releaseDate_HBox;
    private final Label releaseDate_Label;
    private final Label releaseDateDate;

    private final HBox platform_HBox;
    private final Label platform_Label;
    private final HBox platformIcons_HBox;

    private final TextFlow descriptionHolder;
    private final Text description;
    private HBox installButtonsHolder;


    public ApplicationWindow()
    {
        topBar = new HBox();
        leftSide = new VBox();
        rightSide = new VBox();

        title = new Label();
        backToLibrary = new Button("%Back To Library");
        backToLibrary.setOnAction(this::backToLibrary);

        imageView = new ImageView();

        latestUpdate_HBox = new HBox();
        latestUpdate_Label = new Label("%LatestUpdate");
        latestUpdateDate = new Label();

        releaseDate_HBox = new HBox();
        releaseDate_Label = new Label("%ReleaseDate");
        releaseDateDate = new Label();

        platform_HBox = new HBox();
        platform_Label = new Label("%Platform");
        platformIcons_HBox = new HBox();

        latestUpdate_HBox.getChildren().addAll(latestUpdate_Label, latestUpdateDate);
        releaseDate_HBox.getChildren().addAll(releaseDate_Label, releaseDateDate);
        platform_HBox.getChildren().addAll(platform_Label, platformIcons_HBox);

        descriptionHolder = new TextFlow();
        description = new Text();
        descriptionHolder.getChildren().add(description);

        installButtonsHolder = new HBox();

        leftSide.getChildren().addAll(imageView, latestUpdate_HBox, new Separator(), releaseDate_HBox, new Separator(), platform_HBox, new Separator());

        topBar.getChildren().addAll(title, backToLibrary);

        rightSide.getChildren().addAll(descriptionHolder, installButtonsHolder);

        this.getChildren().addAll(topBar, leftSide, rightSide);
    }

    /**
     * This function gets excuted when the user clicks on the "back to library" button
     * The application window gets closed, and the games/programs window is shown
     */
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
        model.addListener(this);
    }

    /**
     * When the model is invalidated, this might mean that an application was opened and needs to be displayed
     * If that's the case, we update the UI in this function so that it reflects the information of the application that is currently set as the
     * "openedApplication" inside the model
     */
    @Override
    public void invalidated(Observable observable)
    {
        if (model.getOpenedApplication() != null)
        {
            ApplicationInfo openedApp = model.getOpenedApplication();

            title.setText(openedApp.name());

            imageView.setImage(openedApp.image());

            LocalDateTime localDateTimeLastUpdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(openedApp.lastUpdate() * 1000), ZoneId.systemDefault());
            LocalDateTime localDateTimeReleaseDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(openedApp.releaseDate() * 1000), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy", SaveLoadManager.getData().getLocale());

            latestUpdateDate.setText(localDateTimeLastUpdate.format(formatter));
            releaseDateDate.setText(localDateTimeReleaseDate.format(formatter));

            platformIcons_HBox.getChildren().clear();
            for (Platforms platform : openedApp.platforms())
            {
                platformIcons_HBox.getChildren().add(new Label(platform.name()));
            }

            description.setText(openedApp.descriptions().get(SaveLoadManager.getData().getLocale()));

            installButtonsHolder.getChildren().clear();
            if (openedApp.version() == null) //If the app isn't installed
            {
                Button installButton = new Button("%Install");
                installButtonsHolder.getChildren().add(installButton);

                installButton.setOnAction(this::installApp);
            }
            else if (openedApp.updateAvailable()) //If the app is installed but if there is an update available
            {
                Button removeButton = new Button("%Remove");
                Button updateButton = new Button("%Update");

                installButtonsHolder.getChildren().add(removeButton);
                installButtonsHolder.getChildren().add(updateButton);

                removeButton.setOnAction(this::removeApp);
                updateButton.setOnAction(this::installApp);
            }
            else //If the app is installed and there is no update available
            {
                Button removeButton = new Button("%Remove");
                Button playButton = new Button("%Play");

                installButtonsHolder.getChildren().add(removeButton);
                installButtonsHolder.getChildren().add(playButton);

                removeButton.setOnAction(this::removeApp);
                playButton.setOnAction(this::installApp);
            }
        }
    }

    /**
     * This function is used to install or update an app
     * By comparing hashes in the cloud, and hashes computed locally, it will determine which files to erase/update
     */
    private void installApp(ActionEvent actionEvent)
    {
        //TODO installeren en update is basically hetzelfde, hashes.txt downloaden hashes hier berekeken (bij installeren 0 obv)
        //hashes bij beide aanwezig -> ok
        //hashes lokaal aanwezig maar niet in cloud -> afvegen
        //hashes in cloud aanwezig maar niet lokaal -> downloaden

        //verschil van 2 dicts A en B met hashes/pad naar bestand
//        Map<K, V> differenceMap = new HashMap<>(A);
//        differenceMap.entrySet().removeAll(B.entrySet());

        //splitsen van hash%pad naar bestand en in dict zetten
//        String input = "key1%value1";
//
//        Map<String, String> map = new HashMap<>();
//
//        String[] keyValue = input.split("%");
//        String key = keyValue[0];
//        String value = keyValue[1];
//        map.put(key, value);
    }

    /**
     * This function is called in order to remove an app
     */
    private void removeApp(ActionEvent actionEvent)
    {

    }
}
