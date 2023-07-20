package org.derdoapp.Helper;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageResizer {

    //720 x 1280
    private static final double WIDTH = 720;
    private static final double HEIGHT = 1280;

    private double calculateRatio(double originalWidth, double originalHeight) {

        double widthRatioToResize = 0;
        if(originalWidth > WIDTH) {
            widthRatioToResize = WIDTH / originalWidth;
        }

        double heightToResize = 0;
        if(originalHeight > HEIGHT) {
            heightToResize = HEIGHT / originalHeight;
        }

        if(widthRatioToResize > heightToResize) {
            return widthRatioToResize;
        }
        else {
            return heightToResize;
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {

        saveFileOn(originalImage, "3");

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private BufferedImage getBufferedImageFromFile(MultipartFile file) throws IOException, Exception {
        Path savePath = saveMK(file, "1_1");
        System.out.println("savePath : " + savePath.toString());
        //URL urlToImage = new URL(savePath.toString());
        File img = new File(savePath.toString());

        BufferedImage imBuff = ImageIO.read(img);

        saveFileOn(imBuff, "1");

        /*BufferedImage imBuff = ImageIO.read(file.getInputStream());
        saveFileOn(imBuff, "1");*/

        return imBuff;
    }

    public BufferedImage resizeFile(MultipartFile file) {
        BufferedImage buffImage = null;
        try {
            BufferedImage resizedImage = null;

            buffImage = getBufferedImageFromFile(file);

            saveFileOn(buffImage, "2");

            int originalWidth = buffImage.getWidth();
            int originalHeight = buffImage.getHeight();
            double percent = calculateRatio(originalWidth, originalHeight);

            int scaledWidth = (int) (originalWidth * percent);
            int scaledHeight = (int) (originalHeight * percent);

            resizedImage = resizeImage(buffImage, scaledWidth, scaledHeight);
            return resizedImage;
        }
        catch (Exception ex) {
            System.out.println("resizeFile");
        }
        finally {
            if(buffImage != null) {
                buffImage.flush();
            }
        }

        return null;
    }

    private void saveFileOn(BufferedImage bufferedImage, String name) throws IOException {
        File outputfile = new File("/Users/Cag/Documents/Projects/gereks/fat/" + name + ".jpg");
        ImageIO.write(bufferedImage, "jpg", outputfile);
    }

    private Path saveMK(MultipartFile file, String name) throws Exception {
        byte[] bytes = file.getBytes();
        Path path = Paths.get("/Users/Cag/Documents/Projects/gereks/fat/" + name + ".jpg");
        Files.write(path, bytes);

        return path;
    }

    private static File convertToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
