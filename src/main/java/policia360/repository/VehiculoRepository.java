package policia360.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Vehiculo;

/**
 * Spring Data JPA repository for the Vehiculo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {}
