package com.jam54.jam54_launcher.Windows.AvailableAppUpdates;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * When an application has an update available, this element/button/class is used as a button on the {@link AvailableAppUpdatesWindow} to represent an app that can be updated
 */
public class AvailableAppUpdateButton extends HBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    private ApplicationInfo applicationInfo;

    private final VBox rightSideHolder;

    public AvailableAppUpdateButton(ApplicationInfo applicationInfo)
    {
        this.applicationInfo = applicationInfo;

        this.getStyleClass().add("availableAppUpdateButton");

        //region app icon
        ImageView appIcon = new ImageView(applicationInfo.image());
        appIcon.setFitWidth(50);
        appIcon.setFitHeight(50);

        Rectangle clipRounded = new Rectangle();
        clipRounded.setWidth(50);
        clipRounded.setHeight(50);
        clipRounded.setArcHeight(10);
        clipRounded.setArcWidth(10);

        appIcon.setClip(clipRounded);
        //endregion

        //region app text
        VBox textContainer = new VBox();

        Text appTitle = new Text(applicationInfo.name());
        Text bottomText = new Text(SaveLoadManager.getTranslation("InstalledVersion") + ": " + applicationInfo.version() + " | " + SaveLoadManager.getTranslation("AvailableVersion") + ": " + applicationInfo.availableVersion());
        bottomText.setId("availableAppUpdateButtonBottomText");

        textContainer.getChildren().addAll(appTitle, bottomText);
        //endregion

        //region fill space
        Region fillSpace = new Region();
        HBox.setHgrow(fillSpace, Priority.ALWAYS);
        //endregion

        rightSideHolder = new VBox();

        this.getChildren().addAll(appIcon, textContainer, fillSpace, rightSideHolder);
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        invalidated(model);
    }

    @Override
    public void invalidated(Observable observable)
    {
        rightSideHolder.getChildren().clear();

        if (model.getUpdatingApp() != null && model.getUpdatingApp() == applicationInfo.id())
        {
            rightSideHolder.getChildren().add(new Text("progressie balk van update in progress"));
        }
        else if (!model.isAppInAppsToUpdateQueue(applicationInfo))
        {
            Button update_Button = new Button(SaveLoadManager.getTranslation("Update"));
            update_Button.setId("primaryButton");
            update_Button.setSkin(new ButtonColor(update_Button, LoadCSSStyles.getCSSColor("-accent-button-main"), LoadCSSStyles.getCSSColor("-accent-button-hovered"), LoadCSSStyles.getCSSColor("-accent-button-clicked")));

            update_Button.setOnAction(e -> {
                model.addAppToAppsUpdateQueue(applicationInfo);
            });

            rightSideHolder.getChildren().add(update_Button);
        }
        else if (model.isAppInAppsToUpdateQueue(applicationInfo))
        {
            rightSideHolder.getChildren().add(new Text("In queue"));
        }
    }
}
