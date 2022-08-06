package com.jam54.jam54_launcher;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Whilst the {@link com.jam54.jam54_launcher.SaveLoad.Jam54LauncherData} is used to have persistent data, by saving and loading data from the disk.
 *
 * This class is used to store data during runtime. It is the model in the MVC pattern, therefore when we change the value of a variable,
 * the listeners will be notified that a variable's value inside the model has been changed
 */
public class Jam54LauncherModel implements Observable
{
    private final List<InvalidationListener> listenerList;

    private boolean newVersionDownloaded;

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
}
