package com.jam54.jam54_launcher;

import javafx.scene.text.Font;

public class LoadInFonts
{
    public void loadFonts()
    {
        //region Lato
        Font.loadFont(Main.class.getResourceAsStream("fonts/Lato/Lato-Regular.ttf"), -1);
        Font.loadFont(Main.class.getResourceAsStream("fonts/Lato/Lato-Bold.ttf"), -1);
        //endregion

        //region NewRubrik
        Font.loadFont(Main.class.getResourceAsStream("fonts/NewRubrik/NewRubrik-Regular.otf"), -1);
        //endregion
    }
}
