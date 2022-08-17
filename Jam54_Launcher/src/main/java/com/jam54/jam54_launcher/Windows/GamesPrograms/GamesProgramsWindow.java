package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Jam54LauncherModel;
import com.jam54.jam54_launcher.Windows.Application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * This class is used to create the Games/Programs window.
 * I.e. it creates a title bar with a title, 2 buttons (games/programs) and a search bar + buttons for all the games/programs
 */
public class GamesProgramsWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    private FlowPane applicationsHolder;

    public GamesProgramsWindow()
    {
        //region button bar
        HBox buttonBar = new HBox();

        buttonBar.getStyleClass().add("buttonBar");
        buttonBar.getChildren().addAll(new Button("Games"), new Button("Programs"));
        //endregion

        //region title bar
        HBox titleBar = new HBox();
        HBox filtersHolder = new HBox();

        HBox.setHgrow(filtersHolder, Priority.ALWAYS);

        Label title = new Label("a");

        SearchBar searchBar = new SearchBar();

        filtersHolder.getChildren().setAll(new Label("Sort By:"), new ChoiceBox<String>(FXCollections.observableArrayList(List.of("Alphabetical ↑", "Alphabetical ↓", "Release Date ↑", "Release Date ↑"))), new ComboBox<String>(FXCollections.observableArrayList(List.of("All Platforms", "Android", "Windows", "Web"))), new CheckBox("Show installed applications only"), searchBar);
        titleBar.getStyleClass().add("titleBar");
        titleBar.getChildren().setAll(title, filtersHolder);
        //endregion

        //region center area
        applicationsHolder = new FlowPane();
        applicationsHolder.getStyleClass().add("applicationsHolder");
        //endregion

        Button test = new Button("Open settings");
//        test.setOnAction(e -> model.setSettingsWindowSelected());
        getChildren().setAll(buttonBar, titleBar, test);
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

        for (Application application : model.getAllApplications())
        {
            applicationsHolder.getChildren().add(new Button(application.name()));
        }
    }
}
