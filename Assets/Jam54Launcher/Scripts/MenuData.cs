using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[Serializable]
public class MenuData
{
    public bool isAstroRunRunning, isSmashAndFlyRunning, isStelexoRunning, isAutoEditorRunning, isDGCTimerRunning, isImageSearcherRunning, isIToWRunning, isWToIRunning;
    public bool AutoUpdateAstroRun, AutoUpdateSmashAndFly, AutoUpdateStelexo, AutoUpdateAutoEditor, AutoUpdateDGCTimer, AutoUpdateImageSearcher, AutoUpdateIToW, AutoUpdateWToI;
    public string VersionAstroRun, VersionSmashAndFly, VersionStelexo, VersionAutoEditor, VersionDGCTimer, VersionImageSearcher, VersionIToW, VersionWToI;

    public string path;

    public int Language;
}
