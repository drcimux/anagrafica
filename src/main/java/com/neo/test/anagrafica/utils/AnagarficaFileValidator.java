package com.neo.test.anagrafica.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.neo.test.anagrafica.model.AnagraficaCsv;
import com.neo.test.anagrafica.repository.AnagraficaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AnagarficaFileValidator {

    private final AnagraficaRepository anagraficaRepository;
   
    @Value("${header.file.anagrafica}")
    private String expectedHeader;
  
	public AnagarficaFileValidator(AnagraficaRepository anagraficaRepository) {
		super();
		this.anagraficaRepository = anagraficaRepository;
	}

	public boolean isValidCSVFormat(MultipartFile file) {
		log.info("Entrata metodo isValidCSVFormat per il file: {}", file.getOriginalFilename());

		String filename = file.getOriginalFilename();
		if (filename == null || !filename.endsWith(".csv") || file.isEmpty()) {
			return false;
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
			String header = reader.readLine();
			return header != null && header.equals(expectedHeader);
		} catch (IOException e) {
			log.error("Errore durante la lettura del file CSV per la validazione del formato", e);
			return false;
		} finally {
			log.info("Uscita metodo isValidCSVFormat");
		}
	}

	public List<String> validateAnagrafica(AnagraficaCsv anagrafica,Set<String> uniqueCodiceFiscaleInFile) {
		List<String> errors = new ArrayList<>();

		validateField(anagrafica.getNome(), "nome", errors,
				value -> value != null && value.length() >= 3 && value.length() <= 22, "Il nome non è valido");

		validateField(anagrafica.getCognome(), "cognome", errors,
				value -> value != null && value.length() >= 3 && value.length() <= 22, "Il cognome non è valido");

		validateFieldDate(anagrafica.getDataNascita(), "data di nascita", errors, "dd-MM-yyyy");

		validateField(anagrafica.getCitta(), "città", errors, 
				value -> value != null, "La città non può essere nulla");

		validateCodiceFiscale(anagrafica.getCodiceFiscale(), errors,uniqueCodiceFiscaleInFile);

		return errors;
	}

	private void validateCodiceFiscale(String codiceFiscale, List<String> errors,Set<String> uniqueCodiceFiscaleInFile) {
		log.info("Validazione del campo codice fiscale: {}", codiceFiscale);

		validateField(codiceFiscale, "codice fiscale", errors, 
				value -> value != null && value.length() == 16,
				"Codice fiscale nullo o malformato");

		if (StringUtils.hasText(codiceFiscale)) {
			validateField(codiceFiscale, "codice fiscale", errors, 
					value -> !uniqueCodiceFiscaleInFile.contains(value),
					"Il codice fiscale è presente più volte nel file");

			validateField(codiceFiscale, "codice fiscale", errors,
					value -> !anagraficaRepository.existsByCodiceFiscale(value),
					"Il codice fiscale è già presente nel database");

			uniqueCodiceFiscaleInFile.add(codiceFiscale);
		}
	}

	private void validateField(String fieldValue, String fieldName, List<String> errors, Predicate<String> predicate,String errorMessage) {
		log.info("Validazione del campo {}: {}", fieldName, fieldValue);
		if (!predicate.test(fieldValue)) {
			String formattedErrorMessage = String.format("%s: %s", errorMessage, fieldValue);
			errors.add(formattedErrorMessage);
		}
	}

	private void validateFieldDate(String fieldValue, String fieldName, List<String> errors, String format) {
		log.info("Validazione del campo data {}: {}", fieldName, fieldValue);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		try {
			LocalDate.parse(fieldValue, formatter);
		} catch (DateTimeParseException e) {
			errors.add(String.format("Il campo %s con valore %s non è una data valida nel formato %s", fieldName,fieldValue, format));
		}
	}
}
