package com.example.WebLogin.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Genera threads del tipo WatchingDirectoryThread para controlar la actividad
 * de una ruta local.
 */
@Service
public class WatchingDirectory {

    // Variable para el control de bucles de carpetas.
    private static Map<String, Thread> listThreads = new HashMap<String, Thread>();
    private static Map<String, WatchingDirectoryThread> listWatchingObjects = new HashMap<String, WatchingDirectoryThread>();
    private static ImagePreviewService imagePreviewService;

    public WatchingDirectory(UserDetailServiceImpl userDetailServiceImpl, ImagePreviewService imagePreviewService) {
        WatchingDirectory.imagePreviewService = imagePreviewService;

        userDetailServiceImpl.getAllPathList().forEach(path -> {
            setInitialPath(path.getPath_dir());
        });
    }

    public void setInitialPath(String initialPath) {
        System.out.println(
                "##################################################################################################");
        System.out.println("## INICIAMOS WATCHDIRECTORY EN --> " + initialPath);
        System.out.println(
                "##################################################################################################");
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

    public static void stopThreads(String path) {
        File[] listSubdir = new File(path).listFiles();

        for (File f : listSubdir) {
            if (!delThead(f.getAbsolutePath())) {
                System.out.println("Stop control directorio --> " + f.getAbsolutePath());
            } else {
                System.out.println("Error al detener el thread, quizá era un archivo --> " + f.getAbsolutePath());
            }
        }

        if (!delThead(path)) {
            System.out.println("Stop control directorio --> " + path);
        } else {
            System.out.println("Error al detener el thread, quizá era un archivo --> " + path);
        }
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

    public static void deleteObjectList(String path) {
        listWatchingObjects.remove(path);
        listThreads.remove(path);
    }

}
