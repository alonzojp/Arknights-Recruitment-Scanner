package com.example.akrecruitmentscanner;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.*;

import javafx.scene.control.TextArea;

import java.awt.image.IndexColorModel;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.cert.PolicyNode;
import javax.imageio.ImageIO;

import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class HomeController {

    @FXML
    Label rarityLabel;

    @FXML
    TextArea tagsTextArea;

    String defaultFilePath = "";

    @FXML
    protected void onDirectoryButtonClick() {
        DirectoryChooser dc = new DirectoryChooser();
        File directoryFile = dc.showDialog(new Stage());
        defaultFilePath = directoryFile.getAbsolutePath();

        rarityLabel.setText("Screenshot Directory Set!");
    }

    @FXML
    protected int onFileButtonClick() throws IOException {
        rarityLabel.setText("");
        tagsTextArea.setText("");

        if(defaultFilePath.isEmpty()) {
            rarityLabel.setText("Please set the screenshot directory first!");
            return 0;
        }

        File file = getLatestFilefromDir(defaultFilePath);

        Tesseract tr = new Tesseract();
        tr.setTessVariable("user_defined_dpi", "600");
        tr.setTessVariable("tessedit_char_whitelist","ACDEFGHMNOPRSTVabcdefghiklmnoprstuvwy- ");

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

            findCombinations(scannedText);

//            String[] splitText = scannedText.split(" ");
//
//            ArrayList<String> processedTags = new ArrayList<String>();
//
//            for(int i = 0; i < splitText.length; i++) {
//                if(splitText[i].length() >= 3) {
//                    processedTags.add(splitText[i]);
//                }
//            }
//
//            System.out.println(processedTags);
//            System.out.println(processedTags.contains("Defense"));

        }
        catch(TesseractException e) {
            System.out.println(e.toString());
        }

        return 0;
    }

    private void findCombinations(String tags) {
        System.out.println(tags);
        String oneStarAlert = "";
        String fourStarAlert = "";
        String fiveStarAlert = "";
        String sixStarAlert = "";

        if(tags.contains("Top")) {
            sixStarAlert = "Top Operator\n";
        }

        if(tags.contains("Senior")) {
            fiveStarAlert = fiveStarAlert + "Senior Operator\n";
        }
        if(tags.contains("Crowd")) {
            fiveStarAlert = fiveStarAlert + "Crowd-Control\n";
        }
        if(tags.contains("Summon")) {
            fiveStarAlert = fiveStarAlert + "Summon\n";
        }
        if(tags.contains("Nuker")) {
            fiveStarAlert = fiveStarAlert + "Nuker\n";
        }

        if(tags.contains("Debuff")) {
            fourStarAlert += "Debuff\n";

            if(tags.contains("Specialist")) {
                fiveStarAlert += "Debuff + Specialist\n";
            }
            if(tags.contains("Fast")) {
                fiveStarAlert += "Debuff + Fast-Redeploy\n";
            }
            if(tags.contains("AoE")) {
                fiveStarAlert += "Debuff + AoE\n";
            }
            if(tags.contains("Melee")) {
                fiveStarAlert += "Debuff + Melee\n";
            }
            if(tags.contains("Supporter")) {
                fiveStarAlert += "Debuff + Supporter\n";
            }
        }

        if(tags.contains("Shift")) {
            fourStarAlert += "Shift\n";

            if(tags.contains("Slow")) {
                fiveStarAlert += "Shift + Slow\n";
            }
            if(tags.contains("DPS")) {
                fiveStarAlert += "Shift + DPS\n";
            }
            if(tags.contains("Defense")) {
                fiveStarAlert += "Shift + Defense\n";
            }
            if(tags.contains("Defender")) {
                fiveStarAlert += "Shift + Defender\n";
            }
        }

        if(tags.contains("Specialist")) {
            fourStarAlert += "Specialist\n";

            if(tags.contains("Slow")) {
                fiveStarAlert += "Specialist + Slow\n";
            }
            if(tags.contains("DPS")) {
                fiveStarAlert += "Specialist + DPS\n";
            }
            if(tags.contains("Survival")) {
                fiveStarAlert += "Specialist + Survival\n";
            }
        }

        if(tags.contains("Support ")) {
            fourStarAlert += "Support\n";

            if(tags.contains("Recovery")) {
                fiveStarAlert += "Support + DP-Recovery\n";
            }
            if(tags.contains("Vanguard")) {
                fiveStarAlert += "Support + Vanguard\n";
            }
        }

        if(tags.contains("Fast")) {
            fourStarAlert += "Fast-Redeploy\n";
        }

        if(tags.contains("Slow")) {
            if(tags.contains("Healing")) {
                fiveStarAlert += "Slow + Healing\n";
            }
            if(tags.contains("DPS") && tags.contains("Caster")) {
                fiveStarAlert += "Slow + DPS + Caster\n";
            }

            if(tags.contains("DPS")) {
                fourStarAlert += "Slow + DPS\n";
            }
            if(tags.contains("Caster")) {
                fourStarAlert += "Slow + Caster\n";
            }
            if(tags.contains("AoE")) {
                fourStarAlert += "Slow + AoE\n";
            }
            if(tags.contains("Sniper")) {
                fourStarAlert += "Slow + Sniper\n";
            }
            if(tags.contains("Melee")) {
                fourStarAlert += "Slow + Melee\n";
            }
            if(tags.contains("Guard")) {
                fourStarAlert += "Slow + Guard\n";
            }
        }

        if(tags.contains("Healing")) {
            if(tags.contains("Caster")) {
                fiveStarAlert += "Healing + Caster\n";
            }

            if(tags.contains("Recovery")) {
                fourStarAlert += "Healing + DP-Recovery\n";
            }
            if(tags.contains("Vanguard")) {
                fourStarAlert += "Healing + Vanguard\n";
            }
        }

        if(tags.contains("Survival")) {
            if(tags.contains("Defense")) {
                fiveStarAlert += "Survival + Defense\n";
            }
            if(tags.contains("Defender")) {
                fiveStarAlert += "Survival + Defender\n";
            }

            if(tags.contains("Ranged")) {
                fourStarAlert += "Survival + DP-Ranged\n";
            }
            if(tags.contains("Sniper")) {
                fourStarAlert += "Survival + Sniper\n";
            }
        }

        if(tags.contains("DPS")) {
            if(tags.contains("Defense")) {
                fiveStarAlert += "DPS + Defense\n";
            }
            if(tags.contains("Defender")) {
                fiveStarAlert += "DPS + Defender\n";
            }
            if(tags.contains("Healing")) {
                fiveStarAlert += "DPS + Healing\n";
            }
            if(tags.contains("Supporter")) {
                fiveStarAlert += "DPS + Supporter\n";
            }
        }

        if(tags.contains("Guard")) {
            if(tags.contains("Defense")) {
                fiveStarAlert += "Guard + Defense\n";
            }
        }

        if(tags.contains("Robot")) {
            oneStarAlert = "Robot\n";
        }

        if(!sixStarAlert.isEmpty()) {
            rarityLabel.setText("6* Found!");
            tagsTextArea.setText(sixStarAlert);
        }
        else if(!fiveStarAlert.isEmpty()) {
            rarityLabel.setText("5* Found!");
            tagsTextArea.setText(fiveStarAlert);
        }
        else if(!fourStarAlert.isEmpty()) {
            rarityLabel.setText("4* Found!");
            tagsTextArea.setText(fourStarAlert);
        }
        else if(!oneStarAlert.isEmpty()) {
            rarityLabel.setText("1* Found!");
            tagsTextArea.setText(oneStarAlert);
        }
        else {
            rarityLabel.setText("No Combinations Found.");
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> rarityLabel.setText(""));
            delay.play();
        }
    }

    private File getLatestFilefromDir(String dirPath){
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

}