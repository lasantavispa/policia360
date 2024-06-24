package policia360.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CalendarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Calendario getCalendarioSample1() {
        return new Calendario().id(1L).concepto("concepto1");
    }

    public static Calendario getCalendarioSample2() {
        return new Calendario().id(2L).concepto("concepto2");
    }

    public static Calendario getCalendarioRandomSampleGenerator() {
        return new Calendario().id(longCount.incrementAndGet()).concepto(UUID.randomUUID().toString());
    }
}
