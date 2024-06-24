package policia360.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import policia360.domain.Trabajadores;
import policia360.repository.TrabajadoresRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Trabajadores}.
 */
@RestController
@RequestMapping("/api/trabajadores")
@Transactional
public class TrabajadoresResource {

    private final Logger log = LoggerFactory.getLogger(TrabajadoresResource.class);

    private static final String ENTITY_NAME = "trabajadores";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TrabajadoresRepository trabajadoresRepository;

    public TrabajadoresResource(TrabajadoresRepository trabajadoresRepository) {
        this.trabajadoresRepository = trabajadoresRepository;
    }

    /**
     * {@code POST  /trabajadores} : Create a new trabajadores.
     *
     * @param trabajadores the trabajadores to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new trabajadores, or with status {@code 400 (Bad Request)} if the trabajadores has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Trabajadores> createTrabajadores(@RequestBody Trabajadores trabajadores) throws URISyntaxException {
        log.debug("REST request to save Trabajadores : {}", trabajadores);
        if (trabajadores.getId() != null) {
            throw new BadRequestAlertException("A new trabajadores cannot already have an ID", ENTITY_NAME, "idexists");
        }
        trabajadores = trabajadoresRepository.save(trabajadores);
        return ResponseEntity.created(new URI("/api/trabajadores/" + trabajadores.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, trabajadores.getId().toString()))
            .body(trabajadores);
    }

    /**
     * {@code PUT  /trabajadores/:id} : Updates an existing trabajadores.
     *
     * @param id the id of the trabajadores to save.
     * @param trabajadores the trabajadores to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trabajadores,
     * or with status {@code 400 (Bad Request)} if the trabajadores is not valid,
     * or with status {@code 500 (Internal Server Error)} if the trabajadores couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Trabajadores> updateTrabajadores(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Trabajadores trabajadores
    ) throws URISyntaxException {
        log.debug("REST request to update Trabajadores : {}, {}", id, trabajadores);
        if (trabajadores.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trabajadores.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trabajadoresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        trabajadores = trabajadoresRepository.save(trabajadores);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trabajadores.getId().toString()))
            .body(trabajadores);
    }

    /**
     * {@code PATCH  /trabajadores/:id} : Partial updates given fields of an existing trabajadores, field will ignore if it is null
     *
     * @param id the id of the trabajadores to save.
     * @param trabajadores the trabajadores to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated trabajadores,
     * or with status {@code 400 (Bad Request)} if the trabajadores is not valid,
     * or with status {@code 404 (Not Found)} if the trabajadores is not found,
     * or with status {@code 500 (Internal Server Error)} if the trabajadores couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Trabajadores> partialUpdateTrabajadores(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Trabajadores trabajadores
    ) throws URISyntaxException {
        log.debug("REST request to partial update Trabajadores partially : {}, {}", id, trabajadores);
        if (trabajadores.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, trabajadores.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!trabajadoresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Trabajadores> result = trabajadoresRepository
            .findById(trabajadores.getId())
            .map(existingTrabajadores -> {
                if (trabajadores.getDni() != null) {
                    existingTrabajadores.setDni(trabajadores.getDni());
                }
                if (trabajadores.getPuesto() != null) {
                    existingTrabajadores.setPuesto(trabajadores.getPuesto());
                }
                if (trabajadores.getTurno() != null) {
                    existingTrabajadores.setTurno(trabajadores.getTurno());
                }
                if (trabajadores.getAntiguedad() != null) {
                    existingTrabajadores.setAntiguedad(trabajadores.getAntiguedad());
                }
                if (trabajadores.getEstado() != null) {
                    existingTrabajadores.setEstado(trabajadores.getEstado());
                }

                return existingTrabajadores;
            })
            .map(trabajadoresRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, trabajadores.getId().toString())
        );
    }

    /**
     * {@code GET  /trabajadores} : get all the trabajadores.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trabajadores in body.
     */
    @GetMapping("")
    public List<Trabajadores> getAllTrabajadores(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all Trabajadores");
        if (eagerload) {
            return trabajadoresRepository.findAllWithEagerRelationships();
        } else {
            return trabajadoresRepository.findAll();
        }
    }

    /**
     * {@code GET  /trabajadores/:id} : get the "id" trabajadores.
     *
     * @param id the id of the trabajadores to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the trabajadores, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Trabajadores> getTrabajadores(@PathVariable("id") Long id) {
        log.debug("REST request to get Trabajadores : {}", id);
        Optional<Trabajadores> trabajadores = trabajadoresRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(trabajadores);
    }

    /**
     * {@code DELETE  /trabajadores/:id} : delete the "id" trabajadores.
     *
     * @param id the id of the trabajadores to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrabajadores(@PathVariable("id") Long id) {
        log.debug("REST request to delete Trabajadores : {}", id);
        trabajadoresRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
