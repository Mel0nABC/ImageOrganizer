/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.WebLogin.filesControl;

import java.io.File;

/**
 * @author mel0n
 */

public class GestorArchivosCarpetas {

    public static File[] getFileDirList(String folder) {
        System.out.println("LECTURA DIRECTORIO: "+folder);

        File path = new File(folder);
        File[] fileListDir = null;
        if (path.exists()) {
            fileListDir = path.listFiles();
        }
        return fileListDir;
    }

}
