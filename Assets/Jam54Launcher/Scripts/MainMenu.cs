using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;

public class MainMenu : MonoBehaviour
{
    public Button GamesButton;

    // Start is called before the first frame update
    void Start()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;

        //Find the object of type 'Button' with the name 'Games_Button' in the root visual element
        GamesButton = rootVisualElement.Q<Button>("Games_Button");

        #region Add corresponding methods to elements
        GamesButton.clicked += GamesButtonPressed;
        #endregion
    }

    private void GamesButtonPressed()
    {
        Debug.Log("games button pressed");
        GamesButton.style.display = DisplayStyle.Flex;
    }
}

/* Cheat sheet
 * Make visible/invisible button.style.display = DisplayStyle.~Flex/None~;
 */
