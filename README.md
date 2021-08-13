# Jam54_Launcher
The Jam54 Launcher is used to install and update all the software developed by jam54. The launcher itself is written in C# and developed with Unity.
> Unity was chosen over a traditional developing environment like WinForms or WPF, since it offered better/more features that are used within the launcher.

---

## Functionality
The launcher itself updates automatically to the latest version available. For this purpose the [Squirrel Updater](https://github.com/Squirrel/Squirrel.Windows) has been used.

The programs/games available within the launcher can either be updated manually or automatically. For this a custom build solution has been developed that makes use of delta updates. This only requires the user to download new files or the files that have been changed, not the whole application. This significantly saves time and bandwidth.

Furthermore, the launcher can be used in a variety of languages and the user can choose a specific install location.

## Downloading the launcher
You can download the launcher from [here]().

---

# Getting the project

### How do I clone the Jam54 Launcher
Open [GitHub Desktop](https://desktop.github.com/) *> file > Clone repository > URL* > enter the following url: https://github.com/jam53/Jam54_Launcher.git and press Clone

### Getting the right version of Unity
Once you cloned the repository, you should install the most recent, stable version of Unity. Make sure you also include *Windows Build Support (IL2CPP)* during the installation.

### Main scene
The **main scene** for this project can be found under:

*Assets > Jam54Launcher > Scenes > MainMenu*

Open the **MainMenu scene**, and hit **play.**

## Development of the Jam54 Launcher

The main reason behind the development  of the launcher, was to create one unified place for people to download all the games/programs that have been developed by jam54. Instead of having to search for download links on that are scattered all around the [official website.](https://jam-54.wixsite.com/jam54)
