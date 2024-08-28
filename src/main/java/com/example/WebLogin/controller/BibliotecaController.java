package com.example.WebLogin.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.WebLogin.filesControl.GestorArchivosCarpetas;
import com.example.WebLogin.persistence.entity.PathEntity;
import com.example.WebLogin.persistence.entity.UserEntity;
import com.example.WebLogin.service.UserDetailServiceImpl;
import com.example.WebLogin.service.WatchingDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class BibliotecaController {

    private String SEPARADOR = File.separator;
    private ObjectMapper mapper = new ObjectMapper();
    private UserDetailServiceImpl userDetailsService;
    private WatchingDirectory watchingDirectory;
    private boolean noExistPath = true;

    public BibliotecaController(UserDetailServiceImpl userDetailsService, WatchingDirectory watchingDirectory) {
        this.userDetailsService = userDetailsService;
        this.watchingDirectory = watchingDirectory;
    }

    /**
     * Devolvemos la lista de carpetas que tiene el usuario configuradas para sus
     * bibliotecas
     * 
     * @return
     */
    @GetMapping("/openConfigDirectory")
    @ResponseBody
    public ObjectNode configDirectory() {

        File[] configDirsTemp = DashBoardController.getPathList(DashBoardController.username);
        List<File> configDirs = new ArrayList<>();

        for (File f : configDirsTemp) {
            configDirs.add(f);
        }
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("configDirs", configDirs);
        return json;
    }



    /**
     * Para guardar una nueva biblioteca en la base de datos.
     * 
     * @param folderPath
     * @return
     */
    @PostMapping("/confirmNewPath")
    @ResponseBody
    public Boolean confirmNewPath(@RequestParam("newFolderParh") String folderPath) {

        if (!new File(folderPath).exists()) {
            return false;
        }

        UserEntity user = userDetailsService.getUserByUsername(DashBoardController.username);
        Set<PathEntity> listaDirectorios = user.getPatchList();
        PathEntity newPath;

        // Comprobamos que el usuario tiene ya asignado ese directorio (no debería)

        for (PathEntity path : listaDirectorios) {
            if (path.getPath_dir().equals(folderPath)) {
                return false;
            }
        }

        Set<PathEntity> pathList = user.getPatchList();
        Set<PathEntity> completePathList = userDetailsService.getAllPathList();

        if (completePathList.size() == 0) {
            newPath = new PathEntity();
            newPath.setPath_dir(folderPath);
            pathList.add(newPath);
            user.setPatchList(pathList);
        }

        if (completePathList.size() > 0) {
            completePathList.forEach(path -> {

                if (path.getPath_dir().equals(folderPath)) {
                    noExistPath = false;
                    pathList.add(path);
                }
            });

            if (noExistPath) {
                newPath = new PathEntity();
                newPath.setPath_dir(folderPath);
                pathList.add(newPath);
            }

            user.setPatchList(pathList);
        }
        if (!userDetailsService.setUSerEntity(user)) {
            return false;
        }
        watchingDirectory.setInitialPath(folderPath);
        return true;
    }


    /**
     * Mapping para cuando accedemos a la configuración de bibliotecas del usuario,
     * proporciona las que ya tenga configuradas.
     * 
     * @param path
     * @return
     */
    @PostMapping("/editDirectory")
    @ResponseBody
    public ObjectNode editDirectory(@RequestParam("path") String path) {
        ObjectNode json = null;
        String osName = System.getProperty("os.name");
        osName = osName.substring(0, 3);
        if (path.equals("rootUnits")) {
            if (osName.equals("Win")) {
                json = getWinDir(path);
            } else {
                json = getUnixDir("/");
            }
        } else {
            if (osName.equals("Win")) {
                json = getWinDir(path);
            } else {
                json = getUnixDir(path);
            }
        }

        return json;
    }

    /**
     * Para eliminar directorios de la biblioteca
     * 
     * @param path
     * @return
     */
    @PostMapping("/delDirectory")
    @ResponseBody
    public Boolean delDirectory(@RequestParam("path") String pathToDel) {
        Set<PathEntity> pathList = userDetailsService.getPathList(DashBoardController.username);
        Set<PathEntity> resultPathList = new HashSet<>();
        for (PathEntity path : pathList) {
            if (!path.getPath_dir().equals(pathToDel)) {
                resultPathList.add(path);
            }
        }

        UserEntity user = userDetailsService.getUserByUsername(DashBoardController.username);
        user.setPatchList(resultPathList);
        if (!userDetailsService.setUSerEntity(user)) {
            return false;
        }
        userDetailsService.cleanPathDataBase();
        WatchingDirectory.stopThreads(pathToDel);
        return true;
    }

    /**
     * Para proporcionar los directorios que tenga configurado en un sistema
     * operativo windows.
     * 
     * @param path
     * @return
     */
    public ObjectNode getWinDir(@RequestParam("path") String path) {
        File[] pathList = null;
        Map<String, String> dirList = new HashMap<>();
        if (path.equals("rootUnits")) {
            pathList = File.listRoots();
            for (File f : pathList) {
                dirList.put(f.getAbsolutePath(), f.getAbsolutePath());
            }
        } else {
            boolean unidad = false;
            for (File f : File.listRoots()) {
                if (f.getAbsolutePath().equals(path + ":\\")) {
                    unidad = true;
                }
            }

            pathList = GestorArchivosCarpetas.getFileDirList(path);
            for (File f : pathList) {
                if (f.isDirectory()) {
                    dirList.put(f.getAbsolutePath(), f.getAbsolutePath());
                }
            }
        }

        TreeMap<String, String> dirListSorted = new TreeMap<>();
        dirListSorted.putAll(dirList);

        String pathResul = "";
        if (!path.equals("rootUnits")) {
            path.replace("\\", "\\\\");
            String[] pathSplit = path.split("\\\\");
            if (pathSplit.length > 1) {
                pathResul = "";
                for (int i = 0; i < pathSplit.length - 1; i++) {
                    if (i == 0) {
                        pathResul += pathSplit[i] + SEPARADOR;
                    } else {
                        pathResul += pathSplit[i] + SEPARADOR;
                    }
                }
            }
        }
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("dirList", dirListSorted);
        json.putPOJO("pathFirst", pathResul);

        return json;
    }

    /**
     * Para proporcionar los directorios que tenga configurado en un sistema
     * operativo basado en unix (linux y mac).
     * 
     * @param path
     * @return
     */
    public ObjectNode getUnixDir(@RequestParam("path") String path) {
        File[] pathList = GestorArchivosCarpetas.getFileDirList(path);
        Map<String, String> dirList = new HashMap<>();

        for (File f : pathList) {
            if (f.isDirectory()) {
                dirList.put("/" + f.getName(), f.getAbsolutePath());
            }
        }

        TreeMap<String, String> dirListSorted = new TreeMap<>();
        dirListSorted.putAll(dirList);

        String[] pathSplit = path.split(SEPARADOR);
        String pathResul = SEPARADOR;
        if (pathSplit.length > 2) {
            pathResul = "";
            for (int i = 0; i < pathSplit.length - 1; i++) {
                if (i != 0) {
                    pathResul += SEPARADOR + pathSplit[i];
                } else {
                    pathResul += pathSplit[i];
                }

            }
        }
        ObjectNode json = mapper.createObjectNode();
        json.putPOJO("dirList", dirListSorted);
        json.putPOJO("pathFirst", pathResul);

        return json;
    }

}
