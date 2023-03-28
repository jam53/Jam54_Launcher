package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import com.jam54.jam54_launcher.Data.Loaders.ApplicationsLoader;
import com.jam54.jam54_launcher.Data.Loaders.OtherLoader;
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
        (new LoadInFonts()).loadFonts();

        Jam54LauncherModel model = new Jam54LauncherModel();

        ApplicationsLoader applicationsLoader = new ApplicationsLoader();

        model.setAllApplications(applicationsLoader.getApplicationInfos());
        model.setVisibleApplicationInfos(model.getAllApplications());
        model.filterAndSortVisibleApplicationInfos(0, false, 0, true, ""); //If we don't sort the application infos initially, they will appear in the wrong order upon startup until the user selects a different sort order. Since platform=0 (all platforms) and installedOnly=false, gamesOnly=true and searchText="" all games will be shown

        OtherLoader otherLoader = new OtherLoader();

        model.setSupportedLanguages(otherLoader.getSupportedLanguages());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"), SaveLoadManager.getResourceBundle());

        MainController controller = new MainController();
        controller.setModel(model);
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 1228, 754); //If we set for example, 300 as prefWidth inside the FXML. The we could make our window bigger and the hboxes, vboxes, flowplanes etc. would resize accordingly. But anything smaller than 300 after resizing. Would just cut of the side of the window. By setting both the prefWidth and prefHeight to 1 inside the FXML. Followed by choosing the correct widht/height inside Java. The window resizes correctly, even at smaller resolutions
        stage.setMinWidth(1228);
        stage.setMinHeight(785);
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