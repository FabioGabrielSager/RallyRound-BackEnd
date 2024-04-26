package org.fs.rallyroundbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.fs.rallyroundbackend.dto.event.EventParticipantResponse;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class MappersConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(new AbstractConverter<EventParticipantEntity, EventParticipantResponse>() {
            @Override
            protected EventParticipantResponse convert(EventParticipantEntity source) {
                ParticipantResume participantNameAndProfilePhoto = new
                        ParticipantResume();
                participantNameAndProfilePhoto.setId(source.getParticipant().getId());
                participantNameAndProfilePhoto.setName(source.getParticipant().getName());

                if(source.getParticipant().getProfilePhoto() != null) {
                    String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(source.getParticipant()
                            .getProfilePhoto());
                    participantNameAndProfilePhoto.setProfilePhoto(participantEncodedProfilePhoto);
                }


                return new EventParticipantResponse(participantNameAndProfilePhoto, source.isEventCreator());
            }
        });

        return modelMapper;
    }

    @Bean("mergerMapper")
    public ModelMapper mergerMapper(){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
