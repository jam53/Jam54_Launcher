# Building The Project
As described in [Understanding what's in the project](./WhatsInTheRepository.md), there are 2 projects in this repository, the *Jam54Launcher* and *Updater*. This means that we will need to build twice, once for each of the projects.

## Building The Jars
Build the main project first, *Jam54Launcher*. By following the steps described in the guide [creating a jar.](./CreatingAJar.md)

This build process should result in folder called *out* to be created, in which there will be a subfolder called *artifacts*. In there you will find a folder called *Jam54_Launcher_jar*.

Repeat the steps listed above for the *Updater* project.

---

Now copy the contents of the *Jam54_Launcher_jar* folder and the *Updater_jar* folder to a new folder. When asked to overwrite already existing files, click yes. This means the two projects have some dependencies in common.
> Don't forget to obfuscate both the `Jam54_Launcher.jar` and `Updater.jar` files.

## Creating An Installer
Once we have built both of our projects, we can create an installer by following the steps described in [creating an installer.](./CreatingAnInstaller.md) 
> ### Note:
> - Make sure to use the `Jam54_Launcher.jar` as the "main jar".

---
Following the guide should yield these commands:
- Windows
    ```
    jpackage --input . --module-path "D:\Program Files\openjfx-18.0.1_windows-x64_bin-jmods\javafx-jmods-18.0.1" --add-modules javafx.controls,javafx.fxml --name "Jam54 Launcher" --icon "..\..\..\src\main\resources\com\jam54_launcher\img\jam54Icon.ico" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --win-dir-chooser --win-shortcut --win-per-user-install --win-menu --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type msi
    ```

- Linux deb
    ```
    jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml --name "Jam54 Launcher" --icon "../jam54Icon.png" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --linux-shortcut --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type deb
    ```

- Linux rpm
    ```
    jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml --name "Jam54 Launcher" --icon "../jam54Icon.png" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --linux-shortcut --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type rpm
    ```

- MacOs
    ```
    jpackage --input . --module-path "../javafx-jmods-18.0.1/" --add-modules javafx.controls,javafx.fxml --name "Jam54 Launcher" --icon "../jam54Icon.icns" --app-version 0.1.0 --vendor "jam54" --copyright "Copyright © 2022 jam54" --description "The Jam54 Launcher is used to install and update all of the software developed by jam54." --main-jar Jam54_Launcher.jar --main-class com.jam54.jam54_launcher.Main --type pkg --mac-package-identifier com.jam54.jam54_launcher --mac-package-name JAM54LAUNCHER
    ```