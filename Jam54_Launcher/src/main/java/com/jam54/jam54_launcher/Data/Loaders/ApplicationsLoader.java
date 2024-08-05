package com.jam54.jam54_launcher.Data.Loaders;

import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Main;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Updating.DownloadFile;
import com.jam54.jam54_launcher.database_access.ApplicationDAO;
import com.jam54.jam54_launcher.database_access.DataAccessContext;
import com.jam54.jam54_launcher.database_access.DataAccessException;
import com.jam54.jam54_launcher.database_access.DataAccessProvider;
import com.jam54.jam54_launcher.database_access.Other.ApplicationInfo;
import com.jam54.jam54_launcher.database_access.jdbc.JDBCDataAccessProvider;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This class is used to load all of the applications' details, and return them as a list of ApplicationInfo objects.
 *
 * First the data gets loaded from the applications.sqlite database
 * After which each application's respective installed version number gets added to the ApplicationInfo object. An empty string will be used if the application isn't installed
 * Finally we will download the applicationsVersoins.properties file, to check whether or not there are new updates available for the apps
 */
public class ApplicationsLoader
{
    /**
     * Returns a list of ApplicationInfo objects, that contain the details of the applications, that is stored inside the applications.sqlite database
     */
    private ArrayList<ApplicationInfo> getDataFromDatabase()
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
            ApplicationDAO dao = dac.getApplicationDAO();

            return dao.getAllApplications(); //We do not need to sort the objects in this list on id, since this has already been done using the SQL query when they were extracted from the database
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method takes the list of ApplicationInfo objects, that were created from the database.
     * And fills in the "version" variable of these objects, and returns the ApplicationInfo objects list
     */
    private ArrayList<ApplicationInfo> addInstalledVersion(ArrayList<ApplicationInfo> apps)
    {
        ArrayList<ApplicationInfo> updatedApps = new ArrayList<>();

        int amountOfAppsInSavefileCurrently = SaveLoadManager.getData().getInstalledApplicationVersions().length;

        for (int i = 0; i < apps.size(); i++)
        {
            updatedApps.add(new ApplicationInfo(
                    apps.get(i).id(),
                    apps.get(i).name(),
                    apps.get(i).image(),
                    apps.get(i).updateAvailable(),
                    apps.get(i).availableVersion(),
                    i < amountOfAppsInSavefileCurrently ? SaveLoadManager.getData().getInstalledApplicationVersions()[i] : null, //Choosing for null would mean a new app has been added to the launcher. Therefore we put null as the default value
                    apps.get(i).descriptions(),
                    apps.get(i).platforms(),
                    apps.get(i).releaseDate(),
                    i < amountOfAppsInSavefileCurrently ? Math.max(SaveLoadManager.getData().getInstalledApplicationLatestUpdates()[i], apps.get(i).lastUpdate()) : 0, //Choosing for null would mean a new app has been added to the launcher. Therefore we put 0 as the default value
                    apps.get(i).isGame()
            ));
        }

        return updatedApps;
    }


    /**
     * This method takes the list of ApplicationInfo objects, that were created from the database + with the added version variable.
     * And fills in the "updateAvailable" variable of these objects, and returns the ApplicationInfo objects list
     */
    private ArrayList<ApplicationInfo> checkForUpdates(ArrayList<ApplicationInfo> apps)
    {
        ArrayList<ApplicationInfo> updatedApps = new ArrayList<>();

        Properties jam54LauncherConfigProperties = new Properties(); //Used to get the URL to download the applicationsVersions.properties file
        Properties applicationsVersionsProperties = new Properties(); //Used to check if the apps have a new version available

        try (InputStream in = Main.class.getResourceAsStream("Jam54LauncherConfig.properties"))
        {
            jam54LauncherConfigProperties.load(in);

            Path applicationsVersions = Files.createTempFile("applicationsVersions", ".properties");
            applicationsVersions.toFile().deleteOnExit();
            DownloadFile.saveUrlToFile(new URI(jam54LauncherConfigProperties.getProperty("applicationVersions")).toURL(), applicationsVersions, 10000, 10000, 10);

            try (InputStream in2 = Files.newInputStream(applicationsVersions))
            {
                applicationsVersionsProperties.load(in2);
            }

            long[] installedApplicationLatestUpdates = new long[apps.size()];
            for (int i = 0; i < apps.size(); i++)
            {
                updatedApps.add(new ApplicationInfo(
                        apps.get(i).id(),
                        apps.get(i).name(),
                        apps.get(i).image(),
                        ! (applicationsVersionsProperties.getProperty("appVersion" + i).equals(apps.get(i).version())), //If the version number in the cloud doesn't equal
                        //the version number stored locally. Then it means there is either a new update available/the app isn't download.
                        //So in both cases we can show the install button, and therefore put this variable, "updateAvailable" to true.
                        applicationsVersionsProperties.getProperty("appVersion" + i),
                        apps.get(i).version(),
                        apps.get(i).descriptions(),
                        apps.get(i).platforms(),
                        apps.get(i).releaseDate(),
                        Long.parseLong(applicationsVersionsProperties.getProperty("appLatestUpdate" + i)),
                        apps.get(i).isGame()
                ));

                installedApplicationLatestUpdates[i] = Long.parseLong(applicationsVersionsProperties.getProperty("appLatestUpdate" + i));
            }
            SaveLoadManager.getData().setInstalledApplicationLatestUpdates(installedApplicationLatestUpdates);
        }
        catch (IOException e)
        {
            updatedApps = apps; //If we don't have an internet connection, we won't be able to check for updates.
            //In that case, we will return the original list of ApplicationInfo objects, and leave the "updateAvailable" variable on false
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }

        return updatedApps;
    }


    /**
     * Returns a list of ApplicationInfo objects, who contain all the details of the applications, needed to display them within the launcher
     */
    public ArrayList<ApplicationInfo> getApplicationInfos()
    {
        return checkForUpdates(addInstalledVersion(getDataFromDatabase()));
    }
}
