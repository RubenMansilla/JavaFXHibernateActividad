package es.nebrija.main;

import es.nebrija.dao.DaoUsuarioImpl;
import es.nebrija.entidades.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField nombreUsuario;

    @FXML
    private TextField contrasena;

    @FXML
    private Button iniciarSesion;

    @FXML
    private Button registrarSesion;

    @FXML
    private Text respuesta;  // Referencia al elemento Text para mostrar la respuesta

    // Método para iniciar sesión
    @FXML
    public void iniciarSesion() throws IOException {
        System.out.println("Iniciando sesión");
        // Obtener los valores del formulario
        String nombre = nombreUsuario.getText();
        String password = contrasena.getText();

        if (nombre.isEmpty() || password.isEmpty()) {
            // Mostrar mensaje en el Text
            respuesta.setText("Por favor, complete ambos campos.");
            return;
        }

        // Verificar usuario en la base de datos
        DaoUsuarioImpl daoLogin = new DaoUsuarioImpl();
        Usuario usuario = daoLogin.leer("nombreUsuario", nombre); // Buscar usuario por nombre

        if (usuario == null) {
            // Si el usuario no existe en la base de datos
            respuesta.setText("Nombre de usuario incorrecto.");
        } else if (usuario != null && !usuario.getContrasena().equals(password)) {
            // Si el usuario existe pero la contraseña no coincide
            respuesta.setText("Contraseña incorrecta.");
        } else {
            // Si el usuario existe y la contraseña coincide
            respuesta.setText("Inicio de sesión exitoso.");
            // Aquí puedes redirigir a otra vista o realizar alguna acción adicional
            cambiarPantallaPrincipal();

        }
    }

    // Método para registrar un nuevo usuario
    @FXML
    public void registrarSesion() {
        // Obtener los valores del formulario
        String nombre = nombreUsuario.getText();
        String password = contrasena.getText();

        if (nombre.isEmpty() || password.isEmpty()) {
            // Mostrar mensaje en el Text
            respuesta.setText("Por favor, complete ambos campos.");
            return;
        }

        // Verificar si el usuario ya existe en la base de datos
        DaoUsuarioImpl daoLogin = new DaoUsuarioImpl();
        Usuario usuarioExistente = daoLogin.leer("nombreUsuario", nombre);

        if (usuarioExistente != null) {
            // Si el usuario ya existe, mostrar mensaje en el Text
            respuesta.setText("El usuario ya está registrado. Intenta con otro nombre.");
        } else {
            // Si el usuario no existe, crear un nuevo usuario y guardarlo en la base de datos
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombre);
            nuevoUsuario.setContrasena(password);

            // Guardar el nuevo usuario
            daoLogin.grabar(nuevoUsuario);

            // Mostrar mensaje de éxito en el Text
            respuesta.setText("Registro exitoso. Ahora puedes iniciar sesión.");

            // Limpiar los campos del formulario
            nombreUsuario.clear();
            contrasena.clear();
        }
    }

    private void cambiarPantallaPrincipal() {
        try {
            // Cargar el archivo FXML de la pantalla principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/nebrija/main/main.fxml"));
            VBox root = loader.load();  // Carga el archivo FXML y lo convierte a VBox

            // Crear la nueva escena con el root de la pantalla principal
            Scene scene = new Scene(root);

            // Obtener la ventana actual (Stage) y cambiar la escena
            Stage stage = (Stage) nombreUsuario.getScene().getWindow();
            stage.setScene(scene);  // Establecer la nueva escena
            stage.show();  // Mostrar la nueva pantalla
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
