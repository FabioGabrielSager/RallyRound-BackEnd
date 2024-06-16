package org.fs.rallyroundbackend.service;

import org.fs.rallyroundbackend.dto.activity.MatchedActivities;
import org.fs.rallyroundbackend.dto.event.EventsForActivityByMonth;
import org.springframework.stereotype.Service;

/**
 * Service interface for managing activities.
 */
@Service
public interface ActivityService {

    /**
     * Saves a new activity with the given name.
     *
     * @param name The name of the activity to save.
     * @return The ID of the newly saved activity.
     */
    String saveNewActivity(String name);

    /**
     * Retrieves matching activity names based on the provided complete or incomplete name.
     *
     * @param name The name or partial name of the activity to match.
     * @return A {@link MatchedActivities} object containing matching activity names.
     */
    MatchedActivities getMatchingActivitiesNames(String name);

    EventsForActivityByMonth getEventsForActivity(Integer month, String inscriptionFeeType);
}
