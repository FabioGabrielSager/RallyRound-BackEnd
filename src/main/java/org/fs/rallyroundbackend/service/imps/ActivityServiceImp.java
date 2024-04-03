package org.fs.rallyroundbackend.service.imps;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.activity.MatchedActivities;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;
import org.fs.rallyroundbackend.repository.ActivityRepository;
import org.fs.rallyroundbackend.service.ActivityService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link ActivityService} implementation for managing activities.
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImp implements ActivityService {
    private final ActivityRepository activityRepository;

    @Override
    public String saveNewActivity(String name) {
        if(this.activityRepository.existsByName(name)) {
            throw new EntityExistsException("");
        }

        return this.activityRepository.save(ActivityEntity.builder().name(name.toLowerCase()).build()).getName();
    }

    @Override
    public MatchedActivities getMatchingActivitiesNames(String name) {
        List<ActivityEntity> activityEntities = this.activityRepository.findMatchesByName(name);

        return new MatchedActivities(
                activityEntities.stream().map(ActivityEntity::getName).toList()
        );
    }
}
