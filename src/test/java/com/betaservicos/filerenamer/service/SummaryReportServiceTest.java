package com.betaservicos.filerenamer.service;

import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Summary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SummaryReportServiceTest {

    private SummaryReportService service;
    private Path generatedFilePath;

    @BeforeEach
    void setUp() {
        Summary summary = new Summary(1, "João da Silva", List.of(
                new FileSummary(10, "arquivo1_renomeado.pdf", true, "Sucesso"),
                new FileSummary(11, "arquivo2_renomeado.pdf", false, "Erro ao processar")
        ));
        service = new SummaryReportService(List.of(summary));
    }

    @AfterEach
    void tearDown() {
        // Apaga arquivos CSV gerados no teste
        File dir = new File(System.getProperty("user.dir"));
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("relatorio_") && file.getName().endsWith(".csv")) {
                file.delete();
            }
        }
    }

    @Test
    void testGenerateCsv_success() throws Exception {
        Summary summary = new Summary(1, "Maria, Souza", List.of(
                new FileSummary(100, "renomeado1.pdf", true, "Ok")
        ));
        boolean success = service.generateCsv(List.of(summary));

        assertTrue(success, "Deveria gerar o CSV com sucesso");

        // Verifica se o arquivo foi criado
        File dir = new File(System.getProperty("user.dir"));
        File[] csvs = dir.listFiles((d, name) -> name.startsWith("relatorio_") && name.endsWith(".csv"));
        assertNotNull(csvs);
        assertTrue(csvs.length > 0);

        // Verifica o conteúdo
        File csvFile = csvs[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String header = reader.readLine();
            assertEquals("PessoaId,PessoaNome,FileId,ArquivoRenomeado,Observacao", header);

            String line = reader.readLine();
            assertTrue(line.contains("Maria") && line.contains("renomeado1.pdf"));
        }
    }

    @Test
    void testGenerateReport_success() throws Exception {
        Summary summary = new Summary(2, "Carlos Teste", List.of(
                new FileSummary(200, "file.pdf", true, "Arquivo renomeado com sucesso")
        ));
        boolean result = service.generateReport(List.of(summary));

        assertTrue(result);

        // Verifica se o arquivo foi criado
        File dir = new File(System.getProperty("user.dir"));
        File[] csvs = dir.listFiles((d, name) -> name.startsWith("relatorio_") && name.endsWith(".csv"));
        assertNotNull(csvs);
        assertTrue(csvs.length > 0);

        File report = csvs[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(report))) {
            String line = reader.readLine();
            assertEquals("----------------------------------------------------------", line);
        }
    }

    @Test
    void testGenerateCsv_withNullFields_shouldHandleGracefully() {
        Summary summary = new Summary(3, null, List.of(
                new FileSummary(300, null, false, null)
        ));
        boolean success = service.generateCsv(List.of(summary));

        assertTrue(success);
    }

    @Test
    void testGenerateCsv_withIOException_shouldReturnFalse() {
        SummaryReportService brokenService = new SummaryReportService(Collections.emptyList()) {
            @Override
            public boolean generateCsv(List<Summary> summaries) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("/invalid_path/test.csv"))) {
                    writer.write("teste");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        };

        boolean result = brokenService.generateCsv(List.of());
        assertFalse(result);
    }

}
