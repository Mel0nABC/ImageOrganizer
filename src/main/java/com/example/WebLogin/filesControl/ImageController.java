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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/localImages")
public class ImageController {



    @GetMapping("/**")
    public ResponseEntity<Resource> cargarGaleria(Model model, HttpServletRequest request){

        String url = request.getRequestURL().toString();
        String uri = url.split("/localImages")[1];

        String[] uriSplit = uri.split("/");
        String filename = uriSplit[uriSplit.length-1];

        String path = uri.substring(0,uri.length()-filename.length());


        String localFilePath = ReadConfigPath.readPath()+path ;
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
//                System.out.println("RESOURCE -> "+resource.getURI());
                if (resource.exists() && resource.isReadable()) {
                    responseEntity = ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
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
        }
        else {

            System.out.println("Ha habido un error inesperado, no se localiza la imagen especificada.");
        }

        return responseEntity;
    }

}
