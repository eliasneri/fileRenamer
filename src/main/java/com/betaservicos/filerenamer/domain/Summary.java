package com.betaservicos.filerenamer.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Summary {

    private Integer summaryPersonId;
    private String summaryPersonName;
    private List<FileSummary> fileSummaryList;

}
