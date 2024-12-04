package es.nebrija.main;

import es.nebrija.dao.DaoDispositivoImpl;
import es.nebrija.dao.DaoMarcaImpl;
import es.nebrija.entidades.Dispositivo;
import es.nebrija.entidades.Marca;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.hibernate.Hibernate;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainController {

    @FXML
    private TextField phoneModelField, priceField, phoneBrand;
    @FXML
    private DatePicker launchDateField;
    @FXML
    private TableView<Dispositivo> tableView;
    @FXML
    private TableColumn<Dispositivo, String> columnModel;
    @FXML
    private TableColumn<Dispositivo, String> columnBrand;
    @FXML
    private Text messageText;

    private Dispositivo dispositivoSeleccionado;

    DaoDispositivoImpl daoDispositivoImpl = new DaoDispositivoImpl();
    DaoMarcaImpl daoMarcaImpl = new DaoMarcaImpl();

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla

        // Asignar el campo "modelo"  a la columna columnModel.
        columnModel.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        // Para la columna de la marca, mostrar solo el nombre de la marca
        columnBrand.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getMarca() != null ? cellData.getValue().getMarca().getNombreMarca() : "Sin Marca"
                )
        );

        // Leer lista de dispositivos
        ArrayList<Dispositivo> listadoDispositivos = (ArrayList) daoDispositivoImpl.leerLista();
        ObservableList<Dispositivo> dispositivos = FXCollections.observableArrayList(listadoDispositivos);

        tableView.setItems(dispositivos);

        // Selección de filas de la tabla
        // Al seleccionar una fila, se muestran los datos del dispositivo
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dispositivoSeleccionado = daoDispositivoImpl.leer(newValue.getIdDispositivo());
                phoneModelField.setText(newValue.getModelo());
                priceField.setText(String.valueOf(newValue.getPrecio()));
                phoneBrand.setText(newValue.getMarca().getNombreMarca());
                launchDateField.setValue(LocalDate.parse(newValue.getFchLanzamiento()));
            }
        });
    }


    @FXML
    void añadirDispositivo() {
        // Limpiar el mensaje previo
        messageText.setText("");
        messageText.setStyle("-fx-fill: red;"); // Rojo para errores por defecto

        try {
            // Validación de campos vacíos
            if (phoneModelField.getText().isEmpty() || priceField.getText().isEmpty() || phoneBrand.getText().isEmpty()) {
                messageText.setText("Todos los campos son obligatorios.");
                return;
            }

            // Validar longitud de campos
            if (phoneModelField.getText().length() > 50) {
                messageText.setText("El modelo no debe superar las 50 letras.");
                return;
            }
            if (phoneBrand.getText().length() > 50) {
                messageText.setText("La marca no debe superar las 50 letras.");
                return;
            }

            // Validar formato del precio
            double precio;
            try {
                precio = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                messageText.setText("El precio debe ser un número válido.");
                return;
            }

            // Buscar o crear la marca
            Marca marca = daoMarcaImpl.leer("nombreMarca", phoneBrand.getText());

            // Si la marca no existe, crearla
            if (marca == null) {
                marca = new Marca(phoneBrand.getText());
                daoMarcaImpl.grabar(marca);
            }

            // Crear el nuevo dispositivo
            Dispositivo dispositivo = new Dispositivo();
            dispositivo.setModelo(phoneModelField.getText());
            dispositivo.setPrecio(precio);
            dispositivo.setMarca(marca);
            dispositivo.setFchLanzamiento(launchDateField.getValue() != null ? launchDateField.getValue().toString() : null);

            // Validar fecha de lanzamiento
            if (dispositivo.getFchLanzamiento() == null) {
                messageText.setText("Debe seleccionar una fecha de lanzamiento.");
                return;
            }

            // Grabar el dispositivo en la base de datos
            daoDispositivoImpl.grabar(dispositivo);

            // Añadir el dispositivo a la lista observable
            ObservableList<Dispositivo> items = tableView.getItems();
            items.add(dispositivo);
            tableView.setItems(items);

            // Mensaje de éxito
            messageText.setText("Dispositivo añadido correctamente.");
            messageText.setStyle("-fx-fill: green;");

            // Limpiar los campos
            phoneModelField.clear();
            priceField.clear();
            phoneBrand.clear();
            launchDateField.setValue(null);

        } catch (Exception e) {
            messageText.setText("Error al añadir el dispositivo: " + e.getMessage());
        }
    }


    @FXML
    void editarDispositivo() {
        // Limpia mensajes previos
        messageText.setText("");
        messageText.setStyle("-fx-fill: red;"); // Rojo para mensajes de error

        if (dispositivoSeleccionado == null) {
            messageText.setText("No se ha seleccionado ningún dispositivo para editar.");
            return;
        }

        // Validaciones
        if (phoneModelField.getText().isEmpty()) {
            messageText.setText("El modelo del teléfono no puede estar vacío.");
            return;
        }

        if (phoneModelField.getText().length() > 50) {
            messageText.setText("El modelo del teléfono no puede superar 50 caracteres.");
            return;
        }

        if (phoneBrand.getText().isEmpty()) {
            messageText.setText("La marca no puede estar vacía.");
            return;
        }

        if (phoneBrand.getText().length() > 50) {
            messageText.setText("La marca no puede superar 50 caracteres.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            messageText.setText("El precio debe ser un número válido.");
            return;
        }

        // Buscar o crear la marca
        Marca marca = daoMarcaImpl.leer("nombreMarca", phoneBrand.getText());
        if (marca == null) {
            marca = new Marca(phoneBrand.getText());
            daoMarcaImpl.grabar(marca);  // Crear la nueva marca si no existe
        }

        // Editar los valores del dispositivo seleccionado
        dispositivoSeleccionado.setModelo(phoneModelField.getText());
        dispositivoSeleccionado.setPrecio(precio);
        dispositivoSeleccionado.setMarca(marca);  // Actualizar la marca
        dispositivoSeleccionado.setFchLanzamiento(launchDateField.getValue().toString());

        // Modificar el dispositivo en la base de datos
        daoDispositivoImpl.modificar(dispositivoSeleccionado);

        // Buscar el dispositivo en la lista de la tabla por ID y reemplazarlo
        ObservableList<Dispositivo> dispositivos = tableView.getItems();
        boolean dispositivoEncontrado = false;

        for (int i = 0; i < dispositivos.size() && !dispositivoEncontrado; i++) {
            if (dispositivos.get(i).getIdDispositivo() == dispositivoSeleccionado.getIdDispositivo()) {
                dispositivos.set(i, dispositivoSeleccionado);  // Reemplazar el item modificado en la lista
                dispositivoEncontrado = true;  // Marca como encontrado y termina el bucle
            }
        }

        actualizarTabla();

        // Mostrar mensaje de éxito
        messageText.setText("Dispositivo editado correctamente.");
        messageText.setStyle("-fx-fill: green;"); // Verde para éxito

        System.out.println("Dispositivo editado: " + dispositivoSeleccionado.getModelo());
    }


    @FXML
    void eliminarDispositivo() {
        if (dispositivoSeleccionado != null) {
            // Eliminar el dispositivo de la base de datos
            daoDispositivoImpl.borrar(dispositivoSeleccionado);

            // Actualizar la lista de dispositivos en la tabla
            actualizarTabla();

            System.out.println("Dispositivo eliminado: " + dispositivoSeleccionado.getModelo());
        }
    }

    private void actualizarTabla() {
        // Leer la lista actualizada de dispositivos desde la base de datos
        ArrayList<Dispositivo> listadoDispositivos = (ArrayList<Dispositivo>) daoDispositivoImpl.leerLista();
        ObservableList<Dispositivo> dispositivos = FXCollections.observableArrayList(listadoDispositivos);

        // Configurar la tabla con la lista actualizada
        tableView.setItems(dispositivos);
        tableView.refresh();  // Refrescar para asegurar que la tabla se actualiza
    }

    @FXML
    void cambiarPantallaExportarImportar() {
        try {
            // Cargar el archivo FXML de la pantalla principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/nebrija/main/exportarImportar.fxml"));
            VBox root = loader.load();  // Carga el archivo FXML y lo convierte a VBox

            // Crear la nueva escena con el root de la pantalla principal
            Scene scene = new Scene(root);

            // Obtener la ventana actual (Stage) y cambiar la escena
            Stage stage = (Stage) phoneModelField.getScene().getWindow();
            stage.setScene(scene);  // Establecer la nueva escena
            stage.show();  // Mostrar la nueva pantalla
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
