package com.neo.test.anagrafica.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.neo.test.anagrafica.entity.Anagrafica;

@Repository
public interface AnagraficaRepository extends JpaRepository<Anagrafica, Long> {
	
	boolean existsByCodiceFiscale(String codiceFiscale);

	Optional<Anagrafica> findByCodiceFiscale(String codiceFiscale);

	  @Query("SELECT a FROM Anagrafica a WHERE a.importFile.id = :idFile")
	 Page<Anagrafica> findByImportFileId(Long idFile, Pageable pageable);
}
