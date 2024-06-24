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
import policia360.domain.Vehiculo;
import policia360.repository.VehiculoRepository;
import policia360.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link policia360.domain.Vehiculo}.
 */
@RestController
@RequestMapping("/api/vehiculos")
@Transactional
public class VehiculoResource {

    private final Logger log = LoggerFactory.getLogger(VehiculoResource.class);

    private static final String ENTITY_NAME = "vehiculo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoResource(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    /**
     * {@code POST  /vehiculos} : Create a new vehiculo.
     *
     * @param vehiculo the vehiculo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vehiculo, or with status {@code 400 (Bad Request)} if the vehiculo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Vehiculo> createVehiculo(@RequestBody Vehiculo vehiculo) throws URISyntaxException {
        log.debug("REST request to save Vehiculo : {}", vehiculo);
        if (vehiculo.getId() != null) {
            throw new BadRequestAlertException("A new vehiculo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        vehiculo = vehiculoRepository.save(vehiculo);
        return ResponseEntity.created(new URI("/api/vehiculos/" + vehiculo.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, vehiculo.getId().toString()))
            .body(vehiculo);
    }

    /**
     * {@code PUT  /vehiculos/:id} : Updates an existing vehiculo.
     *
     * @param id the id of the vehiculo to save.
     * @param vehiculo the vehiculo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehiculo,
     * or with status {@code 400 (Bad Request)} if the vehiculo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vehiculo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> updateVehiculo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Vehiculo vehiculo
    ) throws URISyntaxException {
        log.debug("REST request to update Vehiculo : {}, {}", id, vehiculo);
        if (vehiculo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehiculo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehiculoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        vehiculo = vehiculoRepository.save(vehiculo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehiculo.getId().toString()))
            .body(vehiculo);
    }

    /**
     * {@code PATCH  /vehiculos/:id} : Partial updates given fields of an existing vehiculo, field will ignore if it is null
     *
     * @param id the id of the vehiculo to save.
     * @param vehiculo the vehiculo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehiculo,
     * or with status {@code 400 (Bad Request)} if the vehiculo is not valid,
     * or with status {@code 404 (Not Found)} if the vehiculo is not found,
     * or with status {@code 500 (Internal Server Error)} if the vehiculo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Vehiculo> partialUpdateVehiculo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Vehiculo vehiculo
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vehiculo partially : {}, {}", id, vehiculo);
        if (vehiculo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vehiculo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vehiculoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Vehiculo> result = vehiculoRepository
            .findById(vehiculo.getId())
            .map(existingVehiculo -> {
                if (vehiculo.getIdentificador() != null) {
                    existingVehiculo.setIdentificador(vehiculo.getIdentificador());
                }
                if (vehiculo.getTipovehiculo() != null) {
                    existingVehiculo.setTipovehiculo(vehiculo.getTipovehiculo());
                }
                if (vehiculo.getDisponibilidad() != null) {
                    existingVehiculo.setDisponibilidad(vehiculo.getDisponibilidad());
                }

                return existingVehiculo;
            })
            .map(vehiculoRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehiculo.getId().toString())
        );
    }

    /**
     * {@code GET  /vehiculos} : get all the vehiculos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vehiculos in body.
     */
    @GetMapping("")
    public List<Vehiculo> getAllVehiculos() {
        log.debug("REST request to get all Vehiculos");
        return vehiculoRepository.findAll();
    }

    /**
     * {@code GET  /vehiculos/:id} : get the "id" vehiculo.
     *
     * @param id the id of the vehiculo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vehiculo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> getVehiculo(@PathVariable("id") Long id) {
        log.debug("REST request to get Vehiculo : {}", id);
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(vehiculo);
    }

    /**
     * {@code DELETE  /vehiculos/:id} : delete the "id" vehiculo.
     *
     * @param id the id of the vehiculo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable("id") Long id) {
        log.debug("REST request to delete Vehiculo : {}", id);
        vehiculoRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
