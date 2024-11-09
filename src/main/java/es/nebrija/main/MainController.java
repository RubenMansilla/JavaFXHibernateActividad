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

    private Dispositivo dispositivoSeleccionado;

    DaoDispositivoImpl daoDispositivoImpl = new DaoDispositivoImpl();
    DaoMarcaImpl daoMarcaImpl = new DaoMarcaImpl();

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        columnModel.setCellValueFactory(new PropertyValueFactory<>("modelo"));

        // Para la columna de la marca, mostrar solo el nombre de la marca
        columnBrand.setCellValueFactory(cellData -> {
            Dispositivo dispositivo = cellData.getValue();
            Marca marca = dispositivo.getMarca();

            // Inicializar la marca antes de acceder a su nombre
            if (marca != null) {
                Hibernate.initialize(marca);
            }

            return new SimpleStringProperty(marca != null ? marca.getNombreMarca() : "Desconocida");
        });

        // Leer lista de dispositivos
        ArrayList<Dispositivo> listadoDispositivos = (ArrayList) daoDispositivoImpl.leerLista();
        ObservableList<Dispositivo> dispositivos = FXCollections.observableArrayList(listadoDispositivos);
        tableView.setItems(dispositivos);

        // Selección de filas de la tabla
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
        if (!phoneModelField.getText().isEmpty() && !priceField.getText().isEmpty() && !phoneBrand.getText().isEmpty()) {
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
            dispositivo.setPrecio(Double.parseDouble(priceField.getText()));
            dispositivo.setMarca(marca);  // Asignar la marca
            dispositivo.setFchLanzamiento(launchDateField.getValue().toString());

            // Grabar el dispositivo en la base de datos
            daoDispositivoImpl.grabar(dispositivo);

            // Añadir el dispositivo a la lista observable
            ObservableList<Dispositivo> items = tableView.getItems();
            items.add(dispositivo);  // Añadir dispositivo a la tabla
            tableView.setItems(items);  // Refrescar la tabla

            System.out.println("Dispositivo añadido: " + dispositivo.getModelo());
        }
    }

    @FXML
    void editarDispositivo() {
        if (dispositivoSeleccionado != null) {
            // Buscar o crear la marca
            Marca marca = daoMarcaImpl.leer("nombreMarca", phoneBrand.getText());

            if (marca == null) {
                marca = new Marca(phoneBrand.getText());
                daoMarcaImpl.grabar(marca);  // Crear la nueva marca si no existe
            }

            // Editar los valores del dispositivo seleccionado
            dispositivoSeleccionado.setModelo(phoneModelField.getText());
            dispositivoSeleccionado.setPrecio(Double.parseDouble(priceField.getText()));
            dispositivoSeleccionado.setMarca(marca);  // Actualizar la marca
            dispositivoSeleccionado.setFchLanzamiento(launchDateField.getValue().toString());

            // Modificar el dispositivo en la base de datos
            daoDispositivoImpl.modificar(dispositivoSeleccionado);

            // Buscar el dispositivo en la lista de la tabla por ID y reemplazarlo
            ObservableList<Dispositivo> dispositivos = tableView.getItems();
            for (int i = 0; i < dispositivos.size(); i++) {
                if (dispositivos.get(i).getIdDispositivo() == dispositivoSeleccionado.getIdDispositivo()) {
                    dispositivos.set(i, dispositivoSeleccionado);  // Reemplazar el item modificado en la lista
                    break;
                }
            }

            actualizarTabla();

            System.out.println("Dispositivo editado: " + dispositivoSeleccionado.getModelo());
        }
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

    private void cambiarPantallaGestionEntidad() {
        try {
            // Cargar el archivo FXML de la pantalla principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/nebrija/main/gestionEntidades.fxml"));
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
