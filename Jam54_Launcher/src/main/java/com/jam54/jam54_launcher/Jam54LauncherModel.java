package com.jam54.jam54_launcher;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

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
