# Obfuscation
In order to obfuscate our .jar files, we will use a tool called *Proguard*. We will also have to exclude some classes from the obfuscation process, if not the UI will appear broken.

## Prerequisites
- Download the latest version of Proguard from [the offical GitHub](https://github.com/Guardsquare/proguard/releases/latest)
- Once unzipped, place the jar you would like to obfuscate in the `proguard-<version>\bin` directory
- Create a config file called: `proguard-<version>\bin\proguard.cfg`
  - Adapt the config file to your needs, specifically for this project the following config file is used:
    - ```
        -injars       Jam54_Launcher.jar
        -outjars      Jam54_Launcher_obf.jar
        -libraryjars  D:\Deniz\GitHub\Jam54_Launcher\Jam54_Launcher\out\artifacts\Jam54_Launcher_jar
        -libraryjars  D:\Program Files\jdk-20\jmods

        -dontshrink
        -dontoptimize

        -printmapping myapplication.map

        # Save meta-data for stack traces
        -renamesourcefileattribute SourceFile
        -keepattributes SourceFile,LineNumberTable

        # Rename FXML files together with related views
        -adaptresourcefilenames **.fxml,**.png,**.css
        -adaptresourcefilecontents **.fxml
        -adaptclassstrings

        # Keep all annotations and meta-data
        -keepattributes *Annotation*,Signature,EnclosingMethod

        -keep public class com.jam54.jam54_launcher.Main {
            public static void main(java.lang.String[]);
        }

        # Keep names of fields marked with @FXML attribute
        -keepclassmembers class * {
            @javafx.fxml.FXML *;
        }

        # Keep all classes inside application
        -keep,allowobfuscation class javafx_and_proguard.** {
        }

        # Dont obfuscate these classes, otherwise the UI doesn't look right or some of the functionality doesn't work
        -keep class com.jam54.jam54_launcher.** { *; }
      ``` 

## Obfuscating the jar
Once the steps above are done, you can obfuscate the jar by navigating to `proguard-<version>\bin` and running the command `proguard.bat @proguard.cfg` 

> In case you encounter errors with the obfuscated jar, you can use the following command to run the jar from the command line in order to debug: `java --module-path "D:\GitHub\Jam54_Launcher\Jam54_Launcher\out\artifacts\Jam54_Launcher_jar" --add-modules javafx.controls,javafx.fxml -cp Jam54_Launcher.jar;commons-codec-1.15.jar;commons-io-2.11.0.jar;commons-lang3-3.12.0.jar;gson-2.10.1.jar;javafx-base-20.jar;javafx-base-20-win.jar;javafx-controls-20.jar;javafx-controls-20-win.jar;javafx-fxml-20.jar;javafx-fxml-20-win.jar;javafx-graphics-20.jar;javafx-graphics-20-win.jar;sqlite-jdbc-3.40.1.0.jar com.jam54.jam54_launcher.Main`  
> > Make sure the jars located in `D:\GitHub\Jam54_Launcher\Jam54_Launcher\out\artifacts\Jam54_Launcher_jar` are also placed in the current folder that you are running the command from

## Cleanup
Replace the original jar with the obfuscated one
