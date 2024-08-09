package com.example.WebLogin.controller;

import com.example.WebLogin.filesControl.ReadConfigPath;
import com.example.WebLogin.otherClasses.DirFilePathClass;
import com.example.WebLogin.otherClasses.GetImageProperties;
import com.example.WebLogin.otherClasses.ImageProperties;
import com.example.WebLogin.otherClasses.UriLinks;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.Image;
import java.io.*;
import java.net.URLDecoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
public class DashBoardController {

    private String username = "";
    private String SEPARADOR = File.separator;
    private Authentication authentication;
    private static String actualDirectory = "";

    @PostMapping("/galeria/**")
    public String cargarGaleriaPostquestParam(Model model, HttpServletRequest request) {
        System.out.println("GALERIA POST MAPPING");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();
        cargaContenido(model, request);
        return "redirect:/galeria";
    }

    @GetMapping("/galeria/**")
    public String cargaGaleriaGet(Model model, HttpServletRequest request) {
        System.out.println("GALERIA GET MAPPING");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();
        cargaContenido(model, request);
        return "dashboard";
    }

    @PostMapping("/uploadImg")
    public String uploadImg(@RequestParam("imgFile") MultipartFile[] multipartFileList,
            @RequestParam("uri") String uri) {
        String pathToSave = actualDirectory;
        for (MultipartFile multipartFile : multipartFileList) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            File uploadDir = new File(pathToSave);
            Path uploadPath = Paths.get(uploadDir.getPath());
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return "redirect:" + getUriOk(uri);
    }

    @PostMapping("/mkDir")
    public String mkDir(@RequestParam("dirName") String dirName, @RequestParam("uri") String uri) {
        File dir = new File(actualDirectory + SEPARADOR + dirName);

        if (!dir.exists()) {
            dir.mkdir();
            if (dir.exists()) {
                System.out.println("Directorio creado");
            } else {
                System.out.println("Directorio NO creado");
            }
            System.out.println("TEST");
        }
        return "redirect:" + getUriOk(uri);
    }

    @RequestMapping("/delImgOrDirectory")
    @ResponseBody
    public ObjectNode delImgOrDirectory(@RequestParam("path") String path) {
        String respuesta;
        String delMsg;
        String[] pathDividido = path.split("/");
        String locate = pathDividido[pathDividido.length - 1];
        File imgToDel = new File(actualDirectory + SEPARADOR + locate);

        if (imgToDel.exists() | imgToDel.isDirectory()) {
            if (imgToDel.isFile()) {
                imgToDel.delete();
                delMsg = "Imagen eliminada satisfactoriamente.";
            } else {
                try {
                    FileUtils.deleteDirectory(imgToDel);
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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("respuesta", respuesta);
        json.put("delMsg", delMsg);

        return json;

    }

    @RequestMapping("/rename")
    @ResponseBody
    public ObjectNode renameDirOrFile(@RequestParam("name") String name, @RequestParam("newName") String newName) {

        File renameFolder = new File(actualDirectory + "/" + name);
        File newFile = new File(actualDirectory + "/" + newName);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

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

    @GetMapping("galeria/configDirectory")
    public String configDirectory(Model model) {
        File[] configDirs = ReadConfigPath.getConfigDirList();
        model.addAttribute("configDirs", configDirs);
        return "configimgdirs";
    }

    @GetMapping("/imgProperties")
    @ResponseBody
    public ImageProperties imgProperties(@RequestParam("imgName") String imgName) {
        String imgFullPath = actualDirectory + "/" + imgName;
        return GetImageProperties.getPropertiesImg(imgFullPath);
    }

    public static String uriDecoder(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("PROBLEMA PROBLEMA");
        }
        return uri;
    }


    /**
     * Para preparar una ruta tipo uri para enviarla por redirect:
     *
     * @param uri
     * @return
     */
    public static String getUriOk(String uri) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(uri)
                .encode()
                .build();
        return uriComponents.toUriString();
    }

    public static String getActualDirectory() {
        return actualDirectory;
    }

    /**
     * Cargamos las carpetas e imágenes de la ruta seleccionada.
     *
     * @param model
     * @param request
     */
    public void cargaContenido(Model model, HttpServletRequest request) {

        /**
         * Declaramos variables necesarias para trabajar con URi y los directorios
         * configurados en config.conf.
         */
        String uri = request.getRequestURI();
        uri = uriDecoder(uri);
        uri = devuelveUriLimpio(uri);
        File[] filesDirList;
        filesDirList = ReadConfigPath.getConfigDirList();
        String localStorageDir = "";

        // Zona pra cuando ya no estamos en la raiz de un directorio configurado.
        if (!uri.equals("") && filesDirList.length > 0) {

            // Recorremos todos los directorios configurados. Obtenemos una lista de los
            // existentes con getConfigDirList()
            for (File f : filesDirList) {

                // Con el fin de evitar un try-catch, se comprueba tamaños para hacer substring
                // más adelante.
                if (uri.length() > f.getName().length()) {
                    localStorageDir = uri.substring(1, f.getName().length() + 1);

                    // Comprobamos que el nombre del archivo local, coincide con el que recorremos
                    // de la lista.
                    if (localStorageDir.equals(f.getName())) {
                        // Limpiamos el URi, para no tener la carpeta local.
                        String uriPathLocal = uri.substring(localStorageDir.length() + 1, uri.length());

                        // Para cuando estamos en la raiz de una carpeta configurada.
                        if (uriPathLocal.equals("")) {
                            filesDirList = f.listFiles();
                        } else {
                            // Obtenemos lista de otras carpetas que no sean la raiz.
                            File dir = new File(f.getAbsolutePath() + uriPathLocal);
                            filesDirList = dir.listFiles();
                        }
                        // Hay que mantener actualDirectory actualizado, para que el sistema sepa la
                        // ruta local correcta.
                        DashBoardController.actualDirectory = f.getAbsolutePath() + uriPathLocal;
                    }
                }
            }
        }

        // Se crean dos listas independientes, de archivos y directorios, creando
        // objetos tipo DirFilePathClass.
        if (filesDirList != null) {
            List<DirFilePathClass> fileList = new ArrayList<>();
            List<DirFilePathClass> dirList = new ArrayList<>();

            for (File f : filesDirList) {
                if (f.isFile()) {
                    fileList.add(
                            new DirFilePathClass(f.getName(), f.getName(), "/localImages" + uri + "/" + f.getName()));
                } else {
                    dirList.add(new DirFilePathClass(f.getName(), f.getName(), "/galeria" + uri + "/" + f.getName()));
                }
            }
            // Ordenamos las listas por orden alfabético.
            Collections.sort(fileList, Comparator.comparing(DirFilePathClass::getId));
            Collections.sort(dirList, Comparator.comparing(DirFilePathClass::getId));

            // Generamos una lista de objetos de clase UriLinks para poder generar los href
            // de la barra de URI.
            String[] uriUbicacionTemp = uri.split("/");
            List<UriLinks> uriUbicacion = new ArrayList<>();
            String resultado = "/galeria";
            for (int i = 0; i < uriUbicacionTemp.length; i++) {
                if (i == 0) {
                    uriUbicacion.add(new UriLinks(resultado, resultado));
                } else {
                    resultado += "/" + uriUbicacionTemp[i];
                    uriUbicacion.add(new UriLinks("/" + uriUbicacionTemp[i], resultado));
                }
            }

            // Añadimos los atributos necesarios.
            model.addAttribute("username", "Bienvenido a su tablero personal, " + this.username);
            model.addAttribute("uri", uriDecoder(request.getRequestURI()));
            model.addAttribute("uriUbicacion", uriUbicacion);
            model.addAttribute("fileList", fileList);
            model.addAttribute("dirList", dirList);
            if ((fileList.size() == 0 | fileList == null) && (dirList.size() == 0 | dirList == null)) {
                model.addAttribute("folderStatus", "empty");
            }
        }
    }

    /**
     * Recibiendo un Uri o un href de una imagen, elimina la palabra galeria y
     * localImages respectivamente
     *
     * @param uri
     * @return
     */
    public String devuelveUriLimpio(String uri) {
        if (!uri.equals("/")) {
            String palabraInicio = uri.split("/")[1];
            if (palabraInicio.equals("galeria")) {
                uri = uri.substring("/galeria".length(), uri.length());
            } else {
                uri = uri.substring("/localImages".length(), uri.length());
            }
        } else {
            uri = "";
        }
        return uri;
    }

}
