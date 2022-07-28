# Setting up the project

## Clone the Jam54 Launcher
- Cloning using *Git*:
    ```
    git clone https://github.com/jam53/Jam54_Launcher.git
    ```
- Cloning using *GitHub Desktop*:
    - Open GitHub Desktop > file > Clone repository > URL > enter the following url: https://github.com/jam53/Jam54_Launcher.git and press Clone

## Prerequisites

### Downloading the JDK
This project uses OpenJDK 18.0.2, which may be downloaded from [here.](https://jdk.java.net/18/) This version is used for everything Java related in this repository, this includes but is not limited to:
- The Jam54_Launcher project
- The Updater project
- JavaFX
- The Scene Builder
- The jpackage command

### Download the IDE
We recommend using the *IntelliJ IDEA* IDE, however, any other IDE should work fine.

You may download *IntelliJ IDEA* from [here.](https://www.jetbrains.com/idea/download/)

### Download the *Scene Builder*
Since this project uses `.fxml` files, you may also want to download the *Scene Builder* from [here.](https://gluonhq.com/products/scene-builder/)

### Configuring the IDE
The steps described here are meant for *IntelliJ IDEA*. In case you are using another IDE, the steps needed to configure your IDE of choice should be similar to the ones listed here.

1. On the *Welcome screen*, open the *Customize* tab.
2. Once on the *Customize* tab, click on *All settings...*
3. Inside *Settings*, go to: *Languages & Frameworks* > *JavaFX*
4. Insert the path to the *Scene Builder*. On Windows  this is located inside: `C:\Users\<user>\AppData\Local\SceneBuilder\SceneBuilder.exe` by default.
5. Click on *Apply*

### Opening the project
You can now open the project using *IntelliJ IDEA*