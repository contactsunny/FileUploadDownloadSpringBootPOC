package com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.repository;

import com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.models.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {
}
