package com.example.WebLogin.filesControl;

import com.example.WebLogin.controller.DashBoardController;
import com.example.WebLogin.service.ImagePreviewService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/localImages")
public class ImageController {

    private final String SEPARADOR = File.separator;

    @GetMapping("/**")
    public ResponseEntity<Resource> getOriginalImg(Model model, HttpServletRequest request) {
        String localFilePath = DashBoardController.getActualDirectory();
        return getGenericImg(request, localFilePath);
    }

    @GetMapping("/imagePreview/**")
    public ResponseEntity<Resource> getPreviewImg(Model model, HttpServletRequest request) {
        String localFilePath = DashBoardController.getActualDirectory() + SEPARADOR
                + ImagePreviewService.getDIR_PREVIEW();
        return getGenericImg(request, localFilePath);
    }

    public ResponseEntity<Resource> getGenericImg(HttpServletRequest request, String localFilePath) {

        String url = request.getRequestURL().toString();
        String uri = url.split("/localImages")[1];

        String[] uriSplit = uri.split("/");
        String filename = uriSplit[uriSplit.length - 1];

        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ResponseEntity<Resource> foto = serveFile(filename, localFilePath);

        return foto;

    }

    public ResponseEntity<Resource> serveFile(String filename, String path) {
        path = DashBoardController.uriDecoder(path);
        ResponseEntity<Resource> responseEntity = null;
        File configPath = new File(path);
        Path imageLocation = Paths.get(path);

        if (configPath.exists()) {

            try {
                Path file = imageLocation.resolve(filename);
                Resource resource = new UrlResource(file.toUri());

                if (resource.exists() && resource.isReadable()) {
                    responseEntity = ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "inline; filename=\"" + resource.getFilename() + "\"")
                            .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                            .body(resource);
                } else {
                    responseEntity = ResponseEntity.notFound().build();
                }
            } catch (MalformedURLException e) {
                responseEntity = ResponseEntity.badRequest().build();
                e.printStackTrace();
            } catch (IOException e) {
                responseEntity = ResponseEntity.status(500).build();
                e.printStackTrace();
            }
        } else {

            System.out.println("Ha habido un error inesperado, no se localiza la imagen especificada.");
        }
        return responseEntity;
    }

}
