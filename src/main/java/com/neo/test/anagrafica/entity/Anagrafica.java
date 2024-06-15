package com.neo.test.anagrafica.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Anagrafica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Il nome non può essere nullo")
    @Size(min = 3, max = 22, message = "Il nome deve essere tra 3 e 22 caratteri")
    private String nome;

    @NotNull(message = "Il cognome non può essere nullo")
    @Size(min = 3, max = 22, message = "Il cognome deve essere tra 3 e 22 caratteri")
    private String cognome;

    @NotNull(message = "La data di nascita non può essere nulla")
    @DateTimeFormat(pattern = "dd-MM-yyyy")  
    private Date dataNascita;

    @NotNull(message = "La città non può essere nulla")
    private String citta;

    @NotNull(message = "Il codice fiscale non può essere nullo")
    @Size(min = 16, max = 16, message = "Il codice fiscale deve essere di 16 caratteri")
    private String codiceFiscale;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_import_file")
    private ImportFile importFile;
}
