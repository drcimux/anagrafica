package com.neo.test.anagrafica.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class ImportFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nomeFile;
	private LocalDateTime dataImport;
	private String userImport;
	private String stato;
	private int numeroAnagrafiche;
	private String messaggioErrore;

	@Lob
	private byte[] fileCSV;

	@OneToMany(mappedBy = "importFile")
	private List<Anagrafica> anagrafiche;

}
