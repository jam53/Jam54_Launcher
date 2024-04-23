package com.jam54.jam54_launcher.Windows.AvailableAppUpdates;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class RadialProgressBarWithIcon extends StackPane
{
    private final Image applicationImage;

    public RadialProgressBarWithIcon(Image applicationImage)
    {
        this.applicationImage = applicationImage;

        setProgress(0);
    }

    /**
     * Sets the progress of this node
     * @param progress The amount of progression as a double between 0 and 1.
     */
    public void setProgress(double progress)
    {
        this.getChildren().clear();

        if (progress == 0)
        {
            ImageView appIcon = createAppIcon(50, 50);
            this.getChildren().add(appIcon);
        }
        else
        {
            Circle outerCircle = new Circle(25);
            Arc arc = new Arc(0, 0, 25, 25, 90, -Math.toDegrees(progress * 2 * Math.PI));
            arc.setType(ArcType.ROUND);

            Group clippedOuterCircle = new Group();
            clippedOuterCircle.getChildren().add(outerCircle);
            clippedOuterCircle.getChildren().add(arc);

            Circle innerCircle = new Circle(22);

            ImageView appIcon = createAppIcon(29, 29);
            this.getChildren().addAll(clippedOuterCircle, innerCircle, appIcon);
        }
    }

    /**
     * Creates and returns an app icon with the provided width and height
     */
    private ImageView createAppIcon(int width, int height)
    {
        ImageView appIcon = new ImageView(applicationImage);
        appIcon.setFitWidth(width);
        appIcon.setFitHeight(height);

        Rectangle clipRounded = new Rectangle();
        clipRounded.setWidth(width);
        clipRounded.setHeight(height);
        clipRounded.setArcHeight(10);
        clipRounded.setArcWidth(10);

        appIcon.setClip(clipRounded);

        return appIcon;
    }
}
