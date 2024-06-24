package policia360.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TrabajadoresTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Trabajadores getTrabajadoresSample1() {
        return new Trabajadores().id(1L).dni("dni1");
    }

    public static Trabajadores getTrabajadoresSample2() {
        return new Trabajadores().id(2L).dni("dni2");
    }

    public static Trabajadores getTrabajadoresRandomSampleGenerator() {
        return new Trabajadores().id(longCount.incrementAndGet()).dni(UUID.randomUUID().toString());
    }
}
