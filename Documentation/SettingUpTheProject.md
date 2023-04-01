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
It's recommend to use the latest version of OpenJDK which can be downloaded from [here.](https://jdk.java.net/)  
However, at build time the code gets compiled to Java 20. This is because when the Jam54 Launcher was released initially, this was the latest version of Java at that time. This means that although we recommend the latest version of Java; any version higher than or equal to [OpenJDK 20](https://jdk.java.net/20/) should work.

### Download the Java IDE
We recommend using the *IntelliJ IDEA* IDE, however, any other IDE should work fine.

You may download *IntelliJ IDEA* from [here.](https://www.jetbrains.com/idea/download/)

### Download the *Scene Builder*
Since this project uses `.fxml` files, you may also want to download the *Scene Builder* from [here.](https://gluonhq.com/products/scene-builder/)

### Configuring the Java IDE
The steps described here are meant for *IntelliJ IDEA*. In case you are using another IDE, the steps needed to configure your IDE of choice should be similar to the ones listed here.

1. On the *Welcome screen*, open the *Customize* tab.
2. Once on the *Customize* tab, click on *All settings...*
3. Inside *Settings*, go to: *Languages & Frameworks* > *JavaFX*
4. Insert the path to the *Scene Builder*. On Windows  this is located inside: `C:\Users\<user>\AppData\Local\SceneBuilder\SceneBuilder.exe` by default.
5. Click on *Apply*

### Opening the Java project
You can now open the Jam54_Launcher project using *IntelliJ IDEA*

---

### Downloading the C# IDE
We recommend using the *Visual Studio* IDE, however, any other IDE should work fine.

You may download *Visual Studio* from [here.](https://visualstudio.microsoft.com/downloads/)

### Configuring the C# IDE
.NET 6.0 is used for everything C# related in this repository.

### Opening the C# project
You can now open the Updater project using *Visual Studio*
