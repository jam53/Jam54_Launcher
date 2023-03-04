package com.jam54.jam54_launcher.Data;

import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import com.jam54.jam54_launcher.Windows.Application.Platforms;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Whilst the {@link com.jam54.jam54_launcher.Data.SaveLoad.Jam54LauncherData} class is used to have persistent data, by saving and loading data from/to the disk.
 * And Jam54LauncherConfig.properties is used to only read config values from that don't change.
 *
 * This class is used to store data during runtime. It is the model in the MVC pattern, therefore when we change the value of a variable,
 * the listeners will be notified that a variable's value inside the model has been changed
 */
public class Jam54LauncherModel implements Observable
{
    private final List<InvalidationListener> listenerList;

    private boolean newVersionDownloaded;
    private boolean gamesWindowSelected;
    private boolean programsWindowSelected;
    private boolean settingsWindowSelected;
    private boolean applicationWindowSelected;

    private ApplicationInfo openedApplicationInfo;
    private ArrayList<ApplicationInfo> allApplicationInfos;
    private ArrayList<ApplicationInfo> visibleApplicationInfos;

    private ArrayList<Locale> supportedLanguages;

    public Jam54LauncherModel()
    {
        listenerList = new ArrayList<>();
    }

    private void fireInvalidationEvent()
    {
        for (InvalidationListener listener : listenerList)
        {
            listener.invalidated(this);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener)
    {
        listenerList.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener)
    {
        listenerList.remove(invalidationListener);
    }

    public boolean isNewVersionDownloaded()
    {
        return newVersionDownloaded;
    }

    public void setNewVersionDownloaded(boolean newVersionDownloaded)
    {
        this.newVersionDownloaded = newVersionDownloaded;
        fireInvalidationEvent();
    }

    public boolean isGamesWindowSelected()
    {
        return gamesWindowSelected;
    }

    public void setGamesWindowSelected(boolean gamesWindowSelected)
    {
        this.gamesWindowSelected = gamesWindowSelected;
        fireInvalidationEvent();
    }

    public boolean isProgramsWindowSelected()
    {
        return programsWindowSelected;
    }

    public void setProgramsWindowSelected(boolean programsWindowSelected)
    {
        this.programsWindowSelected = programsWindowSelected;
        fireInvalidationEvent();
    }

    public boolean isSettingsWindowSelected()
    {
        return settingsWindowSelected;
    }

    public void setSettingsWindowSelected(boolean settingsWindowSelected)
    {
        this.settingsWindowSelected = settingsWindowSelected;
        fireInvalidationEvent();
    }

    public boolean isApplicationWindowSelected()
    {
        return applicationWindowSelected;
    }

    public void setApplicationWindowSelected(boolean applicationWindowSelected)
    {
        this.applicationWindowSelected = applicationWindowSelected;
        fireInvalidationEvent();
    }

    public ApplicationInfo getOpenedApplication()
    {
        return openedApplicationInfo;
    }

    public void setOpenedApplication(ApplicationInfo openedApplicationInfo)
    {
        this.openedApplicationInfo = openedApplicationInfo;
        fireInvalidationEvent();
    }

    public ArrayList<ApplicationInfo> getAllApplications()
    {
        return allApplicationInfos;
    }

    public void setAllApplications(ArrayList<ApplicationInfo> allApplicationInfos)
    {
        this.allApplicationInfos = allApplicationInfos;
        fireInvalidationEvent();
    }

    public ArrayList<ApplicationInfo> getVisibleApplicationInfos()
    {
        return visibleApplicationInfos;
    }

    public void setVisibleApplicationInfos(ArrayList<ApplicationInfo> visibleApplicationInfos)
    {
        this.visibleApplicationInfos = visibleApplicationInfos;
        fireInvalidationEvent();
    }

    /**
     * This function sorts a given list of application infos based on the provided wayToSort integer, and also filters out the apps that don't correspond to the selected platform + whether or not we only want to show the installed applications + if we want to show games or programs + what's written inside the searchbar
     * sortOrder:
     *     0 - Alphabetically ascending
     *     1 - Alphabetically descending
     *     2 - Release Date ascending
     *     3 - Release Date descending
     *     4 - Last Update ascending
     *     5 - Last Update descending
     * allowedPlatform:
     *     0 - Android, Windows, Web
     *     1 - Android
     *     2 - Windows
     *     3 - Web
     */
    public void filterAndSortVisibleApplicationInfos(int allowedPlatform, boolean installedOnly, int sortOrder, boolean gamesOnly, String searchText)
    {
        visibleApplicationInfos = new ArrayList<>();
        visibleApplicationInfos.addAll(allApplicationInfos);//Make a deep copy, since we will be removing objects from the arraylist

        ArrayList<Platforms> allowedPlatforms = new ArrayList<>();

        switch (allowedPlatform)
        {
            case 0 ->
            {
                allowedPlatforms.add(Platforms.WINDOWS);
                allowedPlatforms.add(Platforms.ANDROID);
                allowedPlatforms.add(Platforms.WEB);
            }
            case 1 -> allowedPlatforms.add(Platforms.ANDROID);
            case 2 -> allowedPlatforms.add(Platforms.WINDOWS);
            case 3 -> allowedPlatforms.add(Platforms.WEB);
        }

        visibleApplicationInfos.removeIf(Predicate.not(app -> allowedPlatforms.stream().anyMatch(app.platforms()::contains))); //Filter out all the apps whose platform isn't present inside the `allowedPlatforms list`

        visibleApplicationInfos.removeIf(app -> app.version() == null && installedOnly); //Keep only the installed applications or all of them depending on the parameter

        visibleApplicationInfos.removeIf(app -> app.isGame() != gamesOnly); //Remove all games, or programs depending on the value of the `onlyGames` variable

        visibleApplicationInfos.removeIf(Predicate.not(app -> app.name().toLowerCase().contains(searchText.toLowerCase().trim()))); //Check the apps for the text written in the searchbar

        switch (sortOrder)
        {
            case 0 -> visibleApplicationInfos.sort(Comparator.comparing(ApplicationInfo::name, String.CASE_INSENSITIVE_ORDER));
            case 1 -> visibleApplicationInfos.sort((app1, app2) -> app2.name().compareToIgnoreCase(app1.name()));
            case 2 -> visibleApplicationInfos.sort((app1, app2) -> (int) (app2.releaseDate() - app1.releaseDate()));
            case 3 -> visibleApplicationInfos.sort(Comparator.comparingLong(ApplicationInfo::releaseDate));
            case 4 -> visibleApplicationInfos.sort((app1, app2) -> (int) (app2.lastUpdate() - app1.lastUpdate()));
            case 5 -> visibleApplicationInfos.sort(Comparator.comparingLong(ApplicationInfo::lastUpdate));
            default -> System.out.println("Couldn't sort application info objects");
        }

        fireInvalidationEvent();
    }

    public void setSupportedLanguages(ArrayList<Locale> languages)
    {
        this.supportedLanguages = languages;
        fireInvalidationEvent();
    }

    public ArrayList<Locale> getSupportedLanguages()
    {
        return supportedLanguages;
    }
}
