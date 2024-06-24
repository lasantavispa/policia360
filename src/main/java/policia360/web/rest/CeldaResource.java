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
import policia360.domain.Celda;
import policia360.domain.Presos;
import policia360.repository.CeldaRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Celda}.
 */
@RestController
@RequestMapping("/api/celdas")
@Transactional
public class CeldaResource {

    private final Logger log = LoggerFactory.getLogger(CeldaResource.class);

    private static final String ENTITY_NAME = "celda";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CeldaRepository celdaRepository;

    public CeldaResource(CeldaRepository celdaRepository) {
        this.celdaRepository = celdaRepository;
    }

    /**
     * {@code POST  /celdas} : Create a new celda.
     *
     * @param celda the celda to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new celda, or with status {@code 400 (Bad Request)} if the celda has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Celda> createCelda(@RequestBody Celda celda) throws URISyntaxException {
        log.debug("REST request to save Celda : {}", celda);
        if (celda.getId() != null) {
            throw new BadRequestAlertException("A new celda cannot already have an ID", ENTITY_NAME, "idexists");
        }
        celda = celdaRepository.save(celda);
        return ResponseEntity.created(new URI("/api/celdas/" + celda.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, celda.getId().toString()))
            .body(celda);
    }

    /**
     * {@code PUT  /celdas/:id} : Updates an existing celda.
     *
     * @param id the id of the celda to save.
     * @param celda the celda to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated celda,
     * or with status {@code 400 (Bad Request)} if the celda is not valid,
     * or with status {@code 500 (Internal Server Error)} if the celda couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Celda> updateCelda(@PathVariable(value = "id", required = false) final Long id, @RequestBody Celda celda)
        throws URISyntaxException {
        log.debug("REST request to update Celda : {}, {}", id, celda);
        if (celda.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, celda.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!celdaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        celda = celdaRepository.save(celda);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, celda.getId().toString()))
            .body(celda);
    }

    /**
     * {@code PATCH  /celdas/:id} : Partial updates given fields of an existing celda, field will ignore if it is null
     *
     * @param id the id of the celda to save.
     * @param celda the celda to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated celda,
     * or with status {@code 400 (Bad Request)} if the celda is not valid,
     * or with status {@code 404 (Not Found)} if the celda is not found,
     * or with status {@code 500 (Internal Server Error)} if the celda couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Celda> partialUpdateCelda(@PathVariable(value = "id", required = false) final Long id, @RequestBody Celda celda)
        throws URISyntaxException {
        log.debug("REST request to partial update Celda partially : {}, {}", id, celda);
        if (celda.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, celda.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!celdaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Celda> result = celdaRepository
            .findById(celda.getId())
            .map(existingCelda -> {
                if (celda.getNumeroCamas() != null) {
                    existingCelda.setNumeroCamas(celda.getNumeroCamas());
                }
                if (celda.getDisponibilidad() != null) {
                    existingCelda.setDisponibilidad(celda.getDisponibilidad());
                }

                return existingCelda;
            })
            .map(celdaRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, celda.getId().toString())
        );
    }

    /**
     * {@code GET  /celdas} : get all the celdas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of celdas in body.
     */
    @GetMapping("")
    public List<Celda> getAllCeldas() {
        log.debug("REST request to get all Celdas");
        return celdaRepository.findAll();
    }

    /**
     * {@code GET  /celdas/:id} : get the "id" celda.
     *
     * @param id the id of the celda to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the celda, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Celda> getCelda(@PathVariable("id") Long id) {
        log.debug("REST request to get Celda : {}", id);
        Optional<Celda> celda = celdaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(celda);
    }

    @GetMapping("/prisioners")
    public List<Object[]> getPrisoners() {
        return celdaRepository.getPrisoners();
    }

    /**
     * {@code DELETE  /celdas/:id} : delete the "id" celda.
     *
     * @param id the id of the celda to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCelda(@PathVariable("id") Long id) {
        log.debug("REST request to delete Celda : {}", id);
        celdaRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
