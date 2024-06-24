package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.CalendarioTestSamples.*;
import static policia360.domain.TrabajadoresTestSamples.*;
import static policia360.domain.VehiculoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class TrabajadoresTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trabajadores.class);
        Trabajadores trabajadores1 = getTrabajadoresSample1();
        Trabajadores trabajadores2 = new Trabajadores();
        assertThat(trabajadores1).isNotEqualTo(trabajadores2);

        trabajadores2.setId(trabajadores1.getId());
        assertThat(trabajadores1).isEqualTo(trabajadores2);

        trabajadores2 = getTrabajadoresSample2();
        assertThat(trabajadores1).isNotEqualTo(trabajadores2);
    }

    @Test
    void calendarioTest() throws Exception {
        Trabajadores trabajadores = getTrabajadoresRandomSampleGenerator();
        Calendario calendarioBack = getCalendarioRandomSampleGenerator();

        trabajadores.addCalendario(calendarioBack);
        assertThat(trabajadores.getCalendarios()).containsOnly(calendarioBack);
        assertThat(calendarioBack.getTrabajadores()).isEqualTo(trabajadores);

        trabajadores.removeCalendario(calendarioBack);
        assertThat(trabajadores.getCalendarios()).doesNotContain(calendarioBack);
        assertThat(calendarioBack.getTrabajadores()).isNull();

        trabajadores.calendarios(new HashSet<>(Set.of(calendarioBack)));
        assertThat(trabajadores.getCalendarios()).containsOnly(calendarioBack);
        assertThat(calendarioBack.getTrabajadores()).isEqualTo(trabajadores);

        trabajadores.setCalendarios(new HashSet<>());
        assertThat(trabajadores.getCalendarios()).doesNotContain(calendarioBack);
        assertThat(calendarioBack.getTrabajadores()).isNull();
    }

    @Test
    void vehiculoTest() throws Exception {
        Trabajadores trabajadores = getTrabajadoresRandomSampleGenerator();
        Vehiculo vehiculoBack = getVehiculoRandomSampleGenerator();

        trabajadores.addVehiculo(vehiculoBack);
        assertThat(trabajadores.getVehiculos()).containsOnly(vehiculoBack);

        trabajadores.removeVehiculo(vehiculoBack);
        assertThat(trabajadores.getVehiculos()).doesNotContain(vehiculoBack);

        trabajadores.vehiculos(new HashSet<>(Set.of(vehiculoBack)));
        assertThat(trabajadores.getVehiculos()).containsOnly(vehiculoBack);

        trabajadores.setVehiculos(new HashSet<>());
        assertThat(trabajadores.getVehiculos()).doesNotContain(vehiculoBack);
    }
}
