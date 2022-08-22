package com.jam54.jam54_launcher;

import com.jam54.jam54_launcher.SaveLoad.SaveLoadManager;
import com.jam54.jam54_launcher.Updating.LauncherUpdater;
import com.jam54.jam54_launcher.Windows.Application.ApplicationInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        Jam54LauncherModel model = new Jam54LauncherModel();

        //TODO
        //We hebben een SQLite databank. Die elke applicatie bevat.
        //Deze databank wordt meegeleverd met de launcher, en moet dus niet worden binnengehaald.
        //Dit betekent dus dat als de gebruiker de launcher voor de eerte keer opent, en geen internet heeft.
        //Dan zal de gebruiker toch zien welke applicaties er allemaal zijn.
        //Dit betekent natuurlijk wel dat voor elke nieuwe applicatie we toevoegen, we ook een nieuwe versie van de launcher moeten uitbrengen.
        //Maar, we hoeven niet voor elke nieuwe versie van een app. Een nieuwe versie van de launcher uit te brengen
        /*
         * De Sqlite databank heeft de volgende velden:
         * int: id, varchar: name, blob: image (dit is dus echt de foto als data, niet een padnaam naar de foto ofzo)
         * varchar: description, bool: android, bool: web, bool: windows, long: release date (milliseconds), long: latest update (milliseconds)
         *
         * Daarnaast hebben we momenteel ook een Jam54LauncherConfig.properties bestand.
         * Met daarin een veld "versionUrl", dat leidt naar een version.txt bestand.
         * Deze bevat één lijn, namelijk wat de laatste versie is van de launcher.
         *
         * Ik zou deze "versionUrl" variabele aanpassen naar "versionsUrl" en er als comment het volgende bij zetten:
         * The URL to a .properties file. Which contains what the latest version number is for both the launcher itself + all the apps the launcher can install
         * Dus we kunnen nog altijd doen wat we gebruiken van version.txt, de versie van de launcher extraheren om te kijken als er een nieuwe update is
         * Maar daarnaast hebben we voor elke app ook wat het laatste versie nummer is
         * + Nog een extra veld per app, wat in milliseconds de datum van de laatste release aanduidt.
         *
         * Hiernaast hebben we dan ook nog ons persistent saveloadmanager systeem dat we zullen gebruiken.
         * Daarin zullen we het volgende opslaan per app:
         * Het path naar de install locatie
         * Het versie nummer van de geinstalleerde versie
         *
         * Met deze 3 dingen (SQLite databank, versions.properties, saveloadmanager)
         * Met deze kunnen we een lijst opbouwen van alle applicaties die getoond moeten worden in de launcher
         * Dit kan als volgt:
         * In deze Main klasse, in de start methode, nadat we het model hebben gemaakt
         * Maken we een object van een klasse genaamd, ApplicationsInfo creator ofzo iets
         * Daar is er dan een methode, fetch ofzo, of get alle appsInfos of iets gelijkaardgis
         * Dat kunnen we dan setten in het model
         * De ApplicationInfo record klasse vullen we dan als volgt in:
         * In onze databank voeren we een query uit, die ons alle applicaties uit de databank geeft, en per applicatie doen we het volgende:
         * Maak een nieuw ApplicationInfo object
         * id -> Hetzelfde id dat het heeft in de Databank. Do note dat in de databank, .properties file, en saveloadmanager het wel per applicatie steeds hetzelfde id dient te zijn. (Dit ook zetten in de markdown guide "Adding new apps to the launcher" en dan ook nog een guide maken "Updating existing apps in the launcher"
         * name -> Kan je halen uit de databank
         * image -> Kan je halen uit de databank
         * installLocation -> Vullen we gewoon in als null. Null in dit geval betekent dat het niet geinstaleerd is.
         *  (Hierdoor hebben we ook geen apart veld, "isInstalled" ofzo nodig in het record
         * updateAvailable -> false, het is zogezegd niet geinstalleerd, dus kan er ook geen update zijn
         * version -> Dit stellen we ook in als null. Het is namelijk niet geinstalleerd.
         * description -> Kunnen we halen uit de databank
         * platforms -> Kunnen we halen uit de databank
         * releaseDate -> Kunnen we halen uit de databank
         * latestUpdate -> Kunnen we halen uit de databank, wordt mss nog overschreven
         *
         * Dan gaan we over naar onze saveloadmanager.
         * Die krijgt dus de lijst met alle ApplicationInfo object en overloopt ze.
         * We nemen het id, en kijken dan naar getApplication1(of whatever het id is) install location
         * Als het null is, blijft het null en weten we dat het niet geinstalleerd is. Als er wel een waarde is, kunnen we deze toekennen aan het ApplicationInfo object
         * Update available laten nog altijd open(akak doen we niks mee)/op false staan
         * Version zetten we op null, als het null is wat betekent dat het niet geinstalleerd zou zijn. Of we zetten de versie die geinstalleerd is
         * lastUpdate als het null is, niet null invullen maar de waarde van de databank laten staan. als er wel een waarde is, deze overnemen in het ApplicationInfo object ipv degene uit de databank
         *
         * Tenslotte hebben we een lijst van ApplicationInfos, maar die geven we nog niet terug
         * Nu proberen het .properties bestand te downloaden. Als er geen internet is/andere problemen. Dan slaan we deze stap gwn over
         * Als het wel lukt. Ga dan opzoek naar de key "app" + het id dat in het ApplicationInfo object zit dat je momenteel mee bezig bent uit de lijst
         * En kijk wat de laatste versie is. Als dit niet gelijk is aan de waarde van de versie variabele in ons ApplicationInfo object.
         * Dan zetten we updateAvailable op true, anders op false laten staan
         * We kijken ook als de lastUpdate waarde groter is dan degene die momenteel in het ApplicationInfo object staat, zo ja, overschrijven
         *
         * Uiteindelijk geven we deze lijst met ApplicationInfo objecten dan terug. En steken we ze in het model
         * Ipv properties file mss online ook gwn een sqlite database gebruiken. Is mss beter
         */



        ArrayList<ApplicationInfo> applications = new ArrayList<>();

        applications.add(new ApplicationInfo(1, "AstroRun", null, null, false, null, "description", null, 0, 0));

        model.setAllApplications(applications);
        model.setVisibleApplicationInfos(model.getAllApplications());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"), SaveLoadManager.getResourceBundle());

        MainController controller = new MainController();
        controller.setModel(model);
        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 1228, 754); //If we set for example, 300 as prefWidth inside the FXML. The we could make our window bigger and the hboxes, vboxes, flowplanes etc. would resize accordingly. But anything smaller than 300 after resizing. Would just cut of the side of the window. By setting both the prefWidth and prefHeight to 1 inside the FXML. Followed by choosing the correct widht/height inside Java. The window resizes correctly, even at smaller resolutions
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