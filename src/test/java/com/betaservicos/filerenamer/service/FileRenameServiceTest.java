package com.betaservicos.filerenamer.service;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Person;
import com.betaservicos.filerenamer.domain.Summary;
import com.betaservicos.filerenamer.repository.PersonRepository;
import com.betaservicos.filerenamer.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileRenameServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private DatabaseConfig dbConfig;

    private FileRenameService fileRenameService;

    private static final String FOLDER_PATH = "/some/folder/path"; // Caso necessário

    @BeforeEach
    void setUp() {
        fileRenameService = new FileRenameService(personRepository, fileUtils, dbConfig);

    }

    @Test
    void testProcessAllFiles_WithNoPersons_ReturnsEmptyList() {
        when(personRepository.getAllPersonIds()).thenReturn(Collections.emptyList());

        List<Summary> result = fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verifyNoMoreInteractions(personRepository, fileUtils);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void testProcessAllFiles_WithPersonsButNoFiles_ReturnsEmptyList() {
        List<Person> persons = Arrays.asList(
                new Person(1, "João Cleber"),
                new Person(2, "Maria Souza")
        );

        when(personRepository.getAllPersonIds()).thenReturn(persons);
        when(personRepository.getFilesForPerson(anyInt())).thenReturn(Collections.emptyList());

        List<Summary> result = fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verify(personRepository, times(2)).getFilesForPerson(anyInt());
        verifyNoInteractions(fileUtils);

        assertTrue(result.isEmpty());
    }

//    @Test
//    void testProcessAllFiles_WithPersonsAndFiles_ReturnsCorrectSummaryList() {
//        when(dbConfig.isConnected()).thenReturn(true);
//        Person person1 = new Person(1, "João Cleber");
//        Person person2 = new Person(2, "Maria Souza");
//
//        FileRecord file1 = new FileRecord(101, "pdf", "doc1", "João Cleber");
//        FileRecord file2 = new FileRecord(102, "jpg", "img1", "João Cleber");
//
//        FileSummary fileSummary1 = new FileSummary(101, "101_Joao_Cleber_doc1.pdf", true, "Success");
//        FileSummary fileSummary2 = new FileSummary(102, "102_Joao_Cleber_img1.jpg", true, "Success");
//
//        when(personRepository.getAllPersonIds()).thenReturn(Arrays.asList(person1, person2));
//        when(personRepository.getFilesForPerson(1)).thenReturn(Arrays.asList(file1, file2));
//        when(personRepository.getFilesForPerson(2)).thenReturn(Collections.emptyList());
//
//        when(fileUtils.renameFile(anyString(), eq(file1))).thenReturn(fileSummary1);
//        when(fileUtils.renameFile(anyString(), eq(file2))).thenReturn(fileSummary2);
//
//        List<Summary> result = fileRenameService.processAllFiles();
//
//        verify(personRepository, times(1)).getAllPersonIds();
//        verify(personRepository, times(1)).getFilesForPerson(1);
//        verify(personRepository, times(1)).getFilesForPerson(2);
//        verify(fileUtils, times(1)).renameFile(anyString(), eq(file1));
//        verify(fileUtils, times(1)).renameFile(anyString(), eq(file2));
//
//        assertEquals(1, result.size());
//        Summary summary = result.get(0);
//        assertEquals(person1.getPersonId(), summary.getSummaryPersonId());
//        assertEquals(person1.getPersonName(), summary.getSummaryPersonName());
//        assertEquals(2, summary.getFileSummaryList().size());
//        assertTrue(summary.getFileSummaryList().contains(fileSummary1));
//        assertTrue(summary.getFileSummaryList().contains(fileSummary2));
//    }

    @Test
    void testProcessAllFiles_WithFileRenameFailure_LogsErrorAndContinues() {
        when(dbConfig.isConnected()).thenReturn(true);
        Person person = new Person(1, "João Cleber");
        FileRecord file = new FileRecord(101, "pdf", "doc1", "João Cleber");
        FileSummary failedSummary = new FileSummary(101, "semAção", false, "Error message");

        when(personRepository.getAllPersonIds()).thenReturn(Collections.singletonList(person));
        when(personRepository.getFilesForPerson(1)).thenReturn(Collections.singletonList(file));
        when(fileUtils.renameFile(anyString(), eq(file))).thenReturn(failedSummary);

        List<Summary> result = fileRenameService.processAllFiles();

        verify(fileUtils, times(1)).renameFile(anyString(), eq(file));
        assertEquals(1, result.size());
        assertEquals(person.getPersonId(), result.get(0).getSummaryPersonId());
        assertEquals(1, result.get(0).getFileSummaryList().size());
        assertEquals(failedSummary, result.get(0).getFileSummaryList().get(0));
    }

    @Test
    void testProcessAllFiles_WithExceptionOnGetFilesForPerson_ContinuesProcessing() {

        Person person1 = new Person(1, "João Cleber");
        Person person2 = new Person(2, "Maria Souza");

        when(personRepository.getAllPersonIds()).thenReturn(Arrays.asList(person1, person2));
        when(personRepository.getFilesForPerson(1)).thenThrow(new RuntimeException("DB error"));
        when(personRepository.getFilesForPerson(2)).thenReturn(Collections.emptyList());

        List<Summary> result = fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verify(personRepository, times(1)).getFilesForPerson(1);
        verify(personRepository, times(1)).getFilesForPerson(2);

        // Continua processando e retorna lista vazia (nenhum summary gerado)
        assertTrue(result.isEmpty());
    }
}
