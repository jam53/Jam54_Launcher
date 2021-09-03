using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;
using UnityEngine.UIElements.Experimental;

public class MainMenu : MonoBehaviour
{
    //Objects
    public Button Games_Button;
    public VisualElement AppHolder;

    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;

        //Find the object of type 'Button' with the name 'Games_Button' in the root visual element
        Games_Button = rootVisualElement.Q<Button>("Games_Button");
        AppHolder = rootVisualElement.Q<VisualElement>("AppHolder");

        #region Add corresponding methods to the elements
        Games_Button.clicked += GamesButtonPressed;

        AppHolder.RegisterCallback<MouseDownEvent>(test);//MouseOverEvent(hover), MouseOutEvent(mous moves away from element)


        #endregion
    }

    private void Awake()
    {
        QualitySettings.vSyncCount = 0;
        Application.targetFrameRate = 30;
    }

    private void test(MouseDownEvent evt)
    {
        print("it works");
        //builden en // kijken als de lettertypes enz wel groot genoeg zijn.
            // tekst op settings menu is sws te klein, en mss bij main menu en product page ook wa groter
            //Desnoods target resolution veranderen, zodat het groter/kleiner wordt
            //ook naar rainway kijken, hoe groot daar de letterypes izjn
    }


private void GamesButtonPressed()
    {
        Debug.Log("games button pressed");
        Games_Button.experimental.animation.Start(25f, 200f, 3000, (b, val) =>
        {
            b.style.height = val;
        }).Ease(Easing.OutBounce);
            //als je op het fototje zelf klikt op main menu, start je de game
            //druk je op de tekst, dan opent de store page

        

    }
}

/* Cheat sheet
 * Make visible/invisible button.style.display = DisplayStyle.~Flex/None~;
 * https://docs.unity3d.com/Packages/com.unity.ui@1.0/api/UnityEngine.UIElements.Experimental.ITransitionAnimations.html
 * //Games_Button.experimental.animation.Start(25f, 200f, 3000, (b, val) =>
        //{
        //    b.style.height = val;
        //}).Ease(Easing.OutBounce);
 */
