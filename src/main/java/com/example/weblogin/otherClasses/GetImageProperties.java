package com.example.weblogin.otherClasses;

import java.io.File;
import java.io.IOException;

import javax.imageio.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.awt.image.BufferedImage;

public class GetImageProperties {


public static ImageProperties getPropertiesImg(String path) {
        ImageProperties infoImg = new ImageProperties();
        File imageFile = new File(path);
        try {
            BufferedImage myPinture = ImageIO.read(imageFile);
            infoImg.setHeight(myPinture.getHeight());
            infoImg.setWidth(myPinture.getWidth());
            infoImg.setTransparencia(myPinture.getTransparency());
            infoImg.setType(myPinture.getType());
            infoImg.setRutaAbsoluta(imageFile.getAbsolutePath());

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(infoImg);
            System.out.println("JSON INFO IMG:");
            System.out.println(json);
            System.out.println(infoImg);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return infoImg;
    }
 
}




