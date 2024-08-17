package com.example.WebLogin.filesControl;

import com.example.WebLogin.controller.DashBoardController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPopupMenu.Separator;

@RestController
@RequestMapping("/localImages")
public class ImageController {

    @GetMapping("/**")
    public ResponseEntity<Resource> cargarGaleria(Model model, HttpServletRequest request) {

        String url = request.getRequestURL().toString();
        String uri = url.split("/localImages")[1];

        String[] uriSplit = uri.split("/");
        String filename = uriSplit[uriSplit.length - 1];
        String localFilePath = DashBoardController.getActualDirectory();

        try {
            filename = URLDecoder.decode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
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
