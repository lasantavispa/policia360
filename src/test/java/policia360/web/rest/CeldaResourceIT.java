package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.CeldaAsserts.*;
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
import policia360.domain.Celda;
import policia360.domain.enumeration.NumeroCamas;
import policia360.repository.CeldaRepository;

/**
 * Integration tests for the {@link CeldaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CeldaResourceIT {

    private static final NumeroCamas DEFAULT_NUMERO_CAMAS = NumeroCamas.UNO;
    private static final NumeroCamas UPDATED_NUMERO_CAMAS = NumeroCamas.DOS;

    private static final Boolean DEFAULT_DISPONIBILIDAD = false;
    private static final Boolean UPDATED_DISPONIBILIDAD = true;

    private static final String ENTITY_API_URL = "/api/celdas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CeldaRepository celdaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCeldaMockMvc;

    private Celda celda;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Celda createEntity(EntityManager em) {
        Celda celda = new Celda().numeroCamas(DEFAULT_NUMERO_CAMAS).disponibilidad(DEFAULT_DISPONIBILIDAD);
        return celda;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Celda createUpdatedEntity(EntityManager em) {
        Celda celda = new Celda().numeroCamas(UPDATED_NUMERO_CAMAS).disponibilidad(UPDATED_DISPONIBILIDAD);
        return celda;
    }

    @BeforeEach
    public void initTest() {
        celda = createEntity(em);
    }

    @Test
    @Transactional
    void createCelda() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Celda
        var returnedCelda = om.readValue(
            restCeldaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(celda)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Celda.class
        );

        // Validate the Celda in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCeldaUpdatableFieldsEquals(returnedCelda, getPersistedCelda(returnedCelda));
    }

    @Test
    @Transactional
    void createCeldaWithExistingId() throws Exception {
        // Create the Celda with an existing ID
        celda.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCeldaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(celda)))
            .andExpect(status().isBadRequest());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCeldas() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        // Get all the celdaList
        restCeldaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(celda.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroCamas").value(hasItem(DEFAULT_NUMERO_CAMAS.toString())))
            .andExpect(jsonPath("$.[*].disponibilidad").value(hasItem(DEFAULT_DISPONIBILIDAD.booleanValue())));
    }

    @Test
    @Transactional
    void getCelda() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        // Get the celda
        restCeldaMockMvc
            .perform(get(ENTITY_API_URL_ID, celda.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(celda.getId().intValue()))
            .andExpect(jsonPath("$.numeroCamas").value(DEFAULT_NUMERO_CAMAS.toString()))
            .andExpect(jsonPath("$.disponibilidad").value(DEFAULT_DISPONIBILIDAD.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingCelda() throws Exception {
        // Get the celda
        restCeldaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCelda() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the celda
        Celda updatedCelda = celdaRepository.findById(celda.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCelda are not directly saved in db
        em.detach(updatedCelda);
        updatedCelda.numeroCamas(UPDATED_NUMERO_CAMAS).disponibilidad(UPDATED_DISPONIBILIDAD);

        restCeldaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCelda.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCelda))
            )
            .andExpect(status().isOk());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCeldaToMatchAllProperties(updatedCelda);
    }

    @Test
    @Transactional
    void putNonExistingCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(put(ENTITY_API_URL_ID, celda.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(celda)))
            .andExpect(status().isBadRequest());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(celda))
            )
            .andExpect(status().isBadRequest());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(celda)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCeldaWithPatch() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the celda using partial update
        Celda partialUpdatedCelda = new Celda();
        partialUpdatedCelda.setId(celda.getId());

        partialUpdatedCelda.numeroCamas(UPDATED_NUMERO_CAMAS);

        restCeldaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCelda.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCelda))
            )
            .andExpect(status().isOk());

        // Validate the Celda in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCeldaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCelda, celda), getPersistedCelda(celda));
    }

    @Test
    @Transactional
    void fullUpdateCeldaWithPatch() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the celda using partial update
        Celda partialUpdatedCelda = new Celda();
        partialUpdatedCelda.setId(celda.getId());

        partialUpdatedCelda.numeroCamas(UPDATED_NUMERO_CAMAS).disponibilidad(UPDATED_DISPONIBILIDAD);

        restCeldaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCelda.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCelda))
            )
            .andExpect(status().isOk());

        // Validate the Celda in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCeldaUpdatableFieldsEquals(partialUpdatedCelda, getPersistedCelda(partialUpdatedCelda));
    }

    @Test
    @Transactional
    void patchNonExistingCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, celda.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(celda))
            )
            .andExpect(status().isBadRequest());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(celda))
            )
            .andExpect(status().isBadRequest());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCelda() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        celda.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCeldaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(celda)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Celda in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCelda() throws Exception {
        // Initialize the database
        celdaRepository.saveAndFlush(celda);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the celda
        restCeldaMockMvc
            .perform(delete(ENTITY_API_URL_ID, celda.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return celdaRepository.count();
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

    protected Celda getPersistedCelda(Celda celda) {
        return celdaRepository.findById(celda.getId()).orElseThrow();
    }

    protected void assertPersistedCeldaToMatchAllProperties(Celda expectedCelda) {
        assertCeldaAllPropertiesEquals(expectedCelda, getPersistedCelda(expectedCelda));
    }

    protected void assertPersistedCeldaToMatchUpdatableProperties(Celda expectedCelda) {
        assertCeldaAllUpdatablePropertiesEquals(expectedCelda, getPersistedCelda(expectedCelda));
    }
}
