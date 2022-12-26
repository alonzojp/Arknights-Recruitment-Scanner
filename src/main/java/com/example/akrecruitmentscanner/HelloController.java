package com.example.akrecruitmentscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {

        welcomeText.setText("Welcome to JavaFX Application!");

        Tesseract tr = new Tesseract();

        try {
            tr.setDatapath("src/main/resources/com.example.tesseractfiles/TesseractFiles/Tess4J");
            String text = tr.doOCR(new File("src/main/resources/testImage.jpg"));
            System.out.println(text);
        }
        catch(TesseractException e) {
            System.out.println(e.toString());
        }
    }
}