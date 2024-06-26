package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpedienteAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertExpedienteAllPropertiesEquals(Expediente expected, Expediente actual) {
        assertExpedienteAutoGeneratedPropertiesEquals(expected, actual);
        assertExpedienteAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertExpedienteAllUpdatablePropertiesEquals(Expediente expected, Expediente actual) {
        assertExpedienteUpdatableFieldsEquals(expected, actual);
        assertExpedienteUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertExpedienteAutoGeneratedPropertiesEquals(Expediente expected, Expediente actual) {
        assertThat(expected)
            .as("Verify Expediente auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertExpedienteUpdatableFieldsEquals(Expediente expected, Expediente actual) {
        assertThat(expected)
            .as("Verify Expediente relevant properties")
            .satisfies(e -> assertThat(e.getDelito()).as("check delito").isEqualTo(actual.getDelito()))
            .satisfies(e -> assertThat(e.getFecha()).as("check fecha").isEqualTo(actual.getFecha()))
            .satisfies(e -> assertThat(e.getResolucion()).as("check resolucion").isEqualTo(actual.getResolucion()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertExpedienteUpdatableRelationshipsEquals(Expediente expected, Expediente actual) {}
}
