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
import policia360.domain.Calendario;
import policia360.repository.CalendarioRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Calendario}.
 */
@RestController
@RequestMapping("/api/calendarios")
@Transactional
public class CalendarioResource {

    private final Logger log = LoggerFactory.getLogger(CalendarioResource.class);

    private static final String ENTITY_NAME = "calendario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CalendarioRepository calendarioRepository;

    public CalendarioResource(CalendarioRepository calendarioRepository) {
        this.calendarioRepository = calendarioRepository;
    }

    /**
     * {@code POST  /calendarios} : Create a new calendario.
     *
     * @param calendario the calendario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new calendario, or with status {@code 400 (Bad Request)} if the calendario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Calendario> createCalendario(@RequestBody Calendario calendario) throws URISyntaxException {
        log.debug("REST request to save Calendario : {}", calendario);
        if (calendario.getId() != null) {
            throw new BadRequestAlertException("A new calendario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        calendario = calendarioRepository.save(calendario);
        return ResponseEntity.created(new URI("/api/calendarios/" + calendario.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, calendario.getId().toString()))
            .body(calendario);
    }

    /**
     * {@code PUT  /calendarios/:id} : Updates an existing calendario.
     *
     * @param id the id of the calendario to save.
     * @param calendario the calendario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendario,
     * or with status {@code 400 (Bad Request)} if the calendario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the calendario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Calendario> updateCalendario(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Calendario calendario
    ) throws URISyntaxException {
        log.debug("REST request to update Calendario : {}, {}", id, calendario);
        if (calendario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        calendario = calendarioRepository.save(calendario);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, calendario.getId().toString()))
            .body(calendario);
    }

    /**
     * {@code PATCH  /calendarios/:id} : Partial updates given fields of an existing calendario, field will ignore if it is null
     *
     * @param id the id of the calendario to save.
     * @param calendario the calendario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated calendario,
     * or with status {@code 400 (Bad Request)} if the calendario is not valid,
     * or with status {@code 404 (Not Found)} if the calendario is not found,
     * or with status {@code 500 (Internal Server Error)} if the calendario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Calendario> partialUpdateCalendario(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Calendario calendario
    ) throws URISyntaxException {
        log.debug("REST request to partial update Calendario partially : {}, {}", id, calendario);
        if (calendario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, calendario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!calendarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Calendario> result = calendarioRepository
            .findById(calendario.getId())
            .map(existingCalendario -> {
                if (calendario.getFechaInicio() != null) {
                    existingCalendario.setFechaInicio(calendario.getFechaInicio());
                }
                if (calendario.getFechaFin() != null) {
                    existingCalendario.setFechaFin(calendario.getFechaFin());
                }
                if (calendario.getConcepto() != null) {
                    existingCalendario.setConcepto(calendario.getConcepto());
                }

                return existingCalendario;
            })
            .map(calendarioRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, calendario.getId().toString())
        );
    }

    /**
     * {@code GET  /calendarios} : get all the calendarios.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of calendarios in body.
     */
    @GetMapping("")
    public List<Calendario> getAllCalendarios() {
        log.debug("REST request to get all Calendarios");
        return calendarioRepository.findAll();
    }

    /**
     * {@code GET  /calendarios/:id} : get the "id" calendario.
     *
     * @param id the id of the calendario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the calendario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Calendario> getCalendario(@PathVariable("id") Long id) {
        log.debug("REST request to get Calendario : {}", id);
        Optional<Calendario> calendario = calendarioRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(calendario);
    }

    /**
     * {@code DELETE  /calendarios/:id} : delete the "id" calendario.
     *
     * @param id the id of the calendario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendario(@PathVariable("id") Long id) {
        log.debug("REST request to delete Calendario : {}", id);
        calendarioRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
