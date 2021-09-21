using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO.Compression;
using System.Net;
using System.ComponentModel;
using System.IO;

public class AppsUpdater : MonoBehaviour
{
    public static AppsUpdater AppsUpdaterr; //Zodat we het gemakkelijk vanuit andere scripts kunnen oproepen om te saven/loaden
    public bool stopDownloading;
    public Navigation navigation;
    public int ZipToRemoveBasedOnAppIndexBecauseDownloadGotCanceled;

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
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/Stelexo.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Stelexo.zip");
                break;

            case 4: //AutoEditor
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/AutoEditor.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/AutoEditor.zip");
                break;

            case 5: //DGCTimer
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/DGCTimer.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/DGCTimer.zip");
                break;

            case 6: //ImageSearcher
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/ImageSearcher.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/ImageSearcher.zip");
                break;

            case 7: //IToW
                webClient.DownloadFileAsync(new System.Uri("https://github.com/jamhorn/Apps/releases/latest/download/IToW.zip"), SaveLoadManager.SaveLoadManagerr.menuData.path + @"/IToW.zip");
                break;

            case 8: //WToI
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
            Install(); //When the download is complete, move on to install the app
            //unzip and delete the zip
            //when installing/unpacking zip and removing zip is complete, set currenupdatingappindex in Navigation.cs to 0
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

            CleanUpInstallation();
        }




    }

    public void CleanUpInstallation()
    {//Delete the zipfile, update the currentlyUpdatingApp
     //        currentlyUpdatingAppIndex op nul
     // create shortcut
        Debug.Log("Actually managed ot make it here wth");
    }
}
