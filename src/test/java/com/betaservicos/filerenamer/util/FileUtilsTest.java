package com.betaservicos.filerenamer.util;

import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.FileSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {
    @TempDir
    Path tempDir;

    @Test
    void testRenameFile_Success() throws IOException {
        Path testFile = Files.createFile(tempDir.resolve("123"));

        FileRecord record = new FileRecord(123, "txt", "documento", "João Silva");
        FileUtils f = new FileUtils();
        FileSummary result = f.renameFile(tempDir.toString(), record);

        assertTrue(result.isSuccess());
        assertEquals("123_Joao_Silva_documento.txt", result.getFileRenamed());
        assertTrue(Files.exists(tempDir.resolve("123_Joao_Silva_documento.txt")));
    }

    @Test
    void testRenameFile_FileNotFound() {
        FileRecord record = new FileRecord(999, "pdf", "doc", "Maria Souza");

        FileUtils f = new FileUtils();
        FileSummary result = f.renameFile(tempDir.toString(), record);

        assertFalse(result.isSuccess());
        assertEquals("semAção", result.getFileRenamed());
        assertTrue(result.getMessage().contains("Arquivo não encontrado"));
    }

    @Test
    void testRenameFile_InvalidFolder() {
        FileRecord record = new FileRecord(123, "jpg", "foto", "Carlos");

        FileUtils f = new FileUtils();
        FileSummary result = f.renameFile("", record);

        assertFalse(result.isSuccess());
        assertEquals("semAção", result.getFileRenamed());
        assertTrue(result.getMessage().contains("folder invalid"));
    }

    @Test
        void testRenameFile_ObjectError() throws IOException {
        Files.createFile(tempDir.resolve("456_teste.dat"));

        FileRecord record = new FileRecord();

        FileUtils f = new FileUtils();
        FileSummary result = f.renameFile(tempDir.toString(), record);

        assertFalse(result.isSuccess());
        assertEquals("object file invalid", result.getMessage());
    }
    @Test
    void testRenameFile_GenerateNameError() throws IOException {
        Files.createFile(tempDir.resolve("456_teste.dat"));

        FileRecord record = new FileRecord(456, "", "", null);

        FileUtils f = new FileUtils();
        FileSummary result = f.renameFile(tempDir.toString(), record);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("generate new name error"));
    }

    @Test
    void testFindFileById_Found() throws IOException {
        Files.createFile(tempDir.resolve("111"));
        Files.createFile(tempDir.resolve("222"));

        File[] files = tempDir.toFile().listFiles();
        FileUtils f = new FileUtils();
        File found = f.findFileById(files, "111");

        assertNotNull(found);
        assertTrue(found.getName().startsWith("111"));
    }

    @Test
    void testFindFileById_NotFound() throws IOException {
        Files.createFile(tempDir.resolve("333_arquivo.pdf"));

        File[] files = tempDir.toFile().listFiles();

        FileUtils f = new FileUtils();
        File found = f.findFileById(files, "999");

        assertNull(found);
    }

    @Test
    void testGenerateNewFileName_NormalCase() {
        FileRecord record = new FileRecord(789, "png", "imagem", "José Álves");

        FileUtils f = new FileUtils();
        String result = f.generateNewFileName(record, "789");

        assertEquals("789_Jose_Alves_imagem.png", result);
    }

    @Test
    void testGenerateNewFileName_WithSpecialChars() {
        FileRecord record = new FileRecord(101, "docx", "relatório", "Mário Ângelo");

        FileUtils f = new FileUtils();
        String result = f.generateNewFileName(record, "101");

        assertEquals("101_Mario_Angelo_relatorio.docx", result);
    }

    @Test
    void testGenerateNewFileName_NoExtension() {
        FileRecord record = new FileRecord(202, "dat", "dados", "Ana");

        FileUtils f = new FileUtils();
        String result = f.generateNewFileName(record, "202");

        assertEquals("202_Ana_dados.dat", result);
    }

    @Test
    void testGenerateNewFileName_InvalidInput() {
        FileUtils f = new FileUtils();
        assertNull(f.generateNewFileName(null, "test.txt"));
        assertNull(f.generateNewFileName(new FileRecord(), ""));
    }

}
