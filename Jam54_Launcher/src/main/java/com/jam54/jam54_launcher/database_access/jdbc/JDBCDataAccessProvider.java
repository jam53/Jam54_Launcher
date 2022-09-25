package com.jam54.jam54_launcher.database_access.jdbc;

import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.database_access.DataAccessContext;
import com.jam54.jam54_launcher.database_access.DataAccessException;
import com.jam54.jam54_launcher.database_access.DataAccessProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementatie van {@link DataAccessProvider} met een achterliggende JDBC
 * databank.
 */
public class JDBCDataAccessProvider implements DataAccessProvider
{
    private final String dbName;
    public JDBCDataAccessProvider(String dbLocation)
    {
        this.dbName = dbLocation;
    }

    /**
     * Open een verbinding met de databank.
     */
    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection("jdbc:sqlite:" +  dbName);
    }

    @Override
    public DataAccessContext getDataAccessContext() throws DataAccessException
    {
        try
        {
            return new JDBCDataAccessContext(getConnection());
        }
        catch (SQLException ex)
        {
            throw new DataAccessException(SaveLoadManager.getTranslation("CouldntCreateDAC"), ex);
        }
    }
}
