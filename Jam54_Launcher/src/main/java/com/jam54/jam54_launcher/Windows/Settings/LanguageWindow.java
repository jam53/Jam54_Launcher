package com.jam54.jam54_launcher.Windows.Settings;

import com.jam54.jam54_launcher.Data.SaveLoad.SaveLoadManager;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class is used to create the languages window, at the right side within the settings window
 */
public class LanguageWindow extends VBox
{
    ArrayList<String> languages;
    HashMap<String, Locale> mapDisplayNameToLocale;

    public LanguageWindow(ArrayList<Locale> supportedLanguages)
    {
        Label title = new Label("%Language");

        languages = new ArrayList<>();
        mapDisplayNameToLocale = new HashMap<>();

        for (Locale locale : supportedLanguages)
        {
            languages.add(getDisplayName(locale));
            mapDisplayNameToLocale.put(getDisplayName(locale), locale);
        }
        languages.sort(String::compareTo);

        ComboBox<String> languagePicker = new ComboBox<>(FXCollections.observableArrayList(languages));

        languagePicker.getSelectionModel().select(languages.indexOf(getDisplayName(SaveLoadManager.getData().getLocale())));

        languagePicker.setOnAction(e -> SaveLoadManager.getData().setLocale(getLocale(languages.get(languagePicker.getSelectionModel().getSelectedIndex()))));

        Separator separator = new Separator(Orientation.HORIZONTAL);

        Button button = new Button();
        button.setTooltip(new Tooltip("%Changing this setting will only take effect after a restart/Restart required to take effect"));

        this.getChildren().addAll(title, languagePicker, separator, button);
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
}
