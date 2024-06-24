package policia360.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import policia360.domain.Expediente;
import policia360.repository.ExpedienteRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Expediente}.
 */
@RestController
@RequestMapping("/api/expedientes")
@Transactional
public class ExpedienteResource {

    private final Logger log = LoggerFactory.getLogger(ExpedienteResource.class);

    private static final String ENTITY_NAME = "expediente";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpedienteRepository expedienteRepository;

    public ExpedienteResource(ExpedienteRepository expedienteRepository) {
        this.expedienteRepository = expedienteRepository;
    }

    /**
     * {@code POST  /expedientes} : Create a new expediente.
     *
     * @param expediente the expediente to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expediente, or with status {@code 400 (Bad Request)} if the expediente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Expediente> createExpediente(@RequestBody Expediente expediente) throws URISyntaxException {
        log.debug("REST request to save Expediente : {}", expediente);
        if (expediente.getId() != null) {
            throw new BadRequestAlertException("A new expediente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        expediente = expedienteRepository.save(expediente);
        return ResponseEntity.created(new URI("/api/expedientes/" + expediente.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, expediente.getId().toString()))
            .body(expediente);
    }

    /**
     * {@code PUT  /expedientes/:id} : Updates an existing expediente.
     *
     * @param id the id of the expediente to save.
     * @param expediente the expediente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expediente,
     * or with status {@code 400 (Bad Request)} if the expediente is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expediente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Expediente> updateExpediente(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Expediente expediente
    ) throws URISyntaxException {
        log.debug("REST request to update Expediente : {}, {}", id, expediente);
        if (expediente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expediente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!expedienteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        expediente = expedienteRepository.save(expediente);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, expediente.getId().toString()))
            .body(expediente);
    }

    /**
     * {@code PATCH  /expedientes/:id} : Partial updates given fields of an existing expediente, field will ignore if it is null
     *
     * @param id the id of the expediente to save.
     * @param expediente the expediente to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expediente,
     * or with status {@code 400 (Bad Request)} if the expediente is not valid,
     * or with status {@code 404 (Not Found)} if the expediente is not found,
     * or with status {@code 500 (Internal Server Error)} if the expediente couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Expediente> partialUpdateExpediente(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Expediente expediente
    ) throws URISyntaxException {
        log.debug("REST request to partial update Expediente partially : {}, {}", id, expediente);
        if (expediente.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, expediente.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!expedienteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Expediente> result = expedienteRepository
            .findById(expediente.getId())
            .map(existingExpediente -> {
                if (expediente.getDelito() != null) {
                    existingExpediente.setDelito(expediente.getDelito());
                }
                if (expediente.getFecha() != null) {
                    existingExpediente.setFecha(expediente.getFecha());
                }
                if (expediente.getResolucion() != null) {
                    existingExpediente.setResolucion(expediente.getResolucion());
                }

                return existingExpediente;
            })
            .map(expedienteRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, expediente.getId().toString())
        );
    }

    /**
     * {@code GET  /expedientes} : get all the expedientes.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expedientes in body.
     */
    @GetMapping("")
    public List<Expediente> getAllExpedientes(@RequestParam(name = "filter", required = false) String filter) {
        if ("presos-is-null".equals(filter)) {
            log.debug("REST request to get all Expedientes where presos is null");
            return StreamSupport.stream(expedienteRepository.findAll().spliterator(), false)
                .filter(expediente -> expediente.getPresos() == null)
                .toList();
        }
        log.debug("REST request to get all Expedientes");
        return expedienteRepository.findAll();
    }

    /**
     * {@code GET  /expedientes/:id} : get the "id" expediente.
     *
     * @param id the id of the expediente to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expediente, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Expediente> getExpediente(@PathVariable("id") Long id) {
        log.debug("REST request to get Expediente : {}", id);
        Optional<Expediente> expediente = expedienteRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(expediente);
    }

    @GetMapping("/prisionersExp")
    public List<Object[]> getPrisonersPerExp() {
        return expedienteRepository.getPrisonersPerExp();
    }

    /**
     * {@code DELETE  /expedientes/:id} : delete the "id" expediente.
     *
     * @param id the id of the expediente to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpediente(@PathVariable("id") Long id) {
        log.debug("REST request to delete Expediente : {}", id);
        expedienteRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
