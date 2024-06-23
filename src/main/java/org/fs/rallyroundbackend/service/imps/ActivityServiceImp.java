package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.fs.rallyroundbackend.dto.activity.ActivityPage;
import org.fs.rallyroundbackend.dto.activity.MatchedActivities;
import org.fs.rallyroundbackend.dto.event.EventsForActivityByMonth;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.service.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        if(month != null && month > 0 && month < 13) {
            validMonth = month;
        }

        EventsForActivityByMonth result = new EventsForActivityByMonth();
        result.setMonth(validMonth);

        result.setResults(this.activityRepository.getEventsForActivity(validMonth, inscriptionFeeType));

        return result;
    }

    @Override
    public ActivityPage getAllActivities(String name, Boolean enabled, Integer page, Integer limit) {
        int validLimit = 10;
        int validPage = 1;

        if (limit != null) {
            validLimit = limit;
        }

        if (page != null) {
            validPage = page;
        }

        Page<ActivityEntity> activityEntities =
                this.activityRepository.findAll(name != null ? name.toLowerCase() : null, enabled, PageRequest.of(validPage-1, validLimit));

        ActivityPage result = new ActivityPage();
        result.setActivities(activityEntities.getContent());
        result.setPage(validPage);
        result.setPageSize(validLimit);
        result.setTotalElements(activityEntities.getTotalElements());

        return result;
    }

    @Override
    public void disableActivity(UUID activityId) {
        ActivityEntity activity = this.activityRepository.findById(activityId).orElseThrow(
                () -> new EntityNotFoundException("Activity not found.")
        );

        activity.setEnabled(false);

        this.activityRepository.save(activity);
    }

    @Override
    public void enableActivity(UUID activityId) {
        ActivityEntity activity = this.activityRepository.findById(activityId).orElseThrow(
                () -> new EntityNotFoundException("Activity not found.")
        );

        activity.setEnabled(true);

        this.activityRepository.save(activity);
    }
}
