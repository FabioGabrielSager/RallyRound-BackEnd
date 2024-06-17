package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.documents.DocumentDto;
import org.fs.rallyroundbackend.entity.DocumentEntity;
import org.fs.rallyroundbackend.repository.DocumentRepository;
import org.fs.rallyroundbackend.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentServiceImp implements DocumentService {
    private final DocumentRepository documentRepository;
    private final ModelMapper modelMapper;

    @Override
    public DocumentDto getTermsAndConditionHtml() {
        DocumentEntity document = this.documentRepository
                .findByTitleAndContentType("rr terms and conditions", "html")
                .orElseThrow(
                        () -> new EntityNotFoundException("Document not found.")
                );

        DocumentDto documentDto = this.modelMapper.map(document, DocumentDto.class);
        documentDto.setContentType(document.getContentType().getType());

        return documentDto;
    }
}
