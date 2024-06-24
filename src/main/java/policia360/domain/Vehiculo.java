package policia360.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import policia360.domain.enumeration.TipoVehiculo;

/**
 * A Vehiculo.
 */
@Entity
@Table(name = "vehiculo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identificador")
    private String identificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipovehiculo")
    private TipoVehiculo tipovehiculo;

    @Column(name = "disponibilidad")
    private Boolean disponibilidad;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "vehiculos")
    @JsonIgnoreProperties(value = { "calendarios", "vehiculos" }, allowSetters = true)
    private Set<Trabajadores> trabajadores = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vehiculo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificador() {
        return this.identificador;
    }

    public Vehiculo identificador(String identificador) {
        this.setIdentificador(identificador);
        return this;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public TipoVehiculo getTipovehiculo() {
        return this.tipovehiculo;
    }

    public Vehiculo tipovehiculo(TipoVehiculo tipovehiculo) {
        this.setTipovehiculo(tipovehiculo);
        return this;
    }

    public void setTipovehiculo(TipoVehiculo tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }

    public Boolean getDisponibilidad() {
        return this.disponibilidad;
    }

    public Vehiculo disponibilidad(Boolean disponibilidad) {
        this.setDisponibilidad(disponibilidad);
        return this;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Set<Trabajadores> getTrabajadores() {
        return this.trabajadores;
    }

    public void setTrabajadores(Set<Trabajadores> trabajadores) {
        if (this.trabajadores != null) {
            this.trabajadores.forEach(i -> i.removeVehiculo(this));
        }
        if (trabajadores != null) {
            trabajadores.forEach(i -> i.addVehiculo(this));
        }
        this.trabajadores = trabajadores;
    }

    public Vehiculo trabajadores(Set<Trabajadores> trabajadores) {
        this.setTrabajadores(trabajadores);
        return this;
    }

    public Vehiculo addTrabajadores(Trabajadores trabajadores) {
        this.trabajadores.add(trabajadores);
        trabajadores.getVehiculos().add(this);
        return this;
    }

    public Vehiculo removeTrabajadores(Trabajadores trabajadores) {
        this.trabajadores.remove(trabajadores);
        trabajadores.getVehiculos().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehiculo)) {
            return false;
        }
        return getId() != null && getId().equals(((Vehiculo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vehiculo{" +
            "id=" + getId() +
            ", identificador='" + getIdentificador() + "'" +
            ", tipovehiculo='" + getTipovehiculo() + "'" +
            ", disponibilidad='" + getDisponibilidad() + "'" +
            "}";
    }
}
