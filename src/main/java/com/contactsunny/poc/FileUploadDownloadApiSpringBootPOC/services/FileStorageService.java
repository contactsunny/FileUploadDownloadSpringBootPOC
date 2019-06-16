package com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.services;

import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.configuration.FileStorageProperties;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileStorageException;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.models.File;
import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.repository.FileRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.exceptions.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private FileRepository fileRepository;

    private static final Logger logger = Logger.getLogger(FileStorageService.class);

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDirectory())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public File storeFile(MultipartFile file) throws FileStorageException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        fileName = System.currentTimeMillis() + "_" + fileName.replaceAll(" ", "_");

        File fileToSave = new File();
        fileToSave.setFileName(fileName);
        fileToSave.setCreatedAt(System.currentTimeMillis());

        fileToSave = this.fileRepository.save(fileToSave);

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            fileToSave = null;

            logger.error(e.getMessage());
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }

        return fileToSave;
    }

    public Resource loadFileAsResource(String fileId) throws FileNotFoundException {

        Resource resource;
        File file = this.fileRepository.findOne(fileId);

        if (file == null) {
            throw new FileNotFoundException("File with ID " + fileId + " not found!");
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(file.getFileName()).normalize();
            resource = new UrlResource(filePath.toUri());

            if(!resource.exists()) {
                throw new FileNotFoundException("File not found " + file.getFileName());
            }

        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
            throw new FileNotFoundException("File not found " + file.getFileName());
        }

        return resource;
    }
}
