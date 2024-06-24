package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.CeldaTestSamples.*;
import static policia360.domain.PresosTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class CeldaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Celda.class);
        Celda celda1 = getCeldaSample1();
        Celda celda2 = new Celda();
        assertThat(celda1).isNotEqualTo(celda2);

        celda2.setId(celda1.getId());
        assertThat(celda1).isEqualTo(celda2);

        celda2 = getCeldaSample2();
        assertThat(celda1).isNotEqualTo(celda2);
    }

    @Test
    void presosTest() throws Exception {
        Celda celda = getCeldaRandomSampleGenerator();
        Presos presosBack = getPresosRandomSampleGenerator();

        celda.addPresos(presosBack);
        assertThat(celda.getPresos()).containsOnly(presosBack);
        assertThat(presosBack.getCelda()).isEqualTo(celda);

        celda.removePresos(presosBack);
        assertThat(celda.getPresos()).doesNotContain(presosBack);
        assertThat(presosBack.getCelda()).isNull();

        celda.presos(new HashSet<>(Set.of(presosBack)));
        assertThat(celda.getPresos()).containsOnly(presosBack);
        assertThat(presosBack.getCelda()).isEqualTo(celda);

        celda.setPresos(new HashSet<>());
        assertThat(celda.getPresos()).doesNotContain(presosBack);
        assertThat(presosBack.getCelda()).isNull();
    }
}
