package com.example.WebLogin.filesControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WriteConfigPath {

    private static String SEPARADOR = File.separator;

        @SuppressWarnings("null")
    public static Boolean writeConfigDirList(String newPath) {
        File file = new File(System.getProperty("user.dir") + SEPARADOR+"src"+SEPARADOR+"main"+SEPARADOR+"resources"+SEPARADOR+"config.conf");
        System.out.println("FICHERO --> "+file.getAbsolutePath());
        try {
            FileWriter write = new FileWriter(file.getAbsoluteFile(), true);
            write.append("\nconfigpath="+newPath);
            write.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return false;
        }


        if(!file.exists()){
            try {
                file.createNewFile();
                System.out.println("CREANDOOOOOOOOOOOOOOOOOOOOOO");
                if(file.exists()){
                    System.out.println("COMPROBADO QUE SI ESTÁ CREADO");
                }else{
                    System.out.println("NO ESTÁ CREADO");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return true;
    }
}
