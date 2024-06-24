package policia360.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import policia360.domain.Trabajadores;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class TrabajadoresRepositoryWithBagRelationshipsImpl implements TrabajadoresRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String TRABAJADORES_PARAMETER = "trabajadores";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Trabajadores> fetchBagRelationships(Optional<Trabajadores> trabajadores) {
        return trabajadores.map(this::fetchVehiculos);
    }

    @Override
    public Page<Trabajadores> fetchBagRelationships(Page<Trabajadores> trabajadores) {
        return new PageImpl<>(
            fetchBagRelationships(trabajadores.getContent()),
            trabajadores.getPageable(),
            trabajadores.getTotalElements()
        );
    }

    @Override
    public List<Trabajadores> fetchBagRelationships(List<Trabajadores> trabajadores) {
        return Optional.of(trabajadores).map(this::fetchVehiculos).orElse(Collections.emptyList());
    }

    Trabajadores fetchVehiculos(Trabajadores result) {
        return entityManager
            .createQuery(
                "select trabajadores from Trabajadores trabajadores left join fetch trabajadores.vehiculos where trabajadores.id = :id",
                Trabajadores.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Trabajadores> fetchVehiculos(List<Trabajadores> trabajadores) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, trabajadores.size()).forEach(index -> order.put(trabajadores.get(index).getId(), index));
        List<Trabajadores> result = entityManager
            .createQuery(
                "select trabajadores from Trabajadores trabajadores left join fetch trabajadores.vehiculos where trabajadores in :trabajadores",
                Trabajadores.class
            )
            .setParameter(TRABAJADORES_PARAMETER, trabajadores)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
