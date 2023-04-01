package com.jam54.jam54_launcher.Windows.Application;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Animations.ToggleButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.ColorTheme;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Updating.FileSplitterCombiner;
import com.jam54.jam54_launcher.Updating.Hashes;
import com.jam54.jam54_launcher.Windows.GamesPrograms.OptionsWindow;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

        HBox optionsButtonHolder = new HBox();
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
        model.setApplicationWindowSelected(false);
        model.setSettingsWindowSelected(false);
        //We don't set on of the Games/Programs windows' values to true. This is because one of them is already true. (Since we didn't set it to false when opening the ApplicationWindow)
        //And if we go "back to library" from the Application Window. This will allow us to reopen the last page we were on. Either Games or Programs
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
                    iconTextHolder.getChildren().setAll(buttonIcon2, buttonText2);
                    installUpdateButton.setGraphic(iconTextHolder2);

                    if (model.getUpdatingApp() == null && model.getLastValidatingApp() == null && model.getLastRemovingApp() == null)
                    { //You can only remove an app, while there is no other app being downloaded, validated or removed. So we only add the uninstall button when that is the case
                        installButtonsHolder.getChildren().add(removeButton);
                    }
                    installButtonsHolder.getChildren().add(installUpdateButton);

                    installApp = new InstallApp();
                    RemoveApp removeApp = new RemoveApp();

                    installUpdateButton.setOnAction(e ->
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

                    removeButton.setOnAction(e ->
                    {
                        new Thread(removeApp).start();
                        installUpdateButton.setDisable(true);
                        removeButton.setDisable(true);
                        buttonText2.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
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
                        new Thread(removeApp).start();
                        playButton.setDisable(true);
                        removeButton.setDisable(true);
                        buttonText.textProperty().bind(removeApp.messageProperty()); //Update button's text with progress
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
                    if (newV.doubleValue() > 0)
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

                installProgress_Text.textProperty().bind(installApp.messageProperty()); //Update button's text with progress message
                progressBar.progressProperty().bind(installApp.progressProperty()); //Update the progressBar's progress with the progress
            }
            else if (model.getUpdatingApp() != null)
            {
                installButtonsHolder.getChildren().add(installUpdateButton);
                installUpdateButton.textProperty().unbind();
                installUpdateButton.setId("nonPrimaryButton");
                installUpdateButton.setDisable(true);
                installUpdateButton.setSkin(new ButtonColor(installUpdateButton, LoadCSSStyles.getCSSColor("-filled-button-unselected"), LoadCSSStyles.getCSSColor("-filled-button-hovered"), LoadCSSStyles.getCSSColor("-filled-button-clicked")));

                Text text = new Text("");
                while (installUpdateButton.getWidth() <= 0)
                {
                    //wait
                }
                text.setWrappingWidth(installUpdateButton.getWidth() / 1.3);
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
            updateMessage(SaveLoadManager.getTranslation("CALCULATINGHASHES"));

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
            updateMessage("REMOVINGOLDFILES");
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
                    updateMessage(SaveLoadManager.getTranslation("DOWNLOADING") + " " + (Math.round((filesDownloaded/filesToDownload)*100) + "%"));
                    updateProgress(filesDownloaded, filesToDownload);
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
