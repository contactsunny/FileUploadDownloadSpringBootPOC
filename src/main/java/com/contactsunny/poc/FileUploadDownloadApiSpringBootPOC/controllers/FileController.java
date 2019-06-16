package com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.controllers;

import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.dtos.ResponseDto;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileNotFoundException;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileStorageException;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.models.File;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.services.FileStorageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value="/file", produces = "application/json")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger logger = Logger.getLogger(FileController.class);

    @PostMapping("/upload")
    public ResponseDto<File> uploadFile(@RequestParam("file") MultipartFile file)
            throws FileStorageException {

        ResponseDto<File> responseDto = new ResponseDto<>();

        File uploadedFile = fileStorageService.storeFile(file);

        if (uploadedFile != null) {
            responseDto.setData(uploadedFile);
            responseDto.setMessage("File Uploaded Successfully!");
            responseDto.setStatus(1);
        } else {
            responseDto.setStatus(0);
            responseDto.setError("Error uploading file, please try again later!");
        }

        return responseDto;
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, HttpServletRequest request)
            throws FileNotFoundException {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileId);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }
}
