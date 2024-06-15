package com.neo.test.anagrafica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neo.test.anagrafica.entity.ImportFile;

@Repository
public interface ImportFileRepository extends JpaRepository<ImportFile, Long> {
}
