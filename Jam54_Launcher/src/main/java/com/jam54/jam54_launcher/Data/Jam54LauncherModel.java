package com.jam54.jam54_launcher.Data;

import com.jam54.jam54_launcher.Windows.Application.Platforms;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;

import java.util.*;
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
    private final Deque<Route> selectedWindow; //Used as a stack for routing/navigation to keep track of the currently selected screen

    private ApplicationInfo openedApplicationInfo;
    private ArrayList<ApplicationInfo> allApplicationInfos;
    private ArrayList<ApplicationInfo> visibleApplicationInfos;

    private ArrayList<Locale> supportedLanguages;

    private final HashMap<Integer, String> runningApps;

    private Integer updatingApp;

    private final ArrayList<Integer> validatingApps;
    private final ArrayList<Integer> removingApps;

    private final Deque<ApplicationInfo> appsToUpdateQueue;
    private ReadOnlyStringProperty updatingAppMessageProperty; //setten in availableappupdates window maar ook in application window //Generieker maken naar bv installingUpdatingAppMessageProperty
    private ReadOnlyDoubleProperty updatingAppProgressProperty;

    public Jam54LauncherModel()
    {
        listenerList = new ArrayList<>();
        selectedWindow = new ArrayDeque<>();
        runningApps = new HashMap<>();
        validatingApps = new ArrayList<>();
        removingApps = new ArrayList<>();
        appsToUpdateQueue = new ArrayDeque<>();
    }

    private void fireInvalidationEvent()
    {
        //To prevent ConcurrentModificationException, which occurs if the list of listeners is modified while being iterated over, a copy of the listener list is created. This safeguard addresses the rare scenario where a listener is added or removed during the iteration process. Without this precaution, a ConcurrentModificationException may arise due to modifications to the listener list while iterating over it. By iterating over a copied list, any changes to the original list do not affect the iteration process.
        for (InvalidationListener listener : new ArrayList<>(listenerList))
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

    /**
     * Adds the route of another window/screen to the route
     * @param route the location of the window/screen we go to
     */
    public void navigateToWindow(Route route)
    {
        selectedWindow.addFirst(route);
        fireInvalidationEvent();
    }

    public Route getSelectedWindow()
    {
        return selectedWindow.peekFirst();
    }

    /**
     * Goes back to the previous "route", if the current route is the only one left the current route stays selected
     * @return the removed/popped route, or the same route if there wasn't a previous route left to go to
     */
    public Route goToPreviousWindow()
    {
        if (selectedWindow.size() > 1)
        {
            selectedWindow.removeFirst();
            fireInvalidationEvent();
        }

        return getSelectedWindow();
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

    /**
     * This method is used to set the Locale's of the language in which the Jam54Launcher can be displayed
     */
    public void setSupportedLanguages(ArrayList<Locale> languages)
    {
        this.supportedLanguages = languages;
        fireInvalidationEvent();
    }

    /**
     * This function returns a list of all the Locale's language in which the Jam54Launcher can be viewed
     */
    public ArrayList<Locale> getSupportedLanguages()
    {
        return supportedLanguages;
    }

    /**
     * When we start an app, we keep track of which apps we have started. This way we can close that app in the event that the user would like to remove that application.
     * Because if we wouldn't stop the app while it is running, and would attempt to remove it. We might encounter some errors with file locks etc
     */
    public void addRunningApp(int appId, String executableName)
    {
        runningApps.put(appId, executableName);
        fireInvalidationEvent();
    }

    /**
     * This method is used to remove an app from the hashmap that keeps track of all the apps that were launched using the Jam54Launcher.
     * @return Returns the app that was removed from the hashmap
     */
    public String removeRunningApp(int appId)
    {
        return runningApps.remove(appId);
    }

    /**
     * If we start installing/updating an app, we will set the id of app being installed/updated
     * @param applicationId Null means we aren't updating any applications
     */
    public void setUpdatingApp(Integer applicationId)
    {
        updatingApp = applicationId;
        fireInvalidationEvent();
    }

    /**
     * @return  Returns the applicationId of the app that is currently installing/updating. Null if there isn't an app being installed/updated
     */
    public Integer getUpdatingApp()
    {
        return updatingApp;
    }

    /**
     * @return  Returns the applicationInfo of the given appId
     */
    public ApplicationInfo getApp(int id)
    {
        for(ApplicationInfo applicationInfo : allApplicationInfos)
        {
            if (applicationInfo.id() == id)
            {
                return applicationInfo;
            }
        }

        return null;
    }

    /**
     * If an app's files are being validated, we add them to the validatingApps list using this function
     */
    public void addValidatingApp(int appId)
    {
        validatingApps.add(appId);
        fireInvalidationEvent();
    }

    /**
     * If an app's files are done being validated, we remove them from the validatingApps list using this function
     */
    public void removeValidatingApp(Integer appId)
    {
        validatingApps.remove(appId);
        fireInvalidationEvent();
    }

    /**
     * Checks if the validatingApps list contains a given appId
     */
    public boolean isAppValidating(Integer appId)
    {
        return validatingApps.contains(appId);
    }

    /**
     * Gets the last app in the list of apps that is validating, returns null if there are no apps being validated
     */
    public Integer getLastValidatingApp()
    {
        if (validatingApps.size() >= 1)
        {
            return validatingApps.get(validatingApps.size() - 1);
        }
        else
        {
            return null;
        }
    }

    /**
     * If an app's files are being removed, we add them to the removingApps list using this function
     */
    public void addRemovingApp(int appId)
    {
        removingApps.add(appId);
        fireInvalidationEvent();
    }

    /**
     * If an app's files are done being removed, we remove them from the removingApps list using this function
     */
    public void removeRemovingApp(Integer appId)
    {
        removingApps.remove(appId);
        fireInvalidationEvent();
    }

    /**
     * Checks if the removingApps list contains a given appId
     */
    public boolean isAppRemoving(Integer appId)
    {
        return removingApps.contains(appId);
    }

    /**
     * Gets the last app in the list of apps that is removing, returns null if there are no apps being removed
     */
    public Integer getLastRemovingApp()
    {
        if (removingApps.size() >= 1)
        {
            return removingApps.get(removingApps.size() - 1);
        }
        else
        {
            return null;
        }
    }

    /**
     * Adds an app to the queue of apps that contains the apps that the user wants to update
     */
    public void addAppToAppsUpdateQueue(ApplicationInfo applicationInfo)
    {
        appsToUpdateQueue.add(applicationInfo);
        fireInvalidationEvent();
    }

    /**
     * Returns the first app of the queue of apps that the user wants to update
     * @return null if the queue of apps is empty, else the first app in the queue
     */
    public ApplicationInfo peekFirstAppFromAppsToUpdateQueue()
    {
        return appsToUpdateQueue.peekFirst();
    }

    /**
     * Removes an application info object based on the provided id from the queue of apps that the user wants to update
     */
    public void removeAppFromAppsToUpdateQueue(int appId)
    {
        appsToUpdateQueue.removeIf(app -> app.id() == appId);
    }

    /**
     * Returns true if the given app is present in the queue of apps that contains the apps that the user wants to update
     */
    public boolean isAppInAppsToUpdateQueue(ApplicationInfo applicationInfo)
    {
        return appsToUpdateQueue.contains(applicationInfo);
    }

    /**
     * Returns the amount of apps in the queue of apps that contains the apps that the user wants to update
     */
    public int getSizeOfAppsToUpdateQueue()
    {
        return appsToUpdateQueue.size();
    }

    /**
     * Sets a property that holds the text that is displayed when installing/updating an app. For example "CALCULATING HASHES, DOWNLOADING, ..."
     */
    public void setUpdatingAppMessageProperty(ReadOnlyStringProperty property)
    {
        updatingAppMessageProperty = property;
//        fireInvalidationEvent(); Do not call this, otherwise we just get an infinite loop of the place this gets set being invalidated, causing this to be set again, which then causes a new invalidation event, so on and so forth
    }

    /**
     * Gets a property that holds the text that is displayed when installing/updating an app. For example "CALCULATING HASHES, DOWNLOADING, ..."
     */
    public ReadOnlyStringProperty getUpdatingAppMessageProperty()
    {
        return updatingAppMessageProperty;
    }

    /**
     * Sets a property that holds the progression of the amount of downloaded files when installing/updating an app.
     */
    public void setUpdatingAppProgressProperty(ReadOnlyDoubleProperty property)
    {
        updatingAppProgressProperty = property;
//        fireInvalidationEvent(); Do not call this, otherwise we just get an infinite loop of the place this gets set being invalidated, causing this to be set again, which then causes a new invalidation event, so on and so forth
    }

    /**
     * Gets a property that holds the progression of the amount of downloaded files when installing/updating an app.
     */
    public ReadOnlyDoubleProperty getUpdatingAppProgressProperty()
    {
        return updatingAppProgressProperty;
    }
}
