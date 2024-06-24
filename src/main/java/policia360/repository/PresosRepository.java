package policia360.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Presos;

/**
 * Spring Data JPA repository for the Presos entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PresosRepository extends JpaRepository<Presos, Long> {
    @Query("SELECT p.celda.id, p.id FROM Presos p ORDER BY p.celda.id")
    List<Object[]> getPrisoners();
}
// @Query("SELECT p FROM Presos p WHERE p.celda.id = :data")
// @Query("SELECT p.celda.id COUNT(p.id) FROM Presos p GROUP BY p.celda.id")
// @Query("SELECT p.celda.id, p.id FROM Presos p GROUP BY p.celda.id") No funciona
// @Query("SELECT c, p FROM Celda c CROSS JOIN Presos p WHERE p.celda.id = c.id")
// @Query("SELECT c, p FROM Celda c CROSS JOIN Presos p WHERE p.celda.id = c.id ORDER BY c.id")
