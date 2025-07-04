package com.betaservicos.filerenamer.domain;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode
public class FileRecord {

    @EqualsAndHashCode.Include
    private Integer fileId;
    private String fileExtension;
    private String originName;
    private String personName;
}
