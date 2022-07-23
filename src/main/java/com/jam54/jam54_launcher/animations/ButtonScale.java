package com.jam54.jam54_launcher.animations;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.control.skin.ButtonSkin;
import javafx.util.Duration;

/**
 * This class is used as an `-fx-skin`, to animate controls when the user hovers over them
 */
public class ButtonScale extends ButtonSkin
{
    public ButtonScale(Button control) {
        super(control);

        final ScaleTransition fadeIn = new ScaleTransition(Duration.millis(100));
        fadeIn.setNode(control);
        fadeIn.setByX(0.15);
        fadeIn.setByY(0.15);
        control.setOnMouseEntered(e -> fadeIn.playFromStart());

        final ScaleTransition fadeOut = new ScaleTransition(Duration.millis(100));
        fadeOut.setNode(control);
        fadeOut.setToX(1);
        fadeOut.setToY(1);
        control.setOnMouseExited(e -> fadeOut.playFromStart());
    }

}
