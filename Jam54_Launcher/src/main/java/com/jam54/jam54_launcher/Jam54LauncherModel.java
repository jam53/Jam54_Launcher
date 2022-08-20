package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Windows.Application.ApplicationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Whilst the {@link com.jam54.jam54_launcher.SaveLoad.Jam54LauncherData} class is used to have persistent data, by saving and loading data from/to the disk.
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
}
