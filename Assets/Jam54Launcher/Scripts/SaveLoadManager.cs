using UnityEngine;
using System.IO;

public class SaveLoadManager : MonoBehaviour
{
    public static SaveLoadManager SaveLoadManagerr; //Zodat we het gemakkelijk vanuit andere scripts kunnen oproepen om te saven/loaden

    public MenuData menuData = new MenuData();

    private void Awake()
    {
        QualitySettings.vSyncCount = 0; //If we don't disable vsync, the fps would be locked to the monitors refreshrate. E.g. 144fps, which is wayyy higher then what we aim for (30 fps)
        Application.targetFrameRate = 30;

        if (SaveLoadManagerr == null)
        {
            DontDestroyOnLoad(gameObject);
            SaveLoadManagerr = this;
        }

        else if (SaveLoadManagerr != this)
        {
            Destroy(gameObject);
        }
    }


    // Start is called before the first frame update
    void Start()
    {
        LoadSaveFile();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private void LoadSaveFile()
    {
        #region Load default values
        //We first load in a json/object with all of the default values,
        //Later we overwrite it with the user's save file. 
        //--- If the user opens the jam54Launcher for the first time, we will only have the default values and nothing will be overwritten
        //If the user updates to a new version, a part of the default values will be overwritten, and new variables that weren't there in the previous release, will have their default value

        menuData.AutoUpdateAstroRun = true; //We enable autoupdate by default
        menuData.AutoUpdateSmashAndFly = true;
        menuData.AutoUpdateStelexo = true;
        menuData.AutoUpdateAutoEditor = true;
        menuData.AutoUpdateDGCTimer = true;
        menuData.AutoUpdateImageSearcher = true;
        menuData.AutoUpdateIToW = true;
        menuData.AutoUpdateWToI = true;

        menuData.VersionAstroRun = "0.0.0"; //The version number being 0.0.0 means that the program/game isn't installed
        menuData.VersionSmashAndFly = "0.0.0";
        menuData.VersionStelexo = "0.0.0";
        menuData.VersionAutoEditor = "0.0.0";
        menuData.VersionDGCTimer = "0.0.0";
        menuData.VersionImageSearcher = "0.0.0";
        menuData.VersionIToW = "0.0.0";
        menuData.VersionWToI = "0.0.0";

        menuData.path = Application.persistentDataPath; //This is the path to where all of the launcher's games and programs will be installed

        menuData.Language = 0; //The language the launcher displays it's content in. This is an index that corresponds with a certain language, depending on the dropdown in the settings menu
        #endregion

        if (File.Exists(Application.persistentDataPath + @"/Jam54Launcher.json"))//Check if the savefile exists, before trying to load it in
        {
            string json = File.ReadAllText(Application.persistentDataPath + @"/Jam54Launcher.json"); //Load the save file into a string
            JsonUtility.FromJsonOverwrite(json, menuData); //Load user's save file and use it to overwrite the default values defined above, with the users data. Leave new default values that aren't present in the user's save file untouched
        }

        SaveJSONToDisk(); //Normally we would just save the exact same json that's already saved to the drive. The only exception to this is when there are new default values, then we will actually write new stuff to the save file in this line
    }

    public void SaveJSONToDisk()
    { // This saves the current menuData object to a savefile called Jam54Launcher.json
        string json = JsonUtility.ToJson(menuData, true);
        File.WriteAllText(Application.persistentDataPath + @"/Jam54Launcher.json", json);
    }

    public void Save()
    {
        //use this
        // SaveLoadManager.SaveLoadManagerr.menuData.<variable name> = <value>; SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
        //to save values from other scripts
    }

    public void Load()
    {
        //use this
        // SaveLoadManager.SaveLoadManagerr.menuData.<variable name>>;
        //to load values from other scripts
    }


}
