package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Windows.Application.ApplicationButton;
import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class is used to create the Options window.
 * I.e. it creates a window with options for a specific app, with options like "Go to store page", "Verify file integrity" etc
 */
public class OptionsWindow extends VBox
{
    public OptionsWindow(ApplicationInfo info, ApplicationButton applicationButton, Jam54LauncherModel model)
    {
        this.getStyleClass().add("optionsWindow");

        Button storePage_Button = new Button("%Go To Store Page");
        storePage_Button.setOnAction(e -> {applicationButton.selectApplicationWindow(null); applicationButton.closeOptionsWindow();});
        HBox verifyFileIntegrity_ButtonHolder = new HBox();
        HBox createDesktopShortcut_ButtonHolder = new HBox();

        HBox uninstallHolder = new HBox();

        HBox separatorHolder = new HBox();
        separatorHolder.setId("seperatorHolder");

        HBox versionHolder = new HBox();


        if (info.version() != null) //If the app is installed
        {
            try
            {
                Button verifyFileIntegrity_Button = new Button("%Verify file integrity");
                HBox.setHgrow(verifyFileIntegrity_Button, Priority.ALWAYS);
                verifyFileIntegrity_ButtonHolder.getChildren().add(verifyFileIntegrity_Button);
                verifyFileIntegrity_Button.setOnAction(event ->
                {
                    applicationButton.closeOptionsWindow();
                    model.addValidatingApp(info.id());

                    ApplicationWindow applicationWindow = new ApplicationWindow();
                    applicationWindow.setModel(model);
                    ApplicationWindow.InstallApp installApp = applicationWindow.new InstallApp();
                    new Thread(installApp).start();

                    installApp.setOnSucceeded(e ->
                    {
                        model.removeValidatingApp(info.id());

                        ApplicationInfo openedApp = info;

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
                });

                Button createDesktopShortcut_Button = new Button("%Create Desktop Shortcut");
                createDesktopShortcut_Button.setOnAction(e ->
                {
                    applicationButton.closeOptionsWindow();

                    ApplicationWindow applicationWindow = new ApplicationWindow();
                    applicationWindow.createShortcut(info);
                });
                createDesktopShortcut_ButtonHolder.getChildren().add(createDesktopShortcut_Button);

                Button uninstall_Button = new Button("%Uninstall");
                uninstall_Button.setOnAction(event ->
                {
                    applicationButton.closeOptionsWindow();
                    model.addRemovingApp(info.id());

                    ApplicationWindow applicationWindow = new ApplicationWindow();
                    applicationWindow.setModel(model);
                    ApplicationWindow.RemoveApp removeApp = applicationWindow.new RemoveApp();
                    new Thread(removeApp).start();

                    removeApp.setOnSucceeded(e ->
                    {
                        model.removeRemovingApp(info.id());

                        ApplicationInfo openedApp = info;

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
                });

                HBox appSize_TextHolder = new HBox();
                HBox.setHgrow(appSize_TextHolder, Priority.ALWAYS);
                Button appSize_Text = new Button(getFolderSize(Path.of(SaveLoadManager.getData().getDataPath().toString(), "" + info.id())));
                appSize_Text.setId("noButtonStyling");
                appSize_TextHolder.getChildren().add(appSize_Text);
                uninstallHolder.getChildren().addAll(uninstall_Button, appSize_TextHolder);

                Separator separator = new Separator();
                HBox.setHgrow(separator, Priority.ALWAYS);
                separatorHolder.getChildren().add(separator);

                versionHolder.setId("versionHolder");
                Button version_Button = new Button("%Version");
                version_Button.setId("noButtonStyling");
                HBox appVersion_ButtonHolder = new HBox();
                HBox.setHgrow(appVersion_ButtonHolder, Priority.ALWAYS);
                Button appVersion_Button = new Button(info.version());
                appVersion_Button.setId("noButtonStyling");
                appVersion_ButtonHolder.getChildren().add(appVersion_Button);
                versionHolder.getChildren().addAll(version_Button, appVersion_ButtonHolder);
            }
            catch (Exception e)
            { //If we have an installed app, open the store page, remove it, and open the options menu. Then the info object wont be updated and we would therefore think the app is still installed. Stuff inside the try statement would then throw errors; for example when calculating the size of the app etc. That's why there is a try-catch here
                verifyFileIntegrity_ButtonHolder.getChildren().clear();
                createDesktopShortcut_ButtonHolder.getChildren().clear();
            }
        }

        this.getChildren().addAll(storePage_Button, verifyFileIntegrity_ButtonHolder, createDesktopShortcut_ButtonHolder, uninstallHolder, separatorHolder, versionHolder);
    }

    /**
     * Given a path to a folder, returns the size of that folder as either "", "X MB" or "X GB"
     */
    private String getFolderSize(Path path)
    {
        long sizeInBytes = FileUtils.sizeOfDirectory(path.toFile());

        sizeInBytes = sizeInBytes / 1024 / 1024; //Get the size in megabytes instead of bytes

        if (sizeInBytes <= 1) //If the directory is empty
        {
            return "";
        }
        else if (sizeInBytes > 1024) //If the directory is bigger than 1 gigabyte
        {
            sizeInBytes = sizeInBytes / 1024; //Convert it to gigabytes
            return sizeInBytes + " GB";
        }
        else
        {
            return sizeInBytes + " MB";
        }
    }
}
