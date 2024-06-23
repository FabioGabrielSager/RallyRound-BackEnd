package org.fs.rallyroundbackend.controller;

import lombok.RequiredArgsConstructor;
import org.fs.rallyroundbackend.dto.activity.ActivityPage;
import org.fs.rallyroundbackend.dto.activity.MatchedActivities;
import org.fs.rallyroundbackend.dto.event.EventsForActivityByMonth;
import org.fs.rallyroundbackend.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/activities")
@RequiredArgsConstructor
public class ActivityContoller {

    private final ActivityService activityService;

    @GetMapping("matches/{name}")
    public ResponseEntity<MatchedActivities> getMatches(@PathVariable(value = "name") String name) {
        return ResponseEntity.ok(this.activityService.getMatchingActivitiesNames(name));
    }

    @GetMapping("event-counts")
    public ResponseEntity<EventsForActivityByMonth> getEventsForActivity(@RequestParam(required = false) Integer month,
                                                                         @RequestParam(required = false)
                                                                            String inscriptionFeeType) {
        return ResponseEntity.ok(this.activityService.getEventsForActivity(month, inscriptionFeeType));
    }

    @GetMapping
    public ResponseEntity<ActivityPage> getAllActivities(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean enabled,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(this.activityService.getAllActivities(name, enabled, page, limit));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<Void> disableActivity(@PathVariable UUID id) {
        this.activityService.disableActivity(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<Void> enableActivity(@PathVariable UUID id) {
        this.activityService.enableActivity(id);

        return ResponseEntity.noContent().build();
    }
}
