package com.betaservicos.filerenamer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileSummary {

    private Integer fileId;
    private String fileRenamed;
    private boolean success;
    private String message;

}
