module org.example.vindmolleprojekt {
    requires javafx.fxml;
    requires java.management;
    requires transitive eu.hansolo.medusa;


    opens org.example.vindmolleprojekt to javafx.fxml;
    exports org.example.vindmolleprojekt;
}