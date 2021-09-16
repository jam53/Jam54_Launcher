using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SaveLoadManager : MonoBehaviour
{
    public static SaveLoadManager SaveLoadManagerr; //Zodat we het gemakkelijk vanuit andere scripts kunnen oproepen om te saven/loaden

    private void Awake()
    {
        QualitySettings.vSyncCount = 0;
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
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void SaveDefaultSaveFile() //Creates a json/save file with the default values. For example if it's the first time the user uses the launcher, and doesn't have a savefile yet
    {

    }

    public void Save()
    {

    }

    public void Load()
    {

    }


}
