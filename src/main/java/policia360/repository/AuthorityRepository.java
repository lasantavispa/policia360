package policia360.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Authority;

/**
 * Spring Data JPA repository for the Authority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
