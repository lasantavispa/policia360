package policia360.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import policia360.domain.Trabajadores;

public interface TrabajadoresRepositoryWithBagRelationships {
    Optional<Trabajadores> fetchBagRelationships(Optional<Trabajadores> trabajadores);

    List<Trabajadores> fetchBagRelationships(List<Trabajadores> trabajadores);

    Page<Trabajadores> fetchBagRelationships(Page<Trabajadores> trabajadores);
}
