package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.SaveLoad.ColorTheme;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.Properties;

/**
 * Most of the colors for our nodes are set in the stylesheets. However, to achieve smooth transitions/animations between the unselected/hovered/pressed states we used fx-skins. When doing that we have to pass the color as a HEX value to the skin. We aren't able to pass the color variable we defined inside the stylesheets.
 * That's where this class comes into play, given a string of a color variable defined in the stylesheet, it returns the corresponding hex color
 */
public class LoadCSSStyles
{
    static String darkCssFileName = "css/mainDark.css";
    static String lightCssFileName = "css/mainLight.css";

    /**
     * Given the name of a color variable, it will return the colo as a Color object
     * @param cssColorVariable the name of the color variable defined in the stylesheet, for example `-bg-main`
     * @return grabs the HEX color value from the stylesheet, and returns it as a Color object
     */
    public static Color getCSSColor(String cssColorVariable)
    {
        String cssFileName;

        if (SaveLoadManager.getData().getColorTheme() == ColorTheme.DARK)
        {
            cssFileName = darkCssFileName;
        }
        else
        {
            cssFileName = lightCssFileName;
        }

        // Load the CSS file into a Properties object
        Properties cssProperties = new Properties();
        try (InputStream inputStream = Main.class.getResourceAsStream(cssFileName))
        {
            cssProperties.load(inputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Retrieve the value of the specified color property
        return Color.web(cssProperties.getProperty(cssColorVariable).replace(";", ""));
    }
}
