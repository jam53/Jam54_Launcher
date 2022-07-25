# Creating An Installer

## De JavaFX runtime downloaden als JMOD's
Aangezien JavaFX sinds JDK11 niet meer mee gebundeld is met de JDK. Moeten we de JavaFX runtime die ons programma nodig heeft om te runnen eerst downloaden. We downloaden de JavaFX runtime van [hier.](https://gluonhq.com/products/javafx/) Kies voor de optie die bij je besturingssysteem past, en neem voor het type; JMOD.

Unzip deze bestanden eenmaal ze gedownload zijn. We zullen ze later nodig hebben wanneer we het `jpackage` commando zullen gebruiken

## De jar omzetten naar een native applicatie
We zullen nu de tool genaamd `jpackage` gebruiken. Tot Java 11 werd hiervoor `javapacker gebruikt`.

Aan de hand van `jpackage` kunnen we de jar omzetten in een native applicatie, die de gebruiker kan openen door erop te te dubbel klikken. `jpackage` kan een installer produceren voor de volgende platformen:

- Windows (exe, msi)
- MacOs (dmg, pkg)
- Linux (deb, rpm)

`jpackage` maakt een installer. Deze bevat eigenlijk onze jar, en de JRE die nodig is om de jar uit te voeren. Wanneer deze installer dan wordt geopend, wordt ons programma geinstalleerd op de gebruiker zijn apparaat. De jar en JRE worden met andere woorden uit de installer gehaald, en gekopieerd naar een locatie op de gebruiker zijn apparaat.

> Note: Wanneer we een installer maken met `jpackage`, wordt er achterliggend gebruik gemaakt van `jlink`. Om alle onderdelen van de JRE die onze applicatie niet gebruikt weg te strippen. Hierdoor wordt enkel het strikt nodige van de JRE overgehouden.

<br>

### `jpackage` gebruiken
Open de CLI en begeef je naar de directory waar de jar zich bevindt. Voer vervolgens het volgende commando uit:
```
jpackage --input . --name <NameInstaller> --module-path ".../openjfx-18.0.1_<platform name>_bin-jmods/javafx-jmods-18.0.1" --add-modules javafx.controls,javafx.fxml,<andere> --main-jar <NameJar>.jar --main-class <Path.To.Main.Class.In.Jar> --type <Type of installer to produce> 
```
- --input: Dit specifieert waar onze input bestanden staan. In ons geval staan we in de folder waar de jar staat, dus schrijven we een . (= de huidige folder)
  > *Note: Dit zal alle bestanden die in de folder staan, in de installer packen. Dit is dus handig als men niet een fat jar gebruikt, maar 1 main jar met verschillende dependency jars. Dan worden deze ook in de installer gepacked. Ook worden echter alle niet .jar bestanden meegenomen in de installer; de volledige inhoud van de --input folder wordt dus gekopieerd.*
- --module-path: Het pad (tussen "" als er spaties in staan) naar de folder die de JMOD bestanden voor JavaFX bevat.
- --add-modules: Dit zijn de modules die je Java programma nodig heeft om te runnen. Voor JavaFX programma zijn `javafx.controls` en `javafx.fxml` altijd nodig. Verder kan het ook zijn dat je andere modules gebruikt zoals `java.sql` bijvoorbeeld. Om te zien welke modules je Java project gebruikt, open je het bestand genaamd `module-info.java`. Alles dat achter het keyword `requires` staat, zijn de modules die het project nodig heeft.
- --name: De naam van de installer die `jpackage` zal produceren
- --main-jar: De naam van onze jar, die we willen omzetten naar een native applicatie/installer
- --main-class: De naam van onze main klasse, in dit geval `com.example.Main2`
- --type: Het type/voor welk platform de installer wordt gecompileerd:
    - exe of msi (Windows)
    - dmg of pkg (MacOs)
    - deb of rpm (Linux)

<br>

Verdere optionele argumenten die ook nog nuttig kunnen zijn kunnen gevonden worden in de [documentatie voor jpackage.](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html)

Voer het commando uit door op enter te drukken. Dit produceert dan een een installer volgens de gespecifieerde parameters.

# Extra voorbeelden van jpackage voor Windows, Linux en MacOs
- Windows
    ```
    jpackage --input . --module-path "D:\Program Files\openjfx-18.0.1_windows-x64_bin-jmods\javafx-jmods-18.0.1" --add-modules javafx.controls,javafx.fxml,java.sql,java.prefs --name Flash --icon "..\..\..\src\main\resources\be\ugent\logo\Logo.ico" --app-version 1.9.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --win-dir-chooser --win-shortcut --win-per-user-install --win-menu --description "A program used to create and play quizzes" --main-jar flash.jar --main-class be.ugent.flash.Main --type msi
    ```

- Linux deb
    ```
    jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml,java.sql,java.prefs --name Flash --icon "../Logo.png" --app-version 1.9.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --description "A program used to create and play quizzes" --main-jar flash.jar --main-class be.ugent.flash.Main --type deb
    ```

- Linux rpm
    ```
    jpackage --input . --module-path "jmodsjavafxlocatie" --add-modules javafx.controls,javafx.fxml,java.sql,java.prefs --name Flash --icon "..\..\..\src\main\resources\be\ugent\logo\Logo.png" --app-version 1.9.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --linux-shortcut --description "A program used to create and play quizzes" --main-jar flash.jar --main-class be.ugent.flash.Main --type rpm
    ```

- MacOs
    ```
    jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml,java.sql,java.prefs --name Flash --icon "../Logo.icns" --app-version 1.9.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --description "A program used to create and play quizzes" --main-jar flash.jar --main-class be.ugent.flash.Main --type pkg --mac-package-identifier com.jam54.flash --mac-package-name FLASH
    ```


# Additional notes
- [Interesting talk](https://www.youtube.com/watch?v=ZGW9AalZLN4&ab_channel=Devoxx) on jpackage.

-   Je kan ook de tag `--app-version` meegeven. This version number along side with other options that we specify. Will be used for update rules. For example, you can have it automatically remove older versions, when a new version is installed.
    Dus misschien wanneer de applicatie kijkt van er is een nieuwe update. In plaats van dan zelf te kijken welke bestanden vervangen moeten worden. Gewoon die exe runnen (als het op Windows is bv). En die replaced dan alles wat nodig is. Dan wel programma specifieke bestanden in een andere folder, anders zijn die misschien ook weg.

    - It seems that you have to pass the following argument to jpackage to identify installers for the same application: --win-upgrade-uuid "your_uuid_string". As ever with Java/Oracle documentation, they could not have been more mysterious about this if they tried.

      I've also found that I have to increment the version number, too, or else the installer will flash quickly and just hang in the background and do nothing (until you reboot or end the task): --app-version 1.0.1

      You can generate a UUID here: https://www.uuidgenerator.net/

- `--win-dir-chooser` om een custom install locatie te kiezen, wss ook nog andere opties voor de andere platformen. Misschien toch niet zo handig om de gebruiker de install locatie te laten kiezen. Als we de --app-version van hierboven gebruiken. Om automatisch een nieuwe versie van de installer binnen te halen en die de bestaande app te laten replacen. Als de gebruiker opeens een andere locatie kiest, zullen er 2 versies zijn. Tenzei de app eerst checked van, ben ik al geinstalleerd, voordat hij de dir-chooser toont.