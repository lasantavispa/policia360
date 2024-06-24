package policia360.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import policia360.domain.enumeration.NumeroCamas;

/**
 * A Celda.
 */
@Entity
@Table(name = "celda")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Celda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "numero_camas")
    private NumeroCamas numeroCamas;

    @Column(name = "disponibilidad")
    private Boolean disponibilidad;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "celda")
    @JsonIgnoreProperties(value = { "expediente", "celda" }, allowSetters = true)
    private Set<Presos> presos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Celda id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NumeroCamas getNumeroCamas() {
        return this.numeroCamas;
    }

    public Celda numeroCamas(NumeroCamas numeroCamas) {
        this.setNumeroCamas(numeroCamas);
        return this;
    }

    public void setNumeroCamas(NumeroCamas numeroCamas) {
        this.numeroCamas = numeroCamas;
    }

    public Boolean getDisponibilidad() {
        return this.disponibilidad;
    }

    public Celda disponibilidad(Boolean disponibilidad) {
        this.setDisponibilidad(disponibilidad);
        return this;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Set<Presos> getPresos() {
        return this.presos;
    }

    public void setPresos(Set<Presos> presos) {
        if (this.presos != null) {
            this.presos.forEach(i -> i.setCelda(null));
        }
        if (presos != null) {
            presos.forEach(i -> i.setCelda(this));
        }
        this.presos = presos;
    }

    public Celda presos(Set<Presos> presos) {
        this.setPresos(presos);
        return this;
    }

    public Celda addPresos(Presos presos) {
        this.presos.add(presos);
        presos.setCelda(this);
        return this;
    }

    public Celda removePresos(Presos presos) {
        this.presos.remove(presos);
        presos.setCelda(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Celda)) {
            return false;
        }
        return getId() != null && getId().equals(((Celda) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Celda{" +
            "id=" + getId() +
            ", numeroCamas='" + getNumeroCamas() + "'" +
            ", disponibilidad='" + getDisponibilidad() + "'" +
            "}";
    }
}
