package com.betaservicos.filerenamer.repository;

import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.Person;
import com.betaservicos.filerenamer.util.PersonRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;

public class PersonRepository {
    private static final Logger logger = LoggerFactory.getLogger(PersonRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(DriverManagerDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> getAllPersonIds() {
        try {
            logger.info("Buscando id's dos candidatos - (person_id)");
                String sql = "SELECT " +
                        "person_id, " +
                        "name " +
                        "FROM person.person " +
                        "ORDER BY name ASC ";

                List<Person> list = jdbcTemplate.query(sql, new PersonRowMapper());
                logger.info("Encontrado: " + list.size() + " ids!");
            return list;

        } catch (Exception e) {
            logger.error("No list for person id! ", e);
            return null;
        }
    }

    public List<FileRecord> getFilesForPerson(int personId) {
        try {
            logger.info("Buscando Lista de arquivos no banco de Dados para Migração! ");
            String sql = "SELECT " +
                    "f.file_id, " +
                    "f.file_type, " +
                    "f.name, " +
                    "p.name as person_name " +
                    "from person.person as p " +
                    "LEFT JOIN youdocx.files as f ON f.owner_id = p.person_id " +
                    "LEFT JOIN migration.file_summary AS mfs ON mfs.file_summary_file_id = f.file_id " +
                    "WHERE p.person_id = ? " +
                    "AND f.file_type <> 'dir' " +
                    "AND mfs.file_summary_id IS NULL";

            List<FileRecord> files = jdbcTemplate.query(sql, new Object[]{personId}, (rs, rowNum) ->
                    new FileRecord(
                            rs.getInt("file_id"),
                            rs.getString("file_type"),
                            rs.getString("name"),
                            rs.getString("person_name")
                    ));
            logger.info("Encontrado: " + files.size() + " registros para migração!");
            return files;

        } catch (Exception e) {
            logger.error("no list files on DB ", e);
            return null;
        }
    }
}
