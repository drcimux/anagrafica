package com.neo.test.anagrafica.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.neo.test.anagrafica.entity.Anagrafica;
import com.neo.test.anagrafica.entity.ImportFile;
import com.neo.test.anagrafica.exception.AnagraficaException;
import com.neo.test.anagrafica.model.AnagraficaCsv;
import com.neo.test.anagrafica.model.AnagraficaDTO;
import com.neo.test.anagrafica.model.ImportedRecords;
import com.neo.test.anagrafica.model.ModelAnagraficaResponse;
import com.neo.test.anagrafica.model.RejectedRecord;
import com.neo.test.anagrafica.model.RejectedRecords;
import com.neo.test.anagrafica.repository.AnagraficaRepository;
import com.neo.test.anagrafica.repository.ImportFileRepository;
import com.neo.test.anagrafica.service.AnagraficaService;
import com.neo.test.anagrafica.utils.AnagarficaFileValidator;
import com.neo.test.anagrafica.utils.StatoEnum;
import com.opencsv.bean.CsvToBeanBuilder;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing anagrafica imports.
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "anagrafiche")
public class AnagraficaServiceImpl implements AnagraficaService  {

    private final ImportFileRepository importFileRepository;
    private final AnagraficaRepository anagraficaRepository;
    private final AnagarficaFileValidator validator;
    
    private final ModelMapper modelMapper;

    @Value("${header.file.anagrafica}")
    private String expectedHeader;


 

    public AnagraficaServiceImpl(ImportFileRepository importFileRepository, AnagraficaRepository anagraficaRepository,
			AnagarficaFileValidator validator, ModelMapper modelMapper) {
		this.importFileRepository = importFileRepository;
		this.anagraficaRepository = anagraficaRepository;
		this.validator = validator;
		this.modelMapper = modelMapper;
	}


	/**
     * Carica anagrafiche da un file CSV.
     *
     * @param file Il file CSV da caricare.
     * @param user L'utente che ha caricato il file.
     * @return La risposta del modello di anagrafica.
     * @throws AnagraficaException In caso di errore durante il caricamento.
     */
    @Async
    public CompletableFuture<ModelAnagraficaResponse> upload(MultipartFile file, String user) throws AnagraficaException {
        log.info("Entrata nel metodo caricaAnagrafiche con file: {}", file.getOriginalFilename());

        ModelAnagraficaResponse modelAnagraficaResponse = new ModelAnagraficaResponse();

        try {
            ImportFile importFile = createImportFile(file, user);

            if (validator.isValidCSVFormat(file)) {
            	modelAnagraficaResponse = processCSVFile(file, importFile);
            	List<AnagraficaCsv> anagraficheValid = modelAnagraficaResponse.getImportedRecords().getAnagrafiche();
            	List<Anagrafica> anagraficheList = convert(anagraficheValid, importFile);
            	importFile.setNumeroAnagrafiche(anagraficheList.size());
           	    importFile.setStato(getStatoImport(modelAnagraficaResponse));
           	    saveImportData(importFile,anagraficheList);          	
                modelAnagraficaResponse.setImportFileId(importFile.getId());
                log.info("Caricamento completato: {} anagrafiche importate", importFile.getNumeroAnagrafiche());
            } else {
            	handleCSVProcessingError(importFile, "Formato CSV non valido");
                modelAnagraficaResponse.setImportError(importFile.getMessaggioErrore());
                modelAnagraficaResponse.setImportFileId(importFile.getId());
                return CompletableFuture.completedFuture(modelAnagraficaResponse);
            }
        } catch (Exception e) {
            log.error("Errore durante il caricamento delle anagrafiche", e);
            throw new AnagraficaException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, e.getMessage());
        }

        log.info("Uscita dal metodo caricaAnagrafiche");
        return CompletableFuture.completedFuture(modelAnagraficaResponse);
    }
    
    
    @Transactional(readOnly = true)
	public byte[] getFileContentById(Long id) throws AnagraficaException {
		log.info("Start getFileContentById with id: {}", id);

		Optional<ImportFile> optionalImportFile = importFileRepository.findById(id);
		if (optionalImportFile.isEmpty()) {
			log.error("File with id {} not found", id);
			throw new AnagraficaException(HttpStatus.NOT_FOUND.value(), 404, "File not found");
		}

		ImportFile importFile = optionalImportFile.get();
		byte[] fileContent = importFile.getFileCSV();
		if (fileContent == null || fileContent.length == 0) {
			log.error("File content for id {} is empty", id);
			throw new AnagraficaException(HttpStatus.NO_CONTENT.value(), 204, "File content is empty");
		}

		log.info("End getFileContentById with id: {}", id);
		return fileContent;
	}


	@Override
	public Page<AnagraficaDTO> getAllAnagrafiche(Pageable pageable) throws AnagraficaException {
		log.info("Fetching all anagrafiche with pagination");
		List<AnagraficaDTO> anagraficaDTOList = new ArrayList<>();
		Page<Anagrafica> anagrafichePage;
		try {
			anagrafichePage = anagraficaRepository.findAll(pageable);

			anagraficaDTOList = modelMapper.map(anagrafichePage.getContent(), new TypeToken<List<AnagraficaDTO>>() {}.getType());
		} catch (Exception e) {
			log.error("Errore durante il caricamento delle anagrafiche", e);
			throw new AnagraficaException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, e.getMessage());
		}

		log.info("Uscita dal metodo caricaAnagrafiche");
		return new PageImpl<>(anagraficaDTOList, pageable, anagrafichePage.getTotalElements());

	}
	
	@Override
	@Cacheable(key = "#idFile")	
	public Page<AnagraficaDTO> getAnagraficheByImportFileId(Long idFile, Pageable pageable) throws AnagraficaException {
		log.info("Fetching all anagrafiche with pagination by idFile {} " ,idFile);
		List<AnagraficaDTO> anagraficaDTOList = new ArrayList<>();
		Page<Anagrafica> anagrafichePage;
		try {
			anagrafichePage = anagraficaRepository.findByImportFileId(idFile, pageable);

			anagraficaDTOList = modelMapper.map(anagrafichePage.getContent(), new TypeToken<List<AnagraficaDTO>>() {}.getType());
		} catch (Exception e) {
			log.error("Errore durante il caricamento delle anagrafiche", e);
			throw new AnagraficaException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, e.getMessage());
		}

		log.info("Uscita dal metodo getAnagraficheByImportFileId");
		return new PageImpl<>(anagraficaDTOList, pageable, anagrafichePage.getTotalElements());
	}
    
    
    // Metodo per salvare i dati di importazione in modo sincronizzato
    private synchronized void saveImportData(ImportFile importFile, List<Anagrafica> anagraficheList) {
        importFileRepository.save(importFile);
        anagraficaRepository.saveAll(anagraficheList);
    }
    
    
	private String getStatoImport(ModelAnagraficaResponse modelAnagraficaResponse) {
		if (modelAnagraficaResponse.getRejectedRecords().getTotalRejected() == 0) {
			return StatoEnum.OK.getCodice();
		}
		return StatoEnum.WARNING.getCodice();

	}

	/**
     * Processa il file CSV e salva le anagrafiche nel repository.
     *
     * @param file Il file CSV da processare.
     * @param importFile L'oggetto ImportFile corrispondente.
     * @return La risposta del modello di anagrafica.
     * @throws AnagraficaException In caso di errore durante il processamento.
     */
    private ModelAnagraficaResponse processCSVFile(MultipartFile file, ImportFile importFile) throws AnagraficaException {
        log.info("Start method processCSVFile con file: {}", file.getOriginalFilename());

        ModelAnagraficaResponse modelAnagraficaResponse = new ModelAnagraficaResponse();
        ImportedRecords importedRecords = new ImportedRecords();
        RejectedRecords rejectedRecords = new RejectedRecords();

        List<AnagraficaCsv> anagraficheValid = new ArrayList<>();        
        List<RejectedRecord> recordList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            List<AnagraficaCsv> listaAnagraficheCsv = new CsvToBeanBuilder<AnagraficaCsv>(reader)
                    .withType(AnagraficaCsv.class)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withFilter(stringValues -> Arrays.stream(stringValues)
                            .anyMatch(StringUtils::isNotBlank))
                    .build()
                    .parse();
            Set<String> setCodiceFiscale = new HashSet<>();
            for (int i = 0; i < listaAnagraficheCsv.size(); i++) {
                AnagraficaCsv anagraficaCsv = listaAnagraficheCsv.get(i);
                List<String> errors = validator.validateAnagrafica(anagraficaCsv,setCodiceFiscale);

                if (CollectionUtils.isNotEmpty(errors)) {
                    RejectedRecord rejectedReason = new RejectedRecord();
                    rejectedReason.setRowNum(i);
                    rejectedReason.setAnagraficheRej(anagraficaCsv);
                    rejectedReason.setReasonList(errors);
                    recordList.add(rejectedReason);
                } else {
                    anagraficheValid.add(anagraficaCsv);
                }
            }
           
            importedRecords.setAnagrafiche(anagraficheValid);
            importedRecords.setTotalImported(anagraficheValid.size());
            rejectedRecords.setRecordList(recordList);
            rejectedRecords.setTotalRejected(recordList.size());
            modelAnagraficaResponse.setImportedRecords(importedRecords);
            modelAnagraficaResponse.setRejectedRecords(rejectedRecords);

        } catch (Exception e) {
            log.error("Errore durante il processamento del file CSV", e);
            ImportFile importFileError = handleCSVProcessingError(importFile, e.getMessage());
            modelAnagraficaResponse.setImportFileId(importFileError.getId());
            throw new AnagraficaException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, e.getMessage());
        }

        log.info("Exit  metodo processCSVFile");
        return modelAnagraficaResponse;
    }

    private ImportFile createImportFile(MultipartFile file, String user) throws IOException {
        ImportFile importFile = new ImportFile();
        importFile.setNomeFile(file.getOriginalFilename());
        importFile.setDataImport(LocalDateTime.now());
        importFile.setUserImport(user);
        importFile.setFileCSV(file.getBytes());
        return importFile;
    }

    private synchronized ImportFile handleCSVProcessingError(ImportFile importFile, String errorMessage) {
        log.error("{} per il file: {}", errorMessage, importFile.getNomeFile());
        importFile.setStato(StatoEnum.KO.getCodice());
        importFile.setMessaggioErrore(errorMessage);
        return importFileRepository.save(importFile);
    }
    
    /**
     * Converte una lista di AnagraficaCsv in una lista di Anagrafica.
     *
     * @param anagraficheCsv La lista di AnagraficaCsv da convertire.
     * @param importFile L'oggetto ImportFile associato.
     * @return La lista di Anagrafica convertita.
     */
    private List<Anagrafica> convert(List<AnagraficaCsv> anagraficheCsv,ImportFile importFile ) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return anagraficheCsv.stream()
                .map(csv -> {
                    Anagrafica anagrafica = new Anagrafica();
                    anagrafica.setNome(csv.getNome());
                    anagrafica.setCognome(csv.getCognome());
                    try {
                        Date dateNascita = dateFormat.parse(csv.getDataNascita());
                        anagrafica.setDataNascita(dateNascita);
                    } catch (ParseException e) {
                    	log.error("Errore durante il parse della data di nascita", e);
                    }
                    anagrafica.setCitta(csv.getCitta());
                    anagrafica.setCodiceFiscale(csv.getCodiceFiscale());
                    anagrafica.setImportFile(importFile);
                    return anagrafica;
                })
                .toList();
    
    }

  






}
