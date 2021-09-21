using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AppsUpdater : MonoBehaviour
{
    public static AppsUpdater AppsUpdaterr; //Zodat we het gemakkelijk vanuit andere scripts kunnen oproepen om te saven/loaden
    public bool stopDownloading;

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

    public void InstallApp(int appIndex)
    {
        #region Download
        //switch (appIndex)
        //{
        //    case 1: //AstroRun
        //        print();
        //        break;

        //    case 2:

        //    default:
        //        Debug.LogError("Couldn't figure out which app should be downloaded");
        //        break;
        //}

        //InstallApp button clicked
        //    cancle button clicked
        //    updatedownloadingbutton(percenatge);
        #endregion



        #region Install

        #endregion
    }
}
