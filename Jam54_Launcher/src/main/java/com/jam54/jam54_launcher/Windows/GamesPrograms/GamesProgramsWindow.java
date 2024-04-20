package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Animations.CheckBoxColor;
import com.jam54.jam54_launcher.Animations.ComboBoxColor;
import com.jam54.jam54_launcher.Animations.TextFieldColor;
import com.jam54.jam54_launcher.Animations.ToggleButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.LoadCSSStyles;
import com.jam54.jam54_launcher.Windows.Application.ApplicationButton;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

/**
 * This class is used to create the Games/Programs window.
 * I.e. it creates a title bar with a title, 2 buttons (games/programs) and a search bar + buttons for all the games/programs
 */
public class GamesProgramsWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    private final FlowPane applicationsHolder;

    private final ComboBox<String> sortOrder_comboBox;
    private final ComboBox<String> selectedPlatforms_comboBox;
    private final CheckBox installedApplications_checkBox;

    private final ToggleGroup toggleGroup;
    private final ToggleButton gamesToggle;
    private final ToggleButton programsToggle;

    private final Text title;

    private final HBox buttonBar, gamesProgramsTogglesHolder, titleHolder, searchBarHolder;

    public GamesProgramsWindow()
    {
        //region button bar
        buttonBar = new HBox();
        buttonBar.getStyleClass().add("buttonBar");

        toggleGroup = new ToggleGroup(); //This ToggleGroup holds the games/programs toggles
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selected

        gamesToggle = new ToggleButton();
        gamesToggle.setSkin(new ToggleButtonColor(gamesToggle, LoadCSSStyles.getCSSColor("-toggle-button-flat"), Color.web("#2193D3"), Color.web("#494FD6")));
        gamesToggle.setGraphic(new Text(SaveLoadManager.getTranslation("Games"))); //By default the text inside a ToggleButton is displayed usinga "Label" object, which looks terrible and not smooth when using a custom font
        programsToggle = new ToggleButton();
        programsToggle.setGraphic(new Text(SaveLoadManager.getTranslation("Programs")));
        programsToggle.setSkin(new ToggleButtonColor(programsToggle, LoadCSSStyles.getCSSColor("-toggle-button-flat"), Color.web("#2193D3"), Color.web("#494FD6")));


        gamesToggle.setToggleGroup(toggleGroup);
        programsToggle.setToggleGroup(toggleGroup);
        gamesProgramsTogglesHolder = new HBox(programsToggle, gamesToggle);
        gamesProgramsTogglesHolder.prefWidthProperty().bind(buttonBar.prefWidthProperty().divide(3));
        gamesProgramsTogglesHolder.getStyleClass().add("buttonBarToggles");

        toggleGroup.selectToggle(programsToggle);

        title = new Text(SaveLoadManager.getTranslation("Games"));
        titleHolder = new HBox(title);
        titleHolder.prefWidthProperty().bind(buttonBar.prefWidthProperty().divide(3));
        titleHolder.getStyleClass().add("buttonBarTitle");

        TextField searchBar = new TextField();
        searchBar.setPromptText(SaveLoadManager.getTranslation("Search"));
        searchBar.setSkin(new TextFieldColor(searchBar, LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-hollow-button-hovered"), LoadCSSStyles.getCSSColor("-hollow-button-clicked")));
        searchBarHolder = new HBox(searchBar);
        searchBarHolder.prefWidthProperty().bind(buttonBar.prefWidthProperty().divide(3));
        searchBarHolder.getStyleClass().add("buttonBarSearchBar");

        buttonBar.getChildren().addAll(titleHolder, gamesProgramsTogglesHolder, searchBarHolder);
        //endregion

        //region title bar
        HBox filtersHolder = new HBox();

        HBox.setHgrow(filtersHolder, Priority.ALWAYS);

        sortOrder_comboBox = new ComboBoxColor(FXCollections.observableArrayList(List.of(SaveLoadManager.getTranslation("Alphabetical") + " ↓",SaveLoadManager.getTranslation("Alphabetical") + " ↑", SaveLoadManager.getTranslation("ReleaseDate") + " ↓", SaveLoadManager.getTranslation("ReleaseDate") + " ↑", SaveLoadManager.getTranslation("LastUpdated") + " ↓", SaveLoadManager.getTranslation("LastUpdated") + " ↑")), LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-hollow-button-hovered"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"));
        selectedPlatforms_comboBox = new ComboBoxColor(FXCollections.observableArrayList(List.of(SaveLoadManager.getTranslation("AllPlatforms"), SaveLoadManager.getTranslation("Android"), SaveLoadManager.getTranslation("Windows"), SaveLoadManager.getTranslation("Web"))), LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-hollow-button-hovered"), LoadCSSStyles.getCSSColor("-hollow-button-clicked"));

        installedApplications_checkBox = new CheckBox(SaveLoadManager.getTranslation("CurrentlyInstalledApps"));
        installedApplications_checkBox.setSkin(new CheckBoxColor(installedApplications_checkBox, LoadCSSStyles.getCSSColor("-bg-main"), LoadCSSStyles.getCSSColor("-hollow-button-hovered"), LoadCSSStyles.getCSSColor("-accent-button-main")));

        sortOrder_comboBox.getSelectionModel().select(0);
        selectedPlatforms_comboBox.getSelectionModel().select(0);

        sortOrder_comboBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        selectedPlatforms_comboBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        installedApplications_checkBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));

        gamesToggle.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        programsToggle.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));

        searchBar.textProperty().addListener((obs, oldValue, newValue) -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), newValue));

        filtersHolder.getChildren().setAll(new Text(SaveLoadManager.getTranslation("SortBy")), sortOrder_comboBox, selectedPlatforms_comboBox, installedApplications_checkBox);
        filtersHolder.getStyleClass().add("filtersHolder");
        //endregion

        //region center area
        ScrollPane centerArea = new ScrollPane();
        centerArea.setId("centerArea");

        applicationsHolder = new FlowPane();
        applicationsHolder.getStyleClass().add("applicationsHolder");

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

        getChildren().setAll(buttonBar, filtersHolder, centerArea);
    }
    @Override
    public void invalidated(Observable observable)
    {
        fillApplicationsHolder();
    }

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
        fillApplicationsHolder();
    }

    private void fillApplicationsHolder()
    {
        applicationsHolder.getChildren().clear();

        int appplicationsAdded = 0;
        for (ApplicationInfo application : model.getVisibleApplicationInfos())
        {
            ApplicationButton button = new ApplicationButton(application, true, Duration.millis(appplicationsAdded * 100));
            button.setModel(model);
            appplicationsAdded++;

            VBox appButtonHolder = new VBox(); //See explanation for this extra holder in the `applicationButtonHolder` styleclass inside the stylesheet
            appButtonHolder.getStyleClass().add("applicationButtonHolder");

            appButtonHolder.getChildren().add(button);
            applicationsHolder.getChildren().add(appButtonHolder);
        }

        if (applicationsHolder.getChildren().isEmpty())
        {
            HBox noApplicationsMatched = new HBox(new Text(SaveLoadManager.getTranslation("NoAppsForFilters")));
            noApplicationsMatched.setId("noApplicationsMatched");
            applicationsHolder.getChildren().add(noApplicationsMatched);
        }

        int amountOfApps = model.getVisibleApplicationInfos().size();
        title.setText((gamesToggle.isSelected() ? SaveLoadManager.getTranslation("Games") : SaveLoadManager.getTranslation("Programs")) + (amountOfApps > 0 ? " (" + amountOfApps + ")" : ""));
    }
}
