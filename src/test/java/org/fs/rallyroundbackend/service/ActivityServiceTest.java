package org.fs.rallyroundbackend.service;

import jakarta.persistence.EntityExistsException;
import org.fs.rallyroundbackend.config.MappersConfig;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.service.imps.ActivityServiceImp;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Import({MappersConfig.class})
public class ActivityServiceTest {
    @Mock
    private ActivityRepository activityRepository;
    @InjectMocks
    private ActivityServiceImp activityService;

    @Test
    @Tag("saveNewActivity")
    public void saveNewActivity_withRegisteredName() {
        when(this.activityRepository.existsByName("futbol")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> this.activityService.saveNewActivity("futbol"));
    }

    @Test
    @Tag("saveNewActivity")
    public void saveNewActivity_withNotRegisteredName() {
        when(this.activityRepository.existsByName("futbol")).thenReturn(false);
        when(this.activityRepository.save(any(ActivityEntity.class)))
                .thenReturn(new ActivityEntity(UUID.randomUUID(), "futbol"));

        assertEquals(this.activityService.saveNewActivity("futbol"), "futbol");
    }
}
