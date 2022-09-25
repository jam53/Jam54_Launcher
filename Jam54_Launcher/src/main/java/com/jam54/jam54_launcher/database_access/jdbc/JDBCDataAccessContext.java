package com.jam54.jam54_launcher.database_access.jdbc;

import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.database_access.ApplicationDAO;
import com.jam54.jam54_launcher.database_access.DataAccessContext;
import com.jam54.jam54_launcher.database_access.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementatie van {@link com.jam54.jam54_launcher.database_access.DataAccessContext} die gebruik maakt van JDBC.
 */
class JDBCDataAccessContext implements DataAccessContext
{
    private final Connection connection;

    public JDBCDataAccessContext(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    public ApplicationDAO getApplicationDAO()
    {
        return new JDBCApplicationDAO(connection);
    }

    @Override
    public void close() throws DataAccessException
    {
        try
        {
            connection.close();
        }
        catch (SQLException ex)
        {
            throw new DataAccessException(SaveLoadManager.getTranslation("CouldntCloseDAC"), ex);
        }
    }
}
