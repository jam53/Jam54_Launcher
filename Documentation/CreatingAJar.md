# Creating A Jar
Door het compileren van onze java broncode, kunnen we een jar bekomen. Deze kan vervolgens uitgevoerd worden op eender welke omgeving die een implementie biedt voor de JVM met het volgende commando
```
java -jar <NameJar>.jar
```
Het probleem hierbij is dat de gebruiker Java moet hebben geinstalleerd, voordat deze jar kan worden uitgevoerd. Om dit te vermijden, kunnen we onze jar samen met de JRE inpakken in een native applicatie. Die de gebruiker gewoon kan openen en gebruiken.

Aangezien deze native applicatie naast de jar, de JRE bevat. Zal het toch mogelijk zijn om de jar uit te voeren. Zonder dat de gebruiker manueel Java hoeft te gaan installeren.

Verder in dit document worden de stappen opgelijst, om verschillende *.java bronbestanden, om te zetten naar een installer. Deze installer kan zowel gecompileerd voor, Windows, MacOs als Linux.

---

<br>

## Inhoudstafel
1. [Een jar met dependencies maken in Intellij IDEA.](#een-jar-met-dependencies-maken-in-intellij-idea)
2. [De JavaFX runtime downloaden als JMOD's](#de-javafx-runtime-downloaden-als-jmods)
3. [De jar omzetten naar een native applicate](#de-jar-omzetten-naar-een-native-applicatie)
4. [Extra voorbeelden van jpackage voor Windows, Linux en MacOs](#extra-voorbeelden-van-jpackage-voor-windows-linux-en-macos)
5. [Additional notes](#additional-notes)



<br>

## *.java bronbestanden omzetten naar een uitvoerbare jar met dependencies.
Indien men reeds bekend is met proces om een uitvoerbare jar te bekomen, kan deze stap worden overgeslaan.

Hieronder wordt er een korte schets gegeven van hoe er een FAT jar met Intellij IDEA gemaakt kan worden.

Bij het uitvoeren van het project in Intellij IDEA, worden alle *.java bestanden omgezet in verschillende *.class bestanden. Het is echter gemakkelijker als we al deze bundelen in één bestand. Dat wordt een jar genoemd.

Overigens lijkt het op het eerste zich nog gemakkelijker, om ook alle libraries waar we gebruik van maken in deze jar te steken. Zodat we niet één jar hebben met onze eigen code. En verschillende jars voor alle libraries die we gebruiken. Dit maakt het bijvoorbeeld gemakkelijker voor het distribueren en laten installeren van de applicatie naar eindgebruikers toe.

Maar aangezien we toch een native installer maken, maakt het niet uit hoe onze gecompileerde .java bestanden eruit zien. Daar deze toch verpakt zitten in de installer (apart of samen in een FAT jar). Verder zijn de voordelen van een jar met dependencies, dat wanneer er een update moet worden uitgebracht. Deze veel kleiner is, aangezien alle library bestanden waarvan we gebruik maken hoogstwaarschijnlijk ongewijzigd blijven. En enkel de jar is die onze code bevat, gedownload en installeerd moet worden.

<br>

### Een jar met dependencies maken in Intellij IDEA.

1. Ga naar File > Project Structure > Artifacts. En klik op het plusje linksboven. Klik vervolgens op JAR > From modules with dependencies...
    - Kies de main klasse/entry point van het project.
    - Selecteer *copy to the output directory and link via manifest*
    - Pas het pad voor de optie *<u>Directory for META-INF/MANIFEST.MF:</u>* aan als volgt:
        -   ```
            <pad naar project>\<naam project>\resources
            ```
      > Note: Als we in plaats van `resources`, `src\main\java` lieten staan. Dan zal de jar correct compileren. Maar zal er een error opgegooid worden wanneer we deze jar proberen uit te voeren.
    - Druk op: *OK*
    - Controleer of alle libraries die u gebruikt onder het tabje *Output Layout*, te zien zijn in het linker deel van het scherm. (Dit zou normaal automatisch het geval moeten zijn.) Druk vervolgens op: *OK*

2. Ga naar Build > Build Artifacts... en selecteer Build

3. De gecompileerde jar is te vinden onder: `out/artifacts/<naamProject>_jar/<naamProject>.jar`

4. Indien gewenst, kan van de verkregen jars. De jar die de code van ons project bevat eerst geobfuscate worden, alvorens verder te gaan naar de volgende stap.