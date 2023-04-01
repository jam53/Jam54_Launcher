package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Animations.ComboBoxColor;
import com.jam54.jam54_launcher.Animations.ToggleButtonNotGradientColor;
import com.jam54.jam54_launcher.Data.SaveLoad.ColorTheme;
import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.LoadCSSStyles;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class is used to create the languages window, at the right side within the settings window
 */
public class AppearanceWindow extends VBox
{
    private ArrayList<String> languages;
    private HashMap<String, Locale> mapDisplayNameToLocale;

    private boolean themeRestartAlertHasBeenShown;
    private boolean langaugeRestartAlertHasBeenShown;

    public AppearanceWindow(ArrayList<Locale> supportedLanguages)
    {
        this.getStyleClass().add("appearanceWindow");

        Text titleTheme = new Text("%Theme");
        titleTheme.setId("HighlightedSettingsTitle");

        Text descriptionTheme = new Text("%Customize the appearance of the Jam54 Launcher.");
        descriptionTheme.setId("NotHighlightedSettingsDescription");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        }); //This makes it so that there always has to be at least one toggle selected

        ToggleButton darkToggleButton = new ToggleButton();
        darkToggleButton.getStyleClass().add("appearanceWindowToggleButton");
        HBox darkToggleIndicator = new HBox(new HBox());
        Text darkToggleButtonText = new Text("%Dark");
        darkToggleButton.setGraphic(new HBox(darkToggleIndicator, darkToggleButtonText));

        ToggleButton lightToggleButton = new ToggleButton();
        lightToggleButton.getStyleClass().add("appearanceWindowToggleButton");
        HBox lightToggleIndicator = new HBox(new HBox());
        Text lightToggleButtonText = new Text("%Light");
        lightToggleButton.setGraphic(new HBox(lightToggleIndicator, lightToggleButtonText));

        darkToggleButton.setOnAction(e ->
        {
            SaveLoadManager.getData().setColorTheme(ColorTheme.DARK);
            if (!themeRestartAlertHasBeenShown)
            {
                themeRestartAlertHasBeenShown = true;
                showRestartRequiredAlert();
            }
        });
        lightToggleButton.setOnAction(e ->
        {
            SaveLoadManager.getData().setColorTheme(ColorTheme.LIGHT);
            if (!themeRestartAlertHasBeenShown)
            {
                themeRestartAlertHasBeenShown = true;
                showRestartRequiredAlert();
            }
        });

        darkToggleButton.setToggleGroup(toggleGroup);
        lightToggleButton.setToggleGroup(toggleGroup);

        darkToggleButton.setTooltip(new Tooltip(SaveLoadManager.getTranslation("RestartRequiredForEffect")));
        lightToggleButton.setTooltip(new Tooltip(SaveLoadManager.getTranslation("RestartRequiredForEffect")));

        toggleGroup.selectToggle(SaveLoadManager.getData().getColorTheme() == ColorTheme.DARK ? darkToggleButton : lightToggleButton);


        Text titleLanguage = new Text(SaveLoadManager.getTranslation("Language"));
        titleLanguage.setId("HighlightedSettingsTitle");

        Text descriptionLanguage = new Text("%Select a display language.");
        descriptionLanguage.setId("NotHighlightedSettingsDescription");

        languages = new ArrayList<>();
        mapDisplayNameToLocale = new HashMap<>();

        for (Locale locale : supportedLanguages)
        {
            languages.add(getDisplayName(locale));
            mapDisplayNameToLocale.put(getDisplayName(locale), locale);
        }
        languages.sort(String::compareTo);

        ComboBox<String> languagePicker = new ComboBoxColor(FXCollections.observableArrayList(languages), LoadCSSStyles.getCSSColor("-bg-foreground"), LoadCSSStyles.getCSSColor("-bg-selected"), LoadCSSStyles.getCSSColor("-bg-selected"));

        languagePicker.getSelectionModel().select(languages.indexOf(getDisplayName(SaveLoadManager.getData().getLocale())));

        languagePicker.setOnAction(e ->
        {
            SaveLoadManager.getData().setLocale(getLocale(languages.get(languagePicker.getSelectionModel().getSelectedIndex())));
            if (!langaugeRestartAlertHasBeenShown)
            {
                langaugeRestartAlertHasBeenShown = true;
                showRestartRequiredAlert();
            }
        });

        languagePicker.setTooltip(new Tooltip(SaveLoadManager.getTranslation("RestartRequiredForEffect")));

        HBox languagePickerHolder = new HBox(languagePicker);
        languagePickerHolder.getStyleClass().add("languagePickerHolder");

        this.getChildren().addAll(titleTheme, descriptionTheme, darkToggleButton, lightToggleButton, new Separator(Orientation.HORIZONTAL), titleLanguage, descriptionLanguage, languagePickerHolder);
    }

    /**
     * This function takes a local, and returns the display name as a capitalized string, written in the language of that local
     */
    private String getDisplayName(Locale locale)
    {
        return StringUtils.capitalize(locale.getDisplayLanguage(locale));
    }

    /**
     * This function takes a displayname and converts it to a locale
     */
    private Locale getLocale(String displayName)
    {
        return mapDisplayNameToLocale.get(displayName);
    }

    /**
     * This function will show an alert/popup/dialog to the user, telling the user that a restart is required in order for the changes to take effect
     */
    private void showRestartRequiredAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("%Appearance");
        alert.setHeaderText(null);
        alert.setContentText(SaveLoadManager.getTranslation("RestartRequiredForEffect"));

        ButtonType okButtonType = new ButtonType(SaveLoadManager.getTranslation("Ok"), ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButtonType);

        alert.showAndWait();
    }
}