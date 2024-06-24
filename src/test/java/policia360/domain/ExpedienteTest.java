package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.ExpedienteTestSamples.*;
import static policia360.domain.PresosTestSamples.*;

import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class ExpedienteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expediente.class);
        Expediente expediente1 = getExpedienteSample1();
        Expediente expediente2 = new Expediente();
        assertThat(expediente1).isNotEqualTo(expediente2);

        expediente2.setId(expediente1.getId());
        assertThat(expediente1).isEqualTo(expediente2);

        expediente2 = getExpedienteSample2();
        assertThat(expediente1).isNotEqualTo(expediente2);
    }

    @Test
    void presosTest() throws Exception {
        Expediente expediente = getExpedienteRandomSampleGenerator();
        Presos presosBack = getPresosRandomSampleGenerator();

        expediente.setPresos(presosBack);
        assertThat(expediente.getPresos()).isEqualTo(presosBack);
        assertThat(presosBack.getExpediente()).isEqualTo(expediente);

        expediente.presos(null);
        assertThat(expediente.getPresos()).isNull();
        assertThat(presosBack.getExpediente()).isNull();
    }
}
