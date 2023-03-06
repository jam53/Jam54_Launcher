package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Updating.FileSplitterCombiner;
import com.jam54.jam54_launcher.Updating.Hashes;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

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

                InstallApp installApp = new InstallApp();

                installButton.setOnAction(e ->
                {
                    new Thread(installApp).start();
                    installButton.setDisable(true);
                    installButton.textProperty().bind(installApp.messageProperty()); //Update button's text with progress
                });
                installApp.setOnSucceeded(e ->
                {
                    installButton.setDisable(false); //When the task finishes, enable the button again

                    ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());
                    model.setOpenedApplication(updatedApp);

                    ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                    applicationsInModel.remove(openedApp);
                    applicationsInModel.add(updatedApp);
                    model.setAllApplications(applicationsInModel);

                    String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                    installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                    SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                });
            }
            else if (openedApp.updateAvailable()) //If the app is installed but if there is an update available
            {
                Button removeButton = new Button("%Remove");
                Button updateButton = new Button("%Update");

                installButtonsHolder.getChildren().add(removeButton);
                installButtonsHolder.getChildren().add(updateButton);

                InstallApp installApp = new InstallApp();
                RemoveApp removeApp = new RemoveApp();

                updateButton.setOnAction(e -> {
                    new Thread(installApp).start();
                    updateButton.setDisable(true);
                    removeButton.setDisable(true);
                    updateButton.textProperty().bind(installApp.messageProperty()); //Update button's text with progress
                });
                installApp.setOnSucceeded(e -> {
                    updateButton.setDisable(false); //When the task finishes, enable the button again
                    removeButton.setDisable(false);

                    ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());
                    model.setOpenedApplication(updatedApp);

                    ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                    applicationsInModel.remove(openedApp);
                    applicationsInModel.add(updatedApp);
                    model.setAllApplications(applicationsInModel);

                    String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                    installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                    SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                });

                removeButton.setOnAction(e -> {
                    new Thread(removeApp).start();
                    updateButton.setDisable(true);
                    removeButton.setDisable(true);
                    removeButton.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                });

                removeApp.setOnSucceeded(e -> {
                    updateButton.setDisable(false); //When the task finishes, enable the button again
                    removeButton.setDisable(false);

                    ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), openedApp.updateAvailable(), openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());
                    model.setOpenedApplication(updatedApp);

                    ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                    applicationsInModel.remove(openedApp);
                    applicationsInModel.add(updatedApp);
                    model.setAllApplications(applicationsInModel);

                    String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                    installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                    SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                });
            }
            else //If the app is installed and there is no update available
            {
                Button removeButton = new Button("%Remove");
                Button playButton = new Button("%Play");

                installButtonsHolder.getChildren().add(removeButton);
                installButtonsHolder.getChildren().add(playButton);

                RemoveApp removeApp = new RemoveApp();

                removeButton.setOnAction(e -> {
                    new Thread(removeApp).start();
                    playButton.setDisable(true);
                    removeButton.setDisable(true);
                    removeButton.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                });

                removeApp.setOnSucceeded(e -> {
                    playButton.setDisable(false); //When the task finishes, enable the button again
                    removeButton.setDisable(false);

                    ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), openedApp.updateAvailable(), openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());
                    model.setOpenedApplication(updatedApp);

                    ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                    applicationsInModel.remove(openedApp);
                    applicationsInModel.add(updatedApp);
                    model.setAllApplications(applicationsInModel);

                    String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                    installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                    SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                });

                playButton.setOnAction(this::playApp);
            }
        }
    }

    /**
     * This class is used to install or update an app
     * By comparing hashes in the cloud, and hashes computed locally, it will determine which files to erase/update
     *
     * We use a class instead of a function, so that we can make it run async/in parallel to avoid the UI freezing
     */
    private class InstallApp extends Task<Void>
    {
        int openedAppId = model.getOpenedApplication().id();
        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), openedAppId + "");
        String appsBaseDownloadUrl = "";
        HashMap<String, Path> hashesLocal;
        HashMap<String, Path> hashesCloud;

        @Override
        protected Void call()
        {
            //region load Jam54LauncherConfig properties file
            Properties properties = new Properties();

            try (InputStream in = Main.class.getResourceAsStream("Jam54LauncherConfig.properties"))
            {
                properties.load(in);
                appsBaseDownloadUrl = properties.getProperty("appsBaseDownloadUrl") + "/";
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorLoadingJam54LauncherConfig"));
                errorMessage.show();
            }
            //endregion

            //region get hashes
            updateMessage("%Calculating hashes");

            Hashes hashes = new Hashes();
            hashesLocal = hashes.calculateHashesForFilesInDirectory(appInstallationPath);
            hashesCloud = new HashMap<>();

            try
            {
                Path tempFile = Files.createTempFile("Hashes", ".txt");
                FileUtils.copyURLToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + "Hashes.txt"), tempFile.toFile(), 10000, 10000);

                for(String line : FileUtils.readLines(tempFile.toFile(), StandardCharsets.UTF_8))
                {
                    String[] keyValue = line.split("\\|"); //Escaped '|' character
                    hashesCloud.put(keyValue[0], Path.of(keyValue[1]));
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            //endregion

            //region calculate files to remove and remove (hashesLocal verschil hashesCloud)
            updateMessage("%Removing old files");
            HashMap<String, Path> differenceMap = new HashMap<>(hashesLocal);
            differenceMap.entrySet().removeAll(hashesCloud.entrySet());

            for (Path fileToRemove : differenceMap.values())
            {
                Path.of(appInstallationPath.toString(), fileToRemove.toString()).toFile().delete();
            }
            //endregion

            //region calculate files that are either missing or changed and download them (hashesCloud verschil hashesLocal)
            differenceMap = new HashMap<>(hashesCloud);
            differenceMap.entrySet().removeAll(hashesLocal.entrySet());

            try
            {
                float filesDownloaded = 0;
                float filesToDownload = differenceMap.size();
                for (Path fileToDownload : differenceMap.values())
                {
                    filesDownloaded++;
                    updateMessage(Math.round((filesDownloaded/filesToDownload)*100) + "%");
                    FileUtils.copyURLToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + fileToDownload.toString().replace("\\", "/").replace(" ", "%20")), Path.of(appInstallationPath.toString(), fileToDownload.toString()).toFile(), 10000, 10000); //wth
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            //endregion

            //region In case there were splitted files, merge them
            FileSplitterCombiner fileSplitterCombiner = new FileSplitterCombiner();
            updateMessage("%Installing");
            fileSplitterCombiner.combineSplitFiles(appInstallationPath);

            //endregion

            return null;
        }
    }

    /**
     * This class is used to remove an app
     *
     * We use a class instead of a function, so that we can make it run async/in parallel to avoid the UI freezing
     */
    private class RemoveApp extends Task<Void>
    {
        int openedAppId = model.getOpenedApplication().id();
        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), openedAppId + "");

        @Override
        protected Void call()
        {
            updateMessage("%Uninstalling");

            try
            {
                FileUtils.deleteDirectory(appInstallationPath.toFile());
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, "%Couldn't remove application. " + e.getMessage());
                errorMessage.show();
            }

            return null;
        }
    }

    /**
     * This function opens/starts a specific app
     */
    private void playApp(ActionEvent actionEvent)
    {

    }
}
