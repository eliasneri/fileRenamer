package com.betaservicos.filerenamer.config;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.betaservicos.filerenamer.util.AppProperties;
import com.betaservicos.filerenamer.util.Generics;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DatabaseConfig config;
    private boolean status;

    public DatabaseInitializer(DatabaseConfig config) {
        this.config = config;
        this.status = initialize();
    }

    public boolean initialize() {
        if (!config.isConnected()) {
            logger.error("Configuração de banco inválida.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            logger.info("Conexão estabelecida.");

            String schemaName = AppProperties.get("schema");

            if (Generics.isNullOrBlank(schemaName)){
                throw new Exception("Schema not provided!");
            }

            ensureSchemaExists(conn, schemaName);

            boolean summaryExists = tableExists(conn, schemaName, "summary");
            boolean fileSummaryExists = tableExists(conn, schemaName, "file_summary");

            if (!summaryExists || !fileSummaryExists) {
                logger.info("Criando tabelas ausentes...");

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS migration.summary (
                            summary_id serial4 PRIMARY KEY,
                            summary_person_id bigint NOT NULL,
                            summary_person_name varchar(300)
                        );
                        
                        CREATE TABLE IF NOT EXISTS migration.file_summary (
                            file_summary_id serial4 PRIMARY KEY,
                            file_summary_file_id bigint NOT NULL,
                            file_summary_filerenamed varchar(1000),
                            file_summary_success boolean NOT NULL,
                            file_summary_message text,
                            file_summary_fk_summary_id bigint NOT NULL
                        );
                        
                        ALTER TABLE migration.file_summary
                        ADD CONSTRAINT fk_summary_id
                        FOREIGN KEY (file_summary_fk_summary_id)
                        REFERENCES migration.summary (summary_id);
                    """);
                    logger.info("Tabelas criadas com sucesso.");
                }
            } else {
                logger.info("Tabelas já existem. Nenhuma ação necessária.");
            }

            return true;

        } catch (Exception e) {
            logger.error("Erro ao inicializar banco de dados: ", e);
            return false;
        }
    }

    private void ensureSchemaExists(Connection conn, String schemaName) {
        try (ResultSet rs = conn.getMetaData().getSchemas()) {
            boolean exists = false;
            while (rs.next()) {
                if (schemaName.equalsIgnoreCase(rs.getString("TABLE_SCHEM"))) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                logger.info("Schema '{}' não encontrado. Criando...", schemaName);
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("CREATE SCHEMA " + schemaName);
                    logger.info("Schema '{}' criado com sucesso.", schemaName);
                }
            } else {
                logger.info("Schema '{}' já existe.", schemaName);
            }
        } catch (Exception e) {
            logger.error("Erro  {} ", e.getMessage());
        }
    }

    private boolean tableExists(Connection conn, String schema, String tableName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getTables(null, schema, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar existência da tabela '{}.{}'", schema, tableName, e);
            return false;
        }
    }

}
