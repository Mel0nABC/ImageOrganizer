package com.example.WebLogin.filesControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadConfigPath {


    private static String SEPARADOR = File.separator;
    private static File file = new File(System.getProperty("user.dir") + SEPARADOR+"src"+SEPARADOR+"main"+SEPARADOR+"resources"+SEPARADOR+"config.conf");
//    private static String CONFIGPATH = System.getProperty("user.dir") + SEPARADOR+"src"+SEPARADOR+"main"+SEPARADOR+"resources"+SEPARADOR+"config.conf";

    public static String readPath(String configpath) {

          File file = new File(configpath);
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
                scan.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("El fichero NOOOOO existe");
        }
        return configPath;
    }

    @SuppressWarnings("null")
    public static File[] getConfigDirList() {

        ReadConfigPath.getConfigFileExist();


        List<File> directoriosImagenes = new ArrayList<>();
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("El archivo no existe");
        }

        while (scan.hasNext()) {
            String readLine = scan.nextLine();
            String[] readLineParts = readLine.split("=");
            for (int i = 0; i < readLineParts.length; i++) {
                if (readLineParts[i].equals("configpath")) {
                    File testDir = new File(readLineParts[i + 1]);
                    if (testDir.exists()) {
                        directoriosImagenes.add(testDir);
                    }
                }
            }
        }
        scan.close();
        File[] listDirs = new File[directoriosImagenes.size()];
        for(int i = 0; i < directoriosImagenes.size();i++){
            listDirs[i] = directoriosImagenes.get(i);
        }
        return listDirs;
    }

    public static Boolean getConfigFileExist(){
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if(!file.exists()){
            return false;
        }

        return true;
    }



}
