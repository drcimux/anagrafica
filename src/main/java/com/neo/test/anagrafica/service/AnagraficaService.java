package com.neo.test.anagrafica.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.neo.test.anagrafica.exception.AnagraficaException;
import com.neo.test.anagrafica.model.AnagraficaDTO;
import com.neo.test.anagrafica.model.ModelAnagraficaResponse;

public interface AnagraficaService {

	
	 public CompletableFuture<ModelAnagraficaResponse> upload(MultipartFile file, String user) throws AnagraficaException;

	 public byte[] getFileContentById(Long id) throws AnagraficaException;

	 public Page<AnagraficaDTO> getAnagraficheByImportFileId(Long importFileId, Pageable pageable) throws AnagraficaException;

	  public Page<AnagraficaDTO> getAllAnagrafiche(Pageable pageable) throws AnagraficaException;
	 
}
