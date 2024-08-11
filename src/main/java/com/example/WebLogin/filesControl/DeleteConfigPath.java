package com.example.WebLogin.filesControl;

import java.io.File;

public class DeleteConfigPath {

    private static String SEPARADOR = File.separator;

    public static boolean delConfigDirList() {
        File file = new File(System.getProperty("user.dir") + SEPARADOR + "src" + SEPARADOR + "main" + SEPARADOR
                + "resources" + SEPARADOR + "config.conf");

                if(file.exists()){
                    file.delete();
                }
                if(file.exists()){
                    return false;
                }
        return true;
    }
}
