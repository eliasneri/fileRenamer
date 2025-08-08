package com.betaservicos.filerenamer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("application.properties não encontrado");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar application.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
