package es.nebrija.main;

import es.nebrija.dao.DaoDispositivoImpl;
import es.nebrija.dao.DaoMarcaImpl;
import es.nebrija.entidades.Dispositivo;
import es.nebrija.entidades.Marca;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
                daoMarcaImpl.grabar(marca);
            }

            // Editar los valores del dispositivo seleccionado
            dispositivoSeleccionado.setModelo(phoneModelField.getText());
            dispositivoSeleccionado.setPrecio(Double.parseDouble(priceField.getText()));
            dispositivoSeleccionado.setMarca(marca);  // Actualizar la marca
            dispositivoSeleccionado.setFchLanzamiento(launchDateField.getValue().toString());

            // Modificar el dispositivo en la base de datos
            daoDispositivoImpl.modificar(dispositivoSeleccionado);

            // Refrescar la tabla para reflejar los cambios
            tableView.refresh();

            System.out.println("Dispositivo editado: " + dispositivoSeleccionado.getModelo());
        }
    }

    @FXML
    void eliminarDispositivo() {
        if (dispositivoSeleccionado != null) {
            daoDispositivoImpl.borrar(dispositivoSeleccionado);
            tableView.getItems().remove(dispositivoSeleccionado);
            System.out.println("Dispositivo eliminado: " + dispositivoSeleccionado.getModelo());
        }
    }
}
