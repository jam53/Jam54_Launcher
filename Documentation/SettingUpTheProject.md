# Setting up the project

## Cloning the Jam54 Launcher
- Cloning using *Git*:
    ```
    git clone https://github.com/jam53/Jam54_Launcher.git
    ```
- Cloning using *GitHub Desktop*:
    - Open GitHub Desktop > file > Clone repository > URL > enter the following url: https://github.com/jam53/Jam54_Launcher.git and press Clone

## Prerequisites

### Downloading the JDK
It's recommend to use the latest stable version of OpenJDK which can be downloaded from [here](https://jdk.java.net/). Once downloaded and installed, ensure that the `java` executable is added to your system's PATH or environment variables.  
Please note that the code is compiled to Java 21 at build time. This is because Java 21 was the latest version when the Jam54 Launcher was initially released. Therefore, while we recommend using the latest version of Java, any version that is OpenJDK 21 or higher should work.

### Downloading the JavaFX jmods
This step is optional and only necessary if you need to build an installer for the Jam54 Launcher. To download the required jmod files:
1. Go to [jdk.java.net](https://jdk.java.net/) 
2. Select the *ready for use* version of JavaFX. 
3. In the *Builds* section, choose for `jmods` to download the files.

### Downloading Scene Builder
Since this project uses `.fxml` files, you may also want to download [*Scene Builder*](https://gluonhq.com/products/scene-builder/), which is a WYSIWYG editor for the aforementioned `.fxml` file format.

### Downloading the Java IDE
We recommend using the [*IntelliJ IDEA*](https://www.jetbrains.com/idea/download/) IDE, however, any other Java IDE should work fine.

### Configuring the Java IDE
The steps described here are for *IntelliJ IDEA*. If you are using a different IDE, the configuration steps should be similar to those outlined here.

1. On the *Welcome screen*, open the *Customize* tab.
2. Once on the *Customize* tab, click on *All settings...*
3. Inside *Settings*, go to: *Languages & Frameworks* > *JavaFX*
4. Insert the path to the *Scene Builder*. On Windows  this is located inside: `C:\Users\<user>\AppData\Local\SceneBuilder\SceneBuilder.exe` by default.
5. Click on *Apply*

### Opening the Java project
You can now open the Jam54_Launcher project using *IntelliJ IDEA*.

---

### Downloading .NET
It's recommend to use the latest version of .NET which can be downloaded from [here](https://dotnet.microsoft.com/en-us/download). Once downloaded and installed, ensure that the `dotnet` executable is added to your system's PATH or environment variables.  
While we recommend using the latest version of .NET, any version that is higher than the one specified [here](https://github.com/jam53/Jam54_Launcher/blob/master/Updater/Updater.csproj#L5) higher should work.

### Downloading the C# IDE
You can choose from several IDEs for C# development, such as [*Visual Studio*](https://visualstudio.microsoft.com/downloads/), [*Visual Studio Code*](https://code.visualstudio.com/download) or [*Rider*](https://www.jetbrains.com/rider/download/#section=windows). Other C# IDEs should also work fine.

### Opening the C# project
You can now open the Updater project using *Visual Studio*.
