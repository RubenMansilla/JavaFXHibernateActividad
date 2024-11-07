package es.nebrija.entidades;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "marca")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "idMarca")
    private int idMarca;

    @Column(name = "nombreMarca")
    private String nombreMarca;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "marca_id")  // Esto establece la columna de clave foránea en la tabla "dispositivo"
    private Set<Dispositivo> dispositivos = new HashSet<>();

    // Constructores
    public Marca(){}

    public Marca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public Set<Dispositivo> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(Set<Dispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }

    public void addDispositivo(Dispositivo dispositivo) {
        dispositivos.add(dispositivo);
    }

    public void removeDispositivo(Dispositivo dispositivo) {
        dispositivos.remove(dispositivo);
    }
}
