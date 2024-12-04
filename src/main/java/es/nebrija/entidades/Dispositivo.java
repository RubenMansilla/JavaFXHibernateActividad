package es.nebrija.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "dispositivo")
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDispositivo")
    private int idDispositivo;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "fchLanzamiento", nullable = false)
    private String fchLanzamiento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marca_id")
    private Marca marca;  // Relación con la entidad Marca

    // Constructores
    public Dispositivo() {}

    public Dispositivo(String modelo, double precio, Marca marca, String fchLanzamiento) {
        this.modelo = modelo;
        this.precio = precio;
        this.marca = marca;
        this.fchLanzamiento = fchLanzamiento;
    }


    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFchLanzamiento() {
        return fchLanzamiento;
    }

    public void setFchLanzamiento(String fchLanzamiento) {
        this.fchLanzamiento = fchLanzamiento;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }
}
