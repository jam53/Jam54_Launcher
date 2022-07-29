# Understanding What's In The Repository
This repository consists of 2 projects, the *Jam54_Launcher* itself, and the *Updater*.

# Jam54_Launcher
This IntelliJ project contains the main program, i.e. the *Jam54Launcher* that manages all of the applications. It is used to download applications and helps to keep them up to date.

# Updater
Apart from the *Jam54Launcher*, there is another project called *Updater*. This is a C# console application whose sole purpose is to update the *Jam54Launcher* itself. 

# How does it work
The *Jam54Launcher* is the main program, and will normally be launched first. Now let's say there is a new update available for the *Jam54Launcher*. 

In this case the *Jam54Launcher* will start downloading the necessary files in the background. Once it's finished, there will be a green button at the top of the screen that allows the user to relaunch the program in order to complete the update.

One of two things could happen:
- **A)** The user presses on the green button to relaunch the program. 
- **B)** The user closes the program without pressing the button.

## Option A: The User Chose To Relaunch The Program
- In this scenario the *Jam54Launcher* will launch the *Updater*, after which it will immediately close itself
- Once the *Updater* has been launched, it will look for the newly downloaded version of the *Jam54Launcher*. It will then replace the old version of the *Jam54Launcher* with the new one.
- Once the files have been updated, the *Updater* will first launch the Jam54Launcher, before it closes itself
- The *Jam54Launcher* has now been updated, and is ready to be used.

## Option B: The User Closed The Program
- In this scenario the update process will continue the next time the *Jam54Launcher* is launched by the user.
- Right after getting launched, the *Jam54Launcher* will check if a new version was downloaded the previous time it was launched. Which will be the case this time around.
- The *Jam54Launcher* will now launch the *Updater*, after which it will immediately close itself.
- Once the *Updater* has been launched, it will look for the newly downloaded version of the Jam54Launcher. It will then replace the old version of the *Jam54Launcher* with the new one.
- Once the files have been updated, the *Updater* will first launch the Jam54Launcher, before it closes itself
- The *Jam54Launcher* has now been updated, and is ready to be used.