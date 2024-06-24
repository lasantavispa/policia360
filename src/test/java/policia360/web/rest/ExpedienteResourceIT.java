package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.ExpedienteAsserts.*;
import static policia360.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
import policia360.domain.Expediente;
import policia360.domain.enumeration.Resolucion;
import policia360.repository.ExpedienteRepository;

/**
 * Integration tests for the {@link ExpedienteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExpedienteResourceIT {

    private static final String DEFAULT_DELITO = "AAAAAAAAAA";
    private static final String UPDATED_DELITO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA = LocalDate.now(ZoneId.systemDefault());

    private static final Resolucion DEFAULT_RESOLUCION = Resolucion.PendienteJuicio;
    private static final Resolucion UPDATED_RESOLUCION = Resolucion.Culpable;

    private static final String ENTITY_API_URL = "/api/expedientes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExpedienteMockMvc;

    private Expediente expediente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expediente createEntity(EntityManager em) {
        Expediente expediente = new Expediente().delito(DEFAULT_DELITO).fecha(DEFAULT_FECHA).resolucion(DEFAULT_RESOLUCION);
        return expediente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expediente createUpdatedEntity(EntityManager em) {
        Expediente expediente = new Expediente().delito(UPDATED_DELITO).fecha(UPDATED_FECHA).resolucion(UPDATED_RESOLUCION);
        return expediente;
    }

    @BeforeEach
    public void initTest() {
        expediente = createEntity(em);
    }

    @Test
    @Transactional
    void createExpediente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Expediente
        var returnedExpediente = om.readValue(
            restExpedienteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expediente)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Expediente.class
        );

        // Validate the Expediente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertExpedienteUpdatableFieldsEquals(returnedExpediente, getPersistedExpediente(returnedExpediente));
    }

    @Test
    @Transactional
    void createExpedienteWithExistingId() throws Exception {
        // Create the Expediente with an existing ID
        expediente.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpedienteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expediente)))
            .andExpect(status().isBadRequest());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExpedientes() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        // Get all the expedienteList
        restExpedienteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expediente.getId().intValue())))
            .andExpect(jsonPath("$.[*].delito").value(hasItem(DEFAULT_DELITO)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].resolucion").value(hasItem(DEFAULT_RESOLUCION.toString())));
    }

    @Test
    @Transactional
    void getExpediente() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        // Get the expediente
        restExpedienteMockMvc
            .perform(get(ENTITY_API_URL_ID, expediente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(expediente.getId().intValue()))
            .andExpect(jsonPath("$.delito").value(DEFAULT_DELITO))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()))
            .andExpect(jsonPath("$.resolucion").value(DEFAULT_RESOLUCION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExpediente() throws Exception {
        // Get the expediente
        restExpedienteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExpediente() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expediente
        Expediente updatedExpediente = expedienteRepository.findById(expediente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedExpediente are not directly saved in db
        em.detach(updatedExpediente);
        updatedExpediente.delito(UPDATED_DELITO).fecha(UPDATED_FECHA).resolucion(UPDATED_RESOLUCION);

        restExpedienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExpediente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedExpediente))
            )
            .andExpect(status().isOk());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExpedienteToMatchAllProperties(updatedExpediente);
    }

    @Test
    @Transactional
    void putNonExistingExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, expediente.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expediente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(expediente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(expediente)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExpedienteWithPatch() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expediente using partial update
        Expediente partialUpdatedExpediente = new Expediente();
        partialUpdatedExpediente.setId(expediente.getId());

        partialUpdatedExpediente.fecha(UPDATED_FECHA).resolucion(UPDATED_RESOLUCION);

        restExpedienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpediente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpediente))
            )
            .andExpect(status().isOk());

        // Validate the Expediente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpedienteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExpediente, expediente),
            getPersistedExpediente(expediente)
        );
    }

    @Test
    @Transactional
    void fullUpdateExpedienteWithPatch() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the expediente using partial update
        Expediente partialUpdatedExpediente = new Expediente();
        partialUpdatedExpediente.setId(expediente.getId());

        partialUpdatedExpediente.delito(UPDATED_DELITO).fecha(UPDATED_FECHA).resolucion(UPDATED_RESOLUCION);

        restExpedienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExpediente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedExpediente))
            )
            .andExpect(status().isOk());

        // Validate the Expediente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExpedienteUpdatableFieldsEquals(partialUpdatedExpediente, getPersistedExpediente(partialUpdatedExpediente));
    }

    @Test
    @Transactional
    void patchNonExistingExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, expediente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expediente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(expediente))
            )
            .andExpect(status().isBadRequest());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExpediente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        expediente.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExpedienteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(expediente)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Expediente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExpediente() throws Exception {
        // Initialize the database
        expedienteRepository.saveAndFlush(expediente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the expediente
        restExpedienteMockMvc
            .perform(delete(ENTITY_API_URL_ID, expediente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return expedienteRepository.count();
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

    protected Expediente getPersistedExpediente(Expediente expediente) {
        return expedienteRepository.findById(expediente.getId()).orElseThrow();
    }

    protected void assertPersistedExpedienteToMatchAllProperties(Expediente expectedExpediente) {
        assertExpedienteAllPropertiesEquals(expectedExpediente, getPersistedExpediente(expectedExpediente));
    }

    protected void assertPersistedExpedienteToMatchUpdatableProperties(Expediente expectedExpediente) {
        assertExpedienteAllUpdatablePropertiesEquals(expectedExpediente, getPersistedExpediente(expectedExpediente));
    }
}
