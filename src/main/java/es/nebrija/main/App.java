package es.nebrija.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    // Variable que almacena la escena principal de la aplicació
    private static Scene scene;

    // Punto de inicio de la interfaz gráfica de JavaFX
    @Override
    public void start(Stage stage) throws IOException {
        // Se inicializa la escena cargando el diseño del archivo "login.fxml" y se le dan dimensiones de 640x480
        scene = new Scene(loadFXML("login"), 640, 480);
        stage.setScene(scene);

        // Se muestra la ventana principal
        stage.show();
    }

    // Método para cambiar la raíz de la escena actual a otro archivo FXML
    static void setRoot(String fxml) throws IOException {
        // Cambia la raíz de la escena actual, cargando el nuevo diseño
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // Llama al método launch() de Application para iniciar la aplicación JavaFX
        launch();
    }

}