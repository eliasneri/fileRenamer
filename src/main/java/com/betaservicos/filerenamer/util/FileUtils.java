package com.betaservicos.filerenamer.util;

import com.betaservicos.filerenamer.domain.FileRecord;
import com.betaservicos.filerenamer.domain.FileSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static FileSummary renameFile(String folderPath, FileRecord file) {
        try {
            if (folderPath.isBlank()) {
             throw new Exception("folder invalid!");
            }
            File folder = new File(folderPath);
            File[] folderFiles = folder.listFiles();

            if (folderFiles == null) {
                throw new Exception("Pasta: {"+ folderPath +"} não encontrada! ou não é um diretório.");
            }

            File originFile = findFileById(folderFiles, String.valueOf(file.getFileId()));

            if (originFile == null) {
                logger.warn("Arquivo não encontrado na pasta para o id: {}.", file.getFileId());
                FileSummary f = new FileSummary(
                        file.getFileId(),
                        "semAção",
                        false,
                        "Arquivo não encontrado com a referência (Id): " + file.getFileId() + " para: " + file.getPersonName()
                );

                return f;
            }

            String newFileName = generateNewFileName(file, originFile.getName());

            if (newFileName == null || newFileName.isBlank()){
                throw new Exception("generate new name error!");
            }

            Path source = originFile.toPath();
            Path target = Paths.get(folderPath, newFileName);

            Files.move(source, target);
            logger.info("Renomeado: {} -> {}", originFile.getName(), newFileName);

            FileSummary f = new FileSummary(
                    file.getFileId(),
                    newFileName,
                    true,
                    "SUCESSO ! -> " + folderPath + File.separator + newFileName
            );
            return f;

        } catch (Exception e){
            logger.error("error renameFile! : ", e);
            FileSummary f = new FileSummary(
                    file.getFileId(),
                    "semAção",
                    false,
                    e.getMessage()
            );

            return f;

        }
    }

    static File findFileById(File[] files, String id) {
        for (File file : files) {
            if (file.getName().startsWith(id)) {
                return file;
            }
        }

        return null;
    }

    static String generateNewFileName(FileRecord record, String originFileName) {
        if (record == null || originFileName.isBlank()) {
            return null;
        }
        String extension = originFileName.contains(".")
                ? originFileName.substring(originFileName.lastIndexOf('.'))
                : "." + record.getFileExtension();

        String personName = Generics.normalizeString(record.getPersonName().replace(" ", "_"));
        String originName = Generics.normalizeString(record.getOriginName().replace(" ", "_"));

        return String.format("%s_%s_%s%s",
                record.getFileId(),
                personName,
                originName,
                extension)
                .replaceAll("[^\\w.-]", "");
    }

}
