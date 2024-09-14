package com.example.weblogin.controller;

import com.example.weblogin.otherClasses.DirFilePathClass;
import com.example.weblogin.otherClasses.UriLinks;
import com.example.weblogin.persistence.entity.PathEntity;
import com.example.weblogin.persistence.entity.RoleEntity;
import com.example.weblogin.persistence.entity.UserEntity;
import com.example.weblogin.service.ImagePreviewService;
import com.example.weblogin.service.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Controla la página entera menos login.
 */
@Controller
public class DashBoardController {

    private Authentication authentication;
    private static UserDetailServiceImpl userDetailsService;
    private static UserEntity actualUser;
    private ObjectMapper mapper = new ObjectMapper();
    public static String actualDirectory = "";
    public static String username = "";

    public DashBoardController(UserDetailServiceImpl userDetailsService) {
        DashBoardController.userDetailsService = userDetailsService;
    }

    /**
     * Carga página principal post login.
     * 
     * @param model
     * @param request
     * @return Nos envía a dashboard.html.
     */
    @RequestMapping("/galeria/**")
    public String cargaGaleriaGet(Model model, HttpServletRequest request) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        DashBoardController.username = authentication.getName();
        actualUser = userDetailsService.getUserByUsername(username);
        getRoleType(model);
        return "dashboard";
    }

    /**
     * Cargamos las carpetas e imágenes de la ruta seleccionada.
     *
     * @param model
     * @param request
     */
    @RequestMapping("/cargaContenido")
    @ResponseBody
    public ObjectNode cargaContenido(@RequestParam("uri") String uri, @RequestParam("filter") String filter,
            HttpServletRequest request, Model model) {

        uri = uriDecoder(uri);
        uri = returnUriCleaned(uri);
        File[] filesDirList = getPathList(username);
        String localStorageDir = "";
        // Zona para cuando ya no estamos en la raiz de un directorio configurado.
        if (!uri.equals("/")) {
            // Recorremos todos los directorios configurados. Obtenemos una lista de los
            // existentes con getPathList()
            for (File f : filesDirList) {

                if (uri.length() > f.getName().length()) {
                    localStorageDir = uri.substring(1, f.getName().length() + 1);

                    if (localStorageDir.equals(f.getName())) {
                        // Limpiamos el URi, para no tener la carpeta local.
                        String uriPathLocal = uri.substring(localStorageDir.length() + 1,
                                uri.length());

                        // Para cuando estamos en la raiz de una carpeta configurada.
                        if (uriPathLocal.equals("")) {
                            filesDirList = getFilteredFileList(f, filter);
                        } else {

                            File dir = new File(f.getAbsolutePath() + uriPathLocal);
                            filesDirList = getFilteredFileList(dir, filter);
                        }
                        DashBoardController.actualDirectory = f.getAbsolutePath() + uriPathLocal;
                    }
                }
            }
        }

        ObjectNode json = mapper.createObjectNode();

        if (filesDirList != null) {
            List<DirFilePathClass> fileList = new ArrayList<>();
            List<DirFilePathClass> dirList = new ArrayList<>();

            for (File f : filesDirList) {
                if (f.isFile()) {

                    Path path = new File(f.getAbsolutePath()).toPath();
                    try {

                        String mimeType = "";
                        try {
                            mimeType = Files.probeContentType(path).split("/")[0];
                        } catch (NullPointerException e) {
                        }
                        if (mimeType.equals("image") && mimeType != null) {
                            String previewSrc = "";
                            String imageHref = "";
                            if (uri.contains(ImagePreviewService.getDIR_PREVIEW())) {
                                previewSrc = "/localImages" + uri + "/" + f.getName();
                                imageHref = "/localImages" + uri + "/" + f.getName();
                            } else {
                                previewSrc = "/localImages/imagePreview" + uri + "/" + "PREVI_" + f.getName();
                                imageHref = "/localImages" + uri + "/" + f.getName();
                            }

                            fileList.add(new DirFilePathClass(f.getName(), f.getName(), previewSrc, imageHref));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    dirList.add(
                            new DirFilePathClass(f.getName(), f.getName(), "/galeria" + uri + "/" + f.getName(), ""));
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

            String roleType = actualUser.getRoles().stream()
                    .map(RoleEntity::getRoleEnum)
                    .findFirst()
                    .orElse(null).toString();

            actualUser = userDetailsService.getUserByUsername(username);

            json.put("username", DashBoardController.username);
            json.putPOJO("roleType", roleType);

            json.put("uri", uriDecoder(request.getRequestURI()));
            json.putPOJO("uriUbicacion", uriUbicacion);

            json.putPOJO("fileList", fileList);
            json.putPOJO("dirList", dirList);
            json.put("folderStatus", "contains");

            if ((fileList.size() == 0 | fileList == null) && (dirList.size() == 0 | dirList == null)) {
                json.put("folderStatus", "empty");
            }
        }
        return json;
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
     * Recibiendo un Uri o un href de una imagen, elimina la palabra galeria y
     * localImages respectivamente
     *
     * @param uri
     * @return
     */
    public String returnUriCleaned(String uri) {
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

    /**
     * Retorna una lista de la ubicación de f, filtrada por el inicio del texto
     * indicado en filter
     * 
     * @param f,      aportamos el path donde buscar.
     * @param filter, palabra o en este caso, inicio de palabra a seleccionar.
     * @return
     */
    public File[] getFilteredFileList(File f, String filter) {
        File[] filesDirList = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String retName) {
                return retName.toLowerCase().startsWith(filter.toLowerCase());
            }
        });
        return filesDirList;
    }

    /**
     * Método para obtener los directorios de las bibliotecas del usuario.
     * 
     * @param username
     * @return
     */
    public static File[] getPathList(String username) {
        Set<PathEntity> listPath = userDetailsService.getPathList(username);
        File[] filesDirList = new File[listPath.size()];
        int index = 0;
        for (PathEntity path : listPath) {
            filesDirList[index] = new File(path.getPath_dir());
            index++;
        }
        return filesDirList;
    }

    /**
     * Obtenemos el rol del usuario logeado.
     * 
     * @param model
     */
    public void getRoleType(Model model) {
        String roleType = actualUser.getRoles().stream()
                .map(RoleEntity::getRoleEnum)
                .findFirst()
                .orElse(null).toString();
        model.addAttribute("roleType", roleType);
        model.addAttribute("username", username);
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

}
