using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;
using UnityEngine.UIElements.Experimental;

//This script handles all the animations when clicking on certain elements and makes it possible to navigate in the app.
//In other words, the logic behind clicking on a button in order to open a new tab and close the current one is handled here.
//This script may also call certain methods from order scripts, to execute an action
public class Navigation : MonoBehaviour
{
    //UI Objects
    public Button Programs_Unselected_Button, Games_Unselected_Button, InstallLocation_Button, Language_Button, Library_Button;
    public VisualElement Games, Programs, SettingsBackgroundCircle, Settings, HomeBackgroundCircle, InstallLocationPanel, LanguagePanel, Discord, YouTube, PlayStore, ProductPage;

    //Script variables
    private bool LastWindowPrograms; //true means 'programs' is open - false means 'games' is open; on the 'main menu'
    private Color SideBarIconsDefaultColor;
    private bool MainWindowSelected; //if 'MainWindowSelected' and 'SettingsWindow' selected are both false
    private bool SettingsWindowSelected; //it means that the product page is open
    public Texture2D InstallLanguageButtonBackground;

    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Programs_Unselected_Button' in the root visual element
        Programs_Unselected_Button = rootVisualElement.Q<Button>("Programs_Unselected_Button");
        Games_Unselected_Button = rootVisualElement.Q<Button>("Games_Unselected_Button");
        InstallLocation_Button = rootVisualElement.Q<Button>("InstallLocation_Button");
        Language_Button = rootVisualElement.Q<Button>("Language_Button");
        Library_Button = rootVisualElement.Q<Button>("Library_Button");
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

        #endregion


        #region Add corresponding methods to the elements
        Programs_Unselected_Button.clicked += Programs_Unselected_Button_Clicked;
        Games_Unselected_Button.clicked += Games_Unselected_Button_Clicked;
        InstallLocation_Button.clicked += InstallLocation_Button_Clicked;
        Language_Button.clicked += Language_Button_Clicked;
        Library_Button.clicked += Library_Button_Clicked;
        SettingsBackgroundCircle.RegisterCallback<MouseDownEvent>(SettingsBackgroundCircle_Clicked);
        HomeBackgroundCircle.RegisterCallback<MouseDownEvent>(HomeBackgroundCircle_Clicked);
        SettingsBackgroundCircle.RegisterCallback<MouseOverEvent>(SettingsBackgroundCircle_Over);
        HomeBackgroundCircle.RegisterCallback<MouseOverEvent>(HomeBackgroundCircle_Over);
        SettingsBackgroundCircle.RegisterCallback<MouseOutEvent>(SettingsBackgroundCircle_Out);
        HomeBackgroundCircle.RegisterCallback<MouseOutEvent>(HomeBackgroundCircle_Out);
        Discord.RegisterCallback<MouseDownEvent>(Discord_Clicked);
        YouTube.RegisterCallback<MouseDownEvent>(YouTube_Clicked);
        PlayStore.RegisterCallback<MouseDownEvent>(PlayStore_Clicked);
        #endregion
    }

    // Start is called before the first frame update
    void Start()
    {
        SideBarIconsDefaultColor.r = 0.1411765f; SideBarIconsDefaultColor.g = 0.1490196f; SideBarIconsDefaultColor.b = 0.2313726f; SideBarIconsDefaultColor.a = 1f;
        MainWindowSelected = true;
    }

    // Update is called once per frame
    void Update()
    {

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
        if (!LastWindowPrograms)
        {
            Games.style.display = DisplayStyle.None;
            Settings.style.display = DisplayStyle.Flex;
        }

        else if (LastWindowPrograms)
        {
            Programs.style.display = DisplayStyle.None;
            Settings.style.display = DisplayStyle.Flex;
        }

        ProductPage.style.display = DisplayStyle.None;

        SettingsBackgroundCircle.style.backgroundColor = SideBarIconsDefaultColor; //'Enable' the background so it looks selected
        HomeBackgroundCircle.style.backgroundColor = Color.clear; //Make the background transparent so it looks unselected

        MainWindowSelected = false; //we do this to know whether or not the background of the icons in the sidebar can be disabled or not
        SettingsWindowSelected = true; //if the current window that's active is 'settings' for example, the background of the settingsicon should remain there
    }


    //Clicking on this button should bring the user back to either the games/programs page. Depending on where they left off
    private void HomeBackgroundCircle_Clicked(MouseDownEvent evt)
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
