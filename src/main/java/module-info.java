module org.example.vindmolleprojekt {
    requires javafx.fxml;
    requires java.management;
    requires transitive eu.hansolo.medusa;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires atlantafx.base;


    opens org.example.vindmolleprojekt to javafx.fxml;
    exports org.example.vindmolleprojekt;
}