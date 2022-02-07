module com.pocoto.pocotocrazy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.seleniumhq.selenium.chrome_driver;
    requires org.seleniumhq.selenium.api;


    opens com.pocoto.pocotocrazy to javafx.fxml;
    exports com.pocoto.pocotocrazy;
}