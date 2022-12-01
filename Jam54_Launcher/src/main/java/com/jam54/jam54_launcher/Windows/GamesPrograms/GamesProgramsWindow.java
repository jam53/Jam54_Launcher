package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import com.jam54.jam54_launcher.Windows.Application.ApplicationButton;
import com.jam54.jam54_launcher.Windows.Application.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private final Label title;

    public GamesProgramsWindow()
    {
        //region button bar
        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().add("buttonBar");

        toggleGroup = new ToggleGroup(); //This ToggleGroup holds the games/programs toggles
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selected

        gamesToggle = new ToggleButton("%Games");
        programsToggle = new ToggleButton("%Programs");

        gamesToggle.setToggleGroup(toggleGroup);
        programsToggle.setToggleGroup(toggleGroup);

        toggleGroup.selectToggle(gamesToggle);

        buttonBar.getChildren().addAll(gamesToggle, programsToggle);
        //endregion

        //region title bar
        HBox titleBar = new HBox();
        HBox filtersHolder = new HBox();

        HBox.setHgrow(filtersHolder, Priority.ALWAYS);

        title = new Label("%Games");

        TextField searchBar = new TextField();

        searchBar.setPromptText("%Search");

        sortOrder_comboBox = new ComboBox<>(FXCollections.observableArrayList(List.of("%Alphabetical ↓","%Alphabetical ↑", "%Release Date ↓", "%Release Date ↑", "Last Updated ↓", "Last Updated ↑")));
        selectedPlatforms_comboBox = new ComboBox<>(FXCollections.observableArrayList(List.of("%All Platforms", "%Android", "%Windows", "%Web")));
        installedApplications_checkBox = new CheckBox("%Show installed applications only");

        sortOrder_comboBox.getSelectionModel().select(0);
        selectedPlatforms_comboBox.getSelectionModel().select(0);

        sortOrder_comboBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        selectedPlatforms_comboBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        installedApplications_checkBox.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));

        gamesToggle.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));
        programsToggle.setOnAction(e -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), searchBar.getText()));

        searchBar.textProperty().addListener((obs, oldValue, newValue) -> model.filterAndSortVisibleApplicationInfos(selectedPlatforms_comboBox.getSelectionModel().getSelectedIndex(), installedApplications_checkBox.isSelected(), sortOrder_comboBox.getSelectionModel().getSelectedIndex(), gamesToggle.isSelected(), newValue));

        filtersHolder.getChildren().setAll(new Label("%Sort By:"), sortOrder_comboBox, selectedPlatforms_comboBox, installedApplications_checkBox, searchBar);
        titleBar.getStyleClass().add("titleBar");
        titleBar.getChildren().setAll(title, filtersHolder);
        //endregion

        //region center area
        applicationsHolder = new FlowPane();
        applicationsHolder.getStyleClass().add("applicationsHolder");
        //endregion

        getChildren().setAll(buttonBar, titleBar, applicationsHolder);
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

        for (ApplicationInfo application : model.getVisibleApplicationInfos())
        {
            ApplicationButton button = new ApplicationButton(application);
            button.setModel(model);

            applicationsHolder.getChildren().add(button);
        }

        if (applicationsHolder.getChildren().size() == 0)
        {
            applicationsHolder.getChildren().add(new Label("%No items found for the applied filters OR No application matched the provided filters"));
        }
    }
}
