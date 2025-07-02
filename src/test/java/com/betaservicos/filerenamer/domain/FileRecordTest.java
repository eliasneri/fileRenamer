package com.betaservicos.filerenamer.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileRecordTest {
    @Test
    void testFileRecordCreation() {
        FileRecord record = new FileRecord(123, "pdf", "documento", "João Cleber");

        assertEquals(123, record.getFileId());
        assertEquals("pdf", record.getFileExtension());
        assertEquals("documento", record.getOriginName());
        assertEquals("João Cleber", record.getPersonName());
    }

}
