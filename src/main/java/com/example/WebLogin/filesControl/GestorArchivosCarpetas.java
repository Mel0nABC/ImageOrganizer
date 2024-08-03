/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.WebLogin.filesControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author mel0n
 */

public class GestorArchivosCarpetas {

    public static File[] getFileDirList(String folder) {

        File path = new File(folder);
        File[] fileListDir = null;
        if (path.exists()) {
            fileListDir = path.listFiles();
        }
        return fileListDir;
    }


}
