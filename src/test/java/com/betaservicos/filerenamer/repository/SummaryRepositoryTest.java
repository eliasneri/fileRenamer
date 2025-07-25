package com.betaservicos.filerenamer.repository;

import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Summary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SummaryRepositoryTest {

    private JdbcTemplate jdbcTemplateMock;
    private SummaryRepository repository;
    private DataSource dataSourceMock;

    @BeforeEach
    public void setUp() {
        jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
        dataSourceMock = mock(DataSource.class);
        when(jdbcTemplateMock.getDataSource()).thenReturn(dataSourceMock);
        repository = new SummaryRepository(jdbcTemplateMock);
        System.setProperty("schema", "public");
    }

    @Test
    public void testInsertSummarySuccess() throws Exception {
        // Dados de entrada
        FileSummary fileSummary1 = new FileSummary(1, "renamed1.txt", true, "OK");
        FileSummary fileSummary2 = new FileSummary(2, "renamed2.txt", false, "Falha");
        Summary summary = new Summary(123, "João", Arrays.asList(fileSummary1, fileSummary2));

        // Mocks de JDBC
        Connection conn = mock(Connection.class);
        PreparedStatement summaryPs = mock(PreparedStatement.class);
        PreparedStatement filePs = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);

        when(dataSourceMock.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(summaryPs);
        when(conn.prepareStatement(anyString())).thenReturn(filePs);
        when(summaryPs.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(456);
        when(jdbcTemplateMock.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        boolean result = repository.insert(summary);

        assertTrue(result);
        verify(conn).setAutoCommit(false);
        verify(conn).commit();
        verify(conn).close();
        verify(summaryPs).setInt(1, 123);
        verify(summaryPs).setString(2, "João");
        verify(filePs, times(2)).executeUpdate();
    }

    @Test
    public void testInsertSummaryWhenPersonAlreadyExists() throws Exception {
        Summary summary = new Summary(123, "Maria", Collections.emptyList());
        Connection conn = mock(Connection.class);

        when(dataSourceMock.getConnection()).thenReturn(conn);
        when(jdbcTemplateMock.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        boolean result = repository.insert(summary);

        assertFalse(result);
        verify(jdbcTemplateMock).queryForObject(anyString(), any(Object[].class), eq(Integer.class));
        verify(conn).close();
        verify(conn, never()).prepareStatement(anyString(), anyInt());
    }

    @Test
    public void testInsertSummaryWithExceptionRollback() throws Exception {
        Summary summary = new Summary(null, "Carlos", Collections.emptyList());
        Connection conn = mock(Connection.class);

        when(dataSourceMock.getConnection()).thenReturn(conn);

        boolean result = repository.insert(summary);

        assertFalse(result);
        verify(conn).rollback();
        verify(conn).close();
        verify(conn, never()).commit();
    }
}