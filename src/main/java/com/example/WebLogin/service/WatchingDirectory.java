package com.example.WebLogin.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.WebLogin.persistence.entity.PathEntity;

@Service
public class WatchingDirectory {

    // Variable para el control de bucles de carpetas.
    private static Map<String, Thread> listThreads = new HashMap<String, Thread>();
    private static Map<String, WatchingDirectoryThread> listWatchingObjects = new HashMap<String, WatchingDirectoryThread>();
    private static String initialPath;
    private UserDetailServiceImpl userDetailServiceImpl;
    private static ImagePreviewService imagePreviewService;

    public WatchingDirectory(UserDetailServiceImpl userDetailServiceImpl,ImagePreviewService imagePreviewService) {
        this.userDetailServiceImpl = userDetailServiceImpl;
        this.imagePreviewService = imagePreviewService;

        // Obtenemos la lista completa de paths de la tabla path y inicializamos control
        // de directorios.
        userDetailServiceImpl.getAllPathList().forEach(path -> {
            setInitialPath(path.getPath_dir());
        });
    }

    public void setInitialPath(String initialPath) {
        this.initialPath = initialPath;
        System.out.println("##################################################################################################");
        System.out.println("## INICIAMOS WATCHDIRECTORY EN --> " + initialPath);
        System.out.println("##################################################################################################");
        listPath(new File(initialPath));
    }

    public static void listPath(File path) {
        File[] dirList = path.listFiles();

        for (File f : dirList) {
            if (f.isDirectory()) {
                listPath((f.getAbsoluteFile()));
            }
        }
        newthread(path.getAbsolutePath());
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void newthread(String path) {
        WatchingDirectoryThread wtr = new WatchingDirectoryThread();
        wtr.setPath(path);
        wtr.setImagePreviewService(imagePreviewService);
        Thread tr = new Thread(wtr);
        tr.setName(path);
        tr.start();
        listThreads.put(path, tr);
        listWatchingObjects.put(path, wtr);
    }

    public static Map<String, Thread> getThreads() {
        return listThreads;
    }

    public static boolean delThead(String path) {
        WatchingDirectoryThread wtStop = listWatchingObjects.get(path);
        if (wtStop != null) {
            wtStop.setStop(false);
            Thread trStop = listThreads.get(path);
            trStop.interrupt();
            while (trStop.isAlive()) {
            }
            deleteObjectList(path);
            return trStop.isAlive();
        }
        return true;
    }

    public static void stopThreads(String path) {
        for (Map.Entry<String, Thread> entry : listThreads.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                if (!delThead(path)) {
                    System.out.println("Stop control directorio --> " + path);
                    break;
                } else {
                    System.out.println("Error al detener el thread, quizá era un archivo --> " + path);
                }

            }
        }
    }

    public static void deleteObjectList(String path) {
        listWatchingObjects.remove(path);
        listThreads.remove(path);
    }

}
