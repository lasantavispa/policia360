package policia360.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import policia360.domain.enumeration.Estado;
import policia360.domain.enumeration.Puesto;
import policia360.domain.enumeration.Turno;

/**
 * A Trabajadores.
 */
@Entity
@Table(name = "trabajadores")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Trabajadores implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dni")
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(name = "puesto")
    private Puesto puesto;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno")
    private Turno turno;

    @Column(name = "antiguedad")
    private LocalDate antiguedad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trabajadores")
    @JsonIgnoreProperties(value = { "trabajadores" }, allowSetters = true)
    private Set<Calendario> calendarios = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_trabajadores__vehiculo",
        joinColumns = @JoinColumn(name = "trabajadores_id"),
        inverseJoinColumns = @JoinColumn(name = "vehiculo_id")
    )
    @JsonIgnoreProperties(value = { "trabajadores" }, allowSetters = true)
    private Set<Vehiculo> vehiculos = new HashSet<>();

    @JsonIgnoreProperties(value = { "trabajador" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return this.id;
    }

    public Trabajadores id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return this.dni;
    }

    public Trabajadores dni(String dni) {
        this.setDni(dni);
        return this;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Puesto getPuesto() {
        return this.puesto;
    }

    public Trabajadores puesto(Puesto puesto) {
        this.setPuesto(puesto);
        return this;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public Turno getTurno() {
        return this.turno;
    }

    public Trabajadores turno(Turno turno) {
        this.setTurno(turno);
        return this;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public LocalDate getAntiguedad() {
        return this.antiguedad;
    }

    public Trabajadores antiguedad(LocalDate antiguedad) {
        this.setAntiguedad(antiguedad);
        return this;
    }

    public void setAntiguedad(LocalDate antiguedad) {
        this.antiguedad = antiguedad;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public Trabajadores estado(Estado estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Set<Calendario> getCalendarios() {
        return this.calendarios;
    }

    public void setCalendarios(Set<Calendario> calendarios) {
        if (this.calendarios != null) {
            this.calendarios.forEach(i -> i.setTrabajadores(null));
        }
        if (calendarios != null) {
            calendarios.forEach(i -> i.setTrabajadores(this));
        }
        this.calendarios = calendarios;
    }

    public Trabajadores calendarios(Set<Calendario> calendarios) {
        this.setCalendarios(calendarios);
        return this;
    }

    public Trabajadores addCalendario(Calendario calendario) {
        this.calendarios.add(calendario);
        calendario.setTrabajadores(this);
        return this;
    }

    public Trabajadores removeCalendario(Calendario calendario) {
        this.calendarios.remove(calendario);
        calendario.setTrabajadores(null);
        return this;
    }

    public Set<Vehiculo> getVehiculos() {
        return this.vehiculos;
    }

    public void setVehiculos(Set<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public Trabajadores vehiculos(Set<Vehiculo> vehiculos) {
        this.setVehiculos(vehiculos);
        return this;
    }

    public Trabajadores addVehiculo(Vehiculo vehiculo) {
        this.vehiculos.add(vehiculo);
        return this;
    }

    public Trabajadores removeVehiculo(Vehiculo vehiculo) {
        this.vehiculos.remove(vehiculo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Trabajadores)) {
            return false;
        }
        return getId() != null && getId().equals(((Trabajadores) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "Trabajadores [id=" +
            id +
            ", dni=" +
            dni +
            ", puesto=" +
            puesto +
            ", turno=" +
            turno +
            ", antiguedad=" +
            antiguedad +
            ", estado=" +
            estado +
            ", calendarios=" +
            calendarios +
            ", vehiculos=" +
            vehiculos +
            ", user=" +
            user +
            "]"
        );
    }
    // prettier-ignore

}
