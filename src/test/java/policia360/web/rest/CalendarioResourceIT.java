package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.CalendarioAsserts.*;
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
import policia360.domain.Calendario;
import policia360.repository.CalendarioRepository;

/**
 * Integration tests for the {@link CalendarioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CalendarioResourceIT {

    private static final LocalDate DEFAULT_FECHA_INICIO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_INICIO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FECHA_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_CONCEPTO = "AAAAAAAAAA";
    private static final String UPDATED_CONCEPTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/calendarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CalendarioRepository calendarioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCalendarioMockMvc;

    private Calendario calendario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calendario createEntity(EntityManager em) {
        Calendario calendario = new Calendario().fechaInicio(DEFAULT_FECHA_INICIO).fechaFin(DEFAULT_FECHA_FIN).concepto(DEFAULT_CONCEPTO);
        return calendario;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calendario createUpdatedEntity(EntityManager em) {
        Calendario calendario = new Calendario().fechaInicio(UPDATED_FECHA_INICIO).fechaFin(UPDATED_FECHA_FIN).concepto(UPDATED_CONCEPTO);
        return calendario;
    }

    @BeforeEach
    public void initTest() {
        calendario = createEntity(em);
    }

    @Test
    @Transactional
    void createCalendario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Calendario
        var returnedCalendario = om.readValue(
            restCalendarioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calendario)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Calendario.class
        );

        // Validate the Calendario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCalendarioUpdatableFieldsEquals(returnedCalendario, getPersistedCalendario(returnedCalendario));
    }

    @Test
    @Transactional
    void createCalendarioWithExistingId() throws Exception {
        // Create the Calendario with an existing ID
        calendario.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCalendarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calendario)))
            .andExpect(status().isBadRequest());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCalendarios() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        // Get all the calendarioList
        restCalendarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calendario.getId().intValue())))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(DEFAULT_FECHA_INICIO.toString())))
            .andExpect(jsonPath("$.[*].fechaFin").value(hasItem(DEFAULT_FECHA_FIN.toString())))
            .andExpect(jsonPath("$.[*].concepto").value(hasItem(DEFAULT_CONCEPTO)));
    }

    @Test
    @Transactional
    void getCalendario() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        // Get the calendario
        restCalendarioMockMvc
            .perform(get(ENTITY_API_URL_ID, calendario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(calendario.getId().intValue()))
            .andExpect(jsonPath("$.fechaInicio").value(DEFAULT_FECHA_INICIO.toString()))
            .andExpect(jsonPath("$.fechaFin").value(DEFAULT_FECHA_FIN.toString()))
            .andExpect(jsonPath("$.concepto").value(DEFAULT_CONCEPTO));
    }

    @Test
    @Transactional
    void getNonExistingCalendario() throws Exception {
        // Get the calendario
        restCalendarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCalendario() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calendario
        Calendario updatedCalendario = calendarioRepository.findById(calendario.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCalendario are not directly saved in db
        em.detach(updatedCalendario);
        updatedCalendario.fechaInicio(UPDATED_FECHA_INICIO).fechaFin(UPDATED_FECHA_FIN).concepto(UPDATED_CONCEPTO);

        restCalendarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCalendario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCalendario))
            )
            .andExpect(status().isOk());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCalendarioToMatchAllProperties(updatedCalendario);
    }

    @Test
    @Transactional
    void putNonExistingCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calendario.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calendario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calendario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calendario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCalendarioWithPatch() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calendario using partial update
        Calendario partialUpdatedCalendario = new Calendario();
        partialUpdatedCalendario.setId(calendario.getId());

        restCalendarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalendario))
            )
            .andExpect(status().isOk());

        // Validate the Calendario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalendarioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCalendario, calendario),
            getPersistedCalendario(calendario)
        );
    }

    @Test
    @Transactional
    void fullUpdateCalendarioWithPatch() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calendario using partial update
        Calendario partialUpdatedCalendario = new Calendario();
        partialUpdatedCalendario.setId(calendario.getId());

        partialUpdatedCalendario.fechaInicio(UPDATED_FECHA_INICIO).fechaFin(UPDATED_FECHA_FIN).concepto(UPDATED_CONCEPTO);

        restCalendarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalendario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalendario))
            )
            .andExpect(status().isOk());

        // Validate the Calendario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalendarioUpdatableFieldsEquals(partialUpdatedCalendario, getPersistedCalendario(partialUpdatedCalendario));
    }

    @Test
    @Transactional
    void patchNonExistingCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, calendario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calendario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calendario))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCalendario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calendario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalendarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(calendario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Calendario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCalendario() throws Exception {
        // Initialize the database
        calendarioRepository.saveAndFlush(calendario);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the calendario
        restCalendarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, calendario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return calendarioRepository.count();
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

    protected Calendario getPersistedCalendario(Calendario calendario) {
        return calendarioRepository.findById(calendario.getId()).orElseThrow();
    }

    protected void assertPersistedCalendarioToMatchAllProperties(Calendario expectedCalendario) {
        assertCalendarioAllPropertiesEquals(expectedCalendario, getPersistedCalendario(expectedCalendario));
    }

    protected void assertPersistedCalendarioToMatchUpdatableProperties(Calendario expectedCalendario) {
        assertCalendarioAllUpdatablePropertiesEquals(expectedCalendario, getPersistedCalendario(expectedCalendario));
    }
}
