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
    public Button Programs_Unselected_Button, Games_Unselected_Button, InstallLocation_Button, Language_Button, Library_Button, Store_Button;
    public VisualElement Games, Programs, SettingsBackgroundCircle, Settings, HomeBackgroundCircle, InstallLocationPanel, LanguagePanel, Discord, YouTube, PlayStore, ProductPage;
    public VisualElement AppOptions1, AppOptions2, AppOptions3, AppOptions4, AppOptions5, AppOptions6, AppOptions7, AppOptions8, OptionsHolder, OptionsOutsideClicksDetector;
    public VisualElement ProductImage, Android_Image, Windows_Image, PathBackground;
    public Label AppTitle_Label, LatestUpdateDate1_Label, ReleaseDateDate2_Label, Description_Label, VersionNumber;

    //Script variables
    private bool LastWindowPrograms; //true means 'programs' is open - false means 'games' is open; on the 'main menu'
    private Color SideBarIconsDefaultColor;
    private bool MainWindowSelected; //if 'MainWindowSelected' and 'SettingsWindow' selected are both false
    private bool SettingsWindowSelected; //it means that the product page is open
    public Texture2D InstallLanguageButtonBackground;
    private int CurrentAppIndex;
    public AppsInfo AstroRun, SmashAndFly, Stelexo, AutoEditor, DGCTimer, ImageSearcher, IToW, WToI;

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
        OptionsOutsideClicksDetector = rootVisualElement.Q<VisualElement>("OptionsOutsideClicksDetector");
        ProductImage = rootVisualElement.Q<VisualElement>("ProductImage");
        Android_Image = rootVisualElement.Q<VisualElement>("Android_Image");
        Windows_Image = rootVisualElement.Q<VisualElement>("Windows_Image");
        PathBackground = rootVisualElement.Q<VisualElement>("PathBackground");
        AppTitle_Label = rootVisualElement.Q<Label>("AppTitle_Label");
        LatestUpdateDate1_Label = rootVisualElement.Q<Label>("LatestUpdateDate1_Label");
        ReleaseDateDate2_Label = rootVisualElement.Q<Label>("ReleaseDateDate2_Label");
        Description_Label = rootVisualElement.Q<Label>("Description_Label");
        VersionNumber = rootVisualElement.Q<Label>("VersionNumber");
        #endregion


        #region Add corresponding methods to the elements
        Programs_Unselected_Button.clicked += Programs_Unselected_Button_Clicked;
        Games_Unselected_Button.clicked += Games_Unselected_Button_Clicked;
        InstallLocation_Button.clicked += InstallLocation_Button_Clicked;
        Language_Button.clicked += Language_Button_Clicked;
        Library_Button.clicked += Library_Button_Clicked;
        Store_Button.clicked += Store_Button_Clicked;
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
        #endregion
    }

    // Start is called before the first frame update
    void Start()
    {
        SideBarIconsDefaultColor.r = 0.1411765f; SideBarIconsDefaultColor.g = 0.1490196f; SideBarIconsDefaultColor.b = 0.2313726f; SideBarIconsDefaultColor.a = 1f;
        MainWindowSelected = true;
        VersionNumber.text = Application.version;
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

    private Vector2 RepositionOptionsMenu(float mouseX, float mouseY)
    {
        //Als we hebben geklikt linksboven in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        if (mouseX <= Screen.width / 2 && mouseY <= Screen.height / 2)
        {
            return new Vector2(mouseX, mouseY);
        }

        //Als we hebben geklikt rechtsboven in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX >= Screen.width / 2 && mouseY <= Screen.height / 2)
        {
            return new Vector2(mouseX - OptionsHolder.resolvedStyle.width, mouseY);
        }

        //Als we hebben geklikt linksonder in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX <= Screen.width / 2 && mouseY >= Screen.height / 2)
        {
            return new Vector2(mouseX, mouseY - OptionsHolder.resolvedStyle.height);
        }

        //Als we hebben geklikt rechtsonder in het scherm, dan positioneren we het settings menu zodanig dat het niet buiten ons canvas/window wordt gerenderd
        else if (mouseX >= Screen.width / 2 && mouseY >= Screen.height / 2)
        {
            return new Vector2(mouseX - OptionsHolder.resolvedStyle.width, mouseY - OptionsHolder.resolvedStyle.height);
        }

        else
        {
            Debug.LogError("Couldn't return coordinates");
            return new Vector2(0, 0);
        }
    }

    private void AppOptions1_Clicked (MouseDownEvent evt)
    {
        CurrentAppIndex = 1;//AstroRun

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions1.worldBound.x, AppOptions1.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions1.worldBound.x, AppOptions1.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions2_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 2;//Smash&Fly

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions2.worldBound.x, AppOptions2.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions2.worldBound.x, AppOptions2.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions3_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 3;//Stelexo

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions3.worldBound.x, AppOptions3.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions3.worldBound.x, AppOptions3.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions4_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 4;//AutoEditor

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions4.worldBound.x, AppOptions4.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions4.worldBound.x, AppOptions4.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions5_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 5;//DGCTimer

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions5.worldBound.x, AppOptions5.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions5.worldBound.x, AppOptions5.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions6_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 6;//ImageSearcher

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions6.worldBound.x, AppOptions6.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions6.worldBound.x, AppOptions6.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions7_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 7;//IToW

        OptionsHolder.style.left = RepositionOptionsMenu(AppOptions7.worldBound.x, AppOptions7.worldBound.y).x;
        OptionsHolder.style.top = RepositionOptionsMenu(AppOptions7.worldBound.x, AppOptions7.worldBound.y).y;

        OptionsOutsideClicksDetector.style.display = DisplayStyle.Flex;//This makes it so the options menu is visibile. The options menu is a child of this object
    }
    private void AppOptions8_Clicked(MouseDownEvent evt)
    {
        CurrentAppIndex = 8;//WtoI

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

        Games.style.display = DisplayStyle.None; //Close the Games And Programs Windows
        Programs.style.display = DisplayStyle.None;

        ProductPage.style.display = DisplayStyle.Flex; //Open the ProductPage

        HomeBackgroundCircle.style.backgroundColor = Color.clear;

        switch (CurrentAppIndex)
        {
            case 1:
                AppTitle_Label.text = AstroRun.Title;
                ProductImage.style.backgroundImage = AstroRun.Image;
                LatestUpdateDate1_Label.text = AstroRun.LatestUpdate;
                ReleaseDateDate2_Label.text = AstroRun.ReleaseDate;

                if (!AstroRun.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!AstroRun.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = AstroRun.description;

                break;


            case 2:
                AppTitle_Label.text = SmashAndFly.Title;
                ProductImage.style.backgroundImage = SmashAndFly.Image;
                LatestUpdateDate1_Label.text = SmashAndFly.LatestUpdate;
                ReleaseDateDate2_Label.text = SmashAndFly.ReleaseDate;

                if (!SmashAndFly.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!SmashAndFly.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = SmashAndFly.description;

                break;


            case 3:
                AppTitle_Label.text = Stelexo.Title;
                ProductImage.style.backgroundImage = Stelexo.Image;
                LatestUpdateDate1_Label.text = Stelexo.LatestUpdate;
                ReleaseDateDate2_Label.text = Stelexo.ReleaseDate;

                if (!Stelexo.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!Stelexo.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = Stelexo.description;

                break;


            case 4:
                AppTitle_Label.text = AutoEditor.Title;
                ProductImage.style.backgroundImage = AutoEditor.Image;
                LatestUpdateDate1_Label.text = AutoEditor.LatestUpdate;
                ReleaseDateDate2_Label.text = AutoEditor.ReleaseDate;

                if (!AutoEditor.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!AutoEditor.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = AutoEditor.description;

                break;


            case 5:
                AppTitle_Label.text = DGCTimer.Title;
                ProductImage.style.backgroundImage = DGCTimer.Image;
                LatestUpdateDate1_Label.text = DGCTimer.LatestUpdate;
                ReleaseDateDate2_Label.text = DGCTimer.ReleaseDate;

                if (!DGCTimer.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!DGCTimer.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = DGCTimer.description;

                break;


            case 6:
                AppTitle_Label.text = ImageSearcher.Title;
                ProductImage.style.backgroundImage = ImageSearcher.Image;
                ProductImage.style.backgroundColor = ImageSearcher.BackgroundColor;
                LatestUpdateDate1_Label.text = ImageSearcher.LatestUpdate;
                ReleaseDateDate2_Label.text = ImageSearcher.ReleaseDate;

                if (!ImageSearcher.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!ImageSearcher.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = ImageSearcher.description;

                break;


            case 7:
                AppTitle_Label.text = IToW.Title;
                ProductImage.style.backgroundImage = IToW.Image;
                LatestUpdateDate1_Label.text = IToW.LatestUpdate;
                ReleaseDateDate2_Label.text = IToW.ReleaseDate;

                if (!IToW.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!IToW.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = IToW.description;

                break;


            case 8:
                AppTitle_Label.text = WToI.Title;
                ProductImage.style.backgroundImage = WToI.Image;
                LatestUpdateDate1_Label.text = WToI.LatestUpdate;
                ReleaseDateDate2_Label.text = WToI.ReleaseDate;

                if (!WToI.Android)
                {
                    Android_Image.style.display = DisplayStyle.None;
                }

                if (!WToI.Windows)
                {
                    Windows_Image.style.display = DisplayStyle.None;
                }

                Description_Label.text = WToI.description;

                break;

            default:
                Debug.LogError("Couldn't open Product page correctly - from OptionsMenu");
                break;
        }

    }

    public void PathBackground_Clicked(MouseDownEvent evt)
    {//This opens a windows explorer window, to select a path
        using (var fbd = new System.Windows.Forms.FolderBrowserDialog())
        {
            System.Windows.Forms.DialogResult result = fbd.ShowDialog();

            if (result == System.Windows.Forms.DialogResult.OK && !string.IsNullOrWhiteSpace(fbd.SelectedPath))
            {
                print(fbd.SelectedPath);
            }
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
