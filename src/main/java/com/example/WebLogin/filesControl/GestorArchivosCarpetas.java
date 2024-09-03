package com.example.WebLogin.filesControl;

import java.io.File;

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
