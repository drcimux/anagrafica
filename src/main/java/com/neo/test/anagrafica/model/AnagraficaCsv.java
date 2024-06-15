package com.neo.test.anagrafica.model;

import com.opencsv.bean.CsvBindByPosition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnagraficaCsv {
	
	    @CsvBindByPosition(position = 0)
	    private String nome;

	    @CsvBindByPosition(position = 1)
	    private String cognome;

	    @CsvBindByPosition(position = 2)
	    private String dataNascita;

	    @CsvBindByPosition(position = 3)
	    private String citta;
	    
	    @CsvBindByPosition(position = 4)
	    private String codiceFiscale;
	

}
