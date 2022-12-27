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
        tr.setTessVariable("user_defined_dpi", "600");
        tr.setTessVariable("tessedit_char_whitelist","ACDEFGHMNOPRSTVabcdefghiklmnoprstuvwy- ");

        File file = fc.showOpenDialog(new Stage());
        BufferedImage image = ImageIO.read(file);
        int scaledX = (int) (image.getWidth() / 3.288);
        int scaledY = (int) (image.getHeight() / 1.933);
        int scaledW = (int) (image.getWidth() / 2.844);
        int scaledH = (int) (image.getHeight() / 7.248);

        BufferedImage croppedImage = image.getSubimage(scaledX, scaledY, scaledW, scaledH);

        File processor = new File("src/main/resources/imageProcessor.png");

        // I cannot really fully understand what is happening here, but after
        // a lot of testing and research this is basically creating a palette
        // of colors that are then used to reconstruct the image. After a lot
        // of trial and error, it seems that by setting the bytes to the
        // values below, only the colors white and black are produced, and
        // this is leading to the most accurate OCR readings.
        IndexColorModel cm = new IndexColorModel(
                1, // 3 bits of color
                6, // (Only using 6)
                //          RED  GREEN1  GREEN2  BLUE  WHITE BLACK
                new byte[]{   0,     0,     0,    0,    -1,     0},
                new byte[]{   0,     0,     0,    0,    -1,     0},
                new byte[]{   0,     0,     0,    0,    -1,     0});

        // draw source image on new one, with custom palette
        BufferedImage img = new BufferedImage(
                croppedImage.getWidth(), croppedImage.getHeight(), // match source
                BufferedImage.TYPE_BYTE_INDEXED, // required to work
                cm); // custom color model (i.e. palette)
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(croppedImage, 0, 0, null);
        g2.dispose();

        ImageIO.write(img,"png", processor);

        try {
            tr.setDatapath("src/main/resources/com.example.tesseractfiles/TesseractFiles/Tess4J");
            String scannedText = tr.doOCR(processor);

            if(scannedText.contains("Fast")) {
                System.out.println("4* Found!");
                System.out.println(scannedText);
            }
            else {
                System.out.println("No good combinations found.");
                System.out.println(scannedText);
            }

//            String[] processedText = scannedText.split(" ");
//
//            for(int i = 0; i < processedText.length; i++) {
//                if(processedText[i].length() > 3) {
//                    System.out.println(processedText[i]);
//                }
//            }
        }
        catch(TesseractException e) {
            System.out.println(e.toString());
        }
    }

}