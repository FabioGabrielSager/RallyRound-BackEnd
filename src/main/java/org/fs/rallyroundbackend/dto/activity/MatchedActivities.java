package org.fs.rallyroundbackend.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class MatchedActivities {
    private List<String> activities;
}
