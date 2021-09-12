using System.Windows;
using System;
using Squirrel;
using System.Threading.Tasks;
using System.IO;
using System.Reflection;
using System.Diagnostics;
using Microsoft.Win32;
using System.Linq;

namespace Jam54LauncherStarter
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        bool NewUpdateAvailable;

        public MainWindow()
        {
            InitializeComponent();

            Main();
        }


        private async Task Main()
        {
            await AddUnderscoreInFileNames();//only move to the next line, if this one is complete
            await CheckForUpdates();
            await RemoveUnderscoreInFileNames();
            await CreateRegistryUninstallKey();

            //Als we dit niet doen, wordt de value DisplayName aangemaakt in de registry, maar is de waarde, zijnde Jam54Launcher er niet
            RegistryKey key = Registry.CurrentUser.OpenSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher", true);
            key.SetValue("DisplayName", "Jam54Launcher");

            await LaunchJam54LauncherMain();
        }

        //We do this before we launch Squirrel, because file names with spaces in them cause errors for Squirrel
        private async Task AddUnderscoreInFileNames()
        {
            string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);//The location where this script is running from

            if (File.Exists(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources"))//Check if this file exists
            {
                File.Move(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources", path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources");
                //Renames the file, (i.e. it replaces spaces with underscores)
            }
        }

        //Check for updates with Squirrel, and install them if there are any
        private async Task CheckForUpdates()
        {
            using (var manager = new UpdateManager(@"C:\Projects\MyApp\Releases"))//Where does Squirrel have to check for the update files
            {
                var updateInfo = await manager.CheckForUpdate();

                if (updateInfo.ReleasesToApply.Any())//If there is a new update, this returns true
                {
                    NewUpdateAvailable = true;
                }

                await manager.UpdateApp();//If there is an update, this will update the Jam54Launcher, and the next time the Jam54Launcher is opened,
                //the changes will apply. We call this method regardless of whether there is an update or not. If there isn't a new update, it
                //doesn't doe anything
            }
        }

        //We do this before we launch the Jam54Launcher, other wise it will crash since it can't find the files it needs
        private async Task RemoveUnderscoreInFileNames()
        {
            string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);//The location where this script is running from

            if (File.Exists(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources"))//Check if the file exists
            {
                File.Move(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources", path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources"); //Rename the file
            }
        }

        //This adds a registry key and values under Computer\HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher
        //By doing this the Jam54Launcher can be uninstalled from control panel/windows settings
        private async Task CreateRegistryUninstallKey()
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher", true);

            if (key == null) //Check if that registry key exists
            {
                key = Registry.CurrentUser.CreateSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher", true);//If it doesn't exist, create the key

                key.SetValue("DisplayName", "Jam54Launcher");
                key.SetValue("Publisher", "Jam54");

                //System.Drawing.Icon icon = System.Drawing.Icon.ExtractAssociatedIcon(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher\Jam54Launcher.exe");
                //FileStream fileStream = new FileStream(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher\Jam54Launcher.ico", FileMode.Create);
                //icon.Save(fileStream);
                //key.SetValue("DisplayIcon", Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher\Jam54Launcher.ico");
                
                Assembly assembly = Assembly.GetExecutingAssembly();
                FileVersionInfo AppVersion = FileVersionInfo.GetVersionInfo(assembly.Location);
                key.SetValue("DisplayVersion", AppVersion.FileVersion);
                
                key.SetValue("Contact", "jam54.help@outlook.com");
                key.SetValue("InstallDate", DateTime.Now.ToString("yyyyMMdd"));
                key.SetValue("InstallLocation", Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher");
                key.SetValue("QuietUninstallString", Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher\Update.exe --uninstall -s");
                key.SetValue("UninstallString", Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + @"\Local\Jam54Launcher\Update.exe --uninstall");

                if (key != null)
                {
                    key.Close();
                }
            }

            else if (key != null)//If the key does exist, the user is probably updating to a newer version, so we just need to adjust/change the version
            {
                Assembly assembly = Assembly.GetExecutingAssembly();
                FileVersionInfo AppVersion = FileVersionInfo.GetVersionInfo(assembly.Location);
                key.SetValue("DisplayVersion", AppVersion.FileVersion);

                key.Close();
            }
        }

        //Launch the Jam54Launcher
        private async Task LaunchJam54LauncherMain()
        {
            if (NewUpdateAvailable)//If there was a new update available, that means that the new version has been installed,
                //but the changes havent been applied. They only get applied during the next restart. But since we want to have the most recent/
                //updated version immediately, we relaunch the application
            {
                string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), @"\Local\Jam54Launcher\Update.exe");
                Process.Start(path, "--processStart Jam54Launcher.exe"); //Start a new instance of this program
                Environment.Exit(0); //Close the current instance
            }

            else //If there isn't a new update, of which we want to apply the changes immediately, then we can launch the Jam54Launcher right away/straight away
            {
                try
                    //Should this fail, just go to the location under %appdata%, since squirrel always stores the stuff it installs there
                {
                    string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
                    Process.Start(path + @"\Jam54LauncherApp\Jam54LauncherMain.exe");
                }
                catch (Exception ex)
                {
                    string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), @"\Local\Jam54Launcher\Update.exe");
                    Process.Start(path, "--processStart Jam54Launcher.exe");
                }

                Environment.Exit(0); //exit the program
            }

        }


        private void MediaElement_Loaded(object sender, RoutedEventArgs e)
        {
            //not used
        }

        //We use this to loop the loading animation.
        //This function gets called automatically, as soon as the clip that is currently playing, ends
        private void MediaElement_MediaEnded(object sender, RoutedEventArgs e)
        {
            MyMediaElement.LoadedBehavior = System.Windows.Controls.MediaState.Manual;
            //If the clip has ended, set the loadedbehavior to manual, so we can change the mediaelement's properties through script

            MyMediaElement.Stop();//Stop playing the current clip
            MyMediaElement.Position = System.TimeSpan.Zero;//Make it so that the clips start playing from the beginning

            MyMediaElement.Play(); //Play the clip
        }
    }
}
