package com.example.weblogin.controller;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class MakeDirController {

    private final String SEPARADOR = File.separator;
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Crear directorios
     * 
     * @param dirName nombre del directorio a crear.
     * @return json de respuesta.
     */
    @RequestMapping("/mkDir")
    @ResponseBody
    public ObjectNode mkDir(@RequestParam("dirName") String dirName) {
        File dir = new File(DashBoardController.actualDirectory + SEPARADOR + dirName);
        String respuesta = "La carpeta ya existe.";

        if (!dir.exists()) {
            dir.mkdir();
            if (dir.exists()) {
                respuesta = "Carpeta creada satisfactoriamente.";
            }
        }

        ObjectNode json = mapper.createObjectNode();
        json.put("respuesta", respuesta);

        return json;
    }

}
