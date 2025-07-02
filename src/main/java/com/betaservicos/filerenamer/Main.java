package com.betaservicos.filerenamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            logger.info("Iniciando o processo!");
        } catch (Exception e){
            logger.error("Erro durante a execução: ", e);
        }


    }

}
