package no.nav.helsearbeidsgiver.utils.env;

import java.lang.reflect.Field;

public class Environment {

    public static Object inject(Object objekt) {
        for (Field f : objekt.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(EnvironmentValue.class)){
                String name = f.getAnnotation(EnvironmentValue.class).name();
                String value = System.getenv(name);
                if (f.getType() == String.class) {
                    try {
                        f.set(objekt, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return objekt;
    }

    public static <K> K read(Class<K> klass) {
        K object = createNew(klass);
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(EnvironmentValue.class)){
                String name = f.getAnnotation(EnvironmentValue.class).name();
                String value = System.getenv(name);
                if (f.getType() == String.class) {
                    try {
                        f.set(object, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object;
    }

    public static <K> K createNew(Class<K> klass) {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
