package policia360.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import policia360.domain.enumeration.Resolucion;

/**
 * A Expediente.
 */
@Entity
@Table(name = "expediente")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Expediente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "delito")
    private String delito;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "resolucion")
    private Resolucion resolucion;

    @JsonIgnoreProperties(value = { "expediente", "celda" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "expediente")
    private Presos presos;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Expediente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDelito() {
        return this.delito;
    }

    public Expediente delito(String delito) {
        this.setDelito(delito);
        return this;
    }

    public void setDelito(String delito) {
        this.delito = delito;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public Expediente fecha(LocalDate fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Resolucion getResolucion() {
        return this.resolucion;
    }

    public Expediente resolucion(Resolucion resolucion) {
        this.setResolucion(resolucion);
        return this;
    }

    public void setResolucion(Resolucion resolucion) {
        this.resolucion = resolucion;
    }

    public Presos getPresos() {
        return this.presos;
    }

    public void setPresos(Presos presos) {
        if (this.presos != null) {
            this.presos.setExpediente(null);
        }
        if (presos != null) {
            presos.setExpediente(this);
        }
        this.presos = presos;
    }

    public Expediente presos(Presos presos) {
        this.setPresos(presos);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Expediente)) {
            return false;
        }
        return getId() != null && getId().equals(((Expediente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Expediente{" +
            "id=" + getId() +
            ", delito='" + getDelito() + "'" +
            ", fecha='" + getFecha() + "'" +
            ", resolucion='" + getResolucion() + "'" +
            "}";
    }
}
