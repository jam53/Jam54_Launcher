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

        //We do this before we launch Squirrel, because file names with spaces in them causes errors
        private async Task AddUnderscoreInFileNames()
        {
            string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);//The location where this script is running from

            if (File.Exists(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources"))
            {
                File.Move(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources", path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources");
                //Renames the file
            }
        }

        //Check for updates with Squirrel, and install them if there are any
        private async Task CheckForUpdates()
        {
            using (var manager = new UpdateManager(@"C:\Projects\MyApp\Releases"))
            {
                await manager.UpdateApp();
            }
        }

        //We do this before we launch the Jam54Launcher, because other wise it will crash since it can't find this file
        private async Task RemoveUnderscoreInFileNames()
        {
            string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);//The location where this script is running from

            if (File.Exists(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources"))
            {
                File.Move(path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity_default_resources", path + @"\Jam54LauncherApp\Jam54LauncherMain_Data\Resources\unity default resources");
            }
        }

        private async Task CreateRegistryUninstallKey()
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher", true);

            if (key == null)
            {
                key = Registry.CurrentUser.CreateSubKey(@"SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Jam54Launcher", true);

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

            else if (key != null)
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
            try
            {
                string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location);
                Process.Start(path + @"\Jam54LauncherApp\Jam54LauncherMain.exe");
            }
            catch (Exception ex)
            {
                string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), @"\Local\Jam54Launcher\Update.exe");
                Process.Start(path, "--processStart Jam54Launcher.exe");
            }

            Environment.Exit(0);

        }


        private void MediaElement_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void MediaElement_MediaEnded(object sender, RoutedEventArgs e)
        {
            MyMediaElement.LoadedBehavior = System.Windows.Controls.MediaState.Manual;

            MyMediaElement.Stop();
            MyMediaElement.Position = System.TimeSpan.Zero;

            MyMediaElement.Play();
        }
    }
}
