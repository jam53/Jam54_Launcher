import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args) throws URISyntaxException, IOException
    {
        Path oldLauncherVersion = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().resolve("Jam54_Launcher.jar"); //This gets the path to the jar that's currently running (Updater.jar) and takes the directory it's in. After which it modifies to path so it points to the Jam54Launcher's jar AKA Jam54_Launcher.jar
        Files.delete(oldLauncherVersion);

        Path newLauncherVersion = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().resolve("Jam54_Launcher_New.jar"); //This gets the path to the jar that's currently running (Updater.jar) and takes the directory it's in. After which it modifies to path so it points to the Jam54Launches's new jar AKA Jam54_Launcher_new.jar
        newLauncherVersion.toFile().renameTo(oldLauncherVersion.toFile()); //Rename the "Jam54_Launcher_New.jar" to "Jam54_Launcher.jar"

        Process proces = Runtime.getRuntime().exec("java -jar Jam54_Launcher.jar");
        System.exit(0);
    }
}
