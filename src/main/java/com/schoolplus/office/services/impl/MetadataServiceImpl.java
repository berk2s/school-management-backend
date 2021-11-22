package com.schoolplus.office.services.impl;

import com.schoolplus.office.config.MetadataConfiguration;
import com.schoolplus.office.services.MetadataService;
import com.schoolplus.office.web.models.MetadataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MetadataServiceImpl implements MetadataService {

    private final MetadataConfiguration metadataConfiguration;

    @Override
    public MetadataDto getMetadata() {
        return MetadataDto.builder()
                .imageUrl(metadataConfiguration.getImageUrl())
                .build();
    }
}
