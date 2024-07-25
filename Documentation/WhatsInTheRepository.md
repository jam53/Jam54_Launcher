# Understanding What's In The Repository
This repository consists of 2 projects, the *Jam54_Launcher* itself, and the *Updater*.

## Jam54_Launcher
This Java project contains the main program, i.e. the *Jam54Launcher* that manages all of the applications. It is used to download applications and helps to keep them up to date.

## Updater
Apart from the *Jam54Launcher*, there is another project called *Updater*. This is a C# console application whose sole purpose is to update the *Jam54Launcher* itself. 

## How It Works
The *Jam54Launcher* is the main program and is usually launched first. When a new update for the *Jam54Launcher* is available, the launcher begins downloading the necessary files in the background. 

Once the download is complete, a green button will appear at the top of the screen allowing the user to relaunch the launcher to complete the update.

Two scenarios can occur:
1. The user clicks the green button to relaunch the program.
2. The user closes the program without clicking the button.

### Option A: The User Chooses to Relaunch the Program
- In this scenario, the *Jam54Launcher* will start the *Updater* and then immediately close itself.
- The *Updater* will look for the newly downloaded version of the *Jam54Launcher* and replace the old version with the new one.
- After updating the files, the *Updater* will launch the new version of the *Jam54Launcher* before closing itself.
- The *Jam54Launcher* is now updated and ready for use.

### Option B: The User Closes the Program
- If the user closes the program without updating, the update process will resume the next time the *Jam54Launcher* is started.
- Upon launching, the *Jam54Launcher* will check if a new version was downloaded during the previous session. In this case it will find that a new version is available.
- The *Jam54Launcher* will then start the *Updater* and close itself.
- The *Updater* will replace the old version of the *Jam54Launcher* with the newly downloaded version.
- After the update, the *Updater* will launch the updated *Jam54Launcher* before closing itself.
- The *Jam54Launcher* is now updated and ready for use.