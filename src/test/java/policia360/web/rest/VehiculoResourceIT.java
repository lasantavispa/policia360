package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.VehiculoAsserts.*;
import static policia360.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import policia360.IntegrationTest;
import policia360.domain.Vehiculo;
import policia360.domain.enumeration.TipoVehiculo;
import policia360.repository.VehiculoRepository;

/**
 * Integration tests for the {@link VehiculoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VehiculoResourceIT {

    private static final String DEFAULT_IDENTIFICADOR = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFICADOR = "BBBBBBBBBB";

    private static final TipoVehiculo DEFAULT_TIPOVEHICULO = TipoVehiculo.AUTOBUS;
    private static final TipoVehiculo UPDATED_TIPOVEHICULO = TipoVehiculo.COCHEPATRULLA;

    private static final Boolean DEFAULT_DISPONIBILIDAD = false;
    private static final Boolean UPDATED_DISPONIBILIDAD = true;

    private static final String ENTITY_API_URL = "/api/vehiculos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVehiculoMockMvc;

    private Vehiculo vehiculo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehiculo createEntity(EntityManager em) {
        Vehiculo vehiculo = new Vehiculo()
            .identificador(DEFAULT_IDENTIFICADOR)
            .tipovehiculo(DEFAULT_TIPOVEHICULO)
            .disponibilidad(DEFAULT_DISPONIBILIDAD);
        return vehiculo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vehiculo createUpdatedEntity(EntityManager em) {
        Vehiculo vehiculo = new Vehiculo()
            .identificador(UPDATED_IDENTIFICADOR)
            .tipovehiculo(UPDATED_TIPOVEHICULO)
            .disponibilidad(UPDATED_DISPONIBILIDAD);
        return vehiculo;
    }

    @BeforeEach
    public void initTest() {
        vehiculo = createEntity(em);
    }

    @Test
    @Transactional
    void createVehiculo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Vehiculo
        var returnedVehiculo = om.readValue(
            restVehiculoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculo)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Vehiculo.class
        );

        // Validate the Vehiculo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVehiculoUpdatableFieldsEquals(returnedVehiculo, getPersistedVehiculo(returnedVehiculo));
    }

    @Test
    @Transactional
    void createVehiculoWithExistingId() throws Exception {
        // Create the Vehiculo with an existing ID
        vehiculo.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVehiculoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculo)))
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVehiculos() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        // Get all the vehiculoList
        restVehiculoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehiculo.getId().intValue())))
            .andExpect(jsonPath("$.[*].identificador").value(hasItem(DEFAULT_IDENTIFICADOR)))
            .andExpect(jsonPath("$.[*].tipovehiculo").value(hasItem(DEFAULT_TIPOVEHICULO.toString())))
            .andExpect(jsonPath("$.[*].disponibilidad").value(hasItem(DEFAULT_DISPONIBILIDAD.booleanValue())));
    }

    @Test
    @Transactional
    void getVehiculo() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        // Get the vehiculo
        restVehiculoMockMvc
            .perform(get(ENTITY_API_URL_ID, vehiculo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vehiculo.getId().intValue()))
            .andExpect(jsonPath("$.identificador").value(DEFAULT_IDENTIFICADOR))
            .andExpect(jsonPath("$.tipovehiculo").value(DEFAULT_TIPOVEHICULO.toString()))
            .andExpect(jsonPath("$.disponibilidad").value(DEFAULT_DISPONIBILIDAD.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingVehiculo() throws Exception {
        // Get the vehiculo
        restVehiculoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVehiculo() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo
        Vehiculo updatedVehiculo = vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVehiculo are not directly saved in db
        em.detach(updatedVehiculo);
        updatedVehiculo.identificador(UPDATED_IDENTIFICADOR).tipovehiculo(UPDATED_TIPOVEHICULO).disponibilidad(UPDATED_DISPONIBILIDAD);

        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVehiculo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedVehiculo))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVehiculoToMatchAllProperties(updatedVehiculo);
    }

    @Test
    @Transactional
    void putNonExistingVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, vehiculo.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(vehiculo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(vehiculo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVehiculoWithPatch() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo using partial update
        Vehiculo partialUpdatedVehiculo = new Vehiculo();
        partialUpdatedVehiculo.setId(vehiculo.getId());

        partialUpdatedVehiculo.identificador(UPDATED_IDENTIFICADOR);

        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVehiculo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVehiculo))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehiculoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVehiculo, vehiculo), getPersistedVehiculo(vehiculo));
    }

    @Test
    @Transactional
    void fullUpdateVehiculoWithPatch() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vehiculo using partial update
        Vehiculo partialUpdatedVehiculo = new Vehiculo();
        partialUpdatedVehiculo.setId(vehiculo.getId());

        partialUpdatedVehiculo
            .identificador(UPDATED_IDENTIFICADOR)
            .tipovehiculo(UPDATED_TIPOVEHICULO)
            .disponibilidad(UPDATED_DISPONIBILIDAD);

        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVehiculo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVehiculo))
            )
            .andExpect(status().isOk());

        // Validate the Vehiculo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVehiculoUpdatableFieldsEquals(partialUpdatedVehiculo, getPersistedVehiculo(partialUpdatedVehiculo));
    }

    @Test
    @Transactional
    void patchNonExistingVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, vehiculo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vehiculo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(vehiculo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVehiculo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vehiculo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehiculoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(vehiculo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vehiculo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVehiculo() throws Exception {
        // Initialize the database
        vehiculoRepository.saveAndFlush(vehiculo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vehiculo
        restVehiculoMockMvc
            .perform(delete(ENTITY_API_URL_ID, vehiculo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return vehiculoRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Vehiculo getPersistedVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.findById(vehiculo.getId()).orElseThrow();
    }

    protected void assertPersistedVehiculoToMatchAllProperties(Vehiculo expectedVehiculo) {
        assertVehiculoAllPropertiesEquals(expectedVehiculo, getPersistedVehiculo(expectedVehiculo));
    }

    protected void assertPersistedVehiculoToMatchUpdatableProperties(Vehiculo expectedVehiculo) {
        assertVehiculoAllUpdatablePropertiesEquals(expectedVehiculo, getPersistedVehiculo(expectedVehiculo));
    }
}
