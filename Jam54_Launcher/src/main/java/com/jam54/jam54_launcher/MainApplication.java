package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.Loaders.ApplicationsLoader;
import com.jam54.jam54_launcher.Data.Loaders.OtherLoader;
import com.jam54.jam54_launcher.Data.SaveLoad.ColorTheme;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application
{
    public static final float WINDOW_WIDTH = 1228;
    public static final float WINDOW_HEIGHT = 754;

    @Override
    public void start(Stage stage) throws IOException
    {
        (new LoadInFonts()).loadFonts();

        Jam54LauncherModel model = new Jam54LauncherModel();

        ApplicationsLoader applicationsLoader = new ApplicationsLoader();

        model.setAllApplications(applicationsLoader.getApplicationInfos());
        model.setVisibleApplicationInfos(model.getAllApplications());
        model.filterAndSortVisibleApplicationInfos(0, false, 0, false, ""); //If we don't sort the application infos initially, they will appear in the wrong order upon startup until the user selects a different sort order. Since platform=0 (all platforms) and installedOnly=false, gamesOnly=false and searchText="" all programs will be shown

        OtherLoader otherLoader = new OtherLoader();

        model.setSupportedLanguages(otherLoader.getSupportedLanguages());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"), SaveLoadManager.getResourceBundle());

        MainController controller = new MainController();
        controller.setModel(model);
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 1228, 754); // If we set for example 300 as prefWidth inside the FXML, we could make the window larger and the HBoxes, VBoxes, FlowPanes, etc. would resize accordingly. However, resizing the window to a width smaller than 300 would result in the content being cut off. By setting both prefWidth and prefHeight to 1 inside the FXML, and then specifying the desired width and height in Java, the window resizes correctly, even at resolutions smaller than 300.

        if (SaveLoadManager.getData().getColorTheme() == ColorTheme.DARK)
        {
            scene.getStylesheets().add(Main.class.getResource("css/mainDark.css").toString());
        }
        else
        {
            scene.getStylesheets().add(Main.class.getResource("css/mainLight.css").toString());
        }

        stage.setMinWidth(1228);
        stage.setMinHeight(754);
        stage.setTitle("Jam54 Launcher");
        stage.getIcons().add(new Image(Main.class.getResource("img/jam54Icon.png").toString()));
        stage.setScene(scene);
        stage.show();

        LauncherUpdater launcherUpdater = new LauncherUpdater(model);
        launcherUpdater.checkForUpdates();
    }

    public static void run(String[] args)
    {
        launch(args);
    }
}