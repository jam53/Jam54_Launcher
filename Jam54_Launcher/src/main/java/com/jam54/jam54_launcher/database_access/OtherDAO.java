package com.jam54.jam54_launcher.database_access;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Wordt gebruikt om gegevens in de databank aan te maken, aan te passen of te verwijderen. Die niets te maken hebben met applications
 */
public interface OtherDAO
{
    /**
     * Geeft een geordende lijst van alle locales die de applicatie ondersteund
     */
    ArrayList<Locale> getAllSupportedLanguages() throws DataAccessException;
}
