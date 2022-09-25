package com.jam54.jam54_launcher.database_access;

/**
 * Abstractie van een databank. De enige manier om een {@link DataAccessContext} te bekomen
 * is door deze op te vragen bij een data access provider.
 */
public interface DataAccessProvider
{
    /**
     * Geeft een data access context terug die kan gebruikt worden
     * om de databank te bevragen. Een dergelijke context moet na gebruik altijd worden gesloten.
     */
    DataAccessContext getDataAccessContext() throws DataAccessException;
}
