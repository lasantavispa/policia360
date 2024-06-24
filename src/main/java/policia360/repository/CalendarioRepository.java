package policia360.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Calendario;

/**
 * Spring Data JPA repository for the Calendario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CalendarioRepository extends JpaRepository<Calendario, Long> {}
