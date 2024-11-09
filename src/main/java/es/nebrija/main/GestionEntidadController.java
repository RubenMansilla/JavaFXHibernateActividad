package es.nebrija.main;

import es.nebrija.dao.DaoDispositivoImpl;
import es.nebrija.entidades.Dispositivo;
import es.nebrija.entidades.Marca;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.List;

public class GestionEntidadController {
    @FXML
    private ComboBox<Dispositivo> entitySelector;  // ComboBox para seleccionar "Nueva entidad" o una existente
    @FXML
    private TextField modeloField, precioField, marcaField, fechaLanzamientoField;  // Campos de texto para editar
    @FXML
    private Button saveButton, backButton;  // Botones para guardar o volver

    private DaoDispositivoImpl daoDispositivoImpl = new DaoDispositivoImpl();
    private Dispositivo selectedEntity;

    @FXML
    public void initialize() {
        // Configuración inicial del ComboBox
        cargarEntidades();

        // Listener para cambiar entre "Nueva entidad" y una entidad seleccionada
        entitySelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.getModelo().equals("Nueva entidad")) {
                selectedEntity = null;
                limpiarCampos();
            } else {
                selectedEntity = newValue;
                cargarDatosEnCampos(selectedEntity);
            }
        });
    }

    private void cargarEntidades() {
        List<Dispositivo> dispositivos = daoDispositivoImpl.leerLista();  // Obtener entidades de la base de datos
        ObservableList<Dispositivo> listaEntidades = FXCollections.observableArrayList(dispositivos);

        // Agregar "Nueva entidad" como primera opción
        listaEntidades.add(0, new Dispositivo("Nueva entidad", 0.0, null, null));
        entitySelector.setItems(listaEntidades);
        entitySelector.getSelectionModel().selectFirst();  // Seleccionar "Nueva entidad" por defecto
    }

    private void limpiarCampos() {
        modeloField.clear();
        precioField.clear();
        marcaField.clear();
        fechaLanzamientoField.clear();
    }

    private void cargarDatosEnCampos(Dispositivo dispositivo) {
        modeloField.setText(dispositivo.getModelo());
        precioField.setText(String.valueOf(dispositivo.getPrecio()));
        marcaField.setText(dispositivo.getMarca().getNombreMarca());
        fechaLanzamientoField.setText(dispositivo.getFchLanzamiento());
    }

    @FXML
    private void guardarEntidad() {
        if (selectedEntity == null) {
            // Crear y guardar una nueva entidad
            Dispositivo nuevoDispositivo = new Dispositivo();
            nuevoDispositivo.setModelo(modeloField.getText());
            nuevoDispositivo.setPrecio(Double.parseDouble(precioField.getText()));
            nuevoDispositivo.setMarca(new Marca(marcaField.getText()));  // Crear o asignar una marca
            nuevoDispositivo.setFchLanzamiento(fechaLanzamientoField.getText());
            daoDispositivoImpl.grabar(nuevoDispositivo);
            System.out.println("Nueva entidad guardada");
        } else {
            // Actualizar la entidad existente
            selectedEntity.setModelo(modeloField.getText());
            selectedEntity.setPrecio(Double.parseDouble(precioField.getText()));
            selectedEntity.setMarca(new Marca(marcaField.getText()));  // Actualizar marca
            selectedEntity.setFchLanzamiento(fechaLanzamientoField.getText());
            daoDispositivoImpl.modificar(selectedEntity);
            System.out.println("Entidad actualizada");
        }
        cargarEntidades();  // Recargar la lista de entidades
    }

    @FXML
    private void volverALaPantallaPrincipal() {
        // Aquí podrías implementar la lógica para volver a la pantalla principal
    }
}
