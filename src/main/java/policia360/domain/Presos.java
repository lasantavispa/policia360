package policia360.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Presos.
 */
@Entity
@Table(name = "presos")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Presos implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "edad")
    private LocalDate edad;

    @Column(name = "dni")
    private String dni;

    @Column(name = "enfermedades")
    private String enfermedades;

    @JsonIgnoreProperties(value = { "presos" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "presos" }, allowSetters = true)
    private Celda celda;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Presos id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Presos nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public Presos apellido(String apellido) {
        this.setApellido(apellido);
        return this;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getEdad() {
        return this.edad;
    }

    public Presos edad(LocalDate edad) {
        this.setEdad(edad);
        return this;
    }

    public void setEdad(LocalDate edad) {
        this.edad = edad;
    }

    public String getDni() {
        return this.dni;
    }

    public Presos dni(String dni) {
        this.setDni(dni);
        return this;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEnfermedades() {
        return this.enfermedades;
    }

    public Presos enfermedades(String enfermedades) {
        this.setEnfermedades(enfermedades);
        return this;
    }

    public void setEnfermedades(String enfermedades) {
        this.enfermedades = enfermedades;
    }

    public Expediente getExpediente() {
        return this.expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }

    public Presos expediente(Expediente expediente) {
        this.setExpediente(expediente);
        return this;
    }

    public Celda getCelda() {
        return this.celda;
    }

    public void setCelda(Celda celda) {
        this.celda = celda;
    }

    public Presos celda(Celda celda) {
        this.setCelda(celda);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Presos)) {
            return false;
        }
        return getId() != null && getId().equals(((Presos) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "Presos [id=" +
            id +
            ", nombre=" +
            nombre +
            ", apellido=" +
            apellido +
            ", edad=" +
            edad +
            ", dni=" +
            dni +
            ", enfermedades=" +
            enfermedades +
            ", expediente=" +
            expediente +
            ", celda=" +
            celda +
            "]"
        );
    }
    // prettier-ignore

}
