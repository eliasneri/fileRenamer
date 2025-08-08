package com.betaservicos.filerenamer.repository;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.Person;
import com.betaservicos.filerenamer.util.PersonRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PersonRepositoryTest {
    private PersonRepository repository;
    private JdbcTemplate jdbcTemplateMock;

    @BeforeEach
    void setup() {
        jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        repository = new PersonRepository(jdbcTemplateMock);
    }

    @Test
    void testGetFilesForPersons_returnList() {
        List<FileRecord> mockResult = List.of(
                new FileRecord(123, ".pdf", "arquivo.pdf", "João Cleber")
        );

        when(jdbcTemplateMock.query(anyString(), any(Object[].class), any(RowMapper.class)))
                .thenReturn(mockResult);

        List<FileRecord> result = repository.getFilesForPerson(123);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("João Cleber", result.get(0).getPersonName());
    }

    @Test
    void testGetAllPersonIds_returnsList() {
        List<Person> mockPersons = List.of(
            new Person(1, "João Cleber"),
            new Person(2, "Maria Souza"),
            new Person(3, "Carlos Oliveira")
        );

        // Simula o retorno da consulta
        when(jdbcTemplateMock.query(
                anyString(),
                any(PersonRowMapper.class)
        )).thenReturn(mockPersons);

        List<Person> result = repository.getAllPersonIds();

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals(1, result.get(0).getPersonId());
        assertEquals("João Cleber", result.get(0).getPersonName());

        assertEquals(2, result.get(1).getPersonId());
        assertEquals("Maria Souza", result.get(1).getPersonName());

        assertEquals(3, result.get(2).getPersonId());
        assertEquals("Carlos Oliveira", result.get(2).getPersonName());

    }

    @Test
    void testGetAllPersonIds_throwsException_returnsNull() {
        when(jdbcTemplateMock.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("DB error test"));

        List<Person> result = repository.getAllPersonIds();

        assertNull(result);
    }
}
