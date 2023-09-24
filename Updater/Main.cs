using System.Diagnostics;
using System.Threading;

class Updater
{
    static void Main(string[] args)
    {
        Thread.Sleep(5000); //Wait for the Jam54Launcher to close

        string oldLauncher = Path.Combine(Directory.GetCurrentDirectory(), "Jam54_Launcher.jar"); //This grabs the path to the old version of the launcher
        string newLauncher = Path.Combine(Directory.GetCurrentDirectory(), "Jam54_Launcher_New.jar"); //This grabs the path to the new version of the launcher

        string jam54Launcher = Path.Combine(Directory.GetCurrentDirectory(), "..", "Jam54 Launcher.exe"); //This goes up 1 directory, and grabs the path to the executable, used to run the Jam54 Launcher

        if (File.Exists(newLauncher))
        {//Only perform the update process, if there is a new version downloaded
            File.Move(newLauncher, oldLauncher, true); //Replace the old jar of the launcher, with the new version. And rename the new version so it has the same name as the old version.
            Process.Start(jam54Launcher); //Start the Jam54 Laucher
            File.Delete(newLauncher); //Delete the downloaded file after it has been installed
        }
    }
}