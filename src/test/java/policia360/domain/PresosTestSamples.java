package policia360.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PresosTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Presos getPresosSample1() {
        return new Presos().id(1L).nombre("nombre1").apellido("apellido1").dni("dni1").enfermedades("enfermedades1");
    }

    public static Presos getPresosSample2() {
        return new Presos().id(2L).nombre("nombre2").apellido("apellido2").dni("dni2").enfermedades("enfermedades2");
    }

    public static Presos getPresosRandomSampleGenerator() {
        return new Presos()
            .id(longCount.incrementAndGet())
            .nombre(UUID.randomUUID().toString())
            .apellido(UUID.randomUUID().toString())
            .dni(UUID.randomUUID().toString())
            .enfermedades(UUID.randomUUID().toString());
    }
}
