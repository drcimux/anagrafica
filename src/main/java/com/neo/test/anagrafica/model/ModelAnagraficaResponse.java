package com.neo.test.anagrafica.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelAnagraficaResponse {

   
    private ImportedRecords importedRecords;
    private RejectedRecords rejectedRecords;
    private String importError;
    private Long importFileId;


}
