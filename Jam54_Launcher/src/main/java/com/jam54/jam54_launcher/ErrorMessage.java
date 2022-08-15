package com.jam54.jam54_launcher;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class is used to display an error message to the user in a new window
 */
public class ErrorMessage extends BorderPane
{
    private final boolean closeApplication; //When we create a new `ErrorMessage` object, we can choose whether or not the whole launcher needs to be closed or just the ErrorMessage window when the user clicks on OK.
    private final Stage stage;

    public ErrorMessage(boolean closeApplication, String errorMessage)
    {
        this.closeApplication = closeApplication;

        getStylesheets().add(Main.class.getResource("css/ErrorMessage.css").toString());

        Label exclamationMark = new Label("!");
        setAlignment(exclamationMark, Pos.CENTER);
        setLeft(exclamationMark);

        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(new Text(errorMessage));
        setCenter(textFlow);

        Button ok = new Button("%OK");
        ok.setOnAction(this::handle);
        setAlignment(ok, Pos.CENTER);
        setBottom(ok);


        Scene scene = new Scene(this);

        stage = new Stage();
        stage.setTitle("%Error");
        scene.setFill(Color.TRANSPARENT); //Because of the styling defined inside the CSS, the corners of our window will be rounded. If we dont add this line however, there will be a white background behind the rounded corners, instead of them being transparent.
        stage.initStyle(StageStyle.TRANSPARENT); //This makes the bar (with minimize, maximize and close) above our window invisible
        stage.initModality(Modality.APPLICATION_MODAL); //This makes it so the user has to close the error message window first, before they can interact with the launcher again
        stage.setAlwaysOnTop(true); //This makes it so the error message is displayed above all other windows of our program
        stage.getIcons().add(new Image(Main.class.getResource("img/jam54Icon.png").toString()));
        stage.setScene(scene);
    }

    public void show()
    {
        stage.show();
    }

    private void handle(ActionEvent actionEvent)
    {
        if (closeApplication)
        {
            Platform.exit();
        }
        else
        {
            stage.close();
        }
    }
}
