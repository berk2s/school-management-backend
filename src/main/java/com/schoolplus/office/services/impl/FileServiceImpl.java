package com.schoolplus.office.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.schoolplus.office.services.FileService;
import com.schoolplus.office.web.exceptions.FileUploadingException;
import com.schoolplus.office.web.models.ErrorDesc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AmazonS3 amazonS3;

    @Override
    public void upload(String path, String filename, Optional<Map<String, String>> optinalMetaData, InputStream inputStream, Long size) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optinalMetaData.ifPresent(map -> {
            if(!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });

        objectMetadata.setContentLength(size);

        try {
            amazonS3.putObject(path, filename, inputStream, objectMetadata);

            log.info("The file has been uploaded to bucket [fileName: {}]", filename);
        } catch (AmazonServiceException e) {
            log.warn("Uploading process is failed {}", e.getMessage());
            throw new FileUploadingException(ErrorDesc.FAILED_IMAGE_UPLOAD.getDesc());
        }
    }
}
