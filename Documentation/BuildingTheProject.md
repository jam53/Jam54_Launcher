# Building The Project
As described in [Understanding what's in the project](./WhatsInTheRepository.md), there are 2 projects in this repository, the *Jam54Launcher* and *Updater*. Naturally this means that we will need to build twice.

## Building The Jam54_Launcher
Build the main project first, *Jam54Launcher*. By following the steps described in the guide [creating a jar.](./CreatingAJar.md)

This build process should result in folder called *out* to be created, in which there will be a subfolder called *artifacts*. In there you will find a folder called *Jam54_Launcher_jar*.

Move the contents of the *Jam54_Launcher_jar* folder to a new folder.
> Don't forget to obfuscate the `Jam54_Launcher.jar` file.

## Building The Updater
- Right click on the `Updater` project and select *Publish*
- Select publish to *Folder*
- Click either on *Show all settings* or *More actions > edit*
    - Configuration: Release
    - Target framework: net6.0
    - Deployment mode: Self-contained
    - Target runtime: win-x64
    - Produce single file: false
    - Enable ReadyToRun compilation: true
    - Trim unused code: true
- Click on the *Publish* button
- The build will be placed inside: `bin\Release\net6.0\win-x64\`
- Create a subfolder called `Updater` inside the new folder that contains the files produced by building the Jam54_Launcher project
- Move the files produced during this build, into the subfolder called Updater. Place this Updater folder in the folder that contains the jars from building the Jam54_Launcher.

## Creating An Installer
Once we have built both of our projects, we can create an installer by following the steps described in [creating an installer.](./CreatingAnInstaller.md) 

---

Following the guide should yield this command:
- Windows
    ```
    jpackage --input . --module-path "D:\Program Files\openjfx-18.0.2_windows-x64_bin-jmods\javafx-jmods-18.0.2" --add-modules javafx.controls,javafx.fxml,jdk.crypto.ec,java.sql --name "Jam54 Launcher" --icon "D:\GitHub\Jam54_Launcher\Jam54_Launcher\src\main\resources\com\jam54\jam54_launcher\img\jam54Icon.ico" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --win-dir-chooser --win-shortcut --win-per-user-install --win-menu --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type msi
    ```

> ### Note:
> - `--input .` means that all the files/folders in the current folder will be packed into the installer. Therefore we should run this command in the folder where we placed the Updater subfolder + the output of building our Jam54_Launcher project.
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
