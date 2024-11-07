module es.nebrija.actividadTelefonos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core; 
    requires java.sql;
    requires jakarta.persistence;
    requires java.naming;
    requires java.desktop;

    exports es.nebrija.main;
    exports es.nebrija.dao;
    exports es.nebrija.entidades;
    
    opens es.nebrija.main to javafx.fxml;
    opens es.nebrija.entidades to org.hibernate.orm.core;
}