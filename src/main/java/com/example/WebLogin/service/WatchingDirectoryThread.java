package com.example.WebLogin.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Map;

public class WatchingDirectoryThread implements Runnable {

    private Map<String, Thread> listaThreads;
    private boolean watchServiceRun = true;
    private String path = "";

    @Override
    public void run() {
        // TODO Auto-generated method stub
        wathDirectory();
    }

    public void wathDirectory() {
        try {

            // Step 1: Create a Watch Service
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Step 2: Specify the directory which is supposed to be watched.
            Path directorio = Paths.get(path);

            // Step 3: Register the directory path for specific events.
            directorio.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            // Step 4: Poll the events in an infinite loop.
            System.out.println("Controlando directorio --> " + directorio);
            while (watchServiceRun) {
                try {
                    WatchKey key = watchService.take();
                    String respuesta = "";
                    for (WatchEvent<?> event : key.pollEvents()) {

                        // Step 5: From the event context get the file name for each event.
                        // Step 6: Check the type for each event.
                        // Step 7: Perform actions for each type of event.
                        // Handle the specific event

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            File newDir = new File(path + File.separator + event.context());
                            if (newDir.isDirectory()) {
                                System.out.println("VAMOS A CREAR NUEVO THREAD");
                                WatchingDirectory.listPath(newDir);
                            }
                            respuesta = "File created,on --> " + path + " - " + event.context();
                            System.out.println(respuesta);
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            File delDir = new File(path + File.separator + event.context());
                            WatchingDirectory.stopThreads(delDir.getAbsolutePath());

                            respuesta = "File deleted,on --> " + path + " - " + event.context();
                            System.out.println(respuesta);
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            respuesta = "File modified,on --> " + path + " - " + event.context();
                            System.out.println(respuesta);
                        }

                    }
                    writeLog(respuesta);
                    // Step 8: Reset the watch key.
                    key.reset();
                } catch (InterruptedException e) {
                    // Eliminamos objetos de las listas de Threads y WatchingDirectoryThread
                    // WatchingDirectory.deleteObjectList(path);
                }
            }

        } catch (

        IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeLog(String activity) {
        FileWriter escribe;
        File fichero = new File("/media/Almacenamiento/Download/logs/log.txt");
        String date = new GregorianCalendar().toZonedDateTime()
                .format(DateTimeFormatter.ofPattern("d MMM uuuu - HH:mm:ss"));
        try {
            if (!fichero.exists()) {
                fichero.createNewFile();
            }
            escribe = new FileWriter(fichero, true);
            BufferedWriter guarda = new BufferedWriter(escribe);
            guarda.append(date + " - " + activity);
            guarda.newLine();
            guarda.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean getState() {
        return watchServiceRun;
    }

    public void setStop(boolean watchServiceRun) {
        this.watchServiceRun = watchServiceRun;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
