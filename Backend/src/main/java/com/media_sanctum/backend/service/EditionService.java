package com.media_sanctum.backend.service;

import com.media_sanctum.backend.entity.Edition;
import com.media_sanctum.backend.repository.EditionRepository;
import com.media_sanctum.backend.resource.EditionResponse;
import org.springframework.stereotype.Service;

@Service
public class EditionService {

    private final EditionRepository editionRepository;

    public EditionService(EditionRepository editionRepository) {
        this.editionRepository = editionRepository;
    }

    public Edition getEdition(String id) {
        return editionRepository.findById(id).orElse(null);
    }

    public Edition getEditionByHardcoverId(Integer hardcoverId) {
        return editionRepository.findByHardcoverId(hardcoverId).orElse(null);
    }

    public Edition saveEdition(Edition edition) {
        return editionRepository.save(edition);
    }

    public static EditionResponse toResponse(Edition edition) {
        if (edition == null) return null;
        
        return EditionResponse.builder()
                .id(edition.getId())
                .asin(edition.getAsin())
                .isbn10(edition.getIsbn10())
                .isbn13(edition.getIsbn13())
                .language(edition.getLanguage())
                .country(edition.getCountry())
                .editionType(edition.getEditionType().name())
                .image(ImageService.toResponse(edition.getImage()))
                .build();
    }
}
