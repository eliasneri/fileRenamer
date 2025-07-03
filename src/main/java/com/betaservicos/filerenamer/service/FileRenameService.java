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
import java.util.List;

public class FileRenameService {
    private static final Logger logger = LoggerFactory.getLogger(FileRenameService.class);
    private static final String FOLDER_PATH = "meupc/youdocx";

    private final PersonRepository personRepository;

    public FileRenameService(DatabaseConfig dbConfig) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setUrl(dbConfig.getUrl());
        dataSource.setUsername(dbConfig.getUser());
        this.personRepository = new PersonRepository(dataSource);
    }

    public void processAllFiles() {
        List<Person> personIds = personRepository.getAllPersonIds();

        if (personIds.size() == 0) {
            logger.info("Sem ids de usuário para migrar... ABORTANDO");
            return;
        }

        int count = 1;
        List<Summary> listSummary = new ArrayList<>();
        for (Person person : personIds) {
            logger.info("Progresso: {} de {} .", count, personIds.size());
            logger.info("Buscando Arquivos person_id: {} ", person.getPersonId());

            List<FileRecord> filesForPerson = personRepository.getFilesForPerson(person.getPersonId());

            if (!filesForPerson.isEmpty()) {
                List<FileSummary> fileSummary = new ArrayList<>();
                int fileCount = 1;

                for (FileRecord file : filesForPerson) {
                    logger.info("Processando: {} de {} arquivos para: {}", fileCount, filesForPerson.size(), person.getPersonName());
                    FileSummary f = FileUtils.renameFile(FOLDER_PATH, file);
                    fileSummary.add(f);
                    fileCount++;
                }

                Summary s = new Summary(
                        person.getPersonId(),
                        person.getPersonName(),
                        fileSummary
                );

                listSummary.add(s);
            }

            count++;

        }
    }

    // para uso no teste unitário
    protected PersonRepository createRepository(DriverManagerDataSource dataSource) {
        return new PersonRepository(dataSource);
    }
}
