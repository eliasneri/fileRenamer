package com.betaservicos.filerenamer.config;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter @Setter
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private String url;
    private String user;
    private String password;

    public DatabaseConfig() {

        this.user = null;
        this.password = null;
        this.url = null;

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {

            Properties prop = new Properties();
            if (input != null) {
                prop.load(input);
            } else {
                throw new IOException("FAIL! application.properties");
            }

            this.url = prop.getProperty("db.url");
            this.password = prop.getProperty("db.password");
            this.user = prop.getProperty("db.user");

        } catch (IOException ex) {
            logger.error("Erro ao Ler arquivo de Configuração do Banco de Dados! ", ex);
        }
    }

}
