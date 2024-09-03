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

import com.example.WebLogin.WebLoginApplication;

/**
 * Monitorea un directorio específico, controla la actividad en crear, borrar o
 * modificaciones en el interior, tanto de archivos como carpetas.
 */
public class WatchingDirectoryThread implements Runnable {

    private final String SEPARADOR = File.separator;
    private boolean watchServiceRun = true;
    private String path = "";
    private ImagePreviewService imagePreviewService;

    @Override
    public void run() {
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
            String respuesta = "";
            while (watchServiceRun) {
                try {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        // Step 5: From the event context get the file name for each event.
                        // Step 6: Check the type for each event.
                        // Step 7: Perform actions for each type of event.
                        // Handle the specific event

                        imagePreviewService.findImagesToResize(new File(path));

                        File ruta = new File(path + SEPARADOR + event.context());

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            if (ruta.isDirectory()) {
                                WatchingDirectory.listPath(ruta);
                            }
                            respuesta = "CREATED ON --> " + path + SEPARADOR + event.context();
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {

                            // Si lo borrado, contiene el dir de previews, escanearemos el directorio padre
                            // de previews en busca de diferencias (por si se borra una preview.)
                            if (path.contains(ImagePreviewService.getDIR_PREVIEW())) {
                                imagePreviewService.findImagesToResize(
                                        new File(path.split(ImagePreviewService.getDIR_PREVIEW())[0]));
                            }
                            respuesta = "DELETED ON --> " + path + SEPARADOR + event.context();

                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            respuesta = "MODIFIES ON --> " + path + SEPARADOR + event.context();
                        }
                        writeLog(respuesta);
                    }

                    // Step 8: Reset the watch key.
                    key.reset();
                } catch (InterruptedException e) {
                    // Eliminamos objetos de las listas de Threads y WatchingDirectoryThread
                    // WatchingDirectory.deleteObjectList(path);
                }
            }

        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLog(String activity) {
        FileWriter escribe;
        File fichero = WebLoginApplication.getLogFilePath();
        String date = new GregorianCalendar().toZonedDateTime()
                .format(DateTimeFormatter.ofPattern("d MMM uuuu - HH:mm:ss"));
        try {

            escribe = new FileWriter(fichero, true);
            BufferedWriter guarda = new BufferedWriter(escribe);
            guarda.append(date + " - " + activity);
            guarda.newLine();
            guarda.close();
        } catch (IOException e) {
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

    public void setImagePreviewService(ImagePreviewService imagePreviewService) {
        this.imagePreviewService = imagePreviewService;
    }

}
