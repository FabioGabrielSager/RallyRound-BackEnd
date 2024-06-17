package org.fs.rallyroundbackend.controller;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.documents.DocumentDto;
import org.fs.rallyroundbackend.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rr/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping(value = "/terms-and-conditions", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DocumentDto> getTermsAndConditionsHtml() {
        return ResponseEntity.ok(this.documentService.getTermsAndConditionHtml());
    }
}
