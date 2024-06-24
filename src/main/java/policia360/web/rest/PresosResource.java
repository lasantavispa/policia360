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
import policia360.domain.Presos;
import policia360.repository.PresosRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Presos}.
 */
@RestController
@RequestMapping("/api/presos")
@Transactional
public class PresosResource {

    private final Logger log = LoggerFactory.getLogger(PresosResource.class);

    private static final String ENTITY_NAME = "presos";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PresosRepository presosRepository;

    public PresosResource(PresosRepository presosRepository) {
        this.presosRepository = presosRepository;
    }

    /**
     * {@code POST  /presos} : Create a new presos.
     *
     * @param presos the presos to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new presos, or with status {@code 400 (Bad Request)} if the presos has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Presos> createPresos(@RequestBody Presos presos) throws URISyntaxException {
        log.debug("REST request to save Presos : {}", presos);
        if (presos.getId() != null) {
            throw new BadRequestAlertException("A new presos cannot already have an ID", ENTITY_NAME, "idexists");
        }
        presos = presosRepository.save(presos);
        return ResponseEntity.created(new URI("/api/presos/" + presos.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, presos.getId().toString()))
            .body(presos);
    }

    /**
     * {@code PUT  /presos/:id} : Updates an existing presos.
     *
     * @param id the id of the presos to save.
     * @param presos the presos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated presos,
     * or with status {@code 400 (Bad Request)} if the presos is not valid,
     * or with status {@code 500 (Internal Server Error)} if the presos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Presos> updatePresos(@PathVariable(value = "id", required = false) final Long id, @RequestBody Presos presos)
        throws URISyntaxException {
        log.debug("REST request to update Presos : {}, {}", id, presos);
        if (presos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, presos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!presosRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        presos = presosRepository.save(presos);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, presos.getId().toString()))
            .body(presos);
    }

    /**
     * {@code PATCH  /presos/:id} : Partial updates given fields of an existing presos, field will ignore if it is null
     *
     * @param id the id of the presos to save.
     * @param presos the presos to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated presos,
     * or with status {@code 400 (Bad Request)} if the presos is not valid,
     * or with status {@code 404 (Not Found)} if the presos is not found,
     * or with status {@code 500 (Internal Server Error)} if the presos couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Presos> partialUpdatePresos(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Presos presos
    ) throws URISyntaxException {
        log.debug("REST request to partial update Presos partially : {}, {}", id, presos);
        if (presos.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, presos.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!presosRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Presos> result = presosRepository
            .findById(presos.getId())
            .map(existingPresos -> {
                if (presos.getNombre() != null) {
                    existingPresos.setNombre(presos.getNombre());
                }
                if (presos.getApellido() != null) {
                    existingPresos.setApellido(presos.getApellido());
                }
                if (presos.getEdad() != null) {
                    existingPresos.setEdad(presos.getEdad());
                }
                if (presos.getDni() != null) {
                    existingPresos.setDni(presos.getDni());
                }
                if (presos.getEnfermedades() != null) {
                    existingPresos.setEnfermedades(presos.getEnfermedades());
                }

                return existingPresos;
            })
            .map(presosRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, presos.getId().toString())
        );
    }

    /**
     * {@code GET  /presos} : get all the presos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of presos in body.
     */
    @GetMapping("")
    public List<Presos> getAllPresos() {
        log.debug("REST request to get all Presos");
        return presosRepository.findAll();
    }

    /**
     * {@code GET  /presos/:id} : get the "id" presos.
     *
     * @param id the id of the presos to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the presos, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Presos> getPresos(@PathVariable("id") Long id) {
        log.debug("REST request to get Presos : {}", id);
        Optional<Presos> presos = presosRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(presos);
    }

    @GetMapping("/prisioners")
    public List<Object[]> getPrisoners() {
        return presosRepository.getPrisoners();
    }

    /**
     * {@code DELETE  /presos/:id} : delete the "id" presos.
     *
     * @param id the id of the presos to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresos(@PathVariable("id") Long id) {
        log.debug("REST request to delete Presos : {}", id);
        presosRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
