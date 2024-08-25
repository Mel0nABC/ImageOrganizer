package com.example.WebLogin.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class WatchingDirectory {

    // Variable para el control de bucles de carpetas.
    private static Map<String, Thread> listThreads = new HashMap<String, Thread>();
    private static Map<String, WatchingDirectoryThread> listWatchingObjects = new HashMap<String, WatchingDirectoryThread>();
    private static String initialPath = "";

    // public WatchingDirectory(String initialPath){
    // this.initialPath = initialPath;
    // listPath(new File(initialPath));
    // }

    public void setInitialPath(String initialPath) {
        this.initialPath = initialPath;
        listPath(new File(initialPath));

    }

    public static void listPath(File path) {
        File[] dirList = path.listFiles();

        if (initialPath.equals(path.getAbsolutePath())) {
            newthread(path.getAbsolutePath());
        }

        if (dirList.length != 0) {
            for (File f : dirList) {
                if (f.isDirectory()) {
                    listPath((f.getAbsoluteFile()));
                    newthread(f.getAbsolutePath());
                }
            }
        } else {
            // Cuando se crea un nuevo diectoio, con el sevicio ejecutándose.
            newthread(path.getAbsolutePath());
        }
    }

    public static void newthread(String path) {
        WatchingDirectoryThread wtr = new WatchingDirectoryThread();
        wtr.setPath(path);
        Thread tr = new Thread(wtr);
        tr.setName(path);
        tr.start();
        listThreads.put(path, tr);
        listWatchingObjects.put(path, wtr);
    }

    public static Map<String, Thread> getThreads() {
        return listThreads;
    }

    public static boolean stopThead(String path) {
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
                if (!stopThead(path)) {
                    System.out.println("Stop control directorio --> " + path);
                    break;
                }else{
                    System.out.println("Error al detener el thread, quizá era un archivo --> "+path);
                }

            }
        }
    }

    public static void deleteObjectList(String path) {
        listWatchingObjects.remove(path);
        listThreads.remove(path);
    }

}
