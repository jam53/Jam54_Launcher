using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(fileName = "New AppsInfo", menuName = "AppInfo")]
public class AppsInfo : ScriptableObject
{
    public string Title;

    public Texture2D Image;

    public Color BackgroundColor;

    public string LatestUpdate;

    public string ReleaseDate;

    public bool Android;

    public bool Windows;

    public string description;
}
