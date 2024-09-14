package com.example.weblogin.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.weblogin.otherClasses.GetImageProperties;
import com.example.weblogin.otherClasses.ImageProperties;
import com.example.weblogin.service.WatchingDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class ImageDirManagementController {

    private final String SEPARADOR = File.separator;
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectNode json;

    /**
     * Elimina imágenes o carpetas, si se borra una carpeta, se elimina
     * todo su interior
     * 
     * @param list
     * @return
     */
    @RequestMapping("/delImgOrDirectory")
    @ResponseBody
    public ObjectNode delImgOrDirectory(@RequestParam("list") String[] list) {
        String respuesta = "";
        String delMsg = "";

        for (int i = 0; i < list.length; i++) {
            File imgDirToDel = new File(DashBoardController.actualDirectory + SEPARADOR + list[i]);
            if (imgDirToDel.exists() | imgDirToDel.isDirectory()) {
                if (imgDirToDel.isFile()) {
                    imgDirToDel.delete();
                    delMsg = "Imagen eliminada satisfactoriamente.";
                } else {
                    try {
                        WatchingDirectory.stopThreads(imgDirToDel.getAbsolutePath());
                        FileUtils.deleteDirectory(imgDirToDel);
                        delMsg = "Directorio eliminado satisfactoriamente.";
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                respuesta = "ok";
            } else {
                respuesta = "mal";
                delMsg = "Error al intentar eliminar el elemento seleccionado.";
            }
        }

        json = mapper.createObjectNode();
        json.put("respuesta", respuesta);
        json.put("delMsg", delMsg);

        return json;

    }

    /**
     * Mapping para renombrar imágenes o carpetas.
     * 
     * @param name
     * @param newName
     * @return
     */
    @RequestMapping("/rename")
    @ResponseBody
    public ObjectNode renameDirOrFile(@RequestParam("name") String name, @RequestParam("newName") String newName) {

        File renameFolder = new File(DashBoardController.actualDirectory + SEPARADOR + name);
        File newFile = new File(DashBoardController.actualDirectory + SEPARADOR + newName);
        json = mapper.createObjectNode();
        if (!renameFolder.exists() || newFile.exists()) {
            json.put("respuesta", false);
            return json;
        }

        renameFolder.renameTo(newFile);

        if (newFile.exists()) {
            json.put("respuesta", true);
        } else {
            json.put("respuesta", false);
        }
        return json;
    }

    /**
     * Cuando consultamos en la aplicación la información de una imagen
     * 
     * @param imgName
     * @return
     */
    @GetMapping("/imgProperties")
    @ResponseBody
    public ImageProperties imgProperties(@RequestParam("imgName") String imgName) {
        String imgFullPath = DashBoardController.actualDirectory + SEPARADOR + imgName;
        return GetImageProperties.getPropertiesImg(imgFullPath);
    }

    @PostMapping("/mvDirFiles")
    @ResponseBody
    public ObjectNode mvDirFiles(@RequestParam("newFolder") String newFolder,
            @RequestParam("fileDirList") String[] fileDirList) {

        System.out.println("NEW FOLDER --> " + newFolder);

        List<String> errors = new ArrayList<>();

        for (String file : fileDirList) {
            System.out.println("FILE -> " + file);
            File fileOld = new File(DashBoardController.actualDirectory + SEPARADOR + file);
            File fileNew = new File(newFolder + SEPARADOR + file);
            if (!fileNew.exists()) {
                fileOld.renameTo(fileNew);
            } else {
                errors.add(file);
            }
        }
        json = mapper.createObjectNode();
        json.putPOJO("errors", errors);
        return json;
    }
}
