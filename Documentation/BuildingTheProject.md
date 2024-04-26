# Building The Project
As described in [Understanding what's in the project](./WhatsInTheRepository.md), there are 2 projects in this repository, the *Jam54Launcher* and *Updater*. Naturally this means that we will need to build twice.

## Building The Jam54_Launcher
Build the main project first, *Jam54Launcher*. This can be done by executing the Maven command `clean` followed by the Maven command `package` which will create an uber jar. The uber jar contains all of the dependencies required to run the Jam54 Launcher.

This build process should result in a folder called *target* to be created, in which the uber jar `Jam54_Launcher-1.0-SNAPSHOT-jar-with-dependencies.jar` will be placed. 

## Obfuscating the built Jam54_Launcher jar
Once we built the *Jam54Launcher*, we should obfuscate the package within the uber jar that contains our code. This can be done by running the `ObfuscateJam54LauncherBuild.bat` script.

Rename the obfuscated uber jar to `Jam54_Launcher.jar` and move it to a new empty folder.

## Building The Updater
- Right click on the `Updater` project and select *Publish*
- Select publish to *Folder*
- Click either on *Show all settings* or *More actions > edit*
    - Configuration: Release
    - Target framework: net6.0
    - Deployment mode: Self-contained
    - Target runtime: win-x64
    - Produce single file: true
    - Enable ReadyToRun compilation: true
    - Trim unused code: true
- Click on the *Publish* button
- The build will be placed inside: `bin\Release\net6.0\publish\win-x64\`
- Place the `Updater.exe` file inside the same folder that contains the obfuscated uber jar file `Jam54_Launcher.jar`.

## Creating An Installer
Once we have built both of our projects, we can create an installer by following the steps described in [creating an installer.](./CreatingAnInstaller.md) 

---

Following the guide should yield the command below. This command should be executed in the directory that contains both our `Jam54_Launcher.jar` file as well as the `Updater.exe` file:
- Windows
    ```
    jpackage --input . --module-path "D:\Program Files\javafx-jmods-21" --add-modules javafx.controls,javafx.fxml,jdk.crypto.ec,java.sql,java.desktop --name "Jam54 Launcher" --icon "D:\GitHub\Jam54_Launcher\Jam54_Launcher\src\main\resources\com\jam54\jam54_launcher\img\jam54Icon.ico" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2021 jam54" --win-dir-chooser --win-shortcut --win-per-user-install --win-menu --license-file "D:\GitHub\Jam54_Launcher\LICENSE" --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type msi
    ```

> ### Note:
> - `--input .` means that all the files/folders in the current folder will be packed into the installer. Therefore we should run this command in the folder where we placed the `Updater.exe` file + the output of building our Jam54_Launcher project.
> - Make sure to use the `Jam54_Launcher.jar` as the "main jar".
> - The Jam54Launcher is for Windows only, so we tell jpackage to create an .msi installer


<br>

The operating systems below are no longer supported
- ~~Linux deb~~

    ~~```jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml,jdk.crypto.ec --name "Jam54 Launcher" --icon "../jam54Icon.png" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --linux-shortcut --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type deb```~~

- ~~Linux rpm~~

    ~~```jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml,jdk.crypto.ec --name "Jam54 Launcher" --icon "../jam54Icon.png" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --linux-shortcut --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type rpm```~~

- ~~MacOs~~

    ~~```jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml,jdk.crypto.ec --name "Jam54 Launcher" --icon "../jam54Icon.icns" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type pkg --mac-package-identifier com.jam54.jam54_launcher --mac-package-name JAM54LAUNCHER```~~
