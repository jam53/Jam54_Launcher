# Updating The Jam54Launcher
Please follow the steps stated in this document, in chronological order.

# Inside the Jam54Launcher IntelliJ project
- Open *Jam54LauncherData.java*, and update the `version` variable

# When creating the installer
Make sure to update the *--app-version* argument when creating the installer using *jpackage*

# The hosted files
Prepare the following files:
- Jam54LauncherSetup.msi (and installers for any other platforms needed)
    - (Rebuild it according to the [Building The Project](./Documentation/BuildingTheProject.md) guide)
- Jam54_Launcher.jar
    - A newly built jar, that contains the new version of the Jam54Launcher
- version.txt
    - This file should contain one line only, and this line should represent the current version number of the launcher. This has to be the same value as the `version` variable inside *Jam54LauncherData.java*.

---

Upload the files listed above to GitHub, and create a new release