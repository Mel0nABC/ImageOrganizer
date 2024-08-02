package com.example.WebLogin.filesControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadConfigPath {


    private static String SEPARADOR = File.separator;
    private static String CONFIGPATH = System.getProperty("user.dir") + SEPARADOR+"src"+SEPARADOR+"main"+SEPARADOR+"resources"+SEPARADOR+"config.conf";

    public static String readPath() {
        File file = new File(CONFIGPATH);
        String configPath = "";
        if (file.exists()) {
            try {
                Scanner scan = new Scanner(file);
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    String[] palabras = line.split("=");
                    if(palabras[0].equals("configpath")){
                        configPath = palabras[1];
                    }
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("El fichero NOOOOO existe");
        }
        return configPath;
    }


}
