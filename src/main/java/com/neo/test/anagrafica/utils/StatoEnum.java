package com.neo.test.anagrafica.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatoEnum {

	OK("OK"),
	KO("KO"),
	WARNING("WARNING");
		
	private String codice;

       
}