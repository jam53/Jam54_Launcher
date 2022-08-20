package com.jam54.jam54_launcher.Windows.GamesPrograms;

import com.jam54.jam54_launcher.Jam54LauncherModel;
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

import java.util.List;

/**
 * This class is used to create the Games/Programs window.
 * I.e. it creates a title bar with a title, 2 buttons (games/programs) and a search bar + buttons for all the games/programs
 */
public class GamesProgramsWindow extends VBox implements InvalidationListener
{
    private Jam54LauncherModel model;
    private FlowPane applicationsHolder;

    private ToggleGroup toggleGroup;
    private ToggleButton gamesToggle;
    private ToggleButton programsToggle;

    private Label title;

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

        gamesToggle.setOnAction(this::showGames);
        programsToggle.setOnAction(this::showPrograms);

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

        SearchBar searchBar = new SearchBar(); //Deze comment dan weg doen. Maar dus ruwweg komt de werking van de searchBar neer op het volgende:
        //De searchBar houdt een referentie bij naar het model. Maar is geen view, dus geen invalidationListener interface implementeren.
        //Maar vanaf dat de gebruiker begint te typen. Filtert deze klasse de ApplicationInfo objecten uit waarvan de naam de string bevat die de gebruiker typte.
        //(Hoeft niet in het begin van de naam te zijn. Als de naam AstroRun is, en de gebruiker type run, dat moeten we het ook tonen)
        //De searchbar vraagt dus eerst alle visibleApplicationInfo objecten op aan het model, filtert ze, en doet dan setVisibleApplicationInfoObjects
        //Waarom vragen we de visible op, en niet allemaal? Want het kan zijn dat de gebruiker al andere filters had aan staan. Bv enkel apps voor Windows
        //zelfde redenering voor de filter opties hier onder
        //TODO

        filtersHolder.getChildren().setAll(new Label("Sort By:"), new ChoiceBox<String>(FXCollections.observableArrayList(List.of("Alphabetical ↑", "Alphabetical ↓", "Release Date ↑", "Release Date ↑"))), new ComboBox<String>(FXCollections.observableArrayList(List.of("All Platforms", "Android", "Windows", "Web"))), new CheckBox("Show installed applications only"), searchBar);
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
            applicationsHolder.getChildren().add(new Button(application.name()));
        }
    }

    private void showGames(ActionEvent event)
    {
        title.setText("%Games");
        //TODO
        //Zodanig filteren dat getVisibleApplicationInfos enkel de games toont. Daarvoor zullen we dus
        //getAllApplicationsInfos moeten oproepen, en dan kijken welke filters er actief zijn. En dan de objecten terug setten in het model die overblijven
    }

    private void showPrograms(ActionEvent event)
    {
        title.setText("%Programs");
        //TODO
        //Zodanig filteren dat getVisibleApplicationInfos enkel de programmas toont. Daarvoor zullen we dus
        //getAllApplicationsInfos moeten oproepen, en dan kijken welke filters er actief zijn. En dan de objecten terug setten in het model die overblijven
    }
}
