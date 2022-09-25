package com.jam54.jam54_launcher.database_access;

/**
 * Wordt opgegooid wanneer er iets fout gaat met de databank.
 */
public class DataAccessException extends Exception
{

    public DataAccessException(String message, Throwable th)
    {
        super(message, th);
    }

}
