# General Overview Of The Jam54 Laucher
## The idea
Initially I had developed a few programs but I didn't really have one central place where people could download them from. That's when I decided to write some kind of launcher that bundles all of the applications I developed into one place.

Before I wrote the current implementation, I used Unity to develop the launcher. In hindsight this might not have been the best choice, hence why I rewrote it recently. But at the time Unity was the only platform/framework I was comfortable working with.

This last iteration of the launcher was made using Java/JavaFX. I thought it might have been better to opt for a "native dektop app" since I would have to handle a lot of IO to download/patch/install other applications. But as far as I understand web apps using Electron/Tauri are also more than capable handling IO nowadays.  
Knowing what I know now, I would probably choose to implement the launcher as a web app if I had to start over. But the Java/JavaFX implementation works well enough so I will leave it at that for now.

## Features
The main features are the following:
- [An easy way to add applications to the launcher or update existing ones](#an-easy-way-to-add-applications-to-the-launcher-or-update-existing-ones)
- [The launcher updates itself](#the-launcher-updates-itself)
- [Applications are kept up to date using delta updates](#applications-are-kept-up-to-date-using-delta-updates)
- [Enforcing a maximum filesize restriction of x megabytes for an application's binaries.](#enforcing-a-maximum-filesize-restriction-of-megabytes-for-an-applications-binaries)
- [Verifying the file integrity of the installed applications](#verifying-the-file-integrity-of-the-installed-applications)
- [Obfuscation of the launcher's bytecode](#obfuscation-of-the-launchers-bytecode)

## Limitations
- You can only download/update/remove one application at the time

## An easy way to add applications to the launcher or update existing ones
In my previous, Unity implementation of the launcher I didn't really have that much coding experience when creating it. Which caused me to basically hardcode all of the applications that are handled by the launcher into the source code. This isn't easy to manage and doesn't scale at all. Which is why I definitely wanted to improve on this in the new Java implementation.

For this purpose there are 2 files. One database called `applications.sqlite` that comes with the launcher, and one config file called `applicationsVersions.properties` that is hosted in the cloud. The former contains details about the applications (name, picture, description, release date, supported platforms, etc.), the latter is used to check whether or not the application in question has a new update.

Using this database file we can easily add a new application to the launcher by adding a new entry into the database, or update existing ones. Because all of the application's data is contained inside this database, we now longer have the issue of hardcoded data of the applications inside the launcher's source code.

The sqlite3 database is nothing more than 2 simple tables as you can see below:
```sql
CREATE TABLE applications(id INT PRIMARY KEY, name TEXT, logo TEXT, android INT, web INT, windows INT, releaseDate INT, latestUpdate INT, isGame INT);

CREATE TABLE application_description(language TEXT, id INT REFERENCES applications(id), description TEXT, PRIMARY KEY (language, id));
```

The content of the `applicationsVersion.properties` file may look something like this:
```properties
app0=0.3.1
app1=1.0.0
app2=1.0.0
app3=1.0.0
app4=0.10.0
app5=1.0.0
app6=1.0.0
app7=1.4.1
app8=1.9.0
app9=1.1.9
```

This `applicationsVersion.properties` file gets downloaded from wherever it's hosted in the cloud. The version numbers inside are then used to check whether or not there are updates available for any of the installed applications.

## The launcher updates itself
Basically the launcher is only half the story, there is also a small *Updater* program I wrote to work alongside the *launcher*.

The Java *launcher* contains the main program, i.e. the *Jam54Launcher* that manages all of the applications. It is used to download applications and helps to keep them up to date.

Apart from the *Jam54Launcher*, there is another program called *Updater*. This is a C# console application whose sole purpose is to update the *Jam54Launcher* itself.

### How does it work
The *Jam54Launcher* is the main program, and will normally be launched first. Now let's say there is a new update available for the *Jam54Launcher*.

In this case the *Jam54Launcher* will start downloading the necessary files in the background. Once it's finished, there will be a green button at the top of the screen that allows the user to relaunch the launcher in order to complete the update.

One of two things could happen:
    A) The user presses on the green button to relaunch the program.
    B) The user closes the program without pressing the button.

### Option A: The User Chose To Relaunch The Program
- In this scenario the *Jam54Launcher* will launch the *Updater*, after which it will immediately close itself
- Once the *Updater* has been launched, it will look for the newly downloaded version of the *Jam54Launcher*. It will then replace the old version of the *Jam54Launcher* with the new one.
- Once the files have been updated, the *Updater* will first launch the Jam54Launcher, before it closes itself
- The *Jam54Launcher* has now been updated, and is ready to be used.

### Option B: The User Closed The Program
- In this scenario the update process will continue the next time the *Jam54Launcher* is launched by the user.
- Right after getting launched, the *Jam54Launcher* will check if a new version was downloaded the previous time it was launched. Which will be the case this time around.
- The *Jam54Launcher* will now launch the *Updater*, after which it will immediately close itself.
- Once the *Updater* has been launched, it will look for the newly downloaded version of the Jam54Launcher. It will then replace the old version of the *Jam54Launcher* with the new one.
- Once the files have been updated, the *Updater* will first launch the Jam54Launcher, before it closes itself
- The *Jam54Launcher* has now been updated, and is ready to be used.

## Updater
As mentioned previously, this *Updater* C# console program does nothing more than replace the old version of the launcher with the new one (which has already been downloaded by the *launcher*). Therefore the one class below contains all of the code used in the *Updater* program.
```csharp
//Main.cs
using System.Diagnostics;
using System.Threading;

class Updater
{
    static void Main(string[] args)
    {
        Thread.Sleep(5000); //Wait for the Jam54Launcher to close

        string oldLauncher = Path.Combine(Directory.GetCurrentDirectory(), "..", "Jam54_Launcher.jar"); //This goes one directory up, and then grabs the path to the old version of the launcher
        string newLauncher = Path.Combine(Directory.GetCurrentDirectory(), "..", "Jam54_Launcher_New.jar"); //This goes one directory up, and then grabs the path to the new version of the launcher

        string jam54Launcher = Path.Combine(Directory.GetCurrentDirectory(), "..", "..", "Jam54 Launcher.exe"); //This goes up 2 directories, and grabs the path to the executable, used to run the Jam54 Launcher

        if (File.Exists(newLauncher))
        {//Only perform the update process, if there is a new version downloaded
            File.Move(newLauncher, oldLauncher, true); //Replace the old jar of the launcher, with the new version. And rename the new version so it has the same name as the old version.
            Process.Start(jam54Launcher); //Start the Jam54 Laucher
            File.Delete(newLauncher); //Delete the downloaded file after it has been installed
        }
    }
}
```

## Applications are kept up to date using delta updates
This is another aspect I improved upon. In the Unity implementation the downloading/updating process was handled in a rather simple way. For each application, I would host a zip file containing the latest version of that application's binaries. When the user would choose to download an application, I would just download and unzip the zip.
If on the other hand the user would have already had the application installed, but there was an update. Then I would first delete the old binaries of the application, download the new zip file and unzip it.

This of course isn't really efficient when handling updates to already installed applications. If I were to change just one file of an application and push an update. This would mean the user would have to redownload the entire application.

I tried to improve on this in the Java implementation.
I first did some research and as far as I understand most people/companies tend to have a full package containing all of the binaries. And then patch packages that are shipped for every new release. For me this caused a problem since I only wanted to host the latest versions of said applications. I didn't want to host both the full binary for every release and patch packages to go from one version to the next.
> Another thing I thought about was the fact that you not only have to host patch packages to go from lets say version 1.3 to 1.4. But also patch packages to go from any of the even older version to the most recent one. So I would have to create a ton of patch packages to support people updating from not only 1.3 -> 1.4 but also 1.2 -> 1.4, 1.1 -> 1.4 and so on.

Eventually I implemented the delta updates like so:
- For any new version of an application, hash all of the files and store them in a file called `Hashes.txt` and place this `Hashes.txt` file in the root directory of the application's files
    - Example of `Hashes.txt`
```
10521fe73fe05f2ba95d40757d9f676f2091e2ed578da9d5cdef352f986f3bcd|runtime\bin\ucrtbase.dll
2afbfa1d77969d0f4cee4547870355498d5c1da81d241e09556d0bd1d6230f8c|runtime\bin\api-ms-win-core-console-l1-1-0.dll
...
```
- For each file that's part of the application, split the file into chunks of 1MB (1024^2 bytes) and store these in a folder called `Chunks` in the root directory of the application's files. Where the filename of each 1MB chunk is the hash of the 1MB file.
  - For each file of the application, create a file of the same name in the same location with `.hashes` appended to the end of the filename. All of the 1MB chunks that are associated with this file should be stored in the `applicationFile.extension.hashes` file. Where the first value represents the hash of the 1MB chunk and the second value stands for the start index of this 1MB chunk.
    - Example of `applicationFile.extension.hashes`
```
4aee721b7c796ae3c2a4b54de7efefd0b39a9f673c3b96f0e2ab30c19bd360e0|1048576
5cbf1868b078cd5bc622f0f9656dc4221e7dc5497c2f96c039bb480b16953495|3145728
af02b5b12c2c3512dcbc197cf6fa19ecf7539a33f0bb4bbd024717b7832b890b|0
a8581dda005d9cbcfce573ea4c3aacdf6db40837421fb77018baf0d944828ba0|4194304
...
```
> Make sure the `Hashes.txt` file is computed first. Otherwise the `Hashes.txt` file will also contain the hashes of each of the `applicationFile.extension.hashes` files.
- Host the application's file somewhere, along with the `Hashes.txt` file, the `Chunks` directory and the `applicationFile.extension.hashes` associated with each file.
- If the user wants to download an application that isn't installed yet or update an already existing one. First download the `Hashes.txt` file of said application.
    - Save those hashes to a Dictionary/Map called `hashesCloud` (key: hash, value: path to the file)
- Calculate the hashes of all of the files which are already on the user's disk. And store them in a Dictionary/Map called `hashesLocal` (key: hash, value: path to file)
    - > Note: In the case of downloading an application that isn't installed yet. The `hashesLocal` Dictionary/Map would be empty.
- Calculate the set difference between `hashesLocal` - `hashesCloud` and save the remaining values (=without the keys i.e. the path to the files) in a set called `obsoleteFiles`. This yields us all the files that are on the user's filesystem, that have either been modified or removed in the most recent version of the application we are downloading/updating to.
    - The set difference (subtraction) is defined as follows. The set `hashesLocal`−`hashesCloud` consists of elements that are in `hashesLocal` but not in `hashesCloud`. 
        - > Note: This set difference would be ∅ (empty) if we are downloading an application that isn't installed yet.
- Calculate the set difference of `hashesCloud` - `hashesLocal` and store the remaining values (=without the keys i.e. the path to the files) in a set called `changedFiles`. These are all the files that are new and should either be downloaded or updated.
    - > Note: When downloading an application that isn't installed yet, `hashesCloud` - `hashesLocal` will be equal to `hashesCloud`.
- Store the set difference of `obsoleteFiles` - `changedFiles` in a set called `filesToBeDeleted`.
  - These files are no longer part of the new version and may hence be deleted.
- Store the set difference of `changedFiles` - `obsoleteFiles` in a set called `filesToBeDownloaded`.
  - These files weren't part of the previous version and have to be downloaded.
- Store the set intersection of `changedFiles` and `obsoleteFiles` in a set called `filesToBeUpdated`.
  - These are the files that changed from one version to the next. In this case we only need to update the part of the file that changed. Rather than having to download the entire file. For each file path in `filesToBeUpdated`:
    - Split the file into 1MB chunks and calculate the hash for each chunk. Store the start index of the chunk(key) and hash(value) in a Dictionary/Map called `hashesChunksLocal`
    - Download the `applicationFile.extension.hashes` file and store its content in a Dictionary/Map called `hashesChunksCloud` where the start index of the chunk is used for the key and the hash for the value.
    - Calculate the set difference between `hashesChunksCloud` - `hashesChunksLocal`, the resulting set `chunksToReplace` contains all the chunks in the file that have to be replaced.
    - Load the file that needs to be updated into memory.
    - For each key-value pair of the `chunksToReplace` Map:
      - Use the *hash*(value) of the key-value pair to download the file with the filename *hash* from the `Chunks` directory and load the file into a byte array `newBytes`.
      - Use the `newBytes` byte array and the *startIndex*(key) of the key-value pair, to replace the bytes starting from the startIndex with the `newBytes` in the file that needs to be updated that's already loaded into memory and write the file to disk again.

Following the steps above, should result in the most recent version of a given application. Without having to host patch packages.

## Enforcing a maximum filesize restriction of $x$ megabytes for an application's binaries.
I also implemented a rather simple piece functionality that limits the filesize of an application's files. If a given file is larger than $x$ megabytes, then we split the file into chunks that are each $< x$ megabytes. Once we downloaded all of the application's files, we merge those chunks into one file again.

## Verifying the file integrity of the installed applications
Here we hash all the files of a given application on the user's filesystem and compare those to the `Hashes.txt` file hosted in the cloud. In the case that certain files are missing/have a different content, we redownload/update them. If there are files present locally that aren't in `Hashes.txt` we remove them.

## Obfuscation of the launcher's bytecode
Not that anyone would bother to decompile the Java bytecode of the launcher. But in case someone would try, it would hopefully make it a bit harder for them to understand how everything works because of the obfuscation.
