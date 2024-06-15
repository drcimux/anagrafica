package com.neo.test.anagrafica.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public  class ImportedRecords {
    private int totalImported = 0;
    private List<AnagraficaCsv> anagrafiche = new ArrayList<>();

    // Getters and setters
}