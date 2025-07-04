package com.betaservicos.filerenamer.service;

import com.betaservicos.filerenamer.config.DatabaseConfig;
import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.FileSummary;
import com.betaservicos.filerenamer.domain.Person;
import com.betaservicos.filerenamer.domain.Summary;
import com.betaservicos.filerenamer.repository.PersonRepository;
import com.betaservicos.filerenamer.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileRenameService {
    private static final Logger logger = LoggerFactory.getLogger(FileRenameService.class);
    private static final String FOLDER_PATH = "meupc/youdocx";

    private final PersonRepository personRepository;
    private final FileUtils fileUtils;

    public FileRenameService(PersonRepository personRepository, FileUtils fileUtils) {
        this.personRepository = personRepository;
        this.fileUtils = fileUtils;
    }

    public static FileRenameService fromConfig(DatabaseConfig dbConfig) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setUrl(dbConfig.getUrl());
        dataSource.setUsername(dbConfig.getUser());

        PersonRepository personRepository = new PersonRepository(dataSource);
        FileUtils fileUtils = new FileUtils();

        return new FileRenameService(personRepository, fileUtils);
    }

    public List<Summary> processAllFiles() {
        List<Person> personIds = personRepository.getAllPersonIds();

        if (personIds == null || personIds.isEmpty()) {
            logger.info("Sem ids de usuário para migrar... ABORTANDO");
            return Collections.emptyList(); // Melhor que retornar null
        }

        List<Summary> listSummary = new ArrayList<>();
        int count = 1;

        for (Person person : personIds) {
            logger.info("Progresso: {} de {}.", count, personIds.size());
            logger.info("Buscando Arquivos person_id: {}", person.getPersonId());

            List<FileRecord> filesForPerson;
            try {
                filesForPerson = personRepository.getFilesForPerson(person.getPersonId());
            } catch (Exception e) {
                logger.error("Erro ao buscar arquivos para person_id {}: {}", person.getPersonId(), e.getMessage(), e);
                count++;
                continue;
            }

            if (!filesForPerson.isEmpty()) {
                List<FileSummary> fileSummary = new ArrayList<>();
                int fileCount = 1;

                for (FileRecord file : filesForPerson) {
                    logger.info("Processando: {} de {} arquivos para: {}", fileCount, filesForPerson.size(), person.getPersonName());
                    try {
                        FileSummary f = fileUtils.renameFile(FOLDER_PATH, file);
                        fileSummary.add(f);
                    } catch (Exception e) {
                        logger.error("Erro ao renomear arquivo para person_id {}: {}", person.getPersonId(), e.getMessage(), e);
                    }
                    fileCount++;
                }

                if (!fileSummary.isEmpty()) {
                    Summary s = new Summary(
                            person.getPersonId(),
                            person.getPersonName(),
                            fileSummary
                    );
                    listSummary.add(s);
                }
            }

            count++;
        }

        return listSummary;
    }
}
