package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * This class is used to create the InstallationLocation window, at the right side within the settings window
 */
public class InstallationLocationWindow extends VBox
{
    public InstallationLocationWindow()
    {
        this.getStyleClass().add("installationLocationWindow");

        Text title = new Text(SaveLoadManager.getTranslation("GamesProgramsInstallationPath"));
        title.setId("HighlightedSettingsTitle");

        Text description = new Text("%Choose a location for Games and Programs installs.");
        description.setId("NotHighlightedSettingsDescription");

        VBox folderPickerHolder = new VBox();
        folderPickerHolder.getStyleClass().add("folderPickerHolder");

        Text dataFolderSize = new Text(getFolderSize(SaveLoadManager.getData().getDataPath()));
        HBox dataFolderSizeHolder = new HBox(dataFolderSize);
        dataFolderSizeHolder.setId("dataFolderSizeHolder");

        Button picker = new Button();
        picker.setSkin(new ButtonColor(picker, Color.web("#242424"), Color.web("#3E3E3E"), Color.web("#3E3E3E")));

        HBox pickerContainer = new HBox();
        pickerContainer.setId("pickerContainer");
        Text folderPath = new Text(SaveLoadManager.getData().getDataPath().toString());
        pickerContainer.getChildren().addAll(folderPath, new HBox(new HBox()));

        picker.setGraphic(pickerContainer);

        picker.setOnAction(e -> chooseNewDataPathDirectory(SaveLoadManager.getData().getDataPath(), folderPath));

        folderPickerHolder.getChildren().addAll(dataFolderSizeHolder, picker);

        this.getChildren().addAll(title, description, folderPickerHolder);
    }

    /**
     * Opens a folder picker, checks if the selected folder exists.
     * If so it saves the new datapath location and updates the text of the button that called this method + copies the files over and deletes the old dataPath folder
     */
    private void chooseNewDataPathDirectory(Path currentDataPath, Text folderPath)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(currentDataPath.toFile());

        File selectedFolder = directoryChooser.showDialog(this.getScene() != null ? this.getScene().getWindow() : new Scene(this).getWindow());

        if (selectedFolder != null && selectedFolder.isDirectory())
        {
            try
            {
                Path newDataPath = Path.of(selectedFolder.toString(), "Jam54Launcher");

                Files.move(currentDataPath, newDataPath, StandardCopyOption.REPLACE_EXISTING);
                SaveLoadManager.getData().setDataPath(newDataPath);
                folderPath.setText(newDataPath.toString());
            }
            catch (IOException e)
            {
                ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CloseAppsAndDownloads") + " " + e.getMessage());
                errorMessage.show();
            }
        }
    }

    /**
     * Given a path to a folder, returns the size of that folder as either "", "X MB" or "X GB"
     */
    private String getFolderSize(Path path)
    {
        long sizeInBytes = FileUtils.sizeOfDirectory(path.toFile());

        sizeInBytes = sizeInBytes / 1024 / 1024; //Get the size in megabytes instead of bytes

        if (sizeInBytes <= 1) //If the directory is empty
        {
            return "";
        }
        else if (sizeInBytes > 1024) //If the directory is bigger than 1 gigabyte
        {
            sizeInBytes = sizeInBytes / 1024; //Convert it to gigabytes
            return sizeInBytes + " GB";
        }
        else
        {
            return sizeInBytes + " MB";
        }
    }
}
