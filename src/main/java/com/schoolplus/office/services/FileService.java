package com.schoolplus.office.services;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface FileService {

    void upload(String path,
                       String filename,
                       Optional<Map<String, String>> optinalMetaData,
                       InputStream inputStream,
                        Long size);

}
