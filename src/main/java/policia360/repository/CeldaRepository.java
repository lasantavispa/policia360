package policia360.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Celda;

/**
 * Spring Data JPA repository for the Celda entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CeldaRepository extends JpaRepository<Celda, Long> {
    @Query("SELECT p.celda.id, p.id FROM Presos p ORDER BY p.celda.id")
    List<Object[]> getPrisoners();
}
