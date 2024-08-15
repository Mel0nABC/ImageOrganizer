package com.example.WebLogin.controller;

import com.example.WebLogin.filesControl.DeleteConfigPath;
import com.example.WebLogin.filesControl.GestorArchivosCarpetas;
import com.example.WebLogin.filesControl.ReadConfigPath;
import com.example.WebLogin.filesControl.WriteConfigPath;
import com.example.WebLogin.otherClasses.DirFilePathClass;
import com.example.WebLogin.otherClasses.GetImageProperties;
import com.example.WebLogin.otherClasses.ImageProperties;
import com.example.WebLogin.otherClasses.UriLinks;
import com.example.WebLogin.persistence.entity.PathEntity;
import com.example.WebLogin.persistence.entity.UserEntity;
import com.example.WebLogin.service.UserDetailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URLDecoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javax.imageio.spi.ImageReaderWriterSpi;

@Controller
public class DashBoardController {

    private String username = "";
    private String SEPARADOR = File.separator;
    private Authentication authentication;
    private UserDetailServiceImpl userDetailsService;
    private static String actualDirectory = "";

    public DashBoardController(UserDetailServiceImpl users) {
        this.userDetailsService = users;
    }

    @PostMapping("/galeria/**")
    public String cargarGaleriaPostquestParam(Model model, HttpServletRequest request) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();
        return "redirect:/galeria";
    }

    @GetMapping("/galeria/**")
    public String cargaGaleriaGet(Model model, HttpServletRequest request) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();
        return "dashboard";
    }

    public File[] getPathList(String username) {
        List<PathEntity> listPath = userDetailsService.getPathList(username);
        File[] filesDirList = new File[listPath.size()];
        for (int i = 0; i < listPath.size(); i++) {
            filesDirList[i] = new File(listPath.get(i).getPath_dir());
        }
        return filesDirList;
    }

    @PostMapping("/uploadImg")
    @ResponseBody
    public String uploadImg(@RequestParam("inputImgList") MultipartFile[] multipartFileList) {

        for (MultipartFile multipartFile : multipartFileList) {
            @SuppressWarnings("null")
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            File uploadDir = new File(actualDirectory);
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
        return "ok";
    }

    @RequestMapping("/mkDir")
    @ResponseBody
    public ObjectNode mkDir(@RequestParam("dirName") String dirName) {
        File dir = new File(actualDirectory + SEPARADOR + dirName);
        String respuesta = "";
        if (!dir.exists()) {
            dir.mkdir();
            if (dir.exists()) {
                respuesta = "Carpeta creada satisfactoriamente.";
            }
        } else {
            respuesta = "La carpeta ya existe.";
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("respuesta", respuesta);

        return json;
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

    @GetMapping("/openConfigDirectory")
    @ResponseBody
    public ObjectNode configDirectory() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        File[] configDirsTemp = getPathList(username);
        List<File> configDirs = new ArrayList<>();

        for (File f : configDirsTemp) {
            configDirs.add(f);
        }

        json.putPOJO("configDirs", configDirs);
        return json;
    }

    @RequestMapping("/editDirectory")
    @ResponseBody
    public ObjectNode editDirectory(@RequestParam("path") String path) {
        File[] pathList = GestorArchivosCarpetas.getFileDirList(path);
        Map<String, String> dirList = new HashMap<>();
        Map<String, String> fileList = new HashMap<>();

        for (File f : pathList) {
            if (f.isDirectory()) {
                dirList.put(f.getName(), f.getAbsolutePath());
            } else {
                fileList.put(f.getName(), f.getAbsolutePath());
            }
        }

        TreeMap<String, String> dirListSorted = new TreeMap<>();
        dirListSorted.putAll(dirList);
        TreeMap<String, String> fileListSorted = new TreeMap<>();
        fileListSorted.putAll(fileList);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.putPOJO("dirList", dirListSorted);
        json.putPOJO("fileList", fileListSorted);

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
        json.putPOJO("pathFirst", pathResul);

        return json;
    }

    @RequestMapping("/confirmNewPath")
    @ResponseBody
    public Boolean confirmNewPath(@RequestParam("newFolderParh") String folderPath) {

        if (!new File(folderPath).exists()) {
            return false;
        }

        File[] listaDirectorios = getPathList(username);
        List<PathEntity> pathList = new ArrayList<>();
        PathEntity newPath;

        for (File f : listaDirectorios) {
            if (f.getAbsolutePath().equals(folderPath)) {
                return false;
            }
            newPath = new PathEntity();
            newPath.setPath_dir(f.getAbsolutePath());
            pathList.add(newPath);
        }

        newPath = new PathEntity();
        newPath.setPath_dir(folderPath);
        pathList.add(newPath);

        UserEntity user = userDetailsService.getUserByUsername(username);
        user.setPatchList(pathList);

        if (!userDetailsService.setUSerEntity(user)) {
            return false;
        }

        return true;
    }

    @RequestMapping("/delDirectory")
    @ResponseBody
    public Boolean delDirectory(@RequestParam("path") String path) {
        File[] pathList = getPathList(username);
        List<PathEntity> resultPathList = new ArrayList<>();
        for (File f : pathList) {
            if (!f.getAbsolutePath().equals(path)) {
                PathEntity newPath = new PathEntity();
                newPath.setPath_dir(f.getAbsolutePath());
                resultPathList.add(newPath);
            }
        }

        UserEntity user = userDetailsService.getUserByUsername(username);
        user.setPatchList(resultPathList);
        if (!userDetailsService.setUSerEntity(user)) {
            return false;
        }
        return true;
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
    @RequestMapping("/cargaContenido")
    @ResponseBody
    public ObjectNode cargaContenido(@RequestParam("uri") String uri, HttpServletRequest request, Model model) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        this.username = authentication.getName();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        /**
         * Declaramos variables necesarias para trabajar con URi y los directorios
         * configurados en config.conf.
         */
        uri = uriDecoder(uri);
        uri = devuelveUriLimpio(uri);
        File[] filesDirList = getPathList(username);
        String localStorageDir = "";

        // Zona pra cuando ya no estamos en la raiz de un directorio configurado.
        if (!uri.equals("") && filesDirList.length > 0) {

            // Recorremos todos los directorios configurados. Obtenemos una lista de los
            // existentes con getPathList()
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

                    Path path = new File(f.getAbsolutePath()).toPath();
                    try {

                        String mimeType = "";
                        try{
                            mimeType = Files.probeContentType(path).split("/")[0];
                        }catch(NullPointerException e){
                        }
                        
                        if (mimeType.equals("image") && mimeType != null) {
                            fileList.add(new DirFilePathClass(f.getName(), f.getName(),"/localImages" + uri + "/" + f.getName()));
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

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

            json.put("username", "Bienvenido a su tablero personal, " + this.username);
            json.put("uri", uriDecoder(request.getRequestURI()));
            json.putPOJO("uriUbicacion", uriUbicacion);
            json.putPOJO("fileList", fileList);
            json.putPOJO("dirList", dirList);

            if ((fileList.size() == 0 | fileList == null) && (dirList.size() == 0 | dirList == null)) {
                json.put("folderStatus", "empty");
            }
        }
        return json;
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
