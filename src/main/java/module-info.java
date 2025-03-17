module org.example.vindmolleprojekt {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.vindmolleprojekt to javafx.fxml;
    exports org.example.vindmolleprojekt;
}