package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.documents.FAQDto;

import java.util.List;

public interface FAQService {
    List<FAQDto> getFAQs();
}
