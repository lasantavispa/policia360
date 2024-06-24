package policia360.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static policia360.domain.TrabajadoresTestSamples.*;
import static policia360.domain.VehiculoTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import policia360.web.rest.TestUtil;

class VehiculoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vehiculo.class);
        Vehiculo vehiculo1 = getVehiculoSample1();
        Vehiculo vehiculo2 = new Vehiculo();
        assertThat(vehiculo1).isNotEqualTo(vehiculo2);

        vehiculo2.setId(vehiculo1.getId());
        assertThat(vehiculo1).isEqualTo(vehiculo2);

        vehiculo2 = getVehiculoSample2();
        assertThat(vehiculo1).isNotEqualTo(vehiculo2);
    }

    @Test
    void trabajadoresTest() throws Exception {
        Vehiculo vehiculo = getVehiculoRandomSampleGenerator();
        Trabajadores trabajadoresBack = getTrabajadoresRandomSampleGenerator();

        vehiculo.addTrabajadores(trabajadoresBack);
        assertThat(vehiculo.getTrabajadores()).containsOnly(trabajadoresBack);
        assertThat(trabajadoresBack.getVehiculos()).containsOnly(vehiculo);

        vehiculo.removeTrabajadores(trabajadoresBack);
        assertThat(vehiculo.getTrabajadores()).doesNotContain(trabajadoresBack);
        assertThat(trabajadoresBack.getVehiculos()).doesNotContain(vehiculo);

        vehiculo.trabajadores(new HashSet<>(Set.of(trabajadoresBack)));
        assertThat(vehiculo.getTrabajadores()).containsOnly(trabajadoresBack);
        assertThat(trabajadoresBack.getVehiculos()).containsOnly(vehiculo);

        vehiculo.setTrabajadores(new HashSet<>());
        assertThat(vehiculo.getTrabajadores()).doesNotContain(trabajadoresBack);
        assertThat(trabajadoresBack.getVehiculos()).doesNotContain(vehiculo);
    }
}
