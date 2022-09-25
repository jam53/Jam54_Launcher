package com.jam54.jam54_launcher.database_access;

/**
 * Deze data access context geeft ons toegang tot de verschillende
 * data access objects. Deze context moet gesloten worden nadat hij is gebruikt.
 */
public interface DataAccessContext extends AutoCloseable
{

    ApplicationDAO getApplicationDAO();

    @Override
    void close() throws DataAccessException;
}
