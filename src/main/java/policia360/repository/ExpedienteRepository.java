package policia360.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Expediente;

/**
 * Spring Data JPA repository for the Expediente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    @Query("SELECT p.expediente.id, p.id FROM Presos p ORDER BY p.expediente.id")
    List<Object[]> getPrisonersPerExp();
}
