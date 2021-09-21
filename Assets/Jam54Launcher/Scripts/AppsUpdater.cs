using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO.Compression;
using System.Net;
using System.ComponentModel;
using System.IO;
using UnityEngine.UIElements;
using System;
using Unity.RemoteConfig;

public class AppsUpdater : MonoBehaviour
{
    public static AppsUpdater AppsUpdaterr; //Zodat we het gemakkelijk vanuit andere scripts kunnen oproepen om te saven/loaden
    public bool stopDownloading;
    public Navigation navigation;
    public InitializeUI initializeUI;
    public int ZipToRemoveBasedOnAppIndexBecauseDownloadGotCanceled;

    public struct userAttributes { }
    public struct appAttributes { }



    private void Awake()
    {
        if (AppsUpdaterr == null)
        {
            DontDestroyOnLoad(gameObject);
            AppsUpdaterr = this;
        }

        else if (AppsUpdaterr != this)
        {
            Destroy(gameObject);
        }

        ConfigManager.FetchConfigs<userAttributes, appAttributes>(new userAttributes(), new appAttributes());
    }

    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        
    }


    WebClient webClient;
    public void DownloadApp(int appIndex)
    {
        webClient = new WebClient();
        webClient.DownloadFileCompleted += new AsyncCompletedEventHandler(Completed); //Will be called when the download is complete
        webClient.DownloadProgressChanged += new DownloadProgressChangedEventHandler(ProgressChanged); //Will be called when the progress of the download changes


        switch (appIndex)
        {
            case 1: //AstroRun
                //als je op astro run klikt, get it on google play
                break;

            case 2: //Smash&Fly
                //als je op smash&fly klikt, sign up for closed alpha + mail icoontje
                break;

            case 3: //Stelexo
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/Stelexo.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip");
                break;

            case 4: //AutoEditor
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/AutoEditor.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip");
                break;

            case 5: //DGCTimer
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/DGCTimer.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip");
                break;

            case 6: //ImageSearcher
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/ImageSearcher.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip");
                break;

            case 7: //IToW
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/IToW.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip");
                break;

            case 8: //WToI
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip");
                }
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/WToI.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip");
                break;

            default:
                Debug.LogError("Couldn't figure out which app should be downloaded");
                break;
        }

        //InstallApp button clicked
        //    cancle button clicked
        //    updatedownloadingbutton(percenatge);
        //if we start updating, disable the path selection button, then when it's done, enable it agian uwu
    }

    public void CancelDownload()
    {
        webClient.CancelAsync(); //The user canceled the download, the method Completed will now be called immeadiatly. Instead of waiting for 
        //the download to finish
    }

    private void ProgressChanged(object sender, DownloadProgressChangedEventArgs e)
    {
        navigation.UpdateDownloadingButtonProgress(e.ProgressPercentage);
    }

    private void Completed(object sender, AsyncCompletedEventArgs e)
    {
        if (e.Cancelled) //If the download was cancelled
        {
            webClient.Dispose(); //Releases all resources
            

            switch (ZipToRemoveBasedOnAppIndexBecauseDownloadGotCanceled)
            {//Delete paritally downloaded zip file

                case 1: //AstroRun
                        //We don't install AstroRun with the Launcher, since it's on Google Play, so we don't need to deleting anything. Besides, this part of the switch statement should never get called
                    break;

                case 2: //Smash&Fly
                        // We don't install Smash&Fly with the launchers since it's on Google Play, so we don't need to deleting anything. Besides, this part of the switch statement should never get called
                    break;

                case 3: //Stelexo
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip");
                    }
                    break;

                case 4: //AutoEditor
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip");
                    }
                    break;

                case 5: //DGCTimer
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip");
                    }
                    break;

                case 6: //ImageSearcher
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip");
                    }
                    break;

                case 7: //IToW
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip");
                    }
                    break;

                case 8: //WToI
                    if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip"))
                    {
                        File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip");
                    }
                    break;

                default:
                    Debug.LogError("Couldn't delete the paritally downloaded zipfile");
                    break;
            }

            navigation.currentlyUpdatingAppIndex = 0;
            navigation.OpenMainMenu();
        }

        if (!e.Cancelled) //If the download was not cancelled. It means it completed (or some exception was thrown because something went wrong, but we will just assume the download was succesful)
        {
            navigation.Cancel_Button.style.display = DisplayStyle.None; //We won't allow the user to abort, during unzipping
            navigation.Downloading_Label.text = navigation.LocalizeString("#INSTALLING"); //Updat the text inside the progress bar
            Install(); //When the download is complete, move on to install the app
        }
    }

    private void Install()
    {// Install, or in other words, unpack the zip we just downloaded
     //remove cancel button
     //update text indownloading button and stuff

        bool abortUnzipping = false;
        string zipPath = @"c:\example\result.zip";
        switch (navigation.currentlyUpdatingAppIndex)
        {//Get the path to the current

            case 1: //AstroRun
                    //We don't install AstroRun with the Launcher, since it's on Google Play, so we don't need to find the path to the zip.
                abortUnzipping = true;
                break;

            case 2: //Smash&Fly
                    // We don't install Smash&Fly with the launchers since it's on Google Play, so we don't need to find the path to the zip.
                abortUnzipping = true;
                break;

            case 3: //Stelexo
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }

                break;

            case 4: //AutoEditor
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }
                break;

            case 5: //DGCTimer
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }
                break;

            case 6: //ImageSearcher
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }
                break;

            case 7: //IToW
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }
                break;

            case 8: //WToI
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip"))
                {
                    zipPath = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip";
                }

                else
                {
                    Debug.LogError("Couldn't find downloaded zip. Aborting installtion");
                    navigation.currentlyUpdatingAppIndex = 0; //There is no longer an app updating/downloading
                    abortUnzipping = true; //Stop the unzipping
                    navigation.OpenMainMenu(); //open the main menu
                }
                break;

            default:
                Debug.LogError("Couldn't unpack the paritally downloaded zipfile");
                abortUnzipping = true;
                break;
        }




        if (!abortUnzipping)
        {
            System.IO.Compression.ZipFile.ExtractToDirectory(zipPath, SaveLoadManager.SaveLoadManagerr.menuData.path, true); //Extract the zip to the following directory and overwrite files

            RemoveDownloadedZip();
        }
    }

    public void RemoveDownloadedZip()
    {

        switch (navigation.currentlyUpdatingAppIndex)
        {//Delete the downloaded zip file after we unzipped it

            case 1: //AstroRun
                    //We don't install AstroRun with the Launcher, since it's on Google Play, so we don't need to deleting anything. Besides, this part of the switch statement should never get called
                break;

            case 2: //Smash&Fly
                    // We don't install Smash&Fly with the launchers since it's on Google Play, so we don't need to deleting anything. Besides, this part of the switch statement should never get called
                break;

            case 3: //Stelexo
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip");
                }
                break;

            case 4: //AutoEditor
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip");
                }
                break;

            case 5: //DGCTimer
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip");
                }
                break;

            case 6: //ImageSearcher
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip");
                }
                break;

            case 7: //IToW
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip");
                }
                break;

            case 8: //WToI
                if (File.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip"))
                {
                    File.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI.zip");
                }
                break;

            default:
                Debug.LogError("Couldn't delete the downloaded zipfile after unzipping it");
                break;
        }


        CreateShortcut();
    }

    public void CreateShortcut()
    {//Create a shortcut to the installed app on the users desktop
        string deskDir = Environment.GetFolderPath(Environment.SpecialFolder.DesktopDirectory);



        switch (navigation.currentlyUpdatingAppIndex)
        {//Delete the downloaded zip file after we unzipped it

            case 1: //AstroRun
                    //We don't install AstroRun with the Launcher, since it's on Google Play, so we don't need to create a shortcut. Besides, this part of the switch statement should never get called
                break;

            case 2: //Smash&Fly
                    // We don't install Smash&Fly with the launchers since it's on Google Play, so we don't need to create a shortcut. Besides, this part of the switch statement should never get called
                break;

            case 3: //Stelexo
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "Stelexo" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo/Stelexo.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionStelexo = ConfigManager.appConfig.GetString("StelexoVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            case 4: //AutoEditor
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "AutoEditor" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor/AutoEditor.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionAutoEditor = ConfigManager.appConfig.GetString("AutoEditorVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            case 5: //DGCTimer
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "DGCTimer" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer/Timer.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionDGCTimer = ConfigManager.appConfig.GetString("DGCTimerVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            case 6: //ImageSearcher
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "ImageSearcher" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher/ReclameCutter.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionImageSearcher = ConfigManager.appConfig.GetString("ImageSearcherVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            case 7: //IToW
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "IToW" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW/IToW.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionIToW = ConfigManager.appConfig.GetString("IToWVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            case 8: //WToI
                using (StreamWriter writer = new StreamWriter(deskDir + "\\" + "WToI" + ".url"))//Get the desktop directory and create the shorcut file
                {
                    string app = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/WToI/WToI.exe"; //Get the path to the executable
                    writer.WriteLine("[InternetShortcut]"); //The type of shorcute
                    writer.WriteLine("URL=file:///" + app); //Write the path to the executable to the shorcut
                    writer.WriteLine("IconIndex=0");
                    //string icon = app.Replace('\\', '/');
                    writer.WriteLine("IconFile=" + app); //Get the icon for the shorcut, from the executable
                }

                SaveLoadManager.SaveLoadManagerr.menuData.VersionWToI = ConfigManager.appConfig.GetString("WToIVersion"); //Get the current most up to date version number of the app, and save it.
                SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                break;

            default:
                Debug.LogError("Couldn't delete the downloaded zipfile after unzipping it");
                break;
        }


        //does the version number get saved?

        //downloadprogress button vervangen door play en unistall
        //of gewoon teruggaan naar main menu en versie nummer vervangen.
        //het zal dan automatisch play en unistall daar zetten
        //kleurvolle foto inladen

        //play button path + folder name + exe name
        //play button set running to true
        //uninstall delete path if not running
        //unisntall, give message box if still running and trying to delete
        //uninstall, delete shortcute, if it exists
        //uninstall, zet versie nummer op 0

        //updaten doen
        //als er een hogere remote config is
        //alles hier verwijderen en opnieuw downloaden. 
        //Gewoon vorige functies hergebruiken? Uninstall functie en install functie
        //update automatisch met remoteconfig

        navigation.currentlyUpdatingAppIndex = 0; //Everything, from downloading to installing and creating a shortcut has been done, so we put this
        //to 0. This means nothing is updating. There for the user can updat/download other apps again

        initializeUI.Start(); //Check opnieuw de versienummers van de apps, om te bepalen als het gekleurde fotojes moet ingeladen worden. Want daarnet was
        //het nog niet geinstalleerd en dus was het fototje grijs, maar nu zou het kleur moeten worden
        navigation.OpenMainMenu(); //Open the main menu
    }
}
