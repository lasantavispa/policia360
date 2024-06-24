package policia360.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CeldaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Celda getCeldaSample1() {
        return new Celda().id(1L);
    }

    public static Celda getCeldaSample2() {
        return new Celda().id(2L);
    }

    public static Celda getCeldaRandomSampleGenerator() {
        return new Celda().id(longCount.incrementAndGet());
    }
}
