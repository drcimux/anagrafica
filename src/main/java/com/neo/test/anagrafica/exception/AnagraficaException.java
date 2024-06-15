package com.neo.test.anagrafica.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AnagraficaException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int httpCode;
	private final int errorCode;
	private final String label;
	
	  public AnagraficaException(int httpCode, int errorCode, String label) {
	        super(label);
	        this.httpCode = httpCode;
	        this.errorCode = errorCode;
	        this.label = label;
	    }



}
