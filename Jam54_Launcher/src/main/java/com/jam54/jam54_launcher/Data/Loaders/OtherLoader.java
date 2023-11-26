package com.jam54.jam54_launcher.Data.Loaders;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.database_access.*;
import com.jam54.jam54_launcher.database_access.jdbc.JDBCDataAccessProvider;
import org.apache.commons.io.FileUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class is used to load data in the main method from the database that isn't related to applications
 */
public class OtherLoader
{
    private ArrayList<Locale> getDataFromDatabase()
    {
        Path database = null;
        try
        {
            Path tempFile = Files.createTempFile("applications", ".sqlite"); //We can't interact with the database when it's stored inside the jar,
            //it needs to be a separate file; see: https://stackoverflow.com/questions/6499218/how-to-use-sqlite-database-inside-jar-file
            //Therefore we will copy the contents of the database stored inside the jar, to a temporary file outside the jar

            tempFile.toFile().deleteOnExit();
            URL databaseInJar = Main.class.getResource("applications.sqlite");
            FileUtils.copyURLToFile(databaseInJar, tempFile.toFile());

            database = tempFile;
        }
        catch (Exception e)
        {
            ErrorMessage errorMessage = new ErrorMessage(true, e.getMessage());
            errorMessage.show();
        }

        DataAccessProvider dap = new JDBCDataAccessProvider(database.toString());

        try (DataAccessContext dac = dap.getDataAccessContext())
        {
            OtherDAO dao = dac.getOtherDAO();

            return dao.getAllSupportedLanguages();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of Locales, which contains all the supported languages in the launcher
     */
    public ArrayList<Locale> getSupportedLanguages()
    {
        return getDataFromDatabase();
    }
}
