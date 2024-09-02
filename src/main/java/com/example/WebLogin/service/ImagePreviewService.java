package com.example.WebLogin.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

/**
 * Genera imágenes de menor calidad a las originales
 */
@Service
public class ImagePreviewService {

    private static final String SEPARADOR = File.separator;
    private static final String DIR_PREVIEW = "imagePreview";
    public static final String PREVIEW_FILE_CHANGE = SEPARADOR + DIR_PREVIEW + SEPARADOR + "PREVI_";
    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    private final double QUALITY_IMG = 0.5;

    /**
     * Iniciamos la aplicación de busqueda de previews de las imágenes.
     * Se comprueba si existe el directorio denominado en DIR_PREVIEW, dentro de
     * cada carpeta que contenga imágenes que haya dentro de una biblioteca
     * configurada.
     * Si la/s preview/s no existe, se crean.
     * Si se borra las previews, automáticamente, se volverán a generar.
     * Si se borra la imagen original, la preview también será eliminada.
     * 
     * @param userDetailServiceImpl
     */
    public ImagePreviewService(UserDetailServiceImpl userDetailServiceImpl) {
        System.out.println("#####################################");
        System.out.println("## INICIAMOS IMAGE PREVIEW SERVICE ##");
        System.out.println("#####################################");
        userDetailServiceImpl.getAllPathList().forEach(path -> {
            findImagesToResize(new File(path.getPath_dir()));
        });
    }

    /**
     * Buscamos imágenes en el path proporcionado y si existe o no su preview.
     * 
     * @param path
     */
    public void findImagesToResize(File path) {
        System.out.println("ESCANEANDO CAMBIOS EN PEWVIEW EN : "+path);
        if (path.getName().equals(DIR_PREVIEW)) {
            return;
        }

        File[] actualPathList = path.listFiles();

        if (actualPathList == null) {
            return;
        }

        if (mkDir(path.getAbsolutePath())) {
            for (File file : actualPathList) {
                if (file.isFile() && file.listFiles() == null) {
                    resizeImg(file.getAbsolutePath(), getPreviewNameAbsdPath(file));
                }
            }
        } else {
            for (File file : actualPathList) {

                if (filterImgFile(file)) {

                    File previewFile = new File(getPreviewNameAbsdPath(file));

                    if (file.exists() && !previewFile.exists()) {
                        resizeImg(file.getAbsolutePath(), getPreviewNameAbsdPath(file));
                        // System.out.println("RESIZE --> " + file.getAbsolutePath());
                    }

                }
            }
            deletePreviewFilObsolete(path);
        }
    }

    /**
     * Método para crear el directorio denominado en DIR_PREVIEW.
     * 
     * @param path
     * @return
     */
    public boolean mkDir(String path) {
        Boolean imgOnPath = false;
        File pathOnMake = new File(path);
        // Comprobamos si en el directorio hay imágenes, si no hay, no creamos el dir
        // immagePreview
        if (!pathOnMake.exists()) {
            return false;
        }

        if (pathOnMake.listFiles().length == 0 | pathOnMake.listFiles() == null) {
            return false;
        }

        for (File file : pathOnMake.listFiles()) {
            if (filterImgFile(file)) {
                imgOnPath = true;
            }
        }

        if (!imgOnPath) {
            return false;
        }

        File makePreviewDir = new File(path + SEPARADOR + DIR_PREVIEW);
        if (!makePreviewDir.exists()) {
            makePreviewDir.mkdir();
            return true;
        }
        return false;
    }

    /**
     * Genera otra imagen de características diferentes, añadiendo "PREVIEW_"
     * delante de tu nombre.
     * 
     * @param imgFirstPath ubicación de la imagen a transformar.
     * @param imgAfterPath ubicación destino imagen preview.
     * @return Devolvemos true si se ha generado la imagen destino correctamente,
     *         false si no se ha creado
     */
    public boolean resizeImg(String imgFirstPath, String imgAfterPath) {

        try {
            File imgAfer = new File(imgAfterPath);
            Thumbnails.of(new File(imgFirstPath))
                    .size(WIDTH, HEIGHT)
                    .outputQuality(QUALITY_IMG)
                    .toFile(imgAfer);

            if (imgAfer.exists()) {
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Comprueba si el original de una preview existe, si no existe, se
     * borra preview.
     * 
     * @param actualPath directorio donde se ubican imágenes originales.
     */
    public void deletePreviewFilObsolete(File actualPath) {

        File[] previewPathList = new File(actualPath.getAbsolutePath() + SEPARADOR + DIR_PREVIEW).listFiles();
        if (previewPathList == null) {
            return;
        }

        // Recorremos el directorio de previews.
        for (File previewFile : previewPathList) {
            if (!previewFile.isDirectory() && previewFile.listFiles() == null) {
                File originalFile = new File(delPreviewNameAbsdPath(previewFile));
                // Comprobamos si el original existe, si no, borramos preview.
                if (!originalFile.exists() && previewFile.exists()) {
                    System.out.println("BORRAMOS PREVIEW --> "+previewFile.getAbsolutePath());
                    previewFile.delete();
                }
            }
        }
    }

    /**
     * Comprueba si un archivo es de tipo imagen, comprueba su tipo
     * mime, siendo un ejemplo imagen/jpg, usaremos sólo imagen. Así todo formato
     * que sea imagen, entrará.
     * 
     * @param file Archivo a comprobar.
     * @return Devolvemos true, si es un archivo tipo imagen, si no, false.
     */
    public boolean filterImgFile(File file) {

        if (!file.isFile()) {
            return false;
        }

        try {
            Path path = new File(file.getAbsolutePath()).toPath();
            String mimeType = "";

            mimeType = Files.probeContentType(path).split("/")[0];

            if (mimeType.equals("image") && mimeType != null) {
                return true;
            }

        } catch (IOException | NullPointerException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return false;
    }

    /**
     * Genera path absoluto para el nombre de una imagen del tipo
     * preview.
     * 
     * @param file Proporcionamos que imagen original queremos generar tu path tipo
     *             preview.
     * @return Devolvemos un String de un path tipo preview.
     */
    public String getPreviewNameAbsdPath(File file) {
        return file.getParent() + PREVIEW_FILE_CHANGE + file.getName();
    }

    /**
     * Método para eliminar en un path tipo preview, la nomenclatura de preview. Es
     * la inverso a getPreviewNameAbsdPath()
     * 
     * @param previewFile File tipo preview, del cual queremos obtener el path
     *                    original
     * @return Devolvemos String tipo original
     */
    public String delPreviewNameAbsdPath(File previewFile) {
        return previewFile.getAbsolutePath().replace("imagePreview"+SEPARADOR+"PREVI_", "");

    }

    /**
     * Proporcionamos el valor de la constante DIR_PREVIEW.
     * 
     * @return
     */
    public static String getDIR_PREVIEW() {
        return DIR_PREVIEW;
    }

}
