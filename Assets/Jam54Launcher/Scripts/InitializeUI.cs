using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;

public class InitializeUI : MonoBehaviour
{
    //UI Objects
    public Label VersionNumber, Path_Label;

    //Script variables



    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Programs_Unselected_Button' in the root visual element
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
        //UpdateAppsImage();
    }


    public Texture2D MakeGrayscale(Texture2D originalColored)
    {
        Color32[] pixels = originalColored.GetPixels32();
        for (int x = 0; x < originalColored.width; x++)
        {
            for (int y = 0; y < originalColored.height; y++)
            {
                Color32 pixel = pixels[x + y * originalColored.width];
                int p = ((256 * 256 + pixel.r) * 256 + pixel.b) * 256 + pixel.g;
                int b = p % 256;
                p = Mathf.FloorToInt(p / 256);
                int g = p % 256;
                p = Mathf.FloorToInt(p / 256);
                int r = p % 256;
                float l = (0.2126f * r / 255f) + 0.7152f * (g / 255f) + 0.0722f * (b / 255f);
                Color c = new Color(l, l, l, 1);
                originalColored.SetPixel(x, y, c);
            }
        }
        originalColored.Apply(false);
        return originalColored;
    }
}
