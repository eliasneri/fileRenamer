package com.betaservicos.filerenamer.repository;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileRecord;
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

        List<FileRecord> result = repository.getFilesForPersons(123);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("João Cleber", result.get(0).getPersonName());
    }

    @Test
    void testGetAllPersonIds_returnsList() {
        List<Integer> mockIds = List.of(1, 2, 3);

        // Simula o retorno da consulta
        when(jdbcTemplateMock.query(anyString(), any(RowMapper.class)))
                .thenReturn(mockIds);

        List<Integer> result = repository.getAllPersonIds();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void testGetAllPersonIds_throwsException_returnsNull() {
        when(jdbcTemplateMock.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("DB error test"));

        List<Integer> result = repository.getAllPersonIds();

        assertNull(result);
    }
}
