package policia360.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static policia360.domain.TrabajadoresAsserts.*;
import static policia360.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import policia360.IntegrationTest;
import policia360.domain.Trabajadores;
import policia360.domain.enumeration.Estado;
import policia360.domain.enumeration.Puesto;
import policia360.domain.enumeration.Turno;
import policia360.repository.TrabajadoresRepository;

/**
 * Integration tests for the {@link TrabajadoresResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TrabajadoresResourceIT {

    private static final String DEFAULT_DNI = "AAAAAAAAAA";
    private static final String UPDATED_DNI = "BBBBBBBBBB";

    private static final Puesto DEFAULT_PUESTO = Puesto.COMISARIO;
    private static final Puesto UPDATED_PUESTO = Puesto.INSPECTOR;

    private static final Turno DEFAULT_TURNO = Turno.MANANA;
    private static final Turno UPDATED_TURNO = Turno.TARDE;

    private static final LocalDate DEFAULT_ANTIGUEDAD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ANTIGUEDAD = LocalDate.now(ZoneId.systemDefault());

    private static final Estado DEFAULT_ESTADO = Estado.ACTIVO;
    private static final Estado UPDATED_ESTADO = Estado.NOACTIVO;

    private static final String ENTITY_API_URL = "/api/trabajadores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TrabajadoresRepository trabajadoresRepository;

    @Mock
    private TrabajadoresRepository trabajadoresRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTrabajadoresMockMvc;

    private Trabajadores trabajadores;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trabajadores createEntity(EntityManager em) {
        Trabajadores trabajadores = new Trabajadores()
            .dni(DEFAULT_DNI)
            .puesto(DEFAULT_PUESTO)
            .turno(DEFAULT_TURNO)
            .antiguedad(DEFAULT_ANTIGUEDAD)
            .estado(DEFAULT_ESTADO);
        return trabajadores;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trabajadores createUpdatedEntity(EntityManager em) {
        Trabajadores trabajadores = new Trabajadores()
            .dni(UPDATED_DNI)
            .puesto(UPDATED_PUESTO)
            .turno(UPDATED_TURNO)
            .antiguedad(UPDATED_ANTIGUEDAD)
            .estado(UPDATED_ESTADO);
        return trabajadores;
    }

    @BeforeEach
    public void initTest() {
        trabajadores = createEntity(em);
    }

    @Test
    @Transactional
    void createTrabajadores() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Trabajadores
        var returnedTrabajadores = om.readValue(
            restTrabajadoresMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trabajadores)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Trabajadores.class
        );

        // Validate the Trabajadores in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTrabajadoresUpdatableFieldsEquals(returnedTrabajadores, getPersistedTrabajadores(returnedTrabajadores));
    }

    @Test
    @Transactional
    void createTrabajadoresWithExistingId() throws Exception {
        // Create the Trabajadores with an existing ID
        trabajadores.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrabajadoresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trabajadores)))
            .andExpect(status().isBadRequest());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTrabajadores() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        // Get all the trabajadoresList
        restTrabajadoresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trabajadores.getId().intValue())))
            .andExpect(jsonPath("$.[*].dni").value(hasItem(DEFAULT_DNI)))
            .andExpect(jsonPath("$.[*].puesto").value(hasItem(DEFAULT_PUESTO.toString())))
            .andExpect(jsonPath("$.[*].turno").value(hasItem(DEFAULT_TURNO.toString())))
            .andExpect(jsonPath("$.[*].antiguedad").value(hasItem(DEFAULT_ANTIGUEDAD.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrabajadoresWithEagerRelationshipsIsEnabled() throws Exception {
        when(trabajadoresRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTrabajadoresMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(trabajadoresRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTrabajadoresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(trabajadoresRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTrabajadoresMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(trabajadoresRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTrabajadores() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        // Get the trabajadores
        restTrabajadoresMockMvc
            .perform(get(ENTITY_API_URL_ID, trabajadores.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trabajadores.getId().intValue()))
            .andExpect(jsonPath("$.dni").value(DEFAULT_DNI))
            .andExpect(jsonPath("$.puesto").value(DEFAULT_PUESTO.toString()))
            .andExpect(jsonPath("$.turno").value(DEFAULT_TURNO.toString()))
            .andExpect(jsonPath("$.antiguedad").value(DEFAULT_ANTIGUEDAD.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTrabajadores() throws Exception {
        // Get the trabajadores
        restTrabajadoresMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrabajadores() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trabajadores
        Trabajadores updatedTrabajadores = trabajadoresRepository.findById(trabajadores.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrabajadores are not directly saved in db
        em.detach(updatedTrabajadores);
        updatedTrabajadores
            .dni(UPDATED_DNI)
            .puesto(UPDATED_PUESTO)
            .turno(UPDATED_TURNO)
            .antiguedad(UPDATED_ANTIGUEDAD)
            .estado(UPDATED_ESTADO);

        restTrabajadoresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTrabajadores.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTrabajadores))
            )
            .andExpect(status().isOk());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTrabajadoresToMatchAllProperties(updatedTrabajadores);
    }

    @Test
    @Transactional
    void putNonExistingTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, trabajadores.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trabajadores))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(trabajadores))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(trabajadores)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTrabajadoresWithPatch() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trabajadores using partial update
        Trabajadores partialUpdatedTrabajadores = new Trabajadores();
        partialUpdatedTrabajadores.setId(trabajadores.getId());

        partialUpdatedTrabajadores.dni(UPDATED_DNI).puesto(UPDATED_PUESTO).turno(UPDATED_TURNO).estado(UPDATED_ESTADO);

        restTrabajadoresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrabajadores.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrabajadores))
            )
            .andExpect(status().isOk());

        // Validate the Trabajadores in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrabajadoresUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTrabajadores, trabajadores),
            getPersistedTrabajadores(trabajadores)
        );
    }

    @Test
    @Transactional
    void fullUpdateTrabajadoresWithPatch() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trabajadores using partial update
        Trabajadores partialUpdatedTrabajadores = new Trabajadores();
        partialUpdatedTrabajadores.setId(trabajadores.getId());

        partialUpdatedTrabajadores
            .dni(UPDATED_DNI)
            .puesto(UPDATED_PUESTO)
            .turno(UPDATED_TURNO)
            .antiguedad(UPDATED_ANTIGUEDAD)
            .estado(UPDATED_ESTADO);

        restTrabajadoresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrabajadores.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrabajadores))
            )
            .andExpect(status().isOk());

        // Validate the Trabajadores in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTrabajadoresUpdatableFieldsEquals(partialUpdatedTrabajadores, getPersistedTrabajadores(partialUpdatedTrabajadores));
    }

    @Test
    @Transactional
    void patchNonExistingTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, trabajadores.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trabajadores))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(trabajadores))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrabajadores() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trabajadores.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTrabajadoresMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(trabajadores)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trabajadores in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrabajadores() throws Exception {
        // Initialize the database
        trabajadoresRepository.saveAndFlush(trabajadores);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trabajadores
        restTrabajadoresMockMvc
            .perform(delete(ENTITY_API_URL_ID, trabajadores.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return trabajadoresRepository.count();
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

    protected Trabajadores getPersistedTrabajadores(Trabajadores trabajadores) {
        return trabajadoresRepository.findById(trabajadores.getId()).orElseThrow();
    }

    protected void assertPersistedTrabajadoresToMatchAllProperties(Trabajadores expectedTrabajadores) {
        assertTrabajadoresAllPropertiesEquals(expectedTrabajadores, getPersistedTrabajadores(expectedTrabajadores));
    }

    protected void assertPersistedTrabajadoresToMatchUpdatableProperties(Trabajadores expectedTrabajadores) {
        assertTrabajadoresAllUpdatablePropertiesEquals(expectedTrabajadores, getPersistedTrabajadores(expectedTrabajadores));
    }
}
