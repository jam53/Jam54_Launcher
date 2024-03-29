package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Animations.ButtonColor;
import com.jam54.jam54_launcher.Data.Jam54LauncherModel;
import com.jam54.jam54_launcher.ErrorMessage;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.LoadCSSStyles;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import org.apache.commons.io.IOExceptionList;
import org.apache.commons.io.IOIndexedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * This class is used to create the InstallationLocation window, at the right side within the settings window
 */
public class InstallationLocationWindow extends VBox implements InvalidationListener
{
    private final Text dataFolderSize;
    private Jam54LauncherModel model;

    public InstallationLocationWindow()
    {
        this.getStyleClass().add("installationLocationWindow");

        Text title = new Text(SaveLoadManager.getTranslation("GamesProgramsInstallationPath"));
        title.setId("HighlightedSettingsTitle");

        Text description = new Text(SaveLoadManager.getTranslation("ChooseGamesProgramsInstallLocation"));
        description.setId("NotHighlightedSettingsDescription");

        VBox folderPickerHolder = new VBox();
        folderPickerHolder.getStyleClass().add("folderPickerHolder");

        dataFolderSize = new Text(getFolderSize(SaveLoadManager.getData().getDataPath()));
        HBox dataFolderSizeHolder = new HBox(dataFolderSize);
        dataFolderSizeHolder.setId("dataFolderSizeHolder");

        Button picker = new Button();
        picker.setSkin(new ButtonColor(picker, LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-bg-selected"), LoadCSSStyles.getCSSColor("-bg-selected")));

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
        if (model.getUpdatingApp() != null)
        {//This means an installation or updating of an app is ongoing
            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForInstall"), model.getApp(model.getUpdatingApp()).name()) + " " + SaveLoadManager.getTranslation("CancelInstall"));
            errorMessage.show();
            return;
        }
        else if (model.getLastValidatingApp() != null)
        {//This means a file validation of an app is ongoing
            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForValidationProcedure"), model.getApp(model.getUpdatingApp()).name()) + " " + SaveLoadManager.getTranslation("CancelUninstall"));
            errorMessage.show();
            return;
        }
        else if (model.getLastRemovingApp() != null)
        {//This means a file validation of an app is ongoing
            ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("KindlyHoldForUninstall"), model.getApp(model.getUpdatingApp()).name()) + " " + SaveLoadManager.getTranslation("CancelValidationProcedure"));
            errorMessage.show();
            return;
        }

        folderPath.setText(SaveLoadManager.getTranslation("MovingFiles"));

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(currentDataPath.toFile());

        File selectedFolder = directoryChooser.showDialog(this.getScene() != null ? this.getScene().getWindow() : new Scene(this).getWindow());

        if (selectedFolder != null && selectedFolder.isDirectory())
        {
            if (!Files.isWritable(selectedFolder.toPath()) || !Files.isWritable(currentDataPath))
            {
                folderPath.setText(currentDataPath.toString());
                ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("InsufficientPrivileges"), SaveLoadManager.getData().getDataPath()));
                errorMessage.show();
            }
            else
            {
                Path newDataPath = Path.of(selectedFolder.toString(), "Jam54Launcher");
                newDataPath = Files.exists(newDataPath) ? Path.of(newDataPath.toString(), UUID.randomUUID().toString()) : newDataPath; //If there already exists a subfolder with the name "Jam54Launcher", create a new one within that subfolder with a "random" name
                try
                {
                    FileUtils.moveDirectory(currentDataPath.toFile(), newDataPath.toFile());
                    SaveLoadManager.getData().setDataPath(newDataPath);
                    folderPath.setText(newDataPath.toString());
                }
                catch (IOExceptionList e)
                {
                    SaveLoadManager.getData().setDataPath(newDataPath);
                    folderPath.setText(newDataPath.toString());
                    ErrorMessage errorMessage = new ErrorMessage(false, MessageFormat.format(SaveLoadManager.getTranslation("NotAllOldFilesDeleted"), e.getMessage()));
                    errorMessage.show();
                }
                catch (IOException e)
                {
                    folderPath.setText(currentDataPath.toString());

                    if (e.getMessage().startsWith("Cannot move directory: "))
                    {//If the error message is: "Cannot move directory: " + srcDir + " to a subdirectory of itself: " + destDir
                        ErrorMessage errorMessage = new ErrorMessage(false, e.getMessage());
                        errorMessage.show();
                    }
                    else
                    {
                        ErrorMessage errorMessage = new ErrorMessage(false, SaveLoadManager.getTranslation("CloseAppsAndDownloads") + " " + e.getMessage());
                        errorMessage.show();
                    }
                }
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

    public void setModel(Jam54LauncherModel model)
    {
        this.model = model;
        model.addListener(this);
    }

    /**
     * When the model changes/get invalidated, it might be because an app was installed/removed/updated. In that case we ant to update the text of dataFolderSize
     */
    @Override
    public void invalidated(Observable observable)
    {
        dataFolderSize.setText(getFolderSize(SaveLoadManager.getData().getDataPath()));
    }
}
