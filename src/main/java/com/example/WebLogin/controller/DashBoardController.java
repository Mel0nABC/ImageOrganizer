package com.example.WebLogin.controller;


import com.example.WebLogin.filesControl.GestorArchivosCarpetas;
import com.example.WebLogin.filesControl.ReadConfigPath;
import com.example.WebLogin.otherClasses.UriLinks;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
public class DashBoardController {

    private String folder = ReadConfigPath.readPath();
    private String username = "";
    private String SEPARADOR = File.separator;

    @PostMapping("/")
    public String loginUser(@RequestParam("username") String username, Model model, HttpServletRequest request) {
        this.username = username;
        cargaContenido(model, username, request);
        return "dashboard";
    }

    @GetMapping("/")
    public String vuelveInicio(Model model, HttpServletRequest request) {
        cargaContenido(model, this.username, request);
        return "dashboard";
    }

    @GetMapping("/galeria/**")
    public String cargarGaleria(Model model, HttpServletRequest request) {
        cargaContenido(model, username, request);
        return "dashboard";
    }

    public void cargaContenido(Model model, String username, HttpServletRequest request) {
        System.out.println("CONSULTANDO EN DASHBOARD EN cargaContenido");
        String uri = request.getRequestURI();
        uri = uriDecoder(uri);
        uri = devuelveUriLimpio(uri);
        String absolutFolder = folder + uri;

        // Obtención de archivos y carpetas de la carpeta que corresponda.
        File[] filesDirList = GestorArchivosCarpetas.getFileDirList(absolutFolder);

        // Se crean dos listas independientes, de archivos y directorios.
        if (filesDirList != null) {
            List<String> fileList = new ArrayList<>();
            List<String> dirList = new ArrayList<>();

            for (File f : filesDirList) {
                if (f.isFile()) {
                    fileList.add("/localImages" + uri + "/" + f.getName());
                } else {
                    dirList.add("/galeria" + uri + "/" + f.getName());
                }
            }


            // Ordenamos las listas por orden alfabético.
            Collections.sort(fileList);
            Collections.sort(dirList);

            // Generamos una lista de objetos de clase UriLinks para poder generar los href de la barra de URI.
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
            model.addAttribute("username", "Bienvenido a su tablero personal, " + this.username);
            model.addAttribute("uri", uriDecoder(request.getRequestURI()));
            model.addAttribute("uriUbicacion", uriUbicacion);
            model.addAttribute("fileList", fileList);
            model.addAttribute("dirList", dirList);
            if ((fileList.size() == 0 | fileList == null) && (dirList.size() == 0 | dirList == null)) {
                model.addAttribute("folderStatus", "empty");
            }
        } else {

        }
    }

    /**
     * Elimina la palabra galeria o localImages dependiendo que uri le llegue.
     *
     * @param uri Uri que viene de dashboard.html, de algún form.
     * @return devuelve un string con la palabra eliminada.
     */


    /**
     * Recibiendo un Uri o un href de una imagen, elimina la palabra galeria y localImages respectivamente
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


    @PostMapping("/uploadImg")
    public String uploadImg(@RequestParam("imgFile") MultipartFile[] multipartFileList, @RequestParam("uri") String uri, HttpServletRequest request, Model model) {
        String pathToSave = folder + devuelveUriLimpio(uri);
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
    public String mkDir(@RequestParam("dirName") String dirName, @RequestParam("uri") String uri, HttpServletResponse response) {
        File dir = new File(folder + SEPARADOR + devuelveUriLimpio(uri) + SEPARADOR + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            if (dir.exists()) {
                System.out.println("Directorio creado");
            } else {
                System.out.println("Directorio NO creado");
            }
        }
        return "redirect:"+getUriOk(uri);
    }


    @RequestMapping("/delImgOrDirectory")
    public String delImgOrDirectory(@RequestParam("path") String path, @RequestParam("uri") String uri, HttpServletRequest request, HttpServletResponse response, Model model) {

        File imgToDel = new File(folder + devuelveUriLimpio(path));

        String respuesta;
        String delMsg;

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
        return "redirect:" + getUriOk(uri) + "?respDelImg=" + respuesta + "&delMsg=" + delMsg;
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

}
