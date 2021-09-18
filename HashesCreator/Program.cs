using System;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace HashesCreator
{
    class Program
    {
        static void Main(string[] args)
        {
            string path;
            string[] allFiles;

            path = Directory.GetCurrentDirectory();//Get the path to where this program is running from

            allFiles = Directory.GetFiles(path, "*", SearchOption.AllDirectories); //Get all the files in the current and subdirectories + their path

            for (int i = 0; i < allFiles.Length; i++)
            {
                string fileHash = ComputeHash(allFiles[i]); //Compute the hash of the current file

                allFiles[i] = allFiles[i].Replace(path + @"\", ""); //Remove the first part of the path. The result of this is just the filename.extension if the file is in the folder this program is running from. Instead of being preceded with the whole path to that file

                if (allFiles[i] == "HashesCreator.exe")  //If this index of the array has this string, remove it. We do not need it nor want it, since it this program that is creating the hashes
                {
                    allFiles[i] = "";
                }

                else //If it isn't this program itself, complete the array with the file hash
                {
                    allFiles[i] = allFiles[i] + "|" + fileHash; //Adds a seperator character, between the file name and adds the filehash after the seperator
                }
            }

            File.WriteAllLines(path + @"\Hashes.txt", allFiles.Where(l => !string.IsNullOrEmpty(l))); //Write the name + seperator + hashes out to a file and place it in the current folder(where this program is running from)
            //The last part of this line makes sure there aren't any empty lines getting written to the file



        }

        private static string ComputeHash(string filePath)
        {
            using (SHA256 mySHA256 = SHA256.Create()) //Initialize a SHA256 object, this will be used to compute the hashes
            {
                FileStream fileStream = File.OpenRead(filePath); //Create a filestream for the file

                fileStream.Position = 0; //Be sure the filestream is positioned in the beginning of the file

                byte[] hashValue = mySHA256.ComputeHash(fileStream); //Compute the hash of the file

                return ByteArrayToString(hashValue); //Convert the byte array to a string

            }
        }


        private static string ByteArrayToString(byte[] byteArray)
        {
            StringBuilder sOutput = new StringBuilder(byteArray.Length);
            for (int i = 0; i < byteArray.Length - 1; i++)
            {
                sOutput.Append(byteArray[i].ToString("X2"));
            }
            return sOutput.ToString();
        }
    }
}
