package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.CalendarioTestSamples.*;
import static policia360.domain.TrabajadoresTestSamples.*;

import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class CalendarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Calendario.class);
        Calendario calendario1 = getCalendarioSample1();
        Calendario calendario2 = new Calendario();
        assertThat(calendario1).isNotEqualTo(calendario2);

        calendario2.setId(calendario1.getId());
        assertThat(calendario1).isEqualTo(calendario2);

        calendario2 = getCalendarioSample2();
        assertThat(calendario1).isNotEqualTo(calendario2);
    }

    @Test
    void trabajadoresTest() throws Exception {
        Calendario calendario = getCalendarioRandomSampleGenerator();
        Trabajadores trabajadoresBack = getTrabajadoresRandomSampleGenerator();

        calendario.setTrabajadores(trabajadoresBack);
        assertThat(calendario.getTrabajadores()).isEqualTo(trabajadoresBack);

        calendario.trabajadores(null);
        assertThat(calendario.getTrabajadores()).isNull();
    }
}
