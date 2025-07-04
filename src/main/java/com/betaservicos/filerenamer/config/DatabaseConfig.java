package com.betaservicos.filerenamer.config;

import com.betaservicos.filerenamer.util.AppProperties;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter @Setter
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private String url;
    private String user;
    private String password;
    private boolean connected;

    public DatabaseConfig() {
        this.connected = load();
    }

    public boolean load() {

        this.url = AppProperties.get("db.url") + AppProperties.get("db.dataname");
        this.password = AppProperties.get("db.password");
        this.user = AppProperties.get("db.user");

        if (url != null) {
            logger.info("Conectado com sucesso: " + url);
            return true;
        } else {
            logger.error("Não conectado !!!");
            return false;
        }
    }
}
