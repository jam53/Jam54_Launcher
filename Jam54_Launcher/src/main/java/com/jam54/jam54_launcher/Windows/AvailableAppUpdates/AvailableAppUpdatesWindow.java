package com.jam54.jam54_launcher.Windows.AvailableAppUpdates;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.Windows.Application.ApplicationWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to create the available app updates window, where the user can update apps in one centralized place
 */
public class AvailableAppUpdatesWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    private List<ApplicationInfo> appsWithUpdates;

    private final Text title;
    private final Button updateAll_Button;
    private final VBox applicationsHolder;

    public AvailableAppUpdatesWindow()
    {
        this.getStyleClass().add("availableAppUpdatesWindow");

        //region topbar
        title = new Text(SaveLoadManager.getTranslation("PendingApplicationUpdates"));

        Region fillSpace = new Region();
        HBox.setHgrow(fillSpace, Priority.ALWAYS);

        updateAll_Button = new Button(SaveLoadManager.getTranslation("UpdateAll"));
        updateAll_Button.setId("primaryButton");
        updateAll_Button.setSkin(new ButtonColor(updateAll_Button, LoadCSSStyles.getCSSColor("-accent-button-main"), LoadCSSStyles.getCSSColor("-accent-button-hovered"), LoadCSSStyles.getCSSColor("-accent-button-clicked")));

        updateAll_Button.setOnAction(e -> {
            appsWithUpdates.stream().filter(app -> !model.isAppInAppsToUpdateQueue(app)).forEach(model::addAppToAppsUpdateQueue);
        });

        HBox topBar = new HBox();
        topBar.setId("availableAppUpdatesWindowTopBar");
        topBar.getChildren().addAll(title, fillSpace, updateAll_Button);
        //endregion

        //region center area
        ScrollPane centerArea = new ScrollPane();
        centerArea.setId("centerArea");

        applicationsHolder = new VBox();
        applicationsHolder.getStyleClass().add("applicationsWithUpdatesHolder");

        centerArea.setContent(applicationsHolder);

        final double SPEED = 0.01;
        centerArea.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            centerArea.setVvalue(centerArea.getVvalue() - deltaY);
        });


        //region enable/disable scrollbar based on whether or not all of the content is visible inside the scrollpane
        // Get the position and size of the viewport
        applicationsHolder.heightProperty().addListener(e -> {

            Bounds viewportBounds = centerArea.getViewportBounds();
            double viewportHeight = viewportBounds.getHeight();

            // Get the size of the content
            double contentHeight = applicationsHolder.getBoundsInLocal().getHeight();

            // Check if there is content that isn't being shown
            boolean hasUnshownContent = contentHeight > viewportHeight + 18;

            if (hasUnshownContent)
            {
                centerArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            }
            else
            {
                centerArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        });
        //endregion
        //endregion

        this.getChildren().addAll(topBar, centerArea);
    }

    @Override
    public void invalidated(Observable observable)
    {
        appsWithUpdates = model.getAllApplications().stream().filter(applicationInfo -> applicationInfo.updateAvailable() && applicationInfo.version() != null).toList();

        if (appsWithUpdates.isEmpty() || appsWithUpdates.size() == model.getSizeOfAppsToUpdateQueue())
        {//Make the button invisible if there aren't any applications with updates
            updateAll_Button.setVisible(false);
        }

        //region check if we still have apps left the user wants to update and check if we can update or if there are other remove/validating/updating/... operations going on
        if (model.getSizeOfAppsToUpdateQueue() > 0 && model.getOpenedApplication() != null)
        {
            ApplicationInfo openedApp = model.getOpenedApplication();
            if (!model.isAppValidating(openedApp.id()) && !model.isAppRemoving(openedApp.id()) &&
                    model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null
            )
            {
                startAppUpdate(model.peekFirstAppFromAppsToUpdateQueue());
            }
        }
        else if (model.getSizeOfAppsToUpdateQueue() > 0 && model.getOpenedApplication() == null)
        {
            if (model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null)
            {
                startAppUpdate(model.peekFirstAppFromAppsToUpdateQueue());
            }
        }
        //endregion
        fillApplicationsHolder();
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
        invalidated(model);
    }

    private void fillApplicationsHolder()
    {
        applicationsHolder.getChildren().clear();

        for (ApplicationInfo application : appsWithUpdates)
        {
            AvailableAppUpdateButton button = new AvailableAppUpdateButton(application);
            button.setModel(model);

            applicationsHolder.getChildren().add(button);
        }

        if (applicationsHolder.getChildren().isEmpty())
        {
            HBox noApplicationsMatched = new HBox(new Text(SaveLoadManager.getTranslation("NoAvailableUpdates")));
            noApplicationsMatched.setId("noApplicationsMatched");
            applicationsHolder.getChildren().add(noApplicationsMatched);
        }

        int amountOfApps = appsWithUpdates.size();
        title.setText(SaveLoadManager.getTranslation("PendingApplicationUpdates") + (amountOfApps > 0 ? " (" + amountOfApps + ")" : ""));
    }

    /**
     * Starts the update proces for a given ApplicationInfo
     */
    private void startAppUpdate(ApplicationInfo appInfo)
    {
        ApplicationWindow applicationWindow = new ApplicationWindow();
        applicationWindow.setModel(model);
        ApplicationWindow.InstallApp installApp = applicationWindow.new InstallApp();

        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
        {
            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
            errorMessage.show();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(SaveLoadManager.getTranslation("INSTALL"));
            alert.setHeaderText(null);
            alert.getDialogPane().setContent(new Label(SaveLoadManager.getTranslation("ApplicationWillBeInstalledAt") + SaveLoadManager.getData().getDataPath() + "\n" + SaveLoadManager.getTranslation("ChangeInstallLocationInSettings")));

            ButtonType installButtonType = new ButtonType(StringUtils.capitalize(SaveLoadManager.getTranslation("INSTALL").toLowerCase()), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(SaveLoadManager.getTranslation("Cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(installButtonType, cancelButton);

            Optional<ButtonType> result = Optional.empty();
            if (!SaveLoadManager.getData().isChangeInstallLocationAlertWasShown())
            {
                result = alert.showAndWait();
            }
            if (SaveLoadManager.getData().isChangeInstallLocationAlertWasShown() || result.isPresent() && result.get() == installButtonType)
            {// user clicked ok button, do something here after the dialog is closed
                SaveLoadManager.getData().setChangeInstallLocationAlertWasShown(true);
                model.setUpdatingApp(appInfo.id());
                new Thread(installApp).start();

                model.setUpdatingAppMessageProperty(installApp.messageProperty());
                model.setUpdatingAppProgressProperty(installApp.progressProperty());
            }
            else
            {
                model.removeAppFromAppsToUpdateQueue(appInfo.id());
            }
        }
        installApp.setOnSucceeded(e ->
        {
            ApplicationInfo updatedApp = new ApplicationInfo(appInfo.id(), appInfo.name(), appInfo.image(), false, appInfo.availableVersion(), appInfo.availableVersion(), appInfo.descriptions(), appInfo.platforms(), appInfo.releaseDate(), appInfo.lastUpdate(), appInfo.isGame());

            ArrayList<ApplicationInfo> applicationsInModel = model.getAllApplications();
            applicationsInModel.remove(appInfo);
            applicationsInModel.add(updatedApp);
            model.setAllApplications(applicationsInModel);

            ArrayList<ApplicationInfo> newVisibleApplicationsInModel = new ArrayList<>();
            for (ApplicationInfo visibleApp : model.getVisibleApplicationInfos())
            {
                if (visibleApp.id() == (appInfo.id()))
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

            model.removeAppFromAppsToUpdateQueue(appInfo.id());
            model.setUpdatingApp(null);
        });
        installApp.setOnCancelled(e ->
        {
            model.setUpdatingApp(null);
        });
    }
}
