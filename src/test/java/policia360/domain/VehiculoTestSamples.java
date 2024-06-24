package policia360.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VehiculoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Vehiculo getVehiculoSample1() {
        return new Vehiculo().id(1L).identificador("identificador1");
    }

    public static Vehiculo getVehiculoSample2() {
        return new Vehiculo().id(2L).identificador("identificador2");
    }

    public static Vehiculo getVehiculoRandomSampleGenerator() {
        return new Vehiculo().id(longCount.incrementAndGet()).identificador(UUID.randomUUID().toString());
    }
}
