package com.neo.test.anagrafica.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neo.test.anagrafica.exception.AnagraficaException;
import com.neo.test.anagrafica.model.AnagraficaDTO;
import com.neo.test.anagrafica.model.ModelAnagraficaResponse;
import com.neo.test.anagrafica.service.AnagraficaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/anagrafiche")
@SecurityRequirement(name = "in-memory")
@Tag(name = "Anagrafiche Management System", description = "Operations related to Anagrafiche Management")
@RequiredArgsConstructor
public class AnagraficaController {

    
    private AnagraficaService anagraficaService;


    @Autowired
	public AnagraficaController(AnagraficaService anagraficaService) {
		super();
		this.anagraficaService = anagraficaService;
	}
    
    
    
    
    /**
     * Retrieves a paginated list of all anagrafiche records.
     * If {@code idFile} is provided, filters anagrafiche imported from a single CSV file.
     *
     * @param page          Page number (default: 0)
     * @param size          Number of records per page (default: 10)
     * @param sortBy        Field to sort by (default: "id")
     * @param sortDirection Sort direction, either "asc" or "desc" (default: "asc")
     * @param idFile        Optional ID of the CSV import file to filter anagrafiche
     * @return ResponseEntity containing a Page of AnagraficaDTO objects
     */
    @Operation(
    	    summary = "Retrieve all Anagrafiche",
    	    description = "Retrieves a paginated list of all Anagrafiche records. If `idFile` is provided, filters Anagrafiche imported from a single CSV file.",
    	    responses = {
    	        @ApiResponse(
    	            description = "Successfully retrieved anagrafiche",
    	            responseCode = "200",
    	            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    	        ),
    	        @ApiResponse(
    	            description = "Error processing request",
    	            responseCode = "500",
    	            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    	        )
    	    }
    	)
    @GetMapping("/anagrafiche")
    public ResponseEntity<Page<AnagraficaDTO>> getAllAnagrafichePaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc") String sortDirection,
    		@RequestParam(required = false) Long idFile) {

    	   var methodName = "getAllAnagrafichePaginated";
           log.info("{} - Start method with page: {}, size: {}, sortBy: {}, sortDirection: {}, idFile: {}", methodName, page, size, sortBy, sortDirection, idFile);

        try {
            var direction = Sort.Direction.fromString(sortDirection);
            var pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<AnagraficaDTO> anagrafichePage = idFile != null ?
                    anagraficaService.getAnagraficheByImportFileId(idFile, pageable) :
                    anagraficaService.getAllAnagrafiche(pageable);

            log.info("{} - Successfully retrieved {} anagrafiche for page: {}, size: {}", methodName, anagrafichePage.getNumberOfElements(), page, size);
            return ResponseEntity.ok(anagrafichePage);
        } catch (AnagraficaException e) {
            log.error("{} - AnagraficaException: {}", methodName, e.getMessage(), e);
            return ResponseEntity.status(e.getHttpCode()).build();
        } catch (Exception e) {
            log.error("{} - Unexpected error: {}", methodName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            log.info("{} - End method with page: {}, size: {}, sortBy: {}, sortDirection: {}, idFile: {}", methodName, page, size, sortBy, sortDirection, idFile);
        }
    }
    
    
    
    
    /**
     * Uploads an anagrafiche CSV file.
     *
     * @param file The file to upload.
     * @return ResponseEntity containing the response model for anagrafiche.
     */
    @Operation(
            summary = "Upload anagrafiche via CSV file",
            description = "Upload anagrafiche via CSV file and returns a list of responses",
            responses = {
                @ApiResponse(
                    description = "Successfully uploaded anagrafiche",
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModelAnagraficaResponse.class))
                ),
                @ApiResponse(
                    description = "Error processing file",
                    responseCode = "500",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ModelAnagraficaResponse.class))
                )
            }
        )
        @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public ResponseEntity<ModelAnagraficaResponse> uploadAnagrafiche(@RequestParam("file") MultipartFile file) {
			var methodName = "uploadAnagrafiche";
			log.info("{} - Start method", methodName);
			ModelAnagraficaResponse modelAnagraficaResponse = new ModelAnagraficaResponse();

			try {
				User userContext = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				log.info("{} - Input parameters: fileName={}, userId={}", methodName, file.getOriginalFilename(),userContext.getUsername());

				CompletableFuture<ModelAnagraficaResponse> resCompl = anagraficaService.upload(file, userContext.getUsername());
				modelAnagraficaResponse = resCompl.get();
				log.info("{} - Successfully processed file: {}", methodName, file.getOriginalFilename());

				return ResponseEntity.ok(modelAnagraficaResponse);
			} catch (AnagraficaException e) {
				log.error("{} - Error processing file: {} - Exception: {}", methodName, file.getOriginalFilename(),	e.getMessage(), e);
				return ResponseEntity.status(e.getHttpCode()).body(modelAnagraficaResponse);
			} catch (InterruptedException e) {
				log.error("{} - Interrupted Exception error: {}", methodName, e.getMessage(), e);
				 Thread.currentThread().interrupt();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(modelAnagraficaResponse);
				
			} catch (ExecutionException e) {
				log.error("{} - Execution Exception  error: {}", methodName, e.getCause(), e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(modelAnagraficaResponse);
			}  finally {
				log.info("{} - End method", methodName);
			}
		}
    
    
    
    /**
     *  Download a file based on the provided ID.
     *
     * @param file file The file to downalod.
     * @return ResponseEntity containing the response model for anagrafiche.
     */
    @Operation(
            summary = "Download file by ID",
            description = "Download a file based on the provided ID.",
            responses = {
                @ApiResponse(
                    description = "File found and downloaded successfully",
                    responseCode = "200",
                    content = @Content(mediaType = "application/csv")
                ),
                @ApiResponse(
                    description = "File not found",
                    responseCode = "404",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                ),
                @ApiResponse(
                    description = "Internal server error",
                    responseCode = "500",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                )
            }
        )
        @GetMapping(path = "/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        var methodName = "downloadFile";
        log.info("{} - Start method with id: {}", methodName, id);

        try {
            byte[] fileContent = anagraficaService.getFileContentById(id);
      
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "anagrafiche_" + currentDate + ".csv";
            HttpHeaders headers = new HttpHeaders();
            
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ fileName);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/csv");

            log.info("{} - Successfully retrieved file with id: {}", methodName, id);
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (AnagraficaException e) {
            log.error("{} - Error retrieving file with id: {} - Exception: {}", methodName, id, e.getMessage(), e);
            return ResponseEntity.status(e.getHttpCode()).build();
        } catch (Exception e) {
            log.error("{} - Unexpected error: {}", methodName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            log.info("{} - End method with id: {}", methodName, id);
        }
    }
    
    
    
    
    
		@GetMapping("/ping")
		public ResponseEntity<String> test() {
			try {
				return ResponseEntity.ok("OK");
			} catch (Exception e) {
				return ResponseEntity.status(500).body("KO");
			}
		}


}

