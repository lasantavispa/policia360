package policia360.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import policia360.domain.Trabajadores;

/**
 * Spring Data JPA repository for the Trabajadores entity.
 *
 * When extending this class, extend TrabajadoresRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface TrabajadoresRepository extends TrabajadoresRepositoryWithBagRelationships, JpaRepository<Trabajadores, Long> {
    default Optional<Trabajadores> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Trabajadores> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Trabajadores> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
