package com.neo.test.anagrafica.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnagraficaDTO {

	private Long id;

	private String nome;

	private String cognome;

	private Date dataNascita;

	private String citta;

	private String codiceFiscale;
}
