@echo off

rem Change to the directory where the batch file resides
cd /d %~dp0

pushd target

set "thin_jar=Jam54_Launcher-1.0-SNAPSHOT.jar"
set "thin_jar_proguard_obfuscated=Jam54_Launcher-1.0-SNAPSHOT-proguard.jar"
set "thin_jar_obfuscated=Jam54_Launcher-1.0-SNAPSHOT-proguard.jar-out.jar"
set "uber_jar=Jam54_Launcher-1.0-SNAPSHOT-jar-with-dependencies.jar"
set "uber_jar_obfuscated=Jam54_Launcher-1.0-SNAPSHOT-jar-with-dependencies-obfuscated.jar"
set "jam54_package=com\jam54\jam54_launcher"
set "temp_dir=%cd%\jar_temp"

echo The Maven package command yields us two jar files %thin_jar% and %uber_jar%. The former is a thin jar and contains just our code, the latter is an uber jar that contains all of our code + all the dependencies. Now, we want to obfuscate our code but obfuscating the uber jar in it's entirety causes errors and excluding the packages that weren't ours didn't work. Instead this script obfuscates the thin jar first, removes our package from the uber jar and copies our obfuscated code into the uber jar.

rem Ask for paths
set /p proguard_path="Enter the path to proguard.bat (e.g., C:\proguard-x.y.z\bin\proguard.bat): " 
set /p skidfuscator_jar="Enter the path to the Skidfuscator jar (e.g., C:\Skidfuscator.Community.x.y.z.jar): "
set /p jdk_path="Enter the path to the JDK modules (e.g., C:\Program Files\jdk-xx\jmods): "

rem Create a temporary directory
mkdir "%temp_dir%"

rem Update proguardConfig.txt with the provided JDK modules path
echo -libraryjars %jdk_path% > ..\proguardConfig.txt.temp
type ..\proguardConfig.txt >> ..\proguardConfig.txt.temp

rem Step 1: Obfuscate the thin jar with proguard
echo Obfuscating the jar: %thin_jar% and outputting as %thin_jar_proguard_obfuscated%
call "%proguard_path%" @"..\proguardConfig.txt.temp"
del ..\proguardConfig.txt.temp

rem Step 2: Obfuscate the jar that has been obfuscated with proguard once again but now using skidfuscator
echo Obfuscating the jar: %thin_jar_proguard_obfuscated% and outputting as: %thin_jar_obfuscated%
java -jar "%skidfuscator_jar%" obfuscate %thin_jar_proguard_obfuscated% -li=".\lib" --config="..\skidfuscatorConfig.txt"

rem Step 3: Extract the uber JAR into the temporary directory
echo Extracting %uber_jar%...
pushd "%temp_dir%"
jar -xf "..\%uber_jar%"

rem Step 4: Remove the specified package
echo Removing package %jam54_package%...
rmdir /s /q "%jam54_package%"

rem Step 5: Copy the package from the obfuscated thing JAR into the temporary directory
echo Extracting %jam54_package% from %thin_jar_obfuscated%...
jar -xf "..\%thin_jar_obfuscated%" "%jam54_package%"

rem Step 6: Re-create the modified JAR. After obfuscating the thin jar, the obfuscated thin jar for some reason contains a new class with some random name we need. We don't know the name of that class yet though, so we will create the jar anyway. After running this jar the user can tell us which class was missing.
echo Creating new JAR file...
jar -cfM "..\%uber_jar_obfuscated%" .

rem Step 7: Run the newly created jar and ask the user which file is missing.
echo Running JAR file with missing class
java -jar "..\%uber_jar_obfuscated%"

rem Step 8: Prompt user for the name of the missing .class file, which we can then extract from the obfuscatd thin jar
echo Enter the name of the missing .class file (without the .class extension): 
set /p class_file_name="The name of this file is the string of characters just after the error message above; Exception in thread 'main' java.lang.NoClassDefFoundError: "

rem Step 9: Extract missing .class file from the obfuscated thin jar
echo Extracting %class_file_name%.class file from %thin_jar_obfuscated%...
jar -xf "..\%thin_jar_obfuscated%" "%class_file_name%.class"

rem Step 10: Remove the old jar file with the missing .class file
echo Removing old jar file "..\%uber_jar_obfuscated%" ...
del /s /q "..\%uber_jar_obfuscated%"

rem Step 11: Re-create the modified JAR, now with the class that was missing
echo Creating new JAR file...
jar -cfM "..\%uber_jar_obfuscated%" .

rem Step 12: Cleanup temporary directory
echo Cleaning up...
popd
rmdir /s /q "%temp_dir%"


echo The obfuscated uber jar has been created in: %cd%\%uber_jar_obfuscated%