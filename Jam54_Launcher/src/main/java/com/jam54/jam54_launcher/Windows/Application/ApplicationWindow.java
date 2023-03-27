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

import java.io.*;
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
import java.util.concurrent.TimeUnit;

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
    private Button installUpdateButton;
    private InstallApp installApp;


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

        leftSide.getChildren().addAll(latestUpdate_HBox, new Separator(), releaseDate_HBox, new Separator(), platform_HBox, new Separator());

        topBar.getChildren().addAll(title, backToLibrary);

        rightSide.getChildren().addAll(descriptionHolder, installButtonsHolder);

        this.getChildren().addAll(topBar, leftSide, rightSide);

        installUpdateButton = new Button(); //We initialiseren deze variabele reeds hier, in tegenstelling tot de play/remove buttons die elke keer opnieuw worden angemaakt bij het openen van het ApplicationWindow. We doen dit op voorhand omdat in het geval dat de gebruiker een update start, het ApplicationWindow verlaat, en terugkomt. In dat geval zal de install button er weer staan
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
            if (model.isAppValidating(openedApp.id()))
            {
                Button validatingAppButton = new Button("%Kindly hold on whilst " + openedApp.name() + " concludes its file validation procedure.");
                installButtonsHolder.getChildren().add(validatingAppButton);
                validatingAppButton.setDisable(true);
            }
            else if (model.isAppRemoving(openedApp.id()))
            {
                Button removingAppButton = new Button("%Kindly hold until " + openedApp.name() + "'s uninstall procedure finishes.");
                installButtonsHolder.getChildren().add(removingAppButton);
                removingAppButton.setDisable(true);
            }
            else if (model.getUpdatingApp() == null || !openedApp.updateAvailable()) //Check if there isn't another app updating/downloading files
            {
                installUpdateButton.textProperty().unbind();
                if (openedApp.version() == null) //If the app isn't installed
                {
                    installUpdateButton.setText("%Install");
                    installButtonsHolder.getChildren().add(installUpdateButton);

                    installApp = new InstallApp();

                    installUpdateButton.setOnAction(e ->
                    {
                        model.setUpdatingApp(openedApp.id());
                        new Thread(installApp).start();
                        installUpdateButton.setDisable(true);
                        installUpdateButton.textProperty().bind(installApp.messageProperty()); //Update button's text with progress
                    });
                    installApp.setOnSucceeded(e ->
                    {
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again

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
                    installUpdateButton.setText("%Update");
                    Button removeButton = new Button("%Remove");

                    installButtonsHolder.getChildren().add(removeButton);
                    installButtonsHolder.getChildren().add(installUpdateButton);

                    installApp = new InstallApp();
                    RemoveApp removeApp = new RemoveApp();

                    installUpdateButton.setOnAction(e ->
                    {
                        model.setUpdatingApp(openedApp.id());
                        new Thread(installApp).start();
                        installUpdateButton.setDisable(true);
                        removeButton.setDisable(true);
                        installUpdateButton.textProperty().bind(installApp.messageProperty()); //Update button's text with progress
                    });
                    installApp.setOnSucceeded(e ->
                    {
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
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

                    removeButton.setOnAction(e ->
                    {
                        new Thread(removeApp).start();
                        installUpdateButton.setDisable(true);
                        removeButton.setDisable(true);
                        removeButton.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                    });

                    removeApp.setOnSucceeded(e ->
                    {
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
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
                    Button playButton = new Button("%Start");

                    installButtonsHolder.getChildren().add(removeButton);
                    installButtonsHolder.getChildren().add(playButton);

                    RemoveApp removeApp = new RemoveApp();

                    removeButton.setOnAction(e ->
                    {
                        new Thread(removeApp).start();
                        playButton.setDisable(true);
                        removeButton.setDisable(true);
                        removeButton.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                    });

                    removeApp.setOnSucceeded(e ->
                    {
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
            else if (model.getUpdatingApp() == openedApp.id())
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.textProperty().bind(installApp.messageProperty());
            }
            else if (model.getUpdatingApp() != null)
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setText("%Kindly hold on whilst " + model.getApp(model.getUpdatingApp()).name() + " concludes its installation procedure.");
            }
        }
    }

    /**
     * This class is used to install or update an app
     * By comparing hashes in the cloud, and hashes computed locally, it will determine which files to erase/update
     *
     * We use a class instead of a function, so that we can make it run async/in parallel to avoid the UI freezing
     */
    public class InstallApp extends Task<Void>
    {
        int openedAppId = model.getOpenedApplication() != null ? model.getOpenedApplication().id() : model.getLastValidatingApp();
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
                    System.out.println("Downloading: " + fileToDownload);
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
            createShortcut(model.getApp(openedAppId));
            //endregion

            return null;
        }
    }

    /**
     * This class is used to remove an app
     *
     * We use a class instead of a function, so that we can make it run async/in parallel to avoid the UI freezing
     */
    public class RemoveApp extends Task<Void>
    {
        int openedAppId = model.getOpenedApplication() != null ? model.getOpenedApplication().id() : model.getLastRemovingApp();
        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), openedAppId + "");

        @Override
        protected Void call()
        {
            updateMessage("%Uninstalling");

            model.removeRunningApp(openedAppId);
            String openedAppExecutableName = "";

            try
            {
                String entryPoint = FileUtils.readFileToString(Path.of(appInstallationPath.toString(), "EntryPoint.txt").toFile(), StandardCharsets.UTF_8);
                openedAppExecutableName = new File(entryPoint).getName();

                ProcessBuilder closeApp = new ProcessBuilder("cmd.exe", "/c", "taskkill /f /im " + '"' + openedAppExecutableName + '"');

                closeApp.start();

                long startTime = System.currentTimeMillis();
                long maxWaitTime = 20000; // Maximum wait time in milliseconds

                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "tasklist");
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null && System.currentTimeMillis() - startTime < maxWaitTime) {
                    if (line.contains(openedAppExecutableName)) {
                        //We just loop here until the application is no longer present in the list, i.e. until the process that is removing it has finsihed
                        //`process.waitfor();` didn't really work, since we aren't directly launching the application binary, but rather launching a new cmd prompt that we use to launch the application
                    }
                }
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, "%Please ensure that " + openedAppExecutableName + " is closed before proceeding with the uninstallation process. " + e.getMessage());
                errorMessage.show();
            }

            try
            {
                FileUtils.deleteDirectory(appInstallationPath.toFile());
                removeShortcut(model.getApp(openedAppId));
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, "%Please ensure that " + openedAppExecutableName + " is closed before proceeding with the uninstallation process. " + e.getMessage());
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
        int openedAppId = model.getOpenedApplication().id();
        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), openedAppId + "");

        try
        {
            String entryPoint = FileUtils.readFileToString(Path.of(appInstallationPath.toString(), "EntryPoint.txt").toFile(), StandardCharsets.UTF_8);

            String fullPathToApp = entryPoint.contains(":") ? entryPoint : Path.of(appInstallationPath.toString(), entryPoint).toString(); //If the entrypoint contains a ":" character. Then it means it isn't a path to a file, but rather an URL to a website or an URL to send a mail. In such cases we don't want to use Path.of() but just the raw string

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", "\"\" " + '"' + fullPathToApp + '"');
            processBuilder.start();
            model.addRunningApp(openedAppId, new File(entryPoint).getName()); //We keep track of the process used to start this application, this way we can stop/close the application when we want to remove it, in case it would still be running
        }
        catch (IOException e)
        {
            ErrorMessage errorMessage = new ErrorMessage(false, "%Couldn't launch application. " + e.getMessage());
            errorMessage.show();
        }
    }

    /**
     * Create the shortcuts for the given application info on both the desktop and the "Start Menu/Programs" folder, so that the app shows up in Windows Search
     */
    public void createShortcut(ApplicationInfo info)
    {
        int appId = info.id();
        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), appId + "");

        try
        {
            String entryPoint = FileUtils.readFileToString(Path.of(appInstallationPath.toString(), "EntryPoint.txt").toFile(), StandardCharsets.UTF_8);

            String fullPathToApp = entryPoint.contains(":") ? entryPoint : Path.of(appInstallationPath.toString(), entryPoint).toString(); //If the entrypoint contains a ":" character. Then it means it isn't a path to a file, but rather an URL to a website or an URL to send a mail. In such cases we don't want to use Path.of() but just the raw string

            String desktopShortcutCommand = "cmd.exe /c powershell.exe -Command \"$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut(\\\"$([System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Desktop))\\\\" + info.name() + ".lnk\\\"); $Shortcut.TargetPath = \\\"" + fullPathToApp.replace("\\", "\\\\") + "\\\"; $Shortcut.Save()\"";

            String startMenuShortcutCommand = "cmd.exe /c powershell.exe -Command \"$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut(\\\"%userprofile%\\\\\\\\AppData\\\\\\\\Roaming\\\\\\\\Microsoft\\\\\\\\Windows\\\\\\\\Start Menu\\\\\\\\Programs\\\\\\\\\\\\" + info.name() + ".lnk\\\"); $Shortcut.TargetPath = \\\"" + fullPathToApp.replace("\\", "\\\\") + "\\\"; $Shortcut.Save()\"";

            File scriptFile = File.createTempFile ("createShortcuts", ".bat");
            try (PrintWriter script = new PrintWriter(scriptFile)) {
                script.println(desktopShortcutCommand);
                script.println(startMenuShortcutCommand);
            }

            ProcessBuilder createShortcuts = new ProcessBuilder(scriptFile.getAbsolutePath());
            createShortcuts.start();
        }
        catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(false, "%Couldn't create shortcut. " + e.getMessage());
            errorMessage.show();
        }
    }

    /**
     * Removes the shortcuts for the given application info in both the desktop and the "Start Menu/Programs" folder
     */
    public void removeShortcut(ApplicationInfo info)
    {
        int appId = info.id();

        try
        {
            String desktopShortcutCommand = "cmd.exe /c powershell.exe -Command \"Remove-Item \\\"$([System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Desktop))\\\\" + info.name() + ".lnk\\\"";

            String startMenuShortcutCommand = "cmd.exe /c powershell.exe -Command \"Remove-Item \\\"%userprofile%\\\\\\\\AppData\\\\\\\\Roaming\\\\\\\\Microsoft\\\\\\\\Windows\\\\\\\\Start Menu\\\\\\\\Programs\\\\\\\\\\\\" + info.name() + ".lnk\\\"";

            File scriptFile = File.createTempFile ("removeShortcuts", ".bat");
            try (PrintWriter script = new PrintWriter(scriptFile)) {
                script.println(desktopShortcutCommand);
                script.println(startMenuShortcutCommand);
            }

            ProcessBuilder removeShortcuts = new ProcessBuilder(scriptFile.getAbsolutePath());
            removeShortcuts.start();
        }
        catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(false, "%Couldn't remove shortcut. " + e.getMessage());
            errorMessage.show();
        }
    }
}
