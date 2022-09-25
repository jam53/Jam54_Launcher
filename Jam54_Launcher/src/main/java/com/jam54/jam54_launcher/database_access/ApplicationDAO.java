package com.jam54.jam54_launcher.database_access;

import com.jam54.jam54_launcher.Windows.Application.ApplicationInfo;

import java.util.ArrayList;

/**
 * Laat toe om gegevens van applications op te zoeken, aan te maken, te wijzigen of te wissen.
 */
public interface ApplicationDAO
{
    /**
     * Geeft een lijst terug van alle applications
     */
    ArrayList<ApplicationInfo> getAllApplications() throws DataAccessException;

}
