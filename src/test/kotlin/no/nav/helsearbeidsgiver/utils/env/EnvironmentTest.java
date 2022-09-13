package no.nav.helsearbeidsgiver.utils.env;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnvironmentTest {

    @Test
    public void shouldRead() {
        DemoSettings object = Environment.read(DemoSettings.class);
        assertNotNull(object.javaHome);
    }

    @Test
    public void shouldInject() {
        DemoSettings object = new DemoSettings();
        Environment.inject(object);
        assertNotNull(object.javaHome);
    }

}