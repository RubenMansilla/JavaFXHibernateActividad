package es.nebrija.main;

import es.nebrija.dao.DaoMarcaImpl;
import es.nebrija.entidades.Marca;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GestionEntidadController {

    @FXML
    private ComboBox<Marca> entitySelector;

    @FXML
    private TextField modeloField;

    @FXML
    private TextField precioField;

    @FXML
    private TextField fechaLanzamientoField;

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    private DaoMarcaImpl daoMarca;

    public GestionEntidadController() {
        daoMarca = new DaoMarcaImpl();
    }

    @FXML
    public void initialize() {
        cargarMarcas();
    }

    // Método para cargar marcas en el ComboBox
    private void cargarMarcas() {
        List<Marca> marcas = daoMarca.leerLista(); // Llama al método actualizado de DaoMarcaImpl
        System.out.println("Marcas cargadas: " + marcas); // Verificación de contenido

        // Usar un CellFactory para mostrar solo el nombre de la marca en lugar de la referencia del objeto
        entitySelector.setItems(FXCollections.observableArrayList(marcas));
        entitySelector.setCellFactory(param -> new ListCell<Marca>() {
            @Override
            protected void updateItem(Marca item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getNombreMarca()); // Mostrar solo el nombre de la marca
                } else {
                    setText(null);
                }
            }
        });

        // Para asegurar que el nombre también se muestre cuando el usuario seleccione un elemento
        entitySelector.setButtonCell(new ListCell<Marca>() {
            @Override
            protected void updateItem(Marca item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getNombreMarca()); // Mostrar solo el nombre de la marca
                } else {
                    setText(null);
                }
            }
        });
    }

    @FXML
    private void guardarEntidad(ActionEvent event) {
        String modelo = modeloField.getText();
        String precio = precioField.getText();
        Marca marcaSeleccionada = entitySelector.getValue(); // Obtener la marca seleccionada del ComboBox
        String fechaLanzamiento = fechaLanzamientoField.getText();

        if (modelo.isEmpty() || precio.isEmpty() || marcaSeleccionada == null || fechaLanzamiento.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Lógica para guardar la entidad (ejemplo)
        // Dispositivo dispositivo = new Dispositivo(modelo, Double.parseDouble(precio), marcaSeleccionada, fechaLanzamiento);
        // daoDispositivo.grabar(dispositivo);

        mostrarAlerta("Éxito", "Entidad guardada correctamente.");
        limpiarCampos();
    }

    @FXML
    void volverPantallaMain() {
        try {
            // Cargar el archivo FXML de la pantalla principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/nebrija/main/main.fxml"));
            VBox root = loader.load();  // Carga el archivo FXML y lo convierte a VBox

            // Crear la nueva escena con el root de la pantalla principal
            Scene scene = new Scene(root);

            // Obtener la ventana actual (Stage) y cambiar la escena
            Stage stage = (Stage) modeloField.getScene().getWindow();
            stage.setScene(scene);  // Establecer la nueva escena
            stage.show();  // Mostrar la nueva pantalla
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        modeloField.clear();
        precioField.clear();
        fechaLanzamientoField.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
