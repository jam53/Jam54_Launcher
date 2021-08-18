using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;

public class MainMenu : MonoBehaviour
{
    //Objects
    public Button Games_Button;
    public ScrollView GamesPrograms_ScrollView;

    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;

        //Find the object of type 'Button' with the name 'Games_Button' in the root visual element
        Games_Button = rootVisualElement.Q<Button>("Games_Button");

        GamesPrograms_ScrollView = rootVisualElement.Q<ScrollView>("GamesPrograms_ScrollView");

        #region Add corresponding methods to the elements
        Games_Button.clicked += GamesButtonPressed;
        #endregion
    }

    private void Start()
    {
        GamesPrograms_ScrollView.touchScrollBehavior = ScrollView.TouchScrollBehavior.Elastic;
        Debug.Log(GamesPrograms_ScrollView.childCount);
    }

    private void GamesButtonPressed()
    {
        Debug.Log("games button pressed");
        GamesPrograms_ScrollView.Clear();
        for (int i = 0; i < 20; i++)
        {
            GamesPrograms_ScrollView.Add(Games_Button);

        }
        Games_Button.style.display = DisplayStyle.Flex;
    }
}

/* Cheat sheet
 * Make visible/invisible button.style.display = DisplayStyle.~Flex/None~;
 */
