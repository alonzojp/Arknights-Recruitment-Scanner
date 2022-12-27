package com.example.akrecruitmentscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class HelloController {

    @FXML
    private Button fileButton;

    @FXML
    protected void onFileButtonClick() throws IOException {
        FileChooser fc = new FileChooser();
        Tesseract tr = new Tesseract();
        tr.setTessVariable("user_defined_dpi", "300");
        tr.setTessVariable("tessedit_char_whitelist","ACDEFGHMNOPRSTVabcdefghiklmnoprstuvwy- ");

        File file = fc.showOpenDialog(new Stage());
        BufferedImage image = ImageIO.read(file);
        BufferedImage croppedImage = image.getSubimage(810,550, 700,160);

        File processor = new File("src/main/resources/imageProcessor.png");

        IndexColorModel cm = new IndexColorModel(
                3, // 3 bits of color
                6, // (Only using 6)
                //          RED  GREEN1  GREEN2  BLUE  WHITE BLACK
                new byte[]{-100,     0,     0,    0,    -1,     0},
                new byte[]{   0,  -100,    60,    0,    -1,     0},
                new byte[]{   0,     0,     0, -100,    -1,     0});

        // draw source image on new one, with custom palette
        BufferedImage img = new BufferedImage(
                croppedImage.getWidth(), croppedImage.getHeight(), // match source
                BufferedImage.TYPE_BYTE_INDEXED, // required to work
                cm); // custom color model (i.e. palette)
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(croppedImage, 0, 0, null);
        g2.dispose();

        ImageIO.write(img,"png", processor);

//        image = ImageIO.read(file);
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//
//                // Here (x,y)denotes the coordinate of image
//                // for modifying the pixel value.
//                int p = image.getRGB(x, y);
//
//                int a = (p >> 24) & 0xff;
//                int r = (p >> 16) & 0xff;
//                int g = (p >> 8) & 0xff;
//                int b = p & 0xff;
//
//                // calculate average
//                int avg = (r + g + b) / 3;
//
//                // replace RGB value with avg
//                p = (a << 24) | (avg << 16) | (avg << 8)
//                        | avg;
//
//                image.setRGB(x, y, p);
//            }
//        }
        
        try {
            tr.setDatapath("src/main/resources/com.example.tesseractfiles/TesseractFiles/Tess4J");
            String text = tr.doOCR(processor);
            System.out.println(text);
        }
        catch(TesseractException e) {
            System.out.println(e.toString());
        }
    }




}