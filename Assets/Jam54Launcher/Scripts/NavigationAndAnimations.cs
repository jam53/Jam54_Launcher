using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UIElements;
using UnityEngine.UIElements.Experimental;

//This script handles all the animations when clicking on certain elements and makes it possible to navigate in the app.
//In other words, the logic behind clicking on a button in order to open a new tab and close the current one is handled here.
//This script may also call certain methods from order scripts, to execute an action
public class NavigationAndAnimations : MonoBehaviour
{
    //UI Objects

    private void OnEnable()
    {
        //Get the root visual element that contains all the objects we need
        VisualElement rootVisualElement = GetComponent<UIDocument>().rootVisualElement;


        #region Assign objects
        //Find the object of type 'Button' with the name 'Games_Button' in the root visual element
        #endregion


        #region Add corresponding methods to the elements
        #endregion
    }

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    private void Games_Selected_ButtonPressed()
    {

    }

    #region navigation
    //doe da gwn in een nieuw script alst teveel is
    #endregion

    #region animations

    #endregion


}
