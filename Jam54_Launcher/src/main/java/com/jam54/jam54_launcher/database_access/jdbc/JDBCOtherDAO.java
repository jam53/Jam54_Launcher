package com.jam54.jam54_launcher.database_access.jdbc;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.database_access.DataAccessException;
import com.jam54.jam54_launcher.database_access.OtherDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Implementatie van {@link com.jam54.jam54_launcher.database_access.OtherDAO} met behulp van JDBC.
 */
public class JDBCOtherDAO implements OtherDAO
{
    private final Connection connection;

    public JDBCOtherDAO(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * Hulpmethode om een prepared statement aan te maken
     */
    private PreparedStatement prepare(String sql) throws SQLException
    {
        return connection.prepareStatement(sql);
    }

    /**
     * Geeft een geordende lijst van alle talen in de applicatie terug als locales
     */
    @Override
    public ArrayList<Locale> getAllSupportedLanguages() throws DataAccessException
    {
        ArrayList<Locale> languages = new ArrayList<>();

        try (PreparedStatement psLanguages = prepare("SELECT language FROM application_description WHERE id == 0 ORDER BY language"))
        //We gaan ervan uit dat er op zijn minst 1 applicatie in de databank zit. Die met identifier 0. En daarvan nemen we dan alle talen.
        //We assumen ook dat alle applicaties in dezelfde (hoeveelheid) talen vertaald zijn.
        {
            try (ResultSet rsLanguages = psLanguages.executeQuery())
            {
                while (rsLanguages.next())
                {
                    languages.add(Locale.of(rsLanguages.getString("language")));
                }
            }
        }
        catch (SQLException ex)
        {
            ErrorMessage errorMessage = new ErrorMessage(true, SaveLoadManager.getTranslation("CouldntLoadLanguages"));
            errorMessage.show();
        }

        return languages;
    }
}
