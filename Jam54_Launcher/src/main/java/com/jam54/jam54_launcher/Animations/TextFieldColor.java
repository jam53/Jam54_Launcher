package com.jam54.jam54_launcher.Animations;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.sql.Time;

/**
 * This animation transitions smoothly between the default/normal and hovered/selected colors of a textfield
 * This class is used in Java code by writing `TextField.setSkin(...);`
 */
public class TextFieldColor extends TextFieldSkin
{
    /**
     * @param control The ToggleButton to animate
     * @param defaultColor The default/normal color
     * @param hoverColor The hovered color
     * @param selectColor The selected color
     */
    public TextFieldColor(TextField control, Color defaultColor, Color hoverColor, Color selectColor) {
        super(control);

        final ObjectProperty<Color> color = new SimpleObjectProperty<>(defaultColor);

        // String that represents the color above as a JavaFX CSS function:
        // -fx-body-color: rgb(r, g, b);
        // with r, g, b integers between 0 and 255
        final StringBinding cssColorSpec = Bindings.createStringBinding(() ->
        {
            color.get(); //Do not remove, otherwise the animation breaks
            if (control.isHover())
            {
                return "-fx-background-color: " + colorToString(color) + ";";
            }
            else if (control.isFocused())
            {
                return "-fx-background-color: " + colorToString(color) + ";";
            }
            else
            {
                return "-fx-background-color: " + colorToString(color) + ";";
            }
        }, color);

        // bind the button's style property
        control.styleProperty().bind(cssColorSpec);

        final Timeline hoverIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, defaultColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, hoverColor)));

        final Timeline hoverOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, hoverColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, defaultColor)));

        final Timeline focusedColor = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, hoverColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, selectColor)));

        control.setOnMouseEntered(event ->
        {
            if (!control.isFocused())
            {
                hoverIn.play();
            }
        });
        control.setOnMouseExited(event ->
        {
            if (!control.isFocused())
            {
                hoverOut.play();
            }
        });
        control.focusedProperty().addListener((observableValue, notFocused, t1) ->
        {
            if (notFocused)
            {
                hoverOut.play();
            }
        });
        control.setOnKeyPressed(e -> focusedColor.play());
        control.setOnMousePressed(e -> focusedColor.play());
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
