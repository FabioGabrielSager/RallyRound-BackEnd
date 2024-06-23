package org.fs.rallyroundbackend.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.PagedResponse;
import org.fs.rallyroundbackend.entity.events.ActivityEntity;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivityPage extends PagedResponse {
    private List<ActivityEntity> activities;
}
