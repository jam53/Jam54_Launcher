using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Localization.Settings;
using UnityEngine.Localization.Tables;
using UnityEngine.UIElements;
using UnityEngine.UIElements.Experimental;
using System.IO;

//This script handles all the animations when clicking on certain elements and makes it possible to navigate in the app.
//In other words, the logic behind clicking on a button in order to open a new tab and close the current one is handled here.
//This script may also call certain methods from order scripts, to execute an action
public class Navigation : MonoBehaviour
{
    //UI Objects
    private VisualElement rootVisualElement;
    public Button Programs_Unselected_Button, Games_Unselected_Button, InstallLocation_Button, Language_Button, Library_Button, Store_Button;
    public VisualElement Games, Programs, SettingsBackgroundCircle, Settings, HomeBackgroundCircle, InstallLocationPanel, LanguagePanel, Discord, YouTube, PlayStore, ProductPage;
    public VisualElement AppOptions1, AppOptions2, AppOptions3, AppOptions4, AppOptions5, AppOptions6, AppOptions7, AppOptions8, OptionsHolder, OptionsHolderExactCopy,  OptionsOutsideClicksDetector;
    public VisualElement ProductImage, Android_Image, Windows_Image, PathBackground;
    public Label ProductTitle_Label, LatestUpdateDate1_Label, ReleaseDateDate2_Label, Description_Label, VersionNumber, Path_Label, Downloading_Label;
    public DropdownField LanguageSelector_Dropdown;
    public VisualElement Image1, Image2, Image3, Image4, Image5, Image6, Image7, Image8, ProgressBar;
    public Button Install_Button, Uninstall_Button, Play_Button, Cancel_Button, Downloading_Button;

    //Script variables
    private bool LastWindowPrograms; //true means 'programs' is open - false means 'games' is open; on the 'main menu'
    private Color SideBarIconsDefaultColor;
    private bool MainWindowSelected; //if 'MainWindowSelected' and 'SettingsWindow' selected are both false
    public bool SettingsWindowSelected; //it means that the product page is open
    public Texture2D InstallLanguageButtonBackground;
    private static int currentAppIndex; //The app that's currently open on the product page
    public AppsInfo AstroRun, SmashAndFly, Stelexo, AutoEditor, DGCTimer, ImageSearcher, IToW, WToI;
    public int currentlyUpdatingAppIndex; //The app that is currently updating, if the variable is 0. It means that there isn't any app currently downloding/installing/updating

    public void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Programs_Unselected_Button' in the root visual element
        Programs_Unselected_Button = rootVisualElement.Q<Button>("Programs_Unselected_Button");
        Games_Unselected_Button = rootVisualElement.Q<Button>("Games_Unselected_Button");
        InstallLocation_Button = rootVisualElement.Q<Button>("InstallLocation_Button");
        Language_Button = rootVisualElement.Q<Button>("Language_Button");
        Library_Button = rootVisualElement.Q<Button>("Library_Button");
        Store_Button = rootVisualElement.Q<Button>("Store_Button");
        Games = rootVisualElement.Q<VisualElement>("Games");
        Programs = rootVisualElement.Q<VisualElement>("Programs");
        SettingsBackgroundCircle = rootVisualElement.Q<VisualElement>("SettingsBackgroundCircle");
        Settings = rootVisualElement.Q<VisualElement>("Settings");
        HomeBackgroundCircle = rootVisualElement.Q<VisualElement>("HomeBackgroundCircle");
        InstallLocationPanel = rootVisualElement.Q<VisualElement>("InstallLocationPanel");
        LanguagePanel = rootVisualElement.Q<VisualElement>("LanguagePanel");
        Discord = rootVisualElement.Q<VisualElement>("Discord");
        YouTube = rootVisualElement.Q<VisualElement>("YouTube");
        PlayStore = rootVisualElement.Q<VisualElement>("PlayStore");
        ProductPage = rootVisualElement.Q<VisualElement>("ProductPage");
        AppOptions1 = rootVisualElement.Q<VisualElement>("AppOptions1");
        AppOptions2 = rootVisualElement.Q<VisualElement>("AppOptions2");
        AppOptions3 = rootVisualElement.Q<VisualElement>("AppOptions3");
        AppOptions4 = rootVisualElement.Q<VisualElement>("AppOptions4");
        AppOptions5 = rootVisualElement.Q<VisualElement>("AppOptions5");
        AppOptions6 = rootVisualElement.Q<VisualElement>("AppOptions6");
        AppOptions7 = rootVisualElement.Q<VisualElement>("AppOptions7");
        AppOptions8 = rootVisualElement.Q<VisualElement>("AppOptions8");
        OptionsHolder = rootVisualElement.Q<VisualElement>("OptionsHolder");
        OptionsHolderExactCopy = rootVisualElement.Q<VisualElement>("OptionsHolderExactCopy");
        OptionsOutsideClicksDetector = rootVisualElement.Q<VisualElement>("OptionsOutsideClicksDetector");
        ProductImage = rootVisualElement.Q<VisualElement>("ProductImage");
        Android_Image = rootVisualElement.Q<VisualElement>("Android_Image");
        Windows_Image = rootVisualElement.Q<VisualElement>("Windows_Image");
        PathBackground = rootVisualElement.Q<VisualElement>("PathBackground");
        ProductTitle_Label = rootVisualElement.Q<Label>("ProductTitle_Label");
        LatestUpdateDate1_Label = rootVisualElement.Q<Label>("LatestUpdateDate1_Label");
        ReleaseDateDate2_Label = rootVisualElement.Q<Label>("ReleaseDateDate2_Label");
        Description_Label = rootVisualElement.Q<Label>("Description_Label");
        VersionNumber = rootVisualElement.Q<Label>("VersionNumber");
        Path_Label = rootVisualElement.Q<Label>("Path_Label");
        Downloading_Label = rootVisualElement.Q<Label>("Downloading_Label");
        LanguageSelector_Dropdown = rootVisualElement.Q<DropdownField>("LanguageSelector_Dropdown");
        Image1 = rootVisualElement.Q<VisualElement>("Image1");
        Image2 = rootVisualElement.Q<VisualElement>("Image2");
        Image3 = rootVisualElement.Q<VisualElement>("Image3");
        Image4 = rootVisualElement.Q<VisualElement>("Image4");
        Image5 = rootVisualElement.Q<VisualElement>("Image5");
        Image6 = rootVisualElement.Q<VisualElement>("Image6");
        Image7 = rootVisualElement.Q<VisualElement>("Image7");
        Image8 = rootVisualElement.Q<VisualElement>("Image8");
        ProgressBar = rootVisualElement.Q<VisualElement>("ProgressBar");
        Install_Button = rootVisualElement.Q<Button>("Install_Button");
        Uninstall_Button = rootVisualElement.Q<Button>("Uninstall_Button");
        Play_Button = rootVisualElement.Q<Button>("Play_Button");
        Cancel_Button = rootVisualElement.Q<Button>("Cancel_Button");
        Downloading_Button = rootVisualElement.Q<Button>("Downloading_Button");
        #endregion


        #region Add corresponding methods to the elements
        Programs_Unselected_Button.clicked += Programs_Unselected_Button_Clicked;
        Games_Unselected_Button.clicked += Games_Unselected_Button_Clicked;
        InstallLocation_Button.clicked += InstallLocation_Button_Clicked;
        Language_Button.clicked += Language_Button_Clicked;
        Library_Button.clicked += Library_Button_Clicked;
        Store_Button.clicked += Store_Button_Clicked;
        Install_Button.clicked += Install_Button_Clicked;
        Cancel_Button.clicked += Cancel_Button_Clicked;
        SettingsBackgroundCircle.RegisterCallback<MouseDownEvent>(SettingsBackgroundCircle_Clicked);
        HomeBackgroundCircle.RegisterCallback<MouseDownEvent>(HomeBackgroundCircle_Clicked);
        SettingsBackgroundCircle.RegisterCallback<MouseOverEvent>(SettingsBackgroundCircle_Over);
        HomeBackgroundCircle.RegisterCallback<MouseOverEvent>(HomeBackgroundCircle_Over);
        SettingsBackgroundCircle.RegisterCallback<MouseOutEvent>(SettingsBackgroundCircle_Out);
        HomeBackgroundCircle.RegisterCallback<MouseOutEvent>(HomeBackgroundCircle_Out);
        Discord.RegisterCallback<MouseDownEvent>(Discord_Clicked);
        YouTube.RegisterCallback<MouseDownEvent>(YouTube_Clicked);
        PlayStore.RegisterCallback<MouseDownEvent>(PlayStore_Clicked);
        AppOptions1.RegisterCallback<MouseDownEvent>(AppOptions1_Clicked);
        AppOptions2.RegisterCallback<MouseDownEvent>(AppOptions2_Clicked);
        AppOptions3.RegisterCallback<MouseDownEvent>(AppOptions3_Clicked);
        AppOptions4.RegisterCallback<MouseDownEvent>(AppOptions4_Clicked);
        AppOptions5.RegisterCallback<MouseDownEvent>(AppOptions5_Clicked);
        AppOptions6.RegisterCallback<MouseDownEvent>(AppOptions6_Clicked);
        AppOptions7.RegisterCallback<MouseDownEvent>(AppOptions7_Clicked);
        AppOptions8.RegisterCallback<MouseDownEvent>(AppOptions8_Clicked);
        OptionsOutsideClicksDetector.RegisterCallback<MouseDownEvent>(OptionsOutsideClicksDetector_Clicked);
        PathBackground.RegisterCallback<MouseDownEvent>(PathBackground_Clicked);
        LanguageSelector_Dropdown.RegisterCallback<ChangeEvent<string>>(LanguageSelector_Dropdown_ValueChanged);
        Image1.RegisterCallback<MouseDownEvent>(Image1_Clicked);
        Image2.RegisterCallback<MouseDownEvent>(Image2_Clicked);
        Image3.RegisterCallback<MouseDownEvent>(Image3_Clicked);
        Image4.RegisterCallback<MouseDownEvent>(Image4_Clicked);
        Image5.RegisterCallback<MouseDownEvent>(Image5_Clicked);
        Image6.RegisterCallback<MouseDownEvent>(Image6_Clicked);
        Image7.RegisterCallback<MouseDownEvent>(Image7_Clicked);
        Image8.RegisterCallback<MouseDownEvent>(Image8_Clicked);
        #endregion
    }

    // Start is called before the first frame update
    public void Start()
    {
        SideBarIconsDefaultColor.r = 0.1411765f; SideBarIconsDefaultColor.g = 0.1490196f; SideBarIconsDefaultColor.b = 0.2313726f; SideBarIconsDefaultColor.a = 1f;
        MainWindowSelected = true;
    }

    #region navigation
    //Clicking on this button disables/closes the games window and opens the programs window
    private void Programs_Unselected_Button_Clicked()
    {
        Games.style.display = DisplayStyle.None;
        Programs.style.display = DisplayStyle.Flex;
        LastWindowPrograms = true; //Hetgeen dat we nu openen is het programs scherm, daarmee dus ook het 'laatste'/meest recenste scherm.
    }


    //Clicking on this button disables/closes the programs window and opens the games window
    private void Games_Unselected_Button_Clicked()
    {
        Programs.style.display = DisplayStyle.None;
        Games.style.display = DisplayStyle.Flex;
        LastWindowPrograms = false; //Hetgeen dat we nu openen is het games scherm, daarmee dus ook het 'laatste'/meest recenste scherm.
    }


    //Clicking on this button should bring the user to the settings page.
    private void SettingsBackgroundCircle_Clicked(MouseDownEvent evt)
    {
        OpenSettingsMenu();
    }

    public void OpenSettingsMenu()
    {
        Games.style.display = DisplayStyle.None;
        Programs.style.display = DisplayStyle.None;
        Settings.style.display = DisplayStyle.Flex;

        ProductPage.style.display = DisplayStyle.None;

        SettingsBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor; //'Enable' the background so it looks selected
        HomeBackgroundCircle.style.backgroundColor = Color.clear; //Make the background transparent so it looks unselected

        MainWindowSelected = false; //we do this to know whether or not the background of the icons in the sidebar can be disabled or not
        SettingsWindowSelected = true; //if the current window that's active is 'settings' for example, the background of the settingsicon should remain there

        if (currentlyUpdatingAppIndex == 0)
        {//If there isn't an apps updating currentlly, the user can chose a different path
            PathBackground.style.display = DisplayStyle.Flex;
        }

        else
        {//If there is an app updating, the path selector is disabled, and the user can't select a new path
            PathBackground.style.display = DisplayStyle.None;
        }
    }


    //Clicking on this button should bring the user back to either the games/programs page. Depending on where they left off
    private void HomeBackgroundCircle_Clicked(MouseDownEvent evt)
    {
        OpenMainMenu();
    }

    public void OpenMainMenu()
    {
        if (!LastWindowPrograms)
        {
            Settings.style.display = DisplayStyle.None;
            Games.style.display = DisplayStyle.Flex;
        }

        else if (LastWindowPrograms)
        {
            Settings.style.display = DisplayStyle.None;
            Programs.style.display = DisplayStyle.Flex;
        }

        ProductPage.style.display = DisplayStyle.None;

        HomeBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor; //'Enable' the background so it looks selected
        SettingsBackgroundCircle.style.backgroundColor = Color.clear; //Make the background transparent so it looks unselected

        MainWindowSelected = true; //we do this to know whether or not the background of the icons in the sidebar can be disabled or not
        SettingsWindowSelected = false; //if the current window that's active is 'settings' for example, the background of the settingsicon should remain there
    }



    //If we click on the 'InstallLocation_Button', the Language settings tab should become unvisible and the Install settings tab should become visible
    private void InstallLocation_Button_Clicked()
    {
        //We do this with a background image on top of a hover pseudo state in the USS classes
        //Because if the user clicks on the button, and then clicks somewhere empty on the screen,
        //The button will look unselected
        Language_Button.style.backgroundImage = null;//Remove the background pic so it looks unselected
        InstallLocation_Button.style.backgroundImage = InstallLanguageButtonBackground;//Add the background pic so it looks selected

        LanguagePanel.style.display = DisplayStyle.None;//Close Language menu
        InstallLocationPanel.style.display = DisplayStyle.Flex;//Open Install location menu
    }

    //If we click on the 'Language_Button', the Install settings tab should become unvisible and the Language settings tab should become visible
    private void Language_Button_Clicked()
    {
        OpenLanguageSettings();
    }

    public void OpenLanguageSettings()
    {
        InstallLocation_Button.style.backgroundImage = null;//The backgroundimage on the 'Language_Button' button makes it look like it's selected on startup. Since it's the first button in the list. But once we click on the 'Language_Button' button. We no longer want the 'InstallLocation_Button' button to be selected. So we delete the backgroundimage that makes the 'Language_Button' button always look selected
        Language_Button.style.backgroundImage = InstallLanguageButtonBackground;//Add the background pic so it looks selected

        InstallLocationPanel.style.display = DisplayStyle.None;//Close install location menu
        LanguagePanel.style.display = DisplayStyle.Flex; //Open language panel menu
    }

    private void Library_Button_Clicked()
    {
        if (!LastWindowPrograms)
        {
            ProductPage.style.display = DisplayStyle.None;
            Games.style.display = DisplayStyle.Flex;
        }

        else if (LastWindowPrograms)
        {
            ProductPage.style.display = DisplayStyle.None;
            Programs.style.display = DisplayStyle.Flex;
        }

        HomeBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor; //'Enable' the background so it looks selected
        SettingsBackgroundCircle.style.backgroundColor = Color.clear; //Make the background transparent so it looks unselected
    }


    private void Discord_Clicked(MouseDownEvent evt)
    {
        Application.OpenURL("https://discord.gg/z6wEvv7");
    }

    private void YouTube_Clicked(MouseDownEvent evt)
    {
        Application.OpenURL("https://www.youtube.com/c/jam54/");
    }

    private void PlayStore_Clicked(MouseDownEvent evt)
    {
        Application.OpenURL("https://play.google.com/store/apps/details?id=com.jam54.AstroRun&hl=en");
    }

    private Vector2 RepositionOptionsMenu(float mouseX, float mouseY)
    {
        //-----
        //We gebruiken hierronder 'OptionsHolderExactCopy' om de breedte en hoogte te verkrijgen, want
        //'OptionsHolder' is een child van een ander VisualElement, en daardoor returned
        // OptionsHolder.resolvedStyle.widht/height altijd 0 inplaats van de werkelijke waarde
        //-----

        //Als we hebben geklikt linksboven in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        if (mouseX <= rootVisualElement.worldBound.width / 2 && mouseY <= rootVisualElement.worldBound.height * 0.75)
        {
            return new Vector2(mouseX, mouseY);
        }

        //Als we hebben geklikt rechtsboven in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX >= rootVisualElement.worldBound.width / 2 && mouseY <= rootVisualElement.worldBound.height * 0.75)
        {
            return new Vector2(mouseX - OptionsHolderExactCopy.resolvedStyle.width, mouseY);
        }

        //Als we hebben geklikt linksonder in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX <= rootVisualElement.worldBound.width / 2 && mouseY >= rootVisualElement.worldBound.height * 0.75)
        {
            return new Vector2(mouseX, mouseY - OptionsHolderExactCopy.resolvedStyle.height);
        }

        //Als we hebben geklikt rechtsonder in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX >= rootVisualElement.worldBound.width / 2 && mouseY >= rootVisualElement.worldBound.height * 0.75)
        {
            return new Vector2(mouseX - OptionsHolderExactCopy.resolvedStyle.width, mouseY - OptionsHolderExactCopy.resolvedStyle.height);
        }

        else
        {
            Debug.LogError("Couldn't return coordinates");
            return new Vector2(0, 0);
        }
    }

    private void AppOptions1_Clicked (MouseDownEvent evt)
    {
        currentAppIndex = 1;//AstroRun

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions1.worldBound.x, AppOptions1.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions1.worldBound.x, AppOptions1.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions2_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 2;//Smash&Fly

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions2.worldBound.x, AppOptions2.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions2.worldBound.x, AppOptions2.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions3_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 3;//Stelexo

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions3.worldBound.x, AppOptions3.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions3.worldBound.x, AppOptions3.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions4_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 4;//AutoEditor

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions4.worldBound.x, AppOptions4.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions4.worldBound.x, AppOptions4.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions5_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 5;//DGCTimer

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions5.worldBound.x, AppOptions5.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions5.worldBound.x, AppOptions5.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions6_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 6;//ImageSearcher

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions6.worldBound.x, AppOptions6.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions6.worldBound.x, AppOptions6.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions7_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 7;//IToW

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions7.worldBound.x, AppOptions7.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions7.worldBound.x, AppOptions7.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions8_Clicked(MouseDownEvent evt)
    {
        currentAppIndex = 8;//WtoI

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions8.worldBound.x, AppOptions8.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions8.worldBound.x, AppOptions8.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }

    private void OptionsOutsideClicksDetector_Clicked(MouseDownEvent evt)
    {
        OptionsOutsideClicksDetector.style.display = DisplayStyle.None;
    }

    //Open the store page/product page for this specific app
    private void Store_Button_Clicked()
    {
        OptionsOutsideClicksDetector.style.display = DisplayStyle.None; //Close the options menu

        OpenProductPage(currentAppIndex);
    }

    private void OpenProductPage(int appIndex)
    {
        currentAppIndex = appIndex;

        Games.style.display = DisplayStyle.None; //Close the Games And Programs Windows
        Programs.style.display = DisplayStyle.None;

        ProductPage.style.display = DisplayStyle.Flex; //Open the ProductPage

        HomeBackgroundCircle.style.backgroundColor = Color.clear;

        switch (appIndex)
        {
            case 1:
                ProductTitle_Label.text = AstroRun.Title;
                ProductImage.style.backgroundImage = AstroRun.Image;
                LatestUpdateDate1_Label.text = AstroRun.LatestUpdate;
                ReleaseDateDate2_Label.text = AstroRun.ReleaseDate;

                if (!AstroRun.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!AstroRun.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = AstroRun.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionAstroRun == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;
                    
                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 2:
                ProductTitle_Label.text = SmashAndFly.Title;
                ProductImage.style.backgroundImage = SmashAndFly.Image;
                LatestUpdateDate1_Label.text = SmashAndFly.LatestUpdate;
                ReleaseDateDate2_Label.text = SmashAndFly.ReleaseDate;

                if (!SmashAndFly.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!SmashAndFly.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = SmashAndFly.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionSmashAndFly == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 3:
                ProductTitle_Label.text = Stelexo.Title;
                ProductImage.style.backgroundImage = Stelexo.Image;
                LatestUpdateDate1_Label.text = Stelexo.LatestUpdate;
                ReleaseDateDate2_Label.text = Stelexo.ReleaseDate;

                if (!Stelexo.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!Stelexo.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = Stelexo.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionStelexo == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 4:
                ProductTitle_Label.text = AutoEditor.Title;
                ProductImage.style.backgroundImage = AutoEditor.Image;
                LatestUpdateDate1_Label.text = AutoEditor.LatestUpdate;
                ReleaseDateDate2_Label.text = AutoEditor.ReleaseDate;

                if (!AutoEditor.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!AutoEditor.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = AutoEditor.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionAutoEditor == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 5:
                ProductTitle_Label.text = DGCTimer.Title;
                ProductImage.style.backgroundImage = DGCTimer.Image;
                LatestUpdateDate1_Label.text = DGCTimer.LatestUpdate;
                ReleaseDateDate2_Label.text = DGCTimer.ReleaseDate;

                if (!DGCTimer.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!DGCTimer.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = DGCTimer.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionDGCTimer == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 6:
                ProductTitle_Label.text = ImageSearcher.Title;
                ProductImage.style.backgroundImage = ImageSearcher.Image;
                ProductImage.style.backgroundColor = ImageSearcher.BackgroundColor;
                LatestUpdateDate1_Label.text = ImageSearcher.LatestUpdate;
                ReleaseDateDate2_Label.text = ImageSearcher.ReleaseDate;

                if (!ImageSearcher.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!ImageSearcher.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = ImageSearcher.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionImageSearcher == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 7:
                ProductTitle_Label.text = IToW.Title;
                ProductImage.style.backgroundImage = IToW.Image;
                LatestUpdateDate1_Label.text = IToW.LatestUpdate;
                ReleaseDateDate2_Label.text = IToW.ReleaseDate;

                if (!IToW.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!IToW.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = IToW.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionIToW == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;


            case 8:
                ProductTitle_Label.text = WToI.Title;
                ProductImage.style.backgroundImage = WToI.Image;
                LatestUpdateDate1_Label.text = WToI.LatestUpdate;
                ReleaseDateDate2_Label.text = WToI.ReleaseDate;

                if (!WToI.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                    Windows_Image.style.display = DisplayStyle.Flex;
                }

                if (!WToI.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                    Android_Image.style.display = DisplayStyle.Flex;
                }

                Description_Label.text = WToI.description;

                if (SaveLoadManager.SaveLoadManagerr.menuData.VersionWToI == "0.0.0")
                {//Als het niet geinstalleerd is, zet de uninstall + play button uit en zet de install button aan
                 //Moest de game wel geinstalleerd zijn, wordt deze functie niet geroepen want het versie nummer zal niet gelijk zijn aan "0.0.0"
                 //Bij default staat de install button onzichtbaar, en de uninstall + play buttons zichtbaar
                 //Dus als de game geinstalleerd is, moeten we niks doen en kunnen we het laten zoals het is, en zullen de juiste buttons zichtbaar zijn (uninstall en play)

                    Uninstall_Button.style.display = DisplayStyle.None;
                    Play_Button.style.display = DisplayStyle.None;

                    Install_Button.style.display = DisplayStyle.Flex;
                }

                else //Als de game wel is geinstalleerd, zet de Play en uninstall buttons aan. En de install button uit
                {
                    Uninstall_Button.style.display = DisplayStyle.Flex;
                    Play_Button.style.display = DisplayStyle.Flex;

                    Install_Button.style.display = DisplayStyle.None;
                }

                break;

            default:
                Debug.LogError("Couldn't open Product page correctly - from OptionsMenu");
                break;
        }

        string key = Description_Label.text;
        StringTable table = UIDocumentLocalization.currentStringTable;
        if (!string.IsNullOrEmpty(key) && key[0] == '#')
        {
            key = key.TrimStart('#');
            StringTableEntry entry = table[key];
            if (entry != null)
                Description_Label.text = entry.LocalizedValue;
            else
                Debug.LogWarning($"No {table.LocaleIdentifier.Code} translation for key: '{key}'");
        }

        key = LatestUpdateDate1_Label.text;
        table = UIDocumentLocalization.currentStringTable;
        if (!string.IsNullOrEmpty(key) && key[0] == '#')
        {
            key = key.TrimStart('#');
            StringTableEntry entry = table[key];
            if (entry != null)
                LatestUpdateDate1_Label.text = entry.LocalizedValue;
            else
                Debug.LogWarning($"No {table.LocaleIdentifier.Code} translation for key: '{key}'");
        }

        key = ReleaseDateDate2_Label.text;
        table = UIDocumentLocalization.currentStringTable;
        if (!string.IsNullOrEmpty(key) && key[0] == '#')
        {
            key = key.TrimStart('#');
            StringTableEntry entry = table[key];
            if (entry != null)
                ReleaseDateDate2_Label.text = entry.LocalizedValue;
            else
                Debug.LogWarning($"No {table.LocaleIdentifier.Code} translation for key: '{key}'");
        }

        if (currentlyUpdatingAppIndex != 0)
        {//This means there is currently another app updating, so we wont allow the user to update/download a second app
            Install_Button.style.display = DisplayStyle.None; //Disable the button which allows the user to download a second app

            if (currentlyUpdatingAppIndex != currentAppIndex) //If the app the user opened on the product page, isn't the app that's currently updating
            {// Then disable the progress bar and the cancel button
                Downloading_Button.style.display = DisplayStyle.None;
                Cancel_Button.style.display = DisplayStyle.None;
            }

            else if (currentlyUpdatingAppIndex == currentAppIndex)//If the app the user opened on the product page, is the app that's currently updating
            {//Enable the progress bar and cancel button
                Downloading_Button.style.display = DisplayStyle.Flex;
                Cancel_Button.style.display = DisplayStyle.Flex;
            }
        }

        if (currentlyUpdatingAppIndex == 0)
        {//If there isn't anything updating, disable the progress bar and cancel buttons
            Downloading_Button.style.display = DisplayStyle.None;
            Cancel_Button.style.display = DisplayStyle.None;
        }
    }

    private void Image1_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(1);
    }

    private void Image2_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(2);
    }

    private void Image3_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(3);
    }

    private void Image4_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(4);
    }

    private void Image5_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(5);
    }

    private void Image6_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(6);
    }

    private void Image7_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(7);
    }

    private void Image8_Clicked(MouseDownEvent evt)
    {
        OpenProductPage(8);
    }

    public void PathBackground_Clicked(MouseDownEvent evt)
    {
        string oldPath = SaveLoadManager.SaveLoadManagerr.menuData.path;

        SaveLoadManager.SaveLoadManagerr.menuData.path = OpenFileDialog(SaveLoadManager.SaveLoadManagerr.menuData.path);//This calls the method that displays a Folder Dialog from the System.Windows.Forms dll, to select a path
        SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk(); //Save the chosen path to the disk

        Path_Label.text = SaveLoadManager.SaveLoadManagerr.menuData.path; //Update the path in the UI

        //Only move new files and subfolders to new path, if the user chose a new path. If it's the same path, don't do anything
        if (!(oldPath == SaveLoadManager.SaveLoadManagerr.menuData.path))
        {



            //Move the save directory + files over to the new location
            if (Directory.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path)) //If the directory already exist, try to delete it and it's files first
            {
                try
                {
                    Directory.Delete(SaveLoadManager.SaveLoadManagerr.menuData.path, true); //Delete all files and subfolders recursivly + delete the directory itself 
                }
                catch (Exception e)
                {//We might not permissions/files could be in use, so just try/catch
                    Debug.LogError(e.Message);
                }


                if (Directory.Exists(SaveLoadManager.SaveLoadManagerr.menuData.path))
                {//Let's say we tried to delete the folder but it still exists, then just create a subfolder inside the path
                 //Don't actually create the subdirecotory yet on the disk though, we will do that after this if block

                    SaveLoadManager.SaveLoadManagerr.menuData.path = SaveLoadManager.SaveLoadManagerr.menuData.path + @"/Jam54LauncherFiles";
                    SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();
                }
            }


            //This code will throw an error if the target directory already exists
            //Now Create all of the directories
            foreach (string dirPath in Directory.GetDirectories(oldPath, "*", SearchOption.AllDirectories))
            {
                Directory.CreateDirectory(dirPath.Replace(oldPath, SaveLoadManager.SaveLoadManagerr.menuData.path));
            }

            //Copy all the files & Replaces any files with the same name
            foreach (string newPath in Directory.GetFiles(oldPath, "*.*", SearchOption.AllDirectories))
            {
                File.Copy(newPath, newPath.Replace(oldPath, SaveLoadManager.SaveLoadManagerr.menuData.path), true);
            }


            Directory.Delete(oldPath, true); //Delete all files and subfolders recursivly + delete the directory itself 
                                             //del old direcotry
        }
    }

    public void LanguageSelector_Dropdown_ValueChanged(ChangeEvent<string> evt)
    {
        //Debug.Log(evt.newValue); //Returns the actual value 'Nederlands' for example
        //Debug.Log(LanguageSelector_Dropdown.index); //Returns what index has been selected, starting from 0
        SaveLoadManager.SaveLoadManagerr.menuData.Language = LanguageSelector_Dropdown.index; SaveLoadManager.SaveLoadManagerr.SaveJSONToDisk();

        LanguageSelector_Dropdown.index = SaveLoadManager.SaveLoadManagerr.menuData.Language;//Load in the correct index of the dropdown in the language dropdown under the  settings > language panel
        LocalizationSettings.SelectedLocale = LocalizationSettings.AvailableLocales.Locales[SaveLoadManager.SaveLoadManagerr.menuData.Language]; //Load in the current correct language
    }

    public void Install_Button_Clicked()
    {
        AppsUpdater.AppsUpdaterr.DownloadApp(currentAppIndex); //Begin downloading/installing the app
        currentlyUpdatingAppIndex = currentAppIndex;

        Install_Button.style.display = DisplayStyle.None; //Disable the install button

        Cancel_Button.style.display = DisplayStyle.Flex;//Enable the cancel button + progress bar/button
        Downloading_Button.style.display = DisplayStyle.Flex;
    }

    public void Cancel_Button_Clicked()
    {
        AppsUpdater.AppsUpdaterr.CancelDownload();
        AppsUpdater.AppsUpdaterr.ZipToRemoveBasedOnAppIndexBecauseDownloadGotCanceled = currentlyUpdatingAppIndex;

        currentlyUpdatingAppIndex = 0;
    }

    public void UpdateDownloadingButtonProgress(float percentComplete)
    {//Decrease the margin of the download button, this way it grows in size. Which make it looks like a progress bar
        ProgressBar.style.marginRight = Length.Percent(100 - percentComplete); //Margin 100 means the progress bar isn't visible at all, Margin 0 means it's fully visible
        Downloading_Label.text = percentComplete + "%";
    }

    //stop downloading op true als cancel button clicked is


    #endregion



    #region functionality
    private string OpenFileDialog(string currentPath)
    {//This method  displays a Folder Dialog from the System.Windows.Forms dll
     //The currentPath string is returned when the folder chosen by the user wasn't usable. Most likely because the Jam54Launcher doesn't have admins perms, and therefor can't write to the folder the user selected
        using (var fbd = new System.Windows.Forms.FolderBrowserDialog()) //Create a new Folder Browser Dialog
        {
            System.Windows.Forms.DialogResult result = fbd.ShowDialog(); //Show the dialog

            if (result == System.Windows.Forms.DialogResult.OK && !string.IsNullOrWhiteSpace(fbd.SelectedPath)) //If the user selected a path that exists, proceed
            {
                try//We know the user may have selected a folder that needs admin perms to write to, so we check that with an try catch statement
                {
                    System.IO.File.WriteAllText(fbd.SelectedPath + @"\Jam54LauncherFiles", "uwu");//Try to write a temp file to see if we can write to the selected folder

                    if (System.IO.File.Exists(fbd.SelectedPath + @"\Jam54LauncherFiles"))
                    {//If we did manage to create the temp file, delete it
                        System.IO.File.Delete(fbd.SelectedPath + @"\Jam54LauncherFiles");
                    }

                    return fbd.SelectedPath.Replace("\\", "/") + "/Jam54LauncherFiles"; //Return the path selected by the user. Unity uses forward slashes, while this dll (System.Windows.Forms) uses windows' default backslashes. So convert it to forward slashes to be used in unity
                }
                catch (Exception e)//The user selected a folder that (most likely) needs admin perms to write to (could also be another error/exception)
                {
                    System.Windows.Forms.MessageBox.Show(e.Message + " Select another folder that doesn't require administrative privileges.");
                }
            }

            return currentPath; //Return the old path, since the path that the user selected can not be used
        }
    }
    #endregion



    #region animations
    //When the user hovers over the element, enable the background color to make it look like it's hovered over/selected
    private void SettingsBackgroundCircle_Over(MouseOverEvent evt)
    {
        SettingsBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor;
    }


    //When the user hovers over the element, enable the background color to make it look like it's hovered over/selected
    private void HomeBackgroundCircle_Over(MouseOverEvent evt)
    {
        HomeBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor;
    }


    //When the user's mouse leaves the element AKA/i.e. stops hovering over it make the background transparent
    private void SettingsBackgroundCircle_Out(MouseOutEvent evt)
    {
        if (!SettingsWindowSelected)
        {
            SettingsBackgroundCircle.style.backgroundColor = Color.clear; //only disable the background of the icon to make it look unselected, if the main menu isn't open
        }
    }


    //When the user's mouse leaves the element AKA/i.e. stops hovering over it make the background transparent
    private void HomeBackgroundCircle_Out(MouseOutEvent evt)
    {
        if (!MainWindowSelected)
        {
            HomeBackgroundCircle.style.backgroundColor = Color.clear; //only disable the background of the icon to make it look unselected, if the settings menu isn't open
        }
    }
    #endregion


}
