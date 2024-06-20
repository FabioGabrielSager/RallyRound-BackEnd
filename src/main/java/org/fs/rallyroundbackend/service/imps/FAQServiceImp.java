package org.fs.rallyroundbackend.service.imps;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.documents.FAQDto;
import org.fs.rallyroundbackend.repository.FAQRepository;
import org.fs.rallyroundbackend.service.FAQService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FAQServiceImp implements FAQService {
    private final FAQRepository faqRespository;
    private final ModelMapper modelMapper;

    @Override
    public List<FAQDto> getFAQs() {
        return List.of(this.modelMapper.map(this.faqRespository.findAll(), FAQDto[].class));
    }
}
