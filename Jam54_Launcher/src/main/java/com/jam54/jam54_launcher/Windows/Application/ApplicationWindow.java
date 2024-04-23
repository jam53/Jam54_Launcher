package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Animations.ToggleButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.ColorTheme;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Updating.DownloadFile;
import com.jam54.jam54_launcher.Updating.FileSplitterCombiner;
import com.jam54.jam54_launcher.Updating.Hashes;
import com.jam54.jam54_launcher.Windows.GamesPrograms.OptionsWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final Text title;
    private final ToggleButton backToLibrary;

    private final ImageView imageView;
    private final HBox latestUpdate_HBox;
    private final Text latestUpdate_Text;
    private final Text latestUpdateDate;

    private final HBox releaseDate_HBox;
    private final Text releaseDate_Text;
    private final Text releaseDateDate;

    private final HBox platform_HBox;
    private final Text platform_Text;
    private final HBox platformIcons_HBox;

    private final TextFlow descriptionHolder;
    private final Text description;
    private HBox installButtonsHolder;
    private Button installUpdateButton;
    private InstallApp installApp;
    private Popup popup;
    private HBox optionsButtonHolder;

    public ApplicationWindow()
    {
        this.getStyleClass().add("applicationWindow");

        //region topBar
        topBar = new HBox();
        topBar.getStyleClass().add("buttonBar");

        title = new Text();
        HBox titleHolder = new HBox(title);
        titleHolder.prefWidthProperty().bind(topBar.prefWidthProperty().divide(3));
        titleHolder.getStyleClass().add("buttonBarTitle");

        backToLibrary = new ToggleButton();
        backToLibrary.setGraphic(new Text(SaveLoadManager.getTranslation("BackToLibrary"))); //By default the text inside a ToggleButton is displayed usinga "Label" object, which looks terrible and not smooth when using a custom font
        backToLibrary.setSkin(new ToggleButtonColor(backToLibrary, LoadCSSStyles.getCSSColor("-toggle-button-flat"), Color.web("#2193D3"), Color.web("#494FD6")));
        backToLibrary.setOnAction(this::backToLibrary);
        HBox backToLibraryHolder = new HBox(backToLibrary);
        backToLibraryHolder.prefWidthProperty().bind(topBar.prefWidthProperty().divide(3));
        backToLibraryHolder.getStyleClass().add("buttonBarToggles");

        optionsButtonHolder = new HBox();
        optionsButtonHolder.prefWidthProperty().bind(topBar.prefWidthProperty().divide(3));
        Button optionsButton = new Button();
        optionsButton.setId("optionsButton");
        optionsButton.setOnMouseClicked(this::openOptionsWindow);
        optionsButton.setSkin(new ButtonColor(optionsButton, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"), LoadCSSStyles.getCSSColor("-hollow-button-clicked")));
        optionsButtonHolder.getChildren().add(optionsButton);
        optionsButtonHolder.getStyleClass().add("optionsButtonHolder");

        topBar.getChildren().addAll(titleHolder, backToLibraryHolder, optionsButtonHolder);
        //endregion

        //region left side
        leftSide = new VBox();
        leftSide.getStyleClass().add("leftSideApplicationWindow");

        imageView = new ImageView();
        imageView.setFitHeight(346);
        imageView.setFitWidth(346);

        Rectangle clipRounded = new Rectangle();
        clipRounded.setWidth(346);
        clipRounded.setHeight(346);
        clipRounded.setArcHeight(10);
        clipRounded.setArcWidth(10);

        imageView.setClip(clipRounded);

        latestUpdate_HBox = new HBox();
        latestUpdate_Text = new Text(SaveLoadManager.getTranslation("LatestUpdate"));
        latestUpdate_Text.setId("applicationWindowNotHighlightedText");
        latestUpdateDate = new Text();
        latestUpdateDate.setId("applicationWindowHighlightedText");

        releaseDate_HBox = new HBox();
        releaseDate_Text = new Text(SaveLoadManager.getTranslation("ReleaseDate"));
        releaseDate_Text.setId("applicationWindowNotHighlightedText");
        releaseDateDate = new Text();
        releaseDateDate.setId("applicationWindowHighlightedText");

        platform_HBox = new HBox();
        platform_HBox.setId("platform_HBox");
        platform_Text = new Text(SaveLoadManager.getTranslation("Platform"));
        platform_Text.setId("applicationWindowNotHighlightedText");
        platformIcons_HBox = new HBox();

        latestUpdate_HBox.getChildren().addAll(latestUpdate_Text, new HBox(latestUpdateDate));
        releaseDate_HBox.getChildren().addAll(releaseDate_Text, new HBox(releaseDateDate));
        platform_HBox.getChildren().addAll(new HBox(platform_Text, new HBox(platformIcons_HBox)));

        leftSide.getChildren().addAll(imageView, latestUpdate_HBox, new Separator(), releaseDate_HBox, new Separator(), platform_HBox);
        //endregion

        //region right side
        rightSide = new VBox();
        rightSide.getStyleClass().add("rightSideApplicationWindow");

        ScrollPane descriptionScrollPane = new ScrollPane();
        descriptionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        descriptionHolder = new TextFlow();
        descriptionHolder.setLineSpacing(5);
        description = new Text();
        descriptionHolder.getChildren().add(description);

        descriptionScrollPane.setContent(descriptionHolder);

        final double SPEED = 0.01;
        descriptionScrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            descriptionScrollPane.setVvalue(descriptionScrollPane.getVvalue() - deltaY);
        });

        //region enable/disable scrollbar based on whether or not all of the content is visible inside the scrollpane
        // Get the position and size of the viewport
        descriptionHolder.heightProperty().addListener(e -> {

            Bounds viewportBounds = descriptionScrollPane.getViewportBounds();
            double viewportHeight = viewportBounds.getHeight();

            // Get the size of the content
            double contentHeight = descriptionHolder.getBoundsInLocal().getHeight();

            // Check if there is content that isn't being shown
            boolean hasUnshownContent = contentHeight > viewportHeight;

            if (hasUnshownContent)
            {
                descriptionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            }
            else
            {
                descriptionScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        });
        //endregion


        installButtonsHolder = new HBox();

        rightSide.getChildren().addAll(descriptionScrollPane, installButtonsHolder);
        //endregion

        this.getChildren().addAll(topBar, new HBox(leftSide, rightSide));

        installUpdateButton = new Button(); //We initialiseren deze variabele reeds hier, in tegenstelling tot de play/remove buttons die elke keer opnieuw worden angemaakt bij het openen van het ApplicationWindow. We doen dit op voorhand omdat in het geval dat de gebruiker een update start, het ApplicationWindow verlaat, en terugkomt. In dat geval zal de install button er weer staan
    }

    /**
     * This function gets excuted when the user clicks on the "back to library" button
     * The application window gets closed, and the games/programs window is shown
     */
    private void backToLibrary(ActionEvent event)
    {
        model.goToPreviousWindow();
        backToLibrary.setSelected(false); //It would have been better to use a button, rather than a toggle button. But we already have the CSS styling and the ToggleButton skin so in order to relieve some of the work I just used a toggle button. But because it's a ToggleButton and not a Button, it will appear "clicked/hovered" when we return to the application window after having clicked on it before. That's why we set `selected` to `false`
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
        if (model.getOpenedApplication() != null && model.getOpenedApplication().version() != null)
        {//If the current opened app is installed
            optionsButtonHolder.setVisible(true);
        }
        else
        {
            optionsButtonHolder.setVisible(false);
        }

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
                Image platformIcon;
                ColorTheme colorTheme = SaveLoadManager.getData().getColorTheme();

                if (platform == Platforms.ANDROID && colorTheme == ColorTheme.DARK)
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Mobile.png", 44, 44, true, true);
                }
                else if (platform == Platforms.ANDROID && colorTheme == ColorTheme.LIGHT)
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Mobile-whiteTheme.png", 44, 44, true, true);
                }
                else if (platform == Platforms.WINDOWS && colorTheme == ColorTheme.DARK)
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Desktop.png", 44, 44, true, true);
                }
                else if (platform == Platforms.WINDOWS && colorTheme == ColorTheme.LIGHT)
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Desktop-whiteTheme.png", 44, 44, true, true);
                }
                else if (platform == Platforms.WEB && colorTheme == ColorTheme.DARK)
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Web.png", 44, 44, true, true);
                }
                else
                {
                    platformIcon = new Image("/com/jam54/jam54_launcher/img/icons/Web-whiteTheme.png", 44, 44, true, true);
                }

                platformIcons_HBox.getChildren().add(new ImageView(platformIcon));
            }

            description.setText(openedApp.descriptions().get(SaveLoadManager.getData().getLocale()).replace("\\n", "\n\n"));

            installButtonsHolder.getChildren().clear();
            if (model.isAppValidating(openedApp.id()))
            {
                Button validatingAppButton = new Button();
                validatingAppButton.setId("nonPrimaryButton");
                validatingAppButton.setDisable(true);
                validatingAppButton.setSkin(new ButtonColor(validatingAppButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));
                Text text = new Text();
                AtomicBoolean doneOnce = new AtomicBoolean(false);
                validatingAppButton.widthProperty().addListener((obs, oldV, newV) ->
                {
                    if (!doneOnce.get() && newV.doubleValue() > 0)
                    {
                        doneOnce.set(true);
                        text.setWrappingWidth(newV.doubleValue()/1.3);
                        text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForValidationProcedure"), openedApp.name()));
                    }
                });
                validatingAppButton.setGraphic(text);
                installButtonsHolder.getChildren().add(validatingAppButton);
                validatingAppButton.setDisable(true);
            }
            else if (model.isAppRemoving(openedApp.id()))
            {
                Button removingAppButton = new Button();
                removingAppButton.setId("nonPrimaryButton");
                removingAppButton.setDisable(true);
                removingAppButton.setSkin(new ButtonColor(removingAppButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));
                Text text = new Text();
                AtomicBoolean doneOnce = new AtomicBoolean(false);
                removingAppButton.widthProperty().addListener((obs, oldV, newV) ->
                {
                    if (!doneOnce.get() && newV.doubleValue() > 0)
                    {
                        doneOnce.set(true);
                        text.setWrappingWidth(newV.doubleValue()/1.3);
                        text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForUninstall"), openedApp.name()));
                    }
                });
                removingAppButton.setGraphic(text);
                installButtonsHolder.getChildren().add(removingAppButton);
                removingAppButton.setDisable(true);
            }
            else if ((model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null) || !openedApp.updateAvailable()) //Check if there isn't another app updating/downloading files && there isn't another app validating its files && there isn't another app being removed
            {
                installUpdateButton.setDisable(false);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setId("primaryButton");
                installUpdateButton.setSkin(new ButtonColor(installUpdateButton, LoadCSSStyles.getCSSColor("-accent-button-main"), LoadCSSStyles.getCSSColor("-accent-button-hovered"), LoadCSSStyles.getCSSColor("-accent-button-clicked")));
                if (openedApp.version() == null) //If the app isn't installed
                {
                    HBox iconTextHolder = new HBox();
                    ImageView buttonIcon = new ImageView(new Image(Main.class.getResource("img/icons/DownloadWhite.png").toString()));
                    buttonIcon.setFitHeight(24);
                    buttonIcon.setFitWidth(24);
                    Text buttonText = new Text(SaveLoadManager.getTranslation("INSTALL"));
                    iconTextHolder.getChildren().setAll(buttonIcon, buttonText);
                    installUpdateButton.setGraphic(iconTextHolder);
                    installButtonsHolder.getChildren().add(installUpdateButton);

                    installApp = new InstallApp();

                    installUpdateButton.setOnAction(e ->
                    {
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

                                model.setUpdatingApp(openedApp.id());
                                new Thread(installApp).start();

                                installButtonsHolder.getChildren().clear();
                                VBox installProgressHolder = new VBox();

                                Text installProgress_Text = new Text();
                                ProgressBar progressBar = new ProgressBar();

                                installProgressHolder.getChildren().addAll(installProgress_Text, progressBar);
                                installButtonsHolder.getChildren().add(installProgressHolder);

                                installProgress_Text.textProperty().bind(installApp.messageProperty()); //Update button's text with progress message
                                progressBar.progressProperty().bind(installApp.progressProperty()); //Update the progressBar's progress with the progress
                                model.setUpdatingAppMessageProperty(installApp.messageProperty());
                                model.setUpdatingAppProgressProperty(installApp.progressProperty());
                            }
                        }
                    });
                    installApp.setOnSucceeded(e ->
                    {
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again

                        ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                        if (model.getOpenedApplication().id() == updatedApp.id())
                        {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                            model.setOpenedApplication(updatedApp);//We use this to "refresh" this ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
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
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
                    });
                }
                else if (openedApp.updateAvailable()) //If the app is installed but if there is an update available
                {
                    HBox iconTextHolder = new HBox();
                    ImageView buttonIcon = new ImageView(new Image(Main.class.getResource("img/icons/UpdateWhite.png").toString()));
                    buttonIcon.setFitHeight(22);
                    buttonIcon.setFitWidth(22);
                    Text buttonText = new Text(SaveLoadManager.getTranslation("UPDATE"));
                    iconTextHolder.getChildren().setAll(buttonIcon, buttonText);
                    installUpdateButton.setGraphic(iconTextHolder);

                    Button removeButton = new Button();
                    removeButton.setId("nonPrimaryButton");
                    removeButton.setSkin(new ButtonColor(removeButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));
                    HBox iconTextHolder2 = new HBox();
                    ImageView buttonIcon2 = new ImageView(new Image(Main.class.getResource("img/icons/Uninstall.png").toString()));
                    buttonIcon2.setFitHeight(22);
                    buttonIcon2.setFitWidth(22);
                    Text buttonText2 = new Text(SaveLoadManager.getTranslation("UNINSTALL"));
                    iconTextHolder2.getChildren().setAll(buttonIcon2, buttonText2);
                    removeButton.setGraphic(iconTextHolder2);

                    if (model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null)
                    { //You can only remove an app, while there is no other app being downloaded, validated or removed. So we only add the uninstall button when that is the case
                        installButtonsHolder.getChildren().add(removeButton);
                    }
                    installButtonsHolder.getChildren().add(installUpdateButton);

                    installApp = new InstallApp();
                    RemoveApp removeApp = new RemoveApp();

                    installUpdateButton.setOnAction(e ->
                    {
                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            model.setUpdatingApp(openedApp.id());
                            new Thread(installApp).start();

                            installButtonsHolder.getChildren().clear();
                            VBox installProgressHolder = new VBox();

                            Text installProgress_Text = new Text();
                            ProgressBar progressBar = new ProgressBar();

                            installProgressHolder.getChildren().addAll(installProgress_Text, progressBar);
                            installButtonsHolder.getChildren().add(installProgressHolder);

                            installProgress_Text.textProperty().bind(installApp.messageProperty()); //Update button's text with progress message
                            progressBar.progressProperty().bind(installApp.progressProperty()); //Update the progressBar's progress with the progress
                            model.setUpdatingAppMessageProperty(installApp.messageProperty());
                            model.setUpdatingAppProgressProperty(installApp.progressProperty());
                        }
                    });
                    installApp.setOnSucceeded(e ->
                    {
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
                        removeButton.setDisable(false);

                        ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), false, openedApp.availableVersion(), openedApp.availableVersion(), openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                        if (model.getOpenedApplication().id() == updatedApp.id())
                        {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                            model.setOpenedApplication(updatedApp);//We use this to "refresh" this ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
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
                        model.setUpdatingApp(null);
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
                        removeButton.setDisable(false);
                    });

                    removeButton.setOnAction(e ->
                    {
                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            new Thread(removeApp).start();
                            installUpdateButton.setDisable(true);
                            removeButton.setDisable(true);
                            buttonText2.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                        }
                    });

                    removeApp.setOnSucceeded(e ->
                    {
                        installUpdateButton.setDisable(false); //When the task finishes, enable the button again
                        removeButton.setDisable(false);

                        ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), true, openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                        if (model.getOpenedApplication().id() == updatedApp.id())
                        {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                            model.setOpenedApplication(updatedApp);//We use this to "refresh" this ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
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
                else //If the app is installed and there is no update available
                {
                    Button removeButton = new Button();
                    removeButton.setId("nonPrimaryButton");
                    removeButton.setSkin(new ButtonColor(removeButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));
                    HBox iconTextHolder = new HBox();
                    ImageView buttonIcon = new ImageView(new Image(Main.class.getResource("img/icons/Uninstall.png").toString()));
                    buttonIcon.setFitHeight(22);
                    buttonIcon.setFitWidth(22);
                    Text buttonText = new Text(SaveLoadManager.getTranslation("UNINSTALL"));
                    iconTextHolder.getChildren().setAll(buttonIcon, buttonText);
                    removeButton.setGraphic(iconTextHolder);

                    Button playButton = new Button();
                    playButton.setId("primaryButton");
                    playButton.setSkin(new ButtonColor(playButton, LoadCSSStyles.getCSSColor("-accent-button-main"), LoadCSSStyles.getCSSColor("-accent-button-hovered"), LoadCSSStyles.getCSSColor("-accent-button-clicked")));
                    HBox iconTextHolder2 = new HBox();
                    ImageView buttonIcon2 = new ImageView(new Image(Main.class.getResource("img/icons/PlayWhite.png").toString()));
                    buttonIcon2.setFitHeight(20);
                    buttonIcon2.setFitWidth(17);
                    Text buttonText2 = new Text(SaveLoadManager.getTranslation("START"));
                    iconTextHolder2.getChildren().setAll(buttonIcon2, buttonText2);
                    playButton.setGraphic(iconTextHolder2);

                    if (model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null)
                    { //You can only remove an app, while there is no other app being downloaded, validated or removed. So we only add the uninstall button when that is the case
                        installButtonsHolder.getChildren().add(removeButton);
                    }
                    installButtonsHolder.getChildren().add(playButton);

                    RemoveApp removeApp = new RemoveApp();

                    removeButton.setOnAction(e ->
                    {
                        if (!Files.isWritable(SaveLoadManager.getData().getDataPath()))
                        {
                            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                            errorMessage.show();
                        }
                        else
                        {
                            new Thread(removeApp).start();
                            playButton.setDisable(true);
                            removeButton.setDisable(true);
                            buttonText.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
                        }
                    });

                    removeApp.setOnSucceeded(e ->
                    {
                        playButton.setDisable(false); //When the task finishes, enable the button again
                        removeButton.setDisable(false);

                        ApplicationInfo updatedApp = new ApplicationInfo(openedApp.id(), openedApp.name(), openedApp.image(), true, openedApp.availableVersion(), null, openedApp.descriptions(), openedApp.platforms(), openedApp.releaseDate(), openedApp.lastUpdate(), openedApp.isGame());

                        if (model.getOpenedApplication().id() == updatedApp.id())
                        {//Check if the current opened window is of the app we just updated. We don't want to refresh this screen if another app is open. Because that will replace the current open app on screen
                            model.setOpenedApplication(updatedApp);//We use this to "refresh" this ApplicationWindow screen. This way after the install/remove/... operation is finished the correct buttons will be displayed
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

                    playButton.setOnAction(this::playApp);
                }
            }
            else if (model.getLastValidatingApp() != null)
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setId("nonPrimaryButton");
                installUpdateButton.setDisable(true);
                installUpdateButton.setSkin(new ButtonColor(installUpdateButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));

                Text text = new Text("");
                if (installUpdateButton.getWidth() > 0)
                {
                    text.setWrappingWidth(installUpdateButton.getWidth()/1.3);
                    text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForValidationProcedure"), model.getApp(model.getLastValidatingApp()).name()));
                }
                installUpdateButton.widthProperty().addListener((obs, oldV, newV) ->
                {
                    if (newV.doubleValue() > 0)
                    {
                        text.setWrappingWidth(newV.doubleValue()/1.3);
                        text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForValidationProcedure"), model.getApp(model.getLastValidatingApp()).name()));
                    }
                });
                installUpdateButton.setGraphic(text);
            }
            else if (model.getLastRemovingApp() != null)
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setId("nonPrimaryButton");
                installUpdateButton.setDisable(true);
                installUpdateButton.setSkin(new ButtonColor(installUpdateButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));

                Text text = new Text("");
                if (installUpdateButton.getWidth() > 0)
                {
                    text.setWrappingWidth(installUpdateButton.getWidth()/1.3);
                    text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForUninstall"), model.getApp(model.getLastRemovingApp()).name()));

                }
                installUpdateButton.widthProperty().addListener((obs, oldV, newV) ->
                {
                    if (newV.doubleValue() > 0 && model.getLastRemovingApp() != null)
                    {
                        text.setWrappingWidth(newV.doubleValue()/1.3);
                        text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForUninstall"), model.getApp(model.getLastRemovingApp()).name()));
                    }
                });
                installUpdateButton.setGraphic(text);
            }
            else if (model.getUpdatingApp() == openedApp.id())
            {
                installButtonsHolder.getChildren().clear();
                VBox installProgressHolder = new VBox();

                Text installProgress_Text = new Text();
                ProgressBar progressBar = new ProgressBar();

                installProgressHolder.getChildren().addAll(installProgress_Text, progressBar);
                installButtonsHolder.getChildren().add(installProgressHolder);

                if (installApp == null)
                {//This means this install/update was started not from the ApplicationWindow but from the AvailableAppUpdatesWindow
                    installProgress_Text.textProperty().bind(model.getUpdatingAppMessageProperty());
                    progressBar.progressProperty().bind(model.getUpdatingAppProgressProperty());
                }
                else
                {
                    installProgress_Text.textProperty().bind(installApp.messageProperty()); //Update button's text with progress message
                    progressBar.progressProperty().bind(installApp.progressProperty()); //Update the progressBar's progress with the progress
                    model.setUpdatingAppMessageProperty(installApp.messageProperty());
                    model.setUpdatingAppProgressProperty(installApp.progressProperty());
                }
            }
            else if (model.getUpdatingApp() != null)
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setId("nonPrimaryButton");
                installUpdateButton.setDisable(true);
                installUpdateButton.setSkin(new ButtonColor(installUpdateButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));

                Text text = new Text("");
                installUpdateButton.widthProperty().addListener((obs, oldV, newV) -> {
                    text.setWrappingWidth(newV.doubleValue() * 0.7);
                });
                installUpdateButton.graphicProperty().addListener((obs, oldV, newV) -> {
                    text.setWrappingWidth(installUpdateButton.getWidth() * 0.7);
                });
                text.setText(MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForInstall"), model.getApp(model.getUpdatingApp()).name()));
                installUpdateButton.setGraphic(text);
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
        int openedAppId = model.getOpenedApplication() != null ? model.getOpenedApplication().id() : model.getLastValidatingApp() != null ? model.getLastValidatingApp() : model.peekFirstAppFromAppsToUpdateQueue().id(); //If model.getOpenedApplication().id() isn't null and is used to get the id of the app that we will apply the InstallApp code for, this means this InstallApp instance was started from the ApplicationWindow by either updating/downloading an app. If that is null but model.getLastValidatingApp() isn't null, then it means this instance of InstallApp was created from an OptionsWindow to validate an application. If that is also null then it means we have to use model.peekFirstAppFromAppsToUpdateQueue, in that case this instance of InstallApp was created from the AvailableAppUpdatesWindow
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
            {//Normally this should never throw an error, since this file is packed within the JAR, so it should never be missing for example. But we still try-catch it anyway

                Platform.runLater(() -> {//Since we are here in a Task i.e. a separate thread we need to make any GUI related calls using Platform.runLater()
                    ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("ErrorLoadingJam54LauncherConfig"));
                    errorMessage.show();
                });
                super.cancel(); //Makes it so the "endresult" of the Task was "CANCELLED" and not SUCCESFUL"
                return null; //Exit/close and stop further execution of the Task
            }
            //endregion

            //region get hashes for local files and files in the cloud
            updateMessage(SaveLoadManager.getTranslation("CALCULATINGHASHES"));

            Hashes hashes = new Hashes();
            hashesLocal = hashes.calculateHashesForFilesInDirectory(appInstallationPath);
            hashesCloud = new HashMap<>();

            try
            {
                Path tempFile = Files.createTempFile("Hashes", ".txt");
                tempFile.toFile().deleteOnExit();
                DownloadFile.saveUrlToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + "Hashes.txt"), tempFile, 10000, 10000, 10);

                for(String line : FileUtils.readLines(tempFile.toFile(), StandardCharsets.UTF_8))
                {
                    String[] keyValue = line.split("\\|"); //Escaped '|' character
                    hashesCloud.put(keyValue[0], Path.of(keyValue[1]));
                }
            }
            catch (IOException e)
            {
                // Handle the exception (connection/read timeouts)

                Platform.runLater(() -> {//Since we are here in a Task i.e. a separate thread we need to make any GUI related calls using Platform.runLater()
                    ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedDownloadingFiles"));
                    errorMessage.show();
                });
                super.cancel(); //Makes it so the "endresult" of the Task was "CANCELLED" and not SUCCESFUL"
                return null; //Exit/close and stop further execution of the Task
            }
            //endregion

            //region calculate obsolete files that either have to be removed or updated (hashesLocal - hashesCloud)
            HashMap<String, Path> differenceMap = new HashMap<>(hashesLocal);
            differenceMap.entrySet().removeAll(hashesCloud.entrySet());
            Set<Path> obsoleteFiles = new HashSet<>(differenceMap.values());
            //endregion

            //region calculate changed files that either have to be downloaded or updated (hashesCloud - hashesLocal)
            differenceMap = new HashMap<>(hashesCloud);
            differenceMap.entrySet().removeAll(hashesLocal.entrySet());
            Set<Path> changedFiles = new HashSet<>(differenceMap.values());
            //endregion

            //region Calculate the filesToBeDeleted, filesToBeDownloaded and filesToBeUpdated
            Set<Path> filesToBeDeleted = new HashSet<>(obsoleteFiles);
            filesToBeDeleted.removeAll(changedFiles);

            Set<Path> filesToBeDownloaded = new HashSet<>(changedFiles);
            filesToBeDownloaded.removeAll(obsoleteFiles);

            Set<Path> filesToBeUpdated = new HashSet<>(obsoleteFiles);
            filesToBeUpdated.retainAll(changedFiles);
            //endregion

            //region Delete the files that are no longer needed
            updateMessage(SaveLoadManager.getTranslation("REMOVINGOLDFILES"));

            for (Path fileToRemove : filesToBeDeleted)
            {
                Path.of(appInstallationPath.toString(), fileToRemove.toString()).toFile().delete();
            }
            //endregion

            //region Download the missing/new files
            float filesDownloaded = 0;
            float filesToDownload = filesToBeDownloaded.size();
            for (Path fileToDownload : filesToBeDownloaded)
            {
                System.out.println("Downloading: " + fileToDownload);
                filesDownloaded++;
                updateMessage(SaveLoadManager.getTranslation("DOWNLOADING") + " " + (Math.round((filesDownloaded/filesToDownload)*100) + "%"));
                updateProgress(filesDownloaded, filesToDownload);

                try
                {
                    // Attempt to download the file
                    DownloadFile.saveUrlToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + fileToDownload.toString().replace("\\", "/").replace(" ", "%20")), Path.of(appInstallationPath.toString(), fileToDownload.toString()), 10000, 10000, 10);
                }
                catch (IOException e)
                {
                    // Handle the exception (connection/read timeouts)

                    Platform.runLater(() -> {//Since we are here in a Task i.e. a separate thread we need to make any GUI related calls using Platform.runLater()
                        ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedDownloadingFiles"));
                        errorMessage.show();
                    });
                    super.cancel(); //Makes it so the "endresult" of the Task was "CANCELLED" and not SUCCESFUL"
                    return null; //Exit/close and stop further execution of the Task
                }
            }
            //endregion

            //region Update the changed files
            Map<Integer, String> hashesChunksLocal = null;
            Map<Integer, String> hashesChunksCloud = null;
            Map<Integer, String> chunksToReplace = null;

            filesDownloaded = 0;
            filesToDownload = filesToBeUpdated.size();
            for (Path fileToBeUpdated : filesToBeUpdated)
            {
                System.out.println("Downloading chunks for file: " + fileToBeUpdated);
                filesDownloaded++;
                updateMessage(SaveLoadManager.getTranslation("APPLYINGPATCH") + " " + (Math.round((filesDownloaded/filesToDownload)*100) + "%"));
                updateProgress(filesDownloaded, filesToDownload);

                hashesChunksLocal = hashes.calculateChunkHashes(Path.of(appInstallationPath.toString(), fileToBeUpdated.toString()));

                try
                {
                    Path tempFile = Files.createTempFile("HashesChunksCloud", ".txt");
                    tempFile.toFile().deleteOnExit();
                    DownloadFile.saveUrlToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + fileToBeUpdated.toString().replace("\\", "/").replace(" ", "%20") + ".hashes"), tempFile, 10000, 10000, 10);

                    hashesChunksCloud = new HashMap<>();
                    for (String line : FileUtils.readLines(tempFile.toFile(), StandardCharsets.UTF_8))
                    {
                        String[] keyValue = line.split("\\|"); //Escaped '|' character
                        hashesChunksCloud.put(Integer.parseInt(keyValue[1]), keyValue[0]);
                    }
                }
                catch (IOException e)
                {
                    // Handle the exception (connection/read timeouts)

                    Platform.runLater(() -> {//Since we are here in a Task i.e. a separate thread we need to make any GUI related calls using Platform.runLater()
                        ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedDownloadingFiles"));
                        errorMessage.show();
                    });
                    super.cancel(); //Makes it so the "endresult" of the Task was "CANCELLED" and not SUCCESFUL"
                    return null; //Exit/close and stop further execution of the Task
                }

                chunksToReplace = hashesChunksCloud; //No need to make a deep copy, we will replace `hashesChunksCloud` in the next iteration of the loop
                chunksToReplace.entrySet().removeAll(hashesChunksLocal.entrySet());

                try (RandomAccessFile localFileToUpdate = new RandomAccessFile(Path.of(appInstallationPath.toString(), fileToBeUpdated.toString()).toFile(), "rw"))
                {
                    for(Map.Entry<Integer, String> chunkToReplace : chunksToReplace.entrySet())
                    {
                        System.out.println("Downloading chunk " + chunkToReplace.getKey());
                        byte[] newBytes;

                        try
                        {
                            Path tempFile = Files.createTempFile("newBytes", ".bin");
                            tempFile.toFile().deleteOnExit();
                            DownloadFile.saveUrlToFile(new URL(appsBaseDownloadUrl + openedAppId + "/" + "Chunks/" + chunkToReplace.getValue()), tempFile, 10000, 10000, 10);

                            newBytes = FileUtils.readFileToByteArray(tempFile.toFile());
                        }
                        catch (IOException e)
                        {
                            // Handle the exception (connection/read timeouts)

                            Platform.runLater(() -> {//Since we are here in a Task i.e. a separate thread we need to make any GUI related calls using Platform.runLater()
                                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("FailedDownloadingFiles"));
                                errorMessage.show();
                            });
                            super.cancel(); //Makes it so the "endresult" of the Task was "CANCELLED" and not SUCCESFUL"
                            return null; //Exit/close and stop further execution of the Task
                        }

                        localFileToUpdate.seek(chunkToReplace.getKey());
                        localFileToUpdate.write(newBytes);

                        // If the new chunk isn't a "full sized" chunk. This means that this chunk is the last chunk in the file. Now if this last chunk is smaller than the previous last chunk in the local file. The new chunk would overwrite the new bytes, but wouldn't remove the old excess bytes of the larger previous chunk. In that case we will need to truncate the excess bytes in the local file.
                        if (newBytes.length < Hashes.CHUNK_SIZE)
                        {
                            localFileToUpdate.setLength(chunkToReplace.getKey() + newBytes.length);
                        }
                    }

                    //If the chunksToReplace Map is empty, this means that the file in the cloud is an empty file without any content. In that case we don't have any chunks to replace the old chunks of this file with on the user's disk. That's why we do `.setLength(0)` to remove the contents of the file.
                    if (chunksToReplace.isEmpty())
                    {
                        localFileToUpdate.setLength(0);
                    }
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
            //endregion

            //region In case there were splitted files, merge them
            FileSplitterCombiner fileSplitterCombiner = new FileSplitterCombiner();
            updateMessage(SaveLoadManager.getTranslation("INSTALLING"));
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
            updateMessage(SaveLoadManager.getTranslation("UNINSTALLING"));

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
                ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("CloseOpenedAppBeforeUninstall"), openedAppExecutableName) + " " + e.getMessage());
                errorMessage.show();
            }

            try
            {
                FileUtils.deleteDirectory(appInstallationPath.toFile());
                removeShortcut(model.getApp(openedAppId));
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("CloseOpenedAppBeforeUninstall"), openedAppExecutableName) + " " + e.getMessage());
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
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntLaunchApplication") + " " + e.getMessage());
            errorMessage.show();
        }
    }

    /**
     * Create the shortcuts for the given application info on both the desktop and the "Start Menu/Programs" folder, so that the app shows up in Windows Search
     */
    public void createShortcut(ApplicationInfo info)
    {
        int appId = info.id();

        Path launcherEntryPoint = Path.of(System.getProperty("user.dir")); //Path to the executable that is used to launch the launcher
        launcherEntryPoint = launcherEntryPoint.getFileName().toString().equals("app") ? launcherEntryPoint.getParent() : launcherEntryPoint; //If the launcher launched itself, this happens when there was an update for the launcher and the user clicked on "restart to update". In that case user.dir will return the subdirectory "app". We actually want the directory above that contains the Jam54 Launcher executable. If the Jam54 Launcher was launched "normally" the path user.dir returns will be correct and not to the subfolder "app".
        launcherEntryPoint = Path.of(launcherEntryPoint.toString(), "Jam54 Launcher.exe");

        Path appInstallationPath = Path.of(SaveLoadManager.getData().getDataPath().toString(), appId + "");

        try
        {
            String entryPoint = FileUtils.readFileToString(Path.of(appInstallationPath.toString(), "EntryPoint.txt").toFile(), StandardCharsets.UTF_8);

            String fullPathToApp = entryPoint.contains(":") ? entryPoint : Path.of(appInstallationPath.toString(), entryPoint).toString(); //If the entrypoint contains a ":" character. Then it means it isn't a path to a file, but rather an URL to a website or an URL to send a mail. In such cases we don't want to use Path.of() but just the raw string

            String desktopShortcutCommand = "cmd.exe /c powershell.exe -Command \"$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut(\\\"$([System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Desktop))\\\\" + info.name() + ".lnk\\\"); $Shortcut.TargetPath = \\\"" + launcherEntryPoint.toString().replace("\\", "\\\\") + "\\\"; $Shortcut.Arguments = '" + appId + "'; $Shortcut.IconLocation = '" + fullPathToApp.replace("\\", "\\\\") + "'; $Shortcut.Save()\"";
            //Rather than creating an actual shortcut to the application in question. We create a shortcut to the Jam54Launcher and pass the id of the application that we want to start as a CLI argument. This way shortcuts will still work, even if we move the installation directory of the applications after we created a shortcut
            String startMenuShortcutCommand = "cmd.exe /c powershell.exe -Command \"$WshShell = New-Object -comObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut(\\\"%userprofile%\\\\\\\\AppData\\\\\\\\Roaming\\\\\\\\Microsoft\\\\\\\\Windows\\\\\\\\Start Menu\\\\\\\\Programs\\\\\\\\\\\\" + info.name() + ".lnk\\\"); $Shortcut.TargetPath = \\\"" + launcherEntryPoint.toString().replace("\\", "\\\\") + "\\\"; $Shortcut.Arguments = '" + appId + "'; $Shortcut.IconLocation = '" + fullPathToApp.replace("\\", "\\\\") + "'; $Shortcut.Save()\"";

            File scriptFile = File.createTempFile ("createShortcuts", ".bat");
            scriptFile.deleteOnExit();
            try (PrintWriter script = new PrintWriter(scriptFile)) {
                script.println(desktopShortcutCommand);
                script.println(startMenuShortcutCommand);
            }

            ProcessBuilder createShortcuts = new ProcessBuilder(scriptFile.getAbsolutePath());
            createShortcuts.start();
        }
        catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntCreateShortcut") + " " + e.getMessage());
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
            scriptFile.deleteOnExit();
            try (PrintWriter script = new PrintWriter(scriptFile)) {
                script.println(desktopShortcutCommand);
                script.println(startMenuShortcutCommand);
            }

            ProcessBuilder removeShortcuts = new ProcessBuilder(scriptFile.getAbsolutePath());
            removeShortcuts.start();
        }
        catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CouldntRemoveShortcut") + " " + e.getMessage());
            errorMessage.show();
        }
    }

    /**
     * Opens a optionsWindow at the location of the button
     */
    private void openOptionsWindow(MouseEvent event)
    {
        OptionsWindow optionsWindow = new OptionsWindow(this, model);
        if (SaveLoadManager.getData().getColorTheme() == ColorTheme.DARK)
        {
            optionsWindow.getStylesheets().add(Main.class.getResource("css/mainDark.css").toString());
        }
        else
        {
            optionsWindow.getStylesheets().add(Main.class.getResource("css/mainLight.css").toString());
        }

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
