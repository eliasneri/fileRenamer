package com.betaservicos.filerenamer;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            new File("logs").mkdirs();

            logger.info("-------------- APLICAÇÃO INICIADA ----------------");

            DatabaseConfig dbConfig = new DatabaseConfig();

            if (dbConfig.isConnected()) {

            }

            logger.info("-------------- APLICAÇÃO FINALIZADA --------------");

        } catch (Exception e){
            logger.error("Erro durante a execução: ", e);
        }


    }

}
