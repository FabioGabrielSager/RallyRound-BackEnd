package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.dto.activity.MatchedActivities;
import org.fs.rallyroundbackend.dto.event.EventsForActivityByMonth;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * {@link ActivityService} implementation for managing activities.
 */
@Service
@AllArgsConstructor
public class ActivityServiceImp implements ActivityService {
    private ActivityRepository activityRepository;

    @Override
    public String saveNewActivity(String name) {
        if(this.activityRepository.existsByName(name)) {
            throw new EntityExistsException("There is already an activity registered with that name.");
        }

        return this.activityRepository.save(ActivityEntity.builder().name(name.toLowerCase()).build()).getName();
    }

    @Override
    public MatchedActivities getMatchingActivitiesNames(String name) {
        List<ActivityEntity> activityEntities = this.activityRepository.findMatchesByName(name.toLowerCase());

        return new MatchedActivities(
                activityEntities.stream().map(ActivityEntity::getName).toList()
        );
    }

    @Override
    public EventsForActivityByMonth getEventsForActivity(Integer month, String inscriptionFeeType) {
        int validMonth = LocalDate.now().getMonth().getValue();
        if(month != null && month > 1 && month < 13) {
            validMonth = month;
        }

        EventsForActivityByMonth result = new EventsForActivityByMonth();
        result.setMonth(validMonth);

        result.setResults(this.activityRepository.getEventsForActivity(validMonth, inscriptionFeeType));

        return result;
    }
}
