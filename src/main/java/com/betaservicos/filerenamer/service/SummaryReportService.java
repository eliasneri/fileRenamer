package com.betaservicos.filerenamer.service;

import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SummaryReportService {
    private static final Logger logger = LoggerFactory.getLogger(SummaryReportService.class);

    private final List<Summary> summaryList;
    private String fileName;

    public SummaryReportService(List<Summary> summaryList) {
        this.summaryList = summaryList;
        this.fileName = "relatorio_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));
    }

    public boolean generateCsv(List<Summary> summaries) {
        Path outputPath = Paths.get(System.getProperty("user.dir"), (this.fileName + ".csv"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {

            writer.write("PessoaId,PessoaNome,FileId,ArquivoRenomeado,Observacao");
            writer.newLine();

            for (Summary summary : summaries) {
                for (FileSummary file : summary.getFileSummaryList()) {
                    String line = String.format(
                            "%d,%s,%d,%s,%s",
                            summary.getSummaryPersonId(),
                            escapeCsv(summary.getSummaryPersonName()),
                            file.getFileId(),
                            escapeCsv(file.getFileRenamed()),
                            escapeCsv(file.getMessage())
                    );
                    writer.write(line);
                    writer.newLine();
                }
            }

            writer.flush();
            logger.info("CSV gerado com sucesso em: {}" ,outputPath);
            return true;

        } catch (IOException e) {
            logger.error("Erro ao gerar o CSV: {}" , e.getMessage());
            return false;
        }
    }

    public boolean generateReport(List<Summary> summaryList) {
        Path outputPath = Paths.get(System.getProperty("user.dir"), (this.fileName + ".csv"));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {

            for (Summary summary : summaryList) {
                writer.write("----------------------------------------------------------");
                writer.newLine();
                writer.write("Nome no sistema anterior: " + summary.getSummaryPersonName());
                writer.newLine();
                writer.write("Id no sistema anterior: " + summary.getSummaryPersonId());
                writer.newLine();
                writer.write("Arquivos Encontrados:");
                writer.newLine();

                int index = 0;
                for (FileSummary file : summary.getFileSummaryList()) {
                    index++;
                    writer.write(index + " - Id no sistema antigo: " + file.getFileId());
                    writer.newLine();
                    writer.write("..: Arquivo Renomeado: " + file.getFileRenamed());
                    writer.newLine();
                    writer.write("..: Observação: " + file.getMessage());
                    writer.newLine();
                }

                writer.write("----------------------------------------------------------");
                writer.newLine();
                writer.newLine();
            }

            writer.flush();

            logger.info("Relatório TXT gerado com sucesso: {}", outputPath);
            return true;

        } catch (IOException e) {
            logger.error("Erro ao gerar relatório: {}", e.getMessage());
            return false;
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        boolean precisaEscapar = value.contains(",") || value.contains("\"") || value.contains("\n");
        if (precisaEscapar) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
