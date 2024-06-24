package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.CeldaTestSamples.*;
import static policia360.domain.ExpedienteTestSamples.*;
import static policia360.domain.PresosTestSamples.*;

import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class PresosTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Presos.class);
        Presos presos1 = getPresosSample1();
        Presos presos2 = new Presos();
        assertThat(presos1).isNotEqualTo(presos2);

        presos2.setId(presos1.getId());
        assertThat(presos1).isEqualTo(presos2);

        presos2 = getPresosSample2();
        assertThat(presos1).isNotEqualTo(presos2);
    }

    @Test
    void expedienteTest() throws Exception {
        Presos presos = getPresosRandomSampleGenerator();
        Expediente expedienteBack = getExpedienteRandomSampleGenerator();

        presos.setExpediente(expedienteBack);
        assertThat(presos.getExpediente()).isEqualTo(expedienteBack);

        presos.expediente(null);
        assertThat(presos.getExpediente()).isNull();
    }

    @Test
    void celdaTest() throws Exception {
        Presos presos = getPresosRandomSampleGenerator();
        Celda celdaBack = getCeldaRandomSampleGenerator();

        presos.setCelda(celdaBack);
        assertThat(presos.getCelda()).isEqualTo(celdaBack);

        presos.celda(null);
        assertThat(presos.getCelda()).isNull();
    }
}
