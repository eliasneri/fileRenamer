package com.betaservicos.filerenamer.service;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Person;
import com.betaservicos.filerenamer.repository.PersonRepository;
import com.betaservicos.filerenamer.util.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class FileRenameServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private FileRenameService fileRenameService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        DatabaseConfig dbConfig = new DatabaseConfig();
        fileRenameService = new FileRenameService(dbConfig) {
            @Override
            protected PersonRepository createRepository(DriverManagerDataSource dataSource){
                return personRepository;
            }
        };
    }

    @Test
    void testProcessAllFiles_WithNoPersons() {
        when(personRepository.getAllPersonIds()).thenReturn(Collections.emptyList());
        fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verify(personRepository, never()).getFilesForPerson(anyInt());
    }

    @Test
    void testProcessAllFiles_WithPersonsButNoFiles() {
        List<Person> persons = List.of(
                new Person(1, "João Cleber"),
                new Person(2, "Maria Souza")
        );

        when(personRepository.getAllPersonIds()).thenReturn(persons);
        when(personRepository.getFilesForPerson(anyInt())).thenReturn(Collections.emptyList());

        fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verify(personRepository, times(2)).getFilesForPerson(anyInt());
        verify(fileUtils, never()).renameFile(anyString(), any(FileRecord.class));
    }

    @Test
    void testProcessAllFiles_WithPersonsAndFiles() {
        Person person1 = new Person(1, "João Cleber");
        Person person2 = new Person(2, "Maria Souza");

        FileRecord file1 = new FileRecord(101, "pdf", "doc1", "João Silva");
        FileRecord file2 = new FileRecord(102, "jpg", "doc2", "Maria Souza");

        FileSummary fileSummary1 = new FileSummary(101, "101_Joao_Cleber_doc1.pdf", true, anyString());
        FileSummary fileSummary2 = new FileSummary(102, "102_Maria_Souza_doc2.jpg", true, anyString());

        when(personRepository.getAllPersonIds()).thenReturn(Arrays.asList(person1, person2));
        when(personRepository.getFilesForPerson(1)).thenReturn(Arrays.asList(file1, file2));
        when(personRepository.getFilesForPerson(2)).thenReturn(Collections.emptyList());

        when(fileUtils.renameFile(anyString(), eq(file1))).thenReturn(fileSummary1);
        when(fileUtils.renameFile(anyString(), eq(file2))).thenReturn(fileSummary2);

        fileRenameService.processAllFiles();

        verify(personRepository, times(1)).getAllPersonIds();
        verify(personRepository, times(1)).getFilesForPerson(1);
        verify(personRepository, times(1)).getFilesForPerson(2);
        verify(fileUtils, times(2)).renameFile(anyString(), any(FileRecord.class));
    }

    @Test
    void testProcessAllFiles_WithFileRenameFailure() {
        Person person1 = new Person(1, "João Cleber");
        FileRecord file1 = new FileRecord(101, "pdf", "doc1", "João Silva");
        FileSummary failedSummary = new FileSummary(101, "semAção", false, anyString());

        when(personRepository.getAllPersonIds()).thenReturn(Collections.singletonList(person1));
        when(personRepository.getFilesForPerson(1)).thenReturn(Collections.singletonList(file1));
        when(fileUtils.renameFile(anyString(), eq(file1))).thenReturn(failedSummary);

        fileRenameService.processAllFiles();

        verify(fileUtils, times(1)).renameFile(anyString(), eq(file1));
    }

}
