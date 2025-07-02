package com.betaservicos.filerenamer.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConfigTest {
    @Test
    void testDatabaseConfig () {
        DatabaseConfig config = new DatabaseConfig();
        assertNotNull(config.getUrl());
        assertNotNull(config.getUser());
        assertNotNull(config.getPassword());
        assertTrue(config.getUrl().startsWith("jdbc:postgresql"));
    }
}
