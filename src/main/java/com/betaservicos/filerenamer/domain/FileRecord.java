package com.betaservicos.filerenamer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class FileRecord {

    private Integer fileId;
    private String fileExtension;
    private String originName;
    private String personName;
}
