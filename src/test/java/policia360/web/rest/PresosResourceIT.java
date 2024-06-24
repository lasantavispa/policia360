package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.PresosAsserts.*;
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
import policia360.domain.Presos;
import policia360.repository.PresosRepository;

/**
 * Integration tests for the {@link PresosResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PresosResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_EDAD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EDAD = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DNI = "AAAAAAAAAA";
    private static final String UPDATED_DNI = "BBBBBBBBBB";

    private static final String DEFAULT_ENFERMEDADES = "AAAAAAAAAA";
    private static final String UPDATED_ENFERMEDADES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/presos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PresosRepository presosRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPresosMockMvc;

    private Presos presos;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Presos createEntity(EntityManager em) {
        Presos presos = new Presos()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .edad(DEFAULT_EDAD)
            .dni(DEFAULT_DNI)
            .enfermedades(DEFAULT_ENFERMEDADES);
        return presos;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Presos createUpdatedEntity(EntityManager em) {
        Presos presos = new Presos()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .dni(UPDATED_DNI)
            .enfermedades(UPDATED_ENFERMEDADES);
        return presos;
    }

    @BeforeEach
    public void initTest() {
        presos = createEntity(em);
    }

    @Test
    @Transactional
    void createPresos() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Presos
        var returnedPresos = om.readValue(
            restPresosMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presos)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Presos.class
        );

        // Validate the Presos in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPresosUpdatableFieldsEquals(returnedPresos, getPersistedPresos(returnedPresos));
    }

    @Test
    @Transactional
    void createPresosWithExistingId() throws Exception {
        // Create the Presos with an existing ID
        presos.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPresosMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presos)))
            .andExpect(status().isBadRequest());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPresos() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        // Get all the presosList
        restPresosMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(presos.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO)))
            .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD.toString())))
            .andExpect(jsonPath("$.[*].dni").value(hasItem(DEFAULT_DNI)))
            .andExpect(jsonPath("$.[*].enfermedades").value(hasItem(DEFAULT_ENFERMEDADES)));
    }

    @Test
    @Transactional
    void getPresos() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        // Get the presos
        restPresosMockMvc
            .perform(get(ENTITY_API_URL_ID, presos.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(presos.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD.toString()))
            .andExpect(jsonPath("$.dni").value(DEFAULT_DNI))
            .andExpect(jsonPath("$.enfermedades").value(DEFAULT_ENFERMEDADES));
    }

    @Test
    @Transactional
    void getNonExistingPresos() throws Exception {
        // Get the presos
        restPresosMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPresos() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presos
        Presos updatedPresos = presosRepository.findById(presos.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPresos are not directly saved in db
        em.detach(updatedPresos);
        updatedPresos
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .dni(UPDATED_DNI)
            .enfermedades(UPDATED_ENFERMEDADES);

        restPresosMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPresos.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPresos))
            )
            .andExpect(status().isOk());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPresosToMatchAllProperties(updatedPresos);
    }

    @Test
    @Transactional
    void putNonExistingPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(put(ENTITY_API_URL_ID, presos.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presos)))
            .andExpect(status().isBadRequest());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(presos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(presos)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePresosWithPatch() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presos using partial update
        Presos partialUpdatedPresos = new Presos();
        partialUpdatedPresos.setId(presos.getId());

        partialUpdatedPresos.apellido(UPDATED_APELLIDO).edad(UPDATED_EDAD).enfermedades(UPDATED_ENFERMEDADES);

        restPresosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPresos.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPresos))
            )
            .andExpect(status().isOk());

        // Validate the Presos in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPresosUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPresos, presos), getPersistedPresos(presos));
    }

    @Test
    @Transactional
    void fullUpdatePresosWithPatch() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the presos using partial update
        Presos partialUpdatedPresos = new Presos();
        partialUpdatedPresos.setId(presos.getId());

        partialUpdatedPresos
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .dni(UPDATED_DNI)
            .enfermedades(UPDATED_ENFERMEDADES);

        restPresosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPresos.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPresos))
            )
            .andExpect(status().isOk());

        // Validate the Presos in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPresosUpdatableFieldsEquals(partialUpdatedPresos, getPersistedPresos(partialUpdatedPresos));
    }

    @Test
    @Transactional
    void patchNonExistingPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, presos.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(presos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(presos))
            )
            .andExpect(status().isBadRequest());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPresos() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        presos.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPresosMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(presos)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Presos in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePresos() throws Exception {
        // Initialize the database
        presosRepository.saveAndFlush(presos);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the presos
        restPresosMockMvc
            .perform(delete(ENTITY_API_URL_ID, presos.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return presosRepository.count();
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

    protected Presos getPersistedPresos(Presos presos) {
        return presosRepository.findById(presos.getId()).orElseThrow();
    }

    protected void assertPersistedPresosToMatchAllProperties(Presos expectedPresos) {
        assertPresosAllPropertiesEquals(expectedPresos, getPersistedPresos(expectedPresos));
    }

    protected void assertPersistedPresosToMatchUpdatableProperties(Presos expectedPresos) {
        assertPresosAllUpdatablePropertiesEquals(expectedPresos, getPersistedPresos(expectedPresos));
    }
}
