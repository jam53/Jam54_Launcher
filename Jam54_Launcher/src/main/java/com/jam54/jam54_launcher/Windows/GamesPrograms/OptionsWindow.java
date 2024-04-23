package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Windows.Application.ApplicationButton;
import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * This class is used to create the Options window.
 * I.e. it creates a window with options for a specific app, with options like "Go to store page", "Verify file integrity" etc
 */
public class OptionsWindow extends VBox
{
    /**
     * Constructor from when being called from the ApplicationButton
     */
    public OptionsWindow(ApplicationInfo info, ApplicationButton applicationButton, Jam54LauncherModel model)
    {
        this.getStyleClass().add("optionsWindow");

        HBox storePage_ButtonHolder = new HBox();
        Button storePage_Button = new Button();
        HBox.setHgrow(storePage_Button, Priority.ALWAYS);
        storePage_Button.setGraphic(new Text(SaveLoadManager.getTranslation("GoToStorePage")));
        storePage_Button.setOnAction(e -> {applicationButton.selectApplicationWindow(null); applicationButton.closeOptionsWindow();});
        storePage_ButtonHolder.getChildren().add(storePage_Button);

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
                if (model.getUpdatingApp() == null) // if there isn't an app being updated
                {
                    Button verifyFileIntegrity_Button = new Button();
                    verifyFileIntegrity_Button.setGraphic(new Text(SaveLoadManager.getTranslation("VerifyFileIntegrity")));
                    HBox.setHgrow(verifyFileIntegrity_Button, Priority.ALWAYS);
                    verifyFileIntegrity_ButtonHolder.getChildren().add(verifyFileIntegrity_Button);
                    verifyFileIntegrity_Button.setOnAction(event ->
                    {
                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            applicationButton.closeOptionsWindow();
                            model.addValidatingApp(info.id());

                            ApplicationWindow applicationWindow = new ApplicationWindow();
                            applicationWindow.setModel(model);
                            ApplicationWindow.InstallApp installApp = applicationWindow.new InstallApp(info.id());
                            new Thread(installApp).start();

                            installApp.setOnSucceeded(e ->
                            {
                                model.removeValidatingApp(info.id());

                                ApplicationInfo openedApp = info;

                                ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                                ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                                applicationsInModel.remove(openedApp);
                                applicationsInModel.add(updatedApp);
                                model.setAllApplications(applicationsInModel);

                                ArrayList<ApplicationInfo> newVisibleApplicationsInModel = new ArrayList<>();
                                for (ApplicationInfo visibleApp : model.getVisibleApplicationInfos())
                                {
                                    if (visibleApp.id() == (openedApp.id()))
                                    {
                                        newVisibleApplicationsInModel.add(updatedApp);
                                    }
                                    else
                                    {
                                        newVisibleApplicationsInModel.add(visibleApp);
                                    }
                                }
                                model.setVisibleApplicationInfos(newVisibleApplicationsInModel);

                                String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                                installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                                SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                            });
                            installApp.setOnCancelled(e ->
                            {
                                model.removeValidatingApp(info.id());
                            });
                        }
                    });
                }

                Button createDesktopShortcut_Button = new Button();
                createDesktopShortcut_Button.setGraphic(new Text(SaveLoadManager.getTranslation("CreateDesktopShortcut")));
                createDesktopShortcut_Button.setOnAction(e ->
                {
                    applicationButton.closeOptionsWindow();

                    ApplicationWindow applicationWindow = new ApplicationWindow();
                    applicationWindow.createShortcut(info);
                });
                createDesktopShortcut_ButtonHolder.getChildren().add(createDesktopShortcut_Button);

                if (model.getUpdatingApp() == null) // if there isn't an app being updated
                {
                    Button uninstall_Button = new Button();
                    uninstall_Button.setGraphic(new Text(SaveLoadManager.getTranslation("Uninstall")));
                    uninstall_Button.setOnAction(event ->
                    {
                        applicationButton.closeOptionsWindow();

                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            model.addRemovingApp(info.id());

                            ApplicationWindow applicationWindow = new ApplicationWindow();
                            applicationWindow.setModel(model);
                            ApplicationWindow.RemoveApp removeApp = applicationWindow.new RemoveApp();
                            new Thread(removeApp).start();

                            removeApp.setOnSucceeded(e ->
                            {
                                model.removeRemovingApp(info.id());

                                ApplicationInfo openedApp = info;

                                ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), true, openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                                ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                                applicationsInModel.remove(openedApp);
                                applicationsInModel.add(updatedApp);
                                model.setAllApplications(applicationsInModel);

                                ArrayList<ApplicationInfo> newVisibleApplicationsInModel = new ArrayList<>();
                                for (ApplicationInfo visibleApp : model.getVisibleApplicationInfos())
                                {
                                    if (visibleApp.id() == (openedApp.id()))
                                    {
                                        newVisibleApplicationsInModel.add(updatedApp);
                                    }
                                    else
                                    {
                                        newVisibleApplicationsInModel.add(visibleApp);
                                    }
                                }
                                model.setVisibleApplicationInfos(newVisibleApplicationsInModel);

                                String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                                installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                                SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                            });
                        }
                    });

                    HBox appSize_TextHolder = new HBox();
                    HBox.setHgrow(appSize_TextHolder, Priority.ALWAYS);
                    Button appSize_Text = new Button();
                    appSize_Text.setGraphic(new Text(getFolderSize(Path.of(SaveLoadManager.getData().getDataPath().toString(), "" + info.id()))));
                    appSize_Text.setId("noButtonStyling");
                    appSize_TextHolder.getChildren().add(appSize_Text);
                    uninstallHolder.getChildren().addAll(uninstall_Button, appSize_TextHolder);
                }

                Separator separator = new Separator();
                HBox.setHgrow(separator, Priority.ALWAYS);
                separatorHolder.getChildren().add(separator);

                versionHolder.setId("versionHolder");
                Button version_Button = new Button();
                version_Button.setGraphic(new Text(SaveLoadManager.getTranslation("Version")));
                version_Button.setId("noButtonStyling");
                HBox appVersion_ButtonHolder = new HBox();
                HBox.setHgrow(appVersion_ButtonHolder, Priority.ALWAYS);
                Button appVersion_Button = new Button();
                appVersion_Button.setGraphic(new Text(info.version()));
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

        this.getChildren().addAll(storePage_ButtonHolder, verifyFileIntegrity_ButtonHolder, createDesktopShortcut_ButtonHolder, uninstallHolder, separatorHolder, versionHolder);
    }

    /**
     * Constructor from when being called from the ApplicationWindow
     */
    public OptionsWindow(ApplicationWindow applicationWindow, Jam54LauncherModel model)
    {
        this.getStyleClass().add("optionsWindow");

        HBox verifyFileIntegrity_ButtonHolder = new HBox();
        HBox createDesktopShortcut_ButtonHolder = new HBox();

        HBox uninstallHolder = new HBox();

        HBox separatorHolder = new HBox();
        separatorHolder.setId("seperatorHolder");

        HBox versionHolder = new HBox();

        ApplicationInfo info = model.getOpenedApplication();

        if (info.version() != null) //If the app is installed
        {
            try
            {
                if (model.getUpdatingApp() == null) // if there isn't an app being updated
                {
                    Button verifyFileIntegrity_Button = new Button();
                    verifyFileIntegrity_Button.setGraphic(new Text(SaveLoadManager.getTranslation("VerifyFileIntegrity")));
                    HBox.setHgrow(verifyFileIntegrity_Button, Priority.ALWAYS);
                    verifyFileIntegrity_ButtonHolder.getChildren().add(verifyFileIntegrity_Button);
                    verifyFileIntegrity_Button.setOnAction(event ->
                    {
                        applicationWindow.closeOptionsWindow();

                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            model.addValidatingApp(info.id());

                            ApplicationWindow.InstallApp installApp = applicationWindow.new InstallApp(info.id());
                            new Thread(installApp).start();

                            installApp.setOnSucceeded(e ->
                            {
                                model.removeValidatingApp(info.id());

                                ApplicationInfo openedApp = info;

                                ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                                if (model.getOpenedApplication().id() == updatedApp.id())
                                {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                                    model.setOpenedApplication(updatedApp);//We use this to "refresh" the ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
                                }

                                ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                                applicationsInModel.remove(openedApp);
                                applicationsInModel.add(updatedApp);
                                model.setAllApplications(applicationsInModel);

                                ArrayList<ApplicationInfo> newVisibleApplicationsInModel = new ArrayList<>();
                                for (ApplicationInfo visibleApp : model.getVisibleApplicationInfos())
                                {
                                    if (visibleApp.id() == (openedApp.id()))
                                    {
                                        newVisibleApplicationsInModel.add(updatedApp);
                                    }
                                    else
                                    {
                                        newVisibleApplicationsInModel.add(visibleApp);
                                    }
                                }
                                model.setVisibleApplicationInfos(newVisibleApplicationsInModel);

                                String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                                installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                                SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                            });
                            installApp.setOnCancelled(e ->
                            {
                                model.removeValidatingApp(info.id());
                            });
                        }
                    });
                }

                Button createDesktopShortcut_Button = new Button();
                createDesktopShortcut_Button.setGraphic(new Text(SaveLoadManager.getTranslation("CreateDesktopShortcut")));
                createDesktopShortcut_Button.setOnAction(e ->
                {
                    applicationWindow.closeOptionsWindow();
                    applicationWindow.createShortcut(info);
                });
                createDesktopShortcut_ButtonHolder.getChildren().add(createDesktopShortcut_Button);

                if (model.getUpdatingApp() == null) // if there isn't an app being updated
                {
                    Button uninstall_Button = new Button();
                    uninstall_Button.setGraphic(new Text(SaveLoadManager.getTranslation("Uninstall")));
                    uninstall_Button.setOnAction(event ->
                    {
                        applicationWindow.closeOptionsWindow();

                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            model.addRemovingApp(info.id());

                            ApplicationWindow.RemoveApp removeApp = applicationWindow.new RemoveApp();
                            new Thread(removeApp).start();

                            removeApp.setOnSucceeded(e ->
                            {
                                model.removeRemovingApp(info.id());

                                ApplicationInfo openedApp = info;

                                ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), true, openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                                if (model.getOpenedApplication().id() == updatedApp.id())
                                {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                                    model.setOpenedApplication(updatedApp);//We use this to "refresh" the ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
                                }

                                ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
                                applicationsInModel.remove(openedApp);
                                applicationsInModel.add(updatedApp);
                                model.setAllApplications(applicationsInModel);

                                ArrayList<ApplicationInfo> newVisibleApplicationsInModel = new ArrayList<>();
                                for (ApplicationInfo visibleApp : model.getVisibleApplicationInfos())
                                {
                                    if (visibleApp.id() == (openedApp.id()))
                                    {
                                        newVisibleApplicationsInModel.add(updatedApp);
                                    }
                                    else
                                    {
                                        newVisibleApplicationsInModel.add(visibleApp);
                                    }
                                }
                                model.setVisibleApplicationInfos(newVisibleApplicationsInModel);

                                String[] installedApplicationVersions = SaveLoadManager.getData().getInstalledApplicationVersions();
                                installedApplicationVersions[updatedApp.id()] = updatedApp.version();
                                SaveLoadManager.getData().setInstalledApplicationVersions(installedApplicationVersions);
                            });
                        }
                    });

                    HBox appSize_TextHolder = new HBox();
                    HBox.setHgrow(appSize_TextHolder, Priority.ALWAYS);
                    Button appSize_Text = new Button();
                    appSize_Text.setGraphic(new Text(getFolderSize(Path.of(SaveLoadManager.getData().getDataPath().toString(), "" + info.id()))));
                    appSize_Text.setId("noButtonStyling");
                    appSize_TextHolder.getChildren().add(appSize_Text);

                    uninstallHolder.getChildren().addAll(uninstall_Button, appSize_TextHolder);
                }

                Separator separator = new Separator();
                HBox.setHgrow(separator, Priority.ALWAYS);
                separatorHolder.getChildren().add(separator);

                versionHolder.setId("versionHolder");
                Button version_Button = new Button();
                version_Button.setGraphic(new Text(SaveLoadManager.getTranslation("Version")));
                version_Button.setId("noButtonStyling");
                HBox appVersion_ButtonHolder = new HBox();
                HBox.setHgrow(appVersion_ButtonHolder, Priority.ALWAYS);
                Button appVersion_Button = new Button();
                appVersion_Button.setGraphic(new Text(info.version()));
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

        this.getChildren().addAll(verifyFileIntegrity_ButtonHolder, createDesktopShortcut_ButtonHolder, uninstallHolder, separatorHolder, versionHolder);
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