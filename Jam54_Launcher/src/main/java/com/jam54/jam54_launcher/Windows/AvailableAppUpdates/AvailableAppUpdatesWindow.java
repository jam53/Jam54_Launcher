package com.jam54.jam54_launcher.Windows.AvailableAppUpdates;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

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
        fillApplicationsHolder();
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);

        appsWithUpdates = model.getAllApplications().stream().filter(applicationInfo -> applicationInfo.updateAvailable() && applicationInfo.version() != null).toList();
        fillApplicationsHolder();
        if (appsWithUpdates.isEmpty())
        {//Make the button invisible if there aren't any applications with updates
            updateAll_Button.setManaged(false);
        }
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
}
