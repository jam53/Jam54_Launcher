using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;

public class InitializeUI : MonoBehaviour
{
    //UI Objects
    public VisualElement Image1, Image2, Image3, Image4, Image5, Image6, Image7, Image8;
    public Label VersionNumber, Path_Label;

    //Script variables
    [Header("GrayScale Images")]
    public Texture2D AstroRunGrey;
    public Texture2D SmashAndFlyGrey;
    public Texture2D StelexoGrey;
    public Texture2D AutoEditorGrey;
    public Texture2D DGCTimerGrey;
    public Texture2D ImageSearcherGrey;
    public Texture2D IToWGrey;
    public Texture2D WToIGrey;

    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Programs_Unselected_Button' in the root visual element
        Image1 = rootVisualElement.Q<VisualElement>("Image1");
        Image2 = rootVisualElement.Q<VisualElement>("Image2");
        Image3 = rootVisualElement.Q<VisualElement>("Image3");
        Image4 = rootVisualElement.Q<VisualElement>("Image4");
        Image5 = rootVisualElement.Q<VisualElement>("Image5");
        Image6 = rootVisualElement.Q<VisualElement>("Image6");
        Image7 = rootVisualElement.Q<VisualElement>("Image7");
        Image8 = rootVisualElement.Q<VisualElement>("Image8");
        VersionNumber = rootVisualElement.Q<Label>("VersionNumber");
        Path_Label = rootVisualElement.Q<Label>("Path_Label");
        #endregion


        #region Add corresponding methods to the elements

        #endregion
    }

    // Start is called before the first frame update
    void Start()
    {
        VersionNumber.text = Application.version;
        Path_Label.text = SaveLoadManager.SaveLoadManagerr.menuData.path;
        UpdateAppsImages();
    }

    private void UpdateAppsImages()
    {
        //VersionDGCTimer, VersionImageSearcher, VersionIToW, VersionWToI
        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionAstroRun == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image1.style.backgroundImage = AstroRunGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionSmashAndFly == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image2.style.backgroundImage = SmashAndFlyGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionStelexo == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image3.style.backgroundImage = StelexoGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionAutoEditor == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image4.style.backgroundImage = AutoEditorGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionDGCTimer == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image5.style.backgroundImage = DGCTimerGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionImageSearcher == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image6.style.backgroundImage = ImageSearcherGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionIToW == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image7.style.backgroundImage = IToWGrey;// Since it's not installed make it appear grey
        }

        if (SaveLoadManager.SaveLoadManagerr.menuData.VersionWToI == "0.0.0") //version number '0.0.0' means it's not installed
        {
            Image8.style.backgroundImage = WToIGrey;// Since it's not installed make it appear grey
        }
    }
}
