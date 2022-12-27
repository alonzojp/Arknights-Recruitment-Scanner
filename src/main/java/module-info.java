module com.example.akrecruitmentscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires tess4j;
    requires java.desktop;


    opens com.example.akrecruitmentscanner to javafx.fxml;
    exports com.example.akrecruitmentscanner;
}