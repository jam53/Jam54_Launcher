using System;
using System.Drawing;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Drawing.Imaging;

namespace MakePNGGrayScale
{
    class Program
    {
        static void Main(string[] args)
        {
            string path = Directory.GetCurrentDirectory(); //the directory this program is running from
            List<string> Images = Directory.GetFiles(path, "*.png", SearchOption.AllDirectories).ToList(); //Get all the .png files in the current, and subdirectories. And put the path to the image in this list

            foreach (string image in Images) //Loop over every single path to a png in the list
            {
                Bitmap bitmapImage = new Bitmap(image); //Create a bitmap from the png
                MakeImageGrayScale(bitmapImage); //This overwrites the bitmap and returns it in grayscale

                bitmapImage.Save("Grey_" + Path.GetFileName(image), ImageFormat.Png); //Add a prefix "Grey_" to the filename, and save the grayscale image in the folder the program is running from
            }
        }

        private static Bitmap MakeImageGrayScale(Bitmap original)
        {
            int r, g, b;
            int average;
            Color grayPixel;

            for (int x = 0; x < original.Width; x++)
            {//Loop through all of the pixels, and set them one by one
                for (int y = 0; y < original.Height; y++)
                {
                    r = original.GetPixel(x, y).R; //Get the r,g,b values of the pixel
                    g = original.GetPixel(x, y).G;
                    b = original.GetPixel(x, y).B;

                    average = (r + g + b) / 3; //Take the average of the r,g,b values of that pixels. This average will represent a grey color

                    grayPixel = Color.FromArgb(average, average, average); //Create the grey color

                    original.SetPixel(x, y, grayPixel); //Set the pixel to that newly created grey
                }
            }

            return original; //Return the now grey image
        }
    }
}
