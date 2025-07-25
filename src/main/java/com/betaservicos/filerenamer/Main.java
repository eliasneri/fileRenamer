package com.betaservicos.filerenamer;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.config.DatabaseInitializer;
import com.betaservicos.filerenamer.domain.Summary;
import com.betaservicos.filerenamer.service.FileRenameService;
import com.betaservicos.filerenamer.service.SummaryReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            new File("logs").mkdirs();

            logger.info("-------------- APLICAÇÃO INICIADA ----------------");

            DatabaseConfig dbConfig = new DatabaseConfig();

            if (dbConfig.isConnected()) {
                DatabaseInitializer init = new DatabaseInitializer(dbConfig);
                FileRenameService service = FileRenameService.fromConfig(dbConfig);
                List<Summary> summaryList = service.processAllFiles();

                //Relatório
                SummaryReportService reportService = new SummaryReportService(summaryList);
                //txt
                boolean txt = reportService.generateReport(summaryList);
                boolean csv = reportService.generateCsv(summaryList);
                logger.info("Relatório TXT: {}", txt);
                logger.info("Relatório CSV: {}", csv);

            }

            logger.info("-------------- APLICAÇÃO FINALIZADA --------------");

        } catch (Exception e){
            logger.error("Erro durante a execução: ", e);
        }
    }
}
