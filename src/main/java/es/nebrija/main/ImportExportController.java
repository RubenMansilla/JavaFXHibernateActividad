package es.nebrija.main;

import es.nebrija.dao.DaoDispositivoImpl;
import es.nebrija.dao.DaoMarcaImpl;
import es.nebrija.entidades.Dispositivo;
import es.nebrija.entidades.Marca;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;

public class ImportExportController {

    @FXML
    private Button importButton;

    @FXML
    private Button exportButton;

    @FXML
    private Label statusLabel;

    DaoDispositivoImpl daoDispositivoImpl = new DaoDispositivoImpl();
    DaoMarcaImpl daoMarcaImpl = new DaoMarcaImpl();

    @FXML
    private void initialize() {
        // Configuración inicial del controlador
        statusLabel.setText("Estado: Listo para importar/exportar.");
    }

    @FXML
    private void importarDatos(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo para importar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        Stage stage = (Stage) importButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                // Borrar todos los dispositivos y marcas antes de la importación
                borrarDatosExistentes();

                int importedCount = 0;

                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) { // Modelo, Precio, Marca, Fecha de Lanzamiento
                        String modelo = data[0].trim();
                        double precio = Double.parseDouble(data[1].trim());
                        String marcaNombre = data[2].trim();
                        String fechaLanzamiento = data[3].trim();

                        // Buscar o crear la marca
                        Marca marca = daoMarcaImpl.leer("nombreMarca", marcaNombre);
                        if (marca == null) {
                            marca = new Marca(marcaNombre);
                            daoMarcaImpl.grabar(marca);
                        }

                        // Crear y guardar el dispositivo
                        Dispositivo dispositivo = new Dispositivo(modelo, precio, marca, fechaLanzamiento);
                        daoDispositivoImpl.grabar(dispositivo);

                        importedCount++;
                    }
                }

                statusLabel.setText("Importación completada: " + importedCount + " dispositivos importados.");
            } catch (IOException e) {
                statusLabel.setText("Error al leer el archivo: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Importación cancelada.");
        }
    }

    @FXML
    private void exportarDatos(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo exportado");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        Stage stage = (Stage) exportButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                List<Dispositivo> dispositivos = daoDispositivoImpl.leerLista();

                for (Dispositivo dispositivo : dispositivos) {
                    String line = String.format("%s,%.2f,%s,%s",
                            dispositivo.getModelo(),
                            dispositivo.getPrecio(),
                            dispositivo.getMarca() != null ? dispositivo.getMarca().getNombreMarca() : "Sin Marca",
                            dispositivo.getFchLanzamiento());
                    writer.write(line);
                    writer.newLine();
                }

                statusLabel.setText("Exportación completada: " + dispositivos.size() + " dispositivos exportados.");
            } catch (IOException e) {
                statusLabel.setText("Error al escribir el archivo: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Exportación cancelada.");
        }
    }

    private void borrarDatosExistentes() {
        try {
            // Obtener todas las marcas y dispositivos
            List<Marca> todasLasMarcas = daoMarcaImpl.leerLista();
            List<Dispositivo> todosLosDispositivos = daoDispositivoImpl.leerLista();

            // Eliminar todos los dispositivos primero (debido a la relación con Marca)
            for (Dispositivo dispositivo : todosLosDispositivos) {
                daoDispositivoImpl.borrar(dispositivo);
            }

            // Luego eliminar todas las marcas
            for (Marca marca : todasLasMarcas) {
                daoMarcaImpl.borrar(marca);
            }

            System.out.println("Todos los datos existentes han sido eliminados.");
        } catch (Exception e) {
            System.out.println("Error al borrar los datos existentes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void cambiarPantallaMain() {
        try {
            // Cargar el archivo FXML de la pantalla principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/nebrija/main/main.fxml"));
            VBox root = loader.load();  // Carga el archivo FXML y lo convierte a VBox

            // Crear la nueva escena con el root de la pantalla principal
            Scene scene = new Scene(root);

            // Obtener la ventana actual (Stage) y cambiar la escena
            Stage stage = (Stage) importButton.getScene().getWindow();
            stage.setScene(scene);  // Establecer la nueva escena
            stage.show();  // Mostrar la nueva pantalla
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
