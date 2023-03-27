package com.jam54.jam54_launcher.Animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This animation transitions smoothly between the default/normal and hovered/selected colors of a button
 * This class is used in Java code by writing `Button.setSkin(...);`
 */
public class ButtonColor extends ButtonSkin
{
    /**
     * @param control The ToggleButton to animate
     * @param defaultColor The default/normal color
     * @param hoverColor The hovered color
     * @param selectColor The selected color
     */
    public ButtonColor(Button control, Color defaultColor, Color hoverColor, Color selectColor) {
        super(control);

        final ObjectProperty<Color> color = new SimpleObjectProperty<>(defaultColor);

        // String that represents the color above as a JavaFX CSS function:
        // -fx-body-color: rgb(r, g, b);
        // with r, g, b integers between 0 and 255
        final StringBinding cssColorSpec = Bindings.createStringBinding(() ->
        {
            color.get(); //Do not remove, otherwise the animation breaks
            return "-fx-background-color: " + colorToString(color) + ";";
        }, color);

        // bind the button's style property
        control.styleProperty().bind(cssColorSpec);

        final Timeline hoverIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, defaultColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, hoverColor)));

        final Timeline hoverOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, hoverColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, defaultColor)));

        control.hoverProperty().addListener((observableValue, oldV, hovered) ->
        {
            if (hovered)
            {
                hoverIn.play();
            }
            else
            {
                hoverOut.play();
            }
        });
    }

    /**
     * Given a color, returns a string of the form "rgb(<red>, <green>, <blue>)" representing that color
     */
    private String colorToString(Color color)
    {
        return String.format("rgb(%d, %d, %d)", (int) (256 * color.getRed()), (int) (256 * color.getGreen()), (int) (256 * color.getBlue()));
    }

    /**
     * Given a color, returns a string of the form "rgb(<red>, <green>, <blue>)" representing that color
     */
    private String colorToString(ObjectProperty<Color> color)
    {
        return String.format("rgb(%d, %d, %d)", (int) (256 * color.get().getRed()), (int) (256 * color.get().getGreen()), (int) (256 * color.get().getBlue()));
    }
}
