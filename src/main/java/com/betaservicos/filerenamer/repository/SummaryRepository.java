package com.betaservicos.filerenamer.repository;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Summary;
import com.betaservicos.filerenamer.util.AppProperties;
import com.betaservicos.filerenamer.util.Generics;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.*;

@Getter @Setter
public class SummaryRepository {
    private static final Logger logger = LoggerFactory.getLogger(SummaryRepository.class);

    private JdbcTemplate jdbcTemplate;
    private String schemaName;

    public SummaryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.schemaName = AppProperties.get("schema");
    }

    public SummaryRepository(DatabaseConfig dbConfig) {
        if (!dbConfig.isConnected()) {
            throw new IllegalArgumentException("Database configuration is not connected");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbConfig.getUrl());
        dataSource.setUsername(dbConfig.getUser());
        dataSource.setPassword(dbConfig.getPassword());

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.schemaName = AppProperties.get("schema");
    }

    public boolean insert(Summary summary) {
        String SQL_SUMMARY = "INSERT INTO " + this.schemaName + ".summary (summary_person_id, summary_person_name) VALUES (?, ?)";
        String CHECK_PERSON = "SELECT COUNT(*) FROM " + this.schemaName + ".summary WHERE summary_person_id = ?";
        String SQL_FILE_SUMMARY = "INSERT INTO " + this.schemaName + ".file_summary (" +
                "file_summary_file_id, " +
                "file_summary_filerenamed, " +
                "file_summary_success, " +
                "file_summary_message, " +
                "file_summary_fk_summary_id) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);

            if (Generics.isNullOrBlank(String.valueOf(summary.getSummaryPersonId()))) {
                throw new Exception("id not found!");
            }

            Integer count = jdbcTemplate.queryForObject(CHECK_PERSON, new Object[]{summary.getSummaryPersonId()}, Integer.class);

            if (count != null && count > 0) {
                logger.warn("Person_ID ({}) já cadastrado anteriormente.... ignorando insert!", summary.getSummaryPersonId());
                return false;
            }

            // Inserir summary e capturar ID
            PreparedStatement ps = conn.prepareStatement(SQL_SUMMARY, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, summary.getSummaryPersonId());
            ps.setString(2, summary.getSummaryPersonName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            Integer generatedId = null;
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }

            if (generatedId == null) {
                throw new Exception("Erro ao obter ID gerado.");
            }

            // Inserir lista de arquivos
            for (FileSummary item : summary.getFileSummaryList()) {
                PreparedStatement filePs = conn.prepareStatement(SQL_FILE_SUMMARY);
                filePs.setLong(1, item.getFileId());
                filePs.setString(2, item.getFileRenamed());
                filePs.setBoolean(3, item.isSuccess());
                filePs.setString(4, item.getMessage());
                filePs.setInt(5, generatedId);
                filePs.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            logger.error("Erro durante insert: {}", e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Erro ao fazer rollback: {}", ex.getMessage());
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Erro ao fechar conexão: {}", e.getMessage());
                }
            }
        }
    }
}