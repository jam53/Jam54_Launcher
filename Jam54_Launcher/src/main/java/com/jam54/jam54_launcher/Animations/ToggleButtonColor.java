package com.jam54.jam54_launcher.Animations;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This animation transitions smoothly between the default/normal and hovered/selected colors of a toggle button
 * This class is used in Java code by writing `ToggleButton.setSkin(...);`
 */
public class ToggleButtonColor extends ToggleButtonSkin
{
    /**
     * @param control The ToggleButton to animate
     * @param defaultColor The default/normal color
     * @param hoverColor1 The first color of the hovered/selected gradient
     * @param hoverColor2 The second color of the hovered/selected gradient
     */
    public ToggleButtonColor(ToggleButton control, Color defaultColor, Color hoverColor1, Color hoverColor2) {
        super(control);

        final ObjectProperty<Color> color1 = new SimpleObjectProperty<>(defaultColor);
        final ObjectProperty<Color> color2 = new SimpleObjectProperty<>(defaultColor);

        // String that represents the color above as a JavaFX CSS function:
        // -fx-body-color: rgb(r, g, b);
        // with r, g, b integers between 0 and 255
        final StringBinding cssColorSpec = Bindings.createStringBinding(() ->
        {
            color1.get(); //Do not remove, otherwise the animation breaks
            if (control.isHover())
            {
                return "-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, " + colorToString(color1) + ", " + colorToString(color2) + ");";
            }
            else if (control.isSelected())
            {
                return "-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, " + colorToString(hoverColor1) + ", " + colorToString(hoverColor2) + ");";
            }
            else
            {
                return "-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, " + colorToString(defaultColor) + ", " + colorToString(defaultColor) + ");";
            }
        }, color1);

        // bind the button's style property
        control.styleProperty().bind(cssColorSpec);

        final Timeline hoverIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color1, defaultColor)),
                new KeyFrame(Duration.ZERO, new KeyValue(color2, defaultColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color1, hoverColor1)),
                new KeyFrame(Duration.millis(200), new KeyValue(color2, hoverColor2)));

        final Timeline hoverOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color1, hoverColor1)),
                new KeyFrame(Duration.ZERO, new KeyValue(color2, hoverColor2)),
                new KeyFrame(Duration.millis(200), new KeyValue(color1, defaultColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color2, defaultColor)));

        control.setOnMouseEntered(event ->
        {
            if (!control.isSelected())
            {
                hoverIn.play();
            }
        });
        control.setOnMouseExited(event ->
        {
            hoverOut.play();
        });
        control.selectedProperty().addListener((observableValue, notSelected, t1) ->
        {
            if (notSelected)
            {
                hoverOut.play();
            }
        });

        hoverIn.play(); //Dit runt de eerste keer in het begin, zodat de geselecteerde toggle de geselecteerde kleur krijgt
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
