package com.jam54.jam54_launcher.Animations;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This ComboBox class, transitions smoothly between the default/normal and hovered/selected colors of a Combobox
 */
public class ComboBoxColor extends ComboBox
{
    /**
     * @param values The items of the ComboBox
     * @param defaultColor The default/normal color
     * @param hoverColor The hovered color
     * @param selectColor The selected color
     */
    public ComboBoxColor(ObservableList<String> values, Color defaultColor, Color hoverColor, Color selectColor) {

        this.setItems(values);

        final ObjectProperty<Color> color = new SimpleObjectProperty<>(defaultColor);

        // String that represents the color above as a JavaFX CSS function:
        // -fx-body-color: rgb(r, g, b);
        // with r, g, b integers between 0 and 255
        final StringBinding cssColorSpec = Bindings.createStringBinding(() ->
        {
            color.get(); //Do not remove, otherwise the animation breaks
            if (this.isHover())
            {
                return "-fx-background-color: " + colorToString(color) + ";";
            }
            else if (this.isFocused())
            {
                return "-fx-background-color: " + colorToString(selectColor);
            }
            else
            {
                return "-fx-background-color: " + colorToString(defaultColor) + ";";
            }
        }, color);

        // bind the button's style property
        this.styleProperty().bind(cssColorSpec);

        final Timeline hoverIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, defaultColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, hoverColor)));

        final Timeline hoverOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, hoverColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, defaultColor)));

        final Timeline focusedColor = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, hoverColor)),
                new KeyFrame(Duration.millis(200), new KeyValue(color, selectColor)));

        this.setOnMouseEntered(event ->
        {
            if (!this.isFocused())
            {
                hoverIn.play();
            }
        });
        this.setOnMouseExited(event ->
        {
            if (!this.isFocused())
            {
                hoverOut.play();
            }
        });
        this.focusedProperty().addListener((observableValue, notFocused, t1) ->
        {
            if (notFocused)
            {
                hoverOut.play();
            }
        });
        this.setOnKeyPressed(e -> focusedColor.play());
        this.setOnMousePressed(e -> focusedColor.play());

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
