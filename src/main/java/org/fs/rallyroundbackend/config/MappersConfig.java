package org.fs.rallyroundbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.fs.rallyroundbackend.dto.auth.ParticipantFavoriteActivityDto;
import org.fs.rallyroundbackend.dto.event.EventParticipantDto;
import org.fs.rallyroundbackend.dto.location.addresses.AddressDto;
import org.fs.rallyroundbackend.dto.location.addresses.SpecificAddressDto;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;
import org.fs.rallyroundbackend.entity.events.EventParticipantEntity;
import org.fs.rallyroundbackend.entity.events.EventSchedulesEntity;
import org.fs.rallyroundbackend.entity.location.AddressEntity;
import org.fs.rallyroundbackend.entity.location.EntityType;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantFavoriteActivityEntity;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.Base64;

@Configuration
public class MappersConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addConverter(new AbstractConverter<EventParticipantEntity, EventParticipantDto>() {
            @Override
            protected EventParticipantDto convert(EventParticipantEntity source) {
                ParticipantResume participantNameAndProfilePhoto = new
                        ParticipantResume();
                participantNameAndProfilePhoto.setId(source.getParticipant().getId());
                participantNameAndProfilePhoto.setName(source.getParticipant().getName());

                if (source.getParticipant().getProfilePhoto() != null) {
                    String participantEncodedProfilePhoto = Base64.getEncoder().encodeToString(source.getParticipant()
                            .getProfilePhoto());
                    participantNameAndProfilePhoto.setProfilePhoto(participantEncodedProfilePhoto);
                }


                return new EventParticipantDto(participantNameAndProfilePhoto, source.isEventCreator());
            }
        });

        modelMapper.addConverter(new AbstractConverter<EventSchedulesEntity, LocalTime>() {
            @Override
            protected LocalTime convert(EventSchedulesEntity source) {
                return source.getSchedule().getStartingHour().toLocalTime();
            }
        });

        modelMapper.addConverter(new AbstractConverter<AddressEntity, AddressDto>() {
            @Override
            protected AddressDto convert(AddressEntity source) {
                return AddressDto.builder()
                        .entityType(EntityType.Address)
                        .address(SpecificAddressDto.builder()
                                .houseNumber(source.getHouseNumber() != null ? source.getHouseNumber() : "")

                                .countryRegion("Argentina")

                                .streetName(source.getStreet().getName() != null ? source.getStreet().getName() : "")

                                .neighborhood(source.getStreet().getNeighborhood().getName() != null
                                        ? source.getStreet().getNeighborhood().getName() : "")

                                .locality(source.getStreet().getNeighborhood().getLocality().getName() != null
                                        ? source.getStreet().getNeighborhood().getLocality().getName() : "")

                                .adminDistrict2(
                                        source.getStreet().getNeighborhood().getLocality()
                                                .getAdminSubdistrict().getName() != null
                                                ? source.getStreet().getNeighborhood().getLocality()
                                                        .getAdminSubdistrict().getName() : "")

                                .adminDistrict(
                                        source.getStreet().getNeighborhood().getLocality()
                                                .getAdminSubdistrict().getAdminDistrict().getName() != null
                                                ? source.getStreet().getNeighborhood().getLocality()
                                                        .getAdminSubdistrict().getAdminDistrict().getName() : "")

                                .postalCode(source.getPostalCode() != null ? source.getPostalCode() : "")

                                .addressLine(source.getAddressLine().getAddressLine() != null
                                        ? source.getAddressLine().getAddressLine() : "")

                                .formattedAddress(source.getFormattedAddress().getFormattedAddress() != null
                                        ? source.getFormattedAddress().getFormattedAddress() : "")
                                .build())
                        .build();
            }
        });

        modelMapper.addConverter(new AbstractConverter<ParticipantFavoriteActivityEntity,
                ParticipantFavoriteActivityDto>() {
            @Override
            protected ParticipantFavoriteActivityDto convert(ParticipantFavoriteActivityEntity source) {
                return new ParticipantFavoriteActivityDto(source.getActivity().getName(), source.getFavoriteOrder());
            }
        });

        return modelMapper;
    }

    @Bean("mergerMapper")
    public ModelMapper mergerMapper() {
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
