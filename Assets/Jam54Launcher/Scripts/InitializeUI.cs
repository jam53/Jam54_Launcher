using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;

public class InitializeUI : MonoBehaviour
{
    //UI Objects
    public VisualElement Image1, Image2, Image3;
    public Label VersionNumber, Path_Label;

    //Script variables


    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Programs_Unselected_Button' in the root visual element
        Image1 = rootVisualElement.Q<VisualElement>("Image1");
        Image2 = rootVisualElement.Q<VisualElement>("Image2");
        Image3 = rootVisualElement.Q<VisualElement>("Image3");
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
        //VersionAstroRun, VersionSmashAndFly, VersionStelexo, VersionAutoEditor, VersionDGCTimer, VersionImageSearcher, VersionIToW, VersionWToI
        Texture2D testtt = Image3.resolvedStyle.backgroundImage.texture;
        Image1.style.backgroundImage = testtt;
        Image1.style.backgroundImage = MakeGrayscale(testtt);
    }

    private Texture2D MakeGrayscale(Texture2D originalColored)
    {
        float r, g, b;
        float average;
        Color grayScale = Color.white;
        Texture2D neww = originalColored;

        for (int x = 0; x < neww.width; x++)
        {
            for (int y = 0; y < neww.height; y++)
            {
                r = neww.GetPixel(x, y).r;
                g = neww.GetPixel(x, y).g;
                b = neww.GetPixel(x, y).b;

                average = (r + g + b) / 3;

                grayScale.r = grayScale.g = grayScale.b = average;

                neww.SetPixel(x, y, grayScale);
            }
        }

        neww.Apply();
        System.Drawing.Bitmap;
    https://discord.com/channels/@me/511139666825838592/888860114159808552
        return neww;
    }
}
