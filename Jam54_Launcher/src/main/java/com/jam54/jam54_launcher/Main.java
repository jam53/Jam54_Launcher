package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Jam54LauncherModel model = new Jam54LauncherModel();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"), SaveLoadManager.getResourceBundle());

        MainController controller = new MainController();
        controller.setModel(model);
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Jam54 Launcher");
        stage.getIcons().add(new Image(Main.class.getResource("img/jam54Icon.png").toString()));
        stage.setScene(scene);
        stage.show();

        LauncherUpdater launcherUpdater = new LauncherUpdater(model);
        launcherUpdater.checkForUpdates();
    }

    public static void main(String[] args)
    {
        launch();
    }
}