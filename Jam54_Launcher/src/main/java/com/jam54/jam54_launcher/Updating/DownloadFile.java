package com.jam54.jam54_launcher.Updating;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DownloadFile
{
    /**
     * This method is used to download a file from a URL source to a file destination.
     * This method is basically a reimplementation of the `FileUtils.copyURLToFile()` method. However, the `FileUtils.copyURLToFile()` method doesn't throw an error when a connection/read timeout takes place (which it should do), hence why we reimplement it ourselves to achieve that functionality.
     * @param source The URL to copy bytes from, must not be null
     * @param destination The non-directory Path to write bytes to (possibly overwriting), must not be null
     * @param connectionTimeoutMillis The number of milliseconds until this method will time out if no connection could be established to the source
     * @param readTimeoutMillis The number of milliseconds until this method will time out if no data could be read from the source
     * @throws IOException An IoException is thrown when a read or connection timeout occurs
     */
    private static void saveUrlToFile(URL source, Path destination, int connectionTimeoutMillis, int readTimeoutMillis) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection) source.openConnection();

        connection.setConnectTimeout(connectionTimeoutMillis);
        connection.setReadTimeout(readTimeoutMillis);

        try (InputStream in = connection.getInputStream())
        {
            // Use the InputStream to read data and save it to a file
            Files.createDirectories(destination.getParent());
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        finally
        {
            connection.disconnect();
        }
    }

    /**
     * This method is used to download a file from a URL source to a file destination.
     * This method is basically a reimplementation of the `FileUtils.copyURLToFile()` method. However, the `FileUtils.copyURLToFile()` method doesn't throw an error when a connection/read timeout takes place (which it should do), hence why we reimplement it ourselves to achieve that functionality.
     * @param source The URL to copy bytes from, must not be null
     * @param destination The non-directory Path to write bytes to (possibly overwriting), must not be null
     * @param connectionTimeoutMillis The number of milliseconds until this method will time out if no connection could be established to the source
     * @param readTimeoutMillis The number of milliseconds until this method will time out if no data could be read from the source
     * @param amountOfRetries The amount of times the method will retry to download the source before throwing an error
     * @throws IOException An IoException is thrown when a read or connection timeout occurs
     */
    public static void saveUrlToFile(URL source, Path destination, int connectionTimeoutMillis, int readTimeoutMillis, int amountOfRetries) throws IOException
    {
        int retryCount = 0;
        while (retryCount < amountOfRetries)
        {
            try
            {
                // Attempt to download the file
                saveUrlToFile(source, destination, connectionTimeoutMillis, readTimeoutMillis);

                // If the download is successful, break out of the loop
                break;
            }
            catch (IOException e)
            {
                // Handle the exception (connection/read timeouts)
                retryCount++;

                if (retryCount < amountOfRetries)
                {
                    // Log the retry attempt
                    System.out.println("Retry download attempt " + retryCount + " for URL: " + source);
                }
                else
                {// Log that the maximum number of retries has been reached
                    throw e;
                }
            }
        }
    }
}
