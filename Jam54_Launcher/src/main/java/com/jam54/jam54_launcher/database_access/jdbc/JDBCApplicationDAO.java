package com.jam54.jam54_launcher.database_access.jdbc;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import com.jam54.jam54_launcher.Windows.Application.Platforms;
import com.jam54.jam54_launcher.database_access.ApplicationDAO;
import com.jam54.jam54_launcher.database_access.DataAccessException;
import javafx.scene.image.Image;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Implementatie van {@link ApplicationDAO} met behulp van JDBC.
 */
class JDBCApplicationDAO implements ApplicationDAO
{
    private final Connection connection;

    public JDBCApplicationDAO(Connection connection)
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
     * Geeft een lijst terug van alle applications in de databank
     */
    @Override
    public ArrayList<ApplicationInfo> getAllApplications() throws DataAccessException
    {
        ArrayList<ApplicationInfo> applicationInfos = new ArrayList<>();

        try (PreparedStatement psApplications = prepare("SELECT * FROM applications ORDER BY id");
             PreparedStatement psDescriptions = prepare("SELECT * FROM application_description WHERE id == ?"))
        {
            try (ResultSet rsApplications = psApplications.executeQuery()) //We voeren eerst de prepared statement voor onze applications tabel uit. Pas later voeren we de prepared statement voor onze application_description tabel uit
            {
                while (rsApplications.next())
                {
                    HashMap<Locale, String> descriptions = new HashMap<>();

                    psDescriptions.setInt(1, rsApplications.getInt("id"));
                    try (ResultSet rsDescriptions = psDescriptions.executeQuery())
                    {
                        while (rsDescriptions.next())
                        {
                            descriptions.put(Locale.of(rsDescriptions.getString("language")), rsDescriptions.getString("description"));
                        }
                    }


                    ArrayList<Platforms> platforms = new ArrayList<>();
                    if (rsApplications.getInt("android") == 1)
                    {
                        platforms.add(Platforms.ANDROID);
                    }
                    if (rsApplications.getInt("web") == 1)
                    {
                        platforms.add(Platforms.WEB);
                    }
                    if (rsApplications.getInt("windows") == 1)
                    {
                        platforms.add(Platforms.WINDOWS);
                    }


                    ApplicationInfo applicationInfo = new ApplicationInfo(
                            rsApplications.getInt("id"),
                            rsApplications.getString("name"),
                            new Image(rsApplications.getString("logo")),
                            false,
                            null,
                            null,
                            descriptions,
                            platforms,
                            rsApplications.getLong("releaseDate"),
                            rsApplications.getLong("latestUpdate"),
                            rsApplications.getBoolean("isGame"));

                    applicationInfos.add(applicationInfo);

                }
            }
        }
        catch (SQLException ex)
        {
            ErrorMessage errorMessage = new ErrorMessage(true, SaveLoadManager.getTranslation("CouldntLoadApplicationInfos"));
            errorMessage.show();
        }

        return applicationInfos;
    }
}
