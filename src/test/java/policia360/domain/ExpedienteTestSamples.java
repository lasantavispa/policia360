package policia360.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExpedienteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Expediente getExpedienteSample1() {
        return new Expediente().id(1L).delito("delito1");
    }

    public static Expediente getExpedienteSample2() {
        return new Expediente().id(2L).delito("delito2");
    }

    public static Expediente getExpedienteRandomSampleGenerator() {
        return new Expediente().id(longCount.incrementAndGet()).delito(UUID.randomUUID().toString());
    }
}
