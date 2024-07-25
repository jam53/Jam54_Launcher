# General Overview of the Jam54 Laucher
## The Idea
Initially I developed a few programs but lacked a central place for people to download them from. This led me to create a launcher that bundles all the applications I developed into one place.

Before the current implementation, I used Unity to develop the launcher. In hindsight this might not have been the best choice, which is why I recently rewrote it. At the time Unity was the only platform/framework I felt comfortable working with.

This latest iteration of the launcher was made using Java/JavaFX. I opted for a "native desktop app" because I needed to handle a lot of IO operations to download, patch, and install other applications. However, I now understand that web apps using Electron or Tauri are also quite capable of handling IO operations.  
Knowing what I know now, I would probably choose to implement the GUI part of the launcher as a web app if I had to start over. For backend logic and general functionality however, I believe Java would still be the better choice. Nevertheless, the Java/JavaFX implementation works well enough so I will leave it as it is for now.

## Features
The main features are as follows:
- [An Easy Way to Add Applications to the Launcher or Update Existing Ones](#an-easy-way-to-add-applications-to-the-launcher-or-update-existing-ones)
- [The Launcher Updates Itself](#the-launcher-updates-itself)
- [Applications Are Kept Up to Date Using Delta Updates](#applications-are-kept-up-to-date-using-delta-updates)
- [Enforcing a Maximum File Size Restriction of X Megabytes for an Application's Binaries](#enforcing-a-maximum-file-size-restriction-of-x-megabytes-for-an-applications-binaries)
- [Verifying the File Integrity of Installed Applications](#verifying-the-file-integrity-of-installed-applications)
- [Obfuscation of the launcher's bytecode](#obfuscation-of-the-launchers-bytecode)

## Limitations
Only one application can be downloaded, updated, or removed at a time. However, multiple applications can be queued for sequential installation or updates.

## An Easy Way to Add Applications to the Launcher or Update Existing Ones
In my previous Unity implementation of the launcher, my limited coding experience led me to hardcode all the applications into the source code. This approach was difficult to manage and didn't scale well. Therefore, I prioritized improving this aspect in the new Java implementation.

For this purpose, there are two files involved: 
1. **`applications.sqlite`** - A local database included with the launcher that contains details about the applications (e.g., name, picture, description, release date, supported platforms, etc.).
2. **`applicationsVersions.properties`** - A configuration file hosted in the cloud used to check for updates to the applications.

The local database manages application details, while the config file helps determine if new updates are available for a particular application.

By using this database file, we can easily add new applications to the launcher or update existing ones simply by adding or modifying entries in the database. Since all application data is now contained within this database, we have eliminated the issue of hardcoding application information directly into the launcher's source code.

The `sqlite3` database consists of just two simple tables, as shown below:

```sql
CREATE TABLE applications (
    id INT PRIMARY KEY, 
    name TEXT, 
    logo TEXT, 
    android INT, 
    web INT, 
    windows INT, 
    releaseDate INT, 
    latestUpdate INT, 
    isGame INT
);

CREATE TABLE application_description (
    language TEXT, 
    id INT REFERENCES applications(id), 
    description TEXT, 
    PRIMARY KEY (language, id)
);
```

The `applications` table holds basic information about each application, while the `application_description` table contains descriptions of each application in different languages.

The `applicationsVersions.properties` file might look something like this:

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

This file contains version information for each application, where each entry maps an application identifier (e.g., `app0`, `app1`, etc.) to its current version number.

The `applicationsVersions.properties` file is downloaded from its cloud-hosted location. The version numbers within this file are used to check for updates for any of the installed applications.

## The Launcher Updates Itself
The launcher consists of two components: the main *Jam54Launcher* program and a separate *Updater* application.

The Java-based *Jam54Launcher* is the core component that manages all applications. It handles downloading and updating the applications.

In addition to the *Jam54Launcher*, there is a C# console application called *Updater*. Its sole purpose is to update the *Jam54Launcher* itself.

### How It Works
The *Jam54Launcher* is the main program and is usually launched first. When a new update for the *Jam54Launcher* is available, the launcher begins downloading the necessary files in the background. 

Once the download is complete, a green button will appear at the top of the screen allowing the user to relaunch the launcher to complete the update.

Two scenarios can occur:
1. The user clicks the green button to relaunch the program.
2. The user closes the program without clicking the button.

#### Option A: The User Chooses to Relaunch the Program
- In this scenario, the *Jam54Launcher* will start the *Updater* and then immediately close itself.
- The *Updater* will look for the newly downloaded version of the *Jam54Launcher* and replace the old version with the new one.
- After updating the files, the *Updater* will launch the new version of the *Jam54Launcher* before closing itself.
- The *Jam54Launcher* is now updated and ready for use.

#### Option B: The User Closes the Program
- If the user closes the program without updating, the update process will resume the next time the *Jam54Launcher* is started.
- Upon launching, the *Jam54Launcher* will check if a new version was downloaded during the previous session. In this case it will find that a new version is available.
- The *Jam54Launcher* will then start the *Updater* and close itself.
- The *Updater* will replace the old version of the *Jam54Launcher* with the newly downloaded version.
- After the update, the *Updater* will launch the updated *Jam54Launcher* before closing itself.
- The *Jam54Launcher* is now updated and ready for use.

## Updater
As previously mentioned, the *Updater* is a C# console application that simply replaces the old version of the launcher with the newly downloaded version. The following class contains all the code used in the *Updater* program:

```csharp
// Main.cs
using System.Diagnostics;
using System.IO;
using System.Threading;

class Updater
{
    static void Main(string[] args)
    {
        Thread.Sleep(5000); // Wait for the Jam54Launcher to close

        // Define paths for the old and new versions of the launcher
        string oldLauncher = Path.Combine(Directory.GetCurrentDirectory(), "..", "Jam54_Launcher.jar");
        string newLauncher = Path.Combine(Directory.GetCurrentDirectory(), "..", "Jam54_Launcher_New.jar");

        // Path to the executable used to run the Jam54 Launcher
        string jam54Launcher = Path.Combine(Directory.GetCurrentDirectory(), "..", "..", "Jam54 Launcher.exe");

        // Only proceed if a new version is available
        if (File.Exists(newLauncher))
        {
            File.Move(newLauncher, oldLauncher, true); // Replace the old launcher with the new version
            Process.Start(jam54Launcher); // Launch the updated Jam54 Launcher
            File.Delete(newLauncher); // Remove the downloaded file after installation
        }
    }
}
```

## Applications Are Kept Up to Date Using Delta Updates
This is an another area where I made significant improvements. In the Unity implementation, the update process was fairly straightforward: each application had a zip file containing its latest binaries. When a user chose to download an application, I would simply download and unzip the file. If an update was needed for an already installed application, I would delete the old binaries, download the new zip file, and unzip it.

This approach however, was inefficient for updating existing installations. If only a single file of an application changed, the user would still need to redownload and replace the entire application. 

I tried to improve on this in the Java implementation.
After some research I came to the conclusion that most people/companies tend to have a full package containing all of the binaries. After which patch packages are shipped for every new release. For me this initially caused a problem since I only wanted to host the latest versions of said applications. I didn't want to host both the full binary for every release and patch packages to go from one version to the next.

Eventually I did implement an update system for already installed apps using patch packages. In this delta update system, patches are represented as a zip file. Which in turn contain the deltas for each of the files that were changed in the update.  
The creation of patches and the way they are applied roughly goes as follows:

Suppose we have one folder which contains the latest version of an app. We then have another folder which contains subfolders for each of the previous versions of the app for which we would like to generate a patch. The name of these subfolders correspond to the version of the app in the subfolder.

We can now loop over all the subfolders with previous versions of the app, and compare each of them to the folder that contains the latest version. This process will result in a new folder called `Deltas` to be created. After each iteration we generate a zip file which we will place in this `Deltas` folder.  
The created zip file represents the patch that can be used to go from one version to the next. The name of the zip file follows the format `A.B.C-X.Y.Z.zip` where `A.B.C` represents the source version and `X.Y.Z` destination version of the app to which the patch applies. 

The content of these zip files (patches) mirror the exact same directory/file structure of all the files which are present in the destination version of the app, and were also present in the source version directory. With the exception that every filename is appended with the `.gdiff` suffix.  
This means that files that were deleted won't have a corresponding `.gdiff` file in the patch (the zip file) and neither will new files that didn't exist in the source. However, files that haven't been changed from one version to the next still have a corresponding `.gdiff` file.

As could already be inferred from above, the delta between two different versions of the same file is stored in a `.gdiff` file. GDIFF is a binary format that is used to store binary deltas.  
The binary deltas are computed using the xdelta algorithm/program originally developed by Joshua MacDonald. xdelta generates the difference between two binary files. Whose output which we call a *diff*, is then stored in the GDIFF format.

When it comes to applying the patch. We first compute the hashes of all the files that are currently present on disk. These are then compared to the hashes of all the files in the new version of the app. The hashes of the files present in the new version of the app can be obtained from the `Hashes.txt` file, more about this file later.  
Based on the hashes of our locally stored files and the ones of the new version, we can compute the files that need to be deleted, downloaded and updated. Deletable files are files that are no longer present in the new version and may therefore be deleted, this is done first. The next step is to download new files in their entirety that weren't present in the previous version. The remaining files are the ones that were both present in the previous and current version of the app. These are the ones we will update/patch using the deltas we computed beforehand.  
The patching process of these files is performed by iterating over all of the `.gdiff` files present in the zip that represents our patch. Using the old version of the file on disk and the corresponding `.gdiff` file from our patch we can create the new version of the file.  
Once all the files that needed to be deleted, downloaded or updated have been processed, we perform a final check by hashing all of the files of our app once again. Should there be a discrepency between these hashes that we computed and the ones we expect based on the `Hashes.txt` file. In that case we redownload these files in their entirety and replace the local file that was obtained by applying a delta.

Finally, the patches represented by zip files can be split into smaller parts if they are larger than a specified file size treshhold. The previously mentioned format `A.B.C-X.Y.Z.zip` now becomes `A.B.C-X.Y.Z.zip.partN` where `N` represents the index of the splitted zip file starting at 1.

Initially I implemented delta updates using chunks, this was later superseded by delta updates that utilize binary diffs which is explained above. The albeit worse way of using chunks for delta updates can be found below for the sake of completeness:
- For any new version of an application, hash all of the files and store them in a file named `Hashes.txt`, which is placed in the root directory of the application's files.
    - Example of `Hashes.txt`
```
10521fe73fe05f2ba95d40757d9f676f2091e2ed578da9d5cdef352f986f3bcd|runtime\bin\ucrtbase.dll
2afbfa1d77969d0f4cee4547870355498d5c1da81d241e09556d0bd1d6230f8c|runtime\bin\api-ms-win-core-console-l1-1-0.dll
...
```
- For each file that's part of the application, split the file into chunks of 1MB (1024^2 bytes) and store these chunks in a folder named `Chunks` in the root directory of the application's files. Where the filename of each 1MB chunk is the hash of that 1MB file.
  - For each file in the application, create a corresponding file with the same name and `.hashes` appended to the end of the filename located in the same directory. This corresponding `.hashes` file should list all 1MB chunks associated with the original file. Each entry in this file includes the hash of the 1MB chunk and the byte offset where the chunk starts in the original file.
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

## Enforcing a Maximum File Size Restriction of X Megabytes for an Application's Binaries
I implemented a feature to enforce a maximum file size restriction for an application's binaries. If a file exceeds `X` megabytes, it is split into smaller chunks each less than `X` megabytes. After downloading all the chunks, they are merged back together to form the complete file.

## Verifying the File Integrity of Installed Applications
We verify the integrity of installed applications by hashing all the files associated with the application on the user's filesystem and comparing these hashes to those listed in the `Hashes.txt` file hosted in the cloud. If any files are missing or have different content, they are re-downloaded. Conversely, any local files not listed in `Hashes.txt` are removed.

## Obfuscation of the Launcher's Bytecode
Although it's unlikely that someone would attempt to decompile the Java bytecode of the launcher, obfuscation is used to make it more challenging to understand the inner workings of the code. This added layer of complexity helps protect the launcher's implementation from reverse engineering.
