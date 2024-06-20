package org.fs.rallyroundbackend.controller;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.documents.FAQDto;
import org.fs.rallyroundbackend.service.FAQService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rr/api/v1/faq")
@RequiredArgsConstructor
public class FAQController {
    private final FAQService faqService;

    @GetMapping
    public ResponseEntity<List<FAQDto>> getAllFAQs() {
        return ResponseEntity.ok(this.faqService.getFAQs());
    }
}
