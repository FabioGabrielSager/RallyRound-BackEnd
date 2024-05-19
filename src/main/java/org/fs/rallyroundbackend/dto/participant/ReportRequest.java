package org.fs.rallyroundbackend.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.entity.users.participant.ReportMotive;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class ReportRequest {
    protected UUID reportedUserId;
    protected ReportMotive reportMotive;
    protected String description;
    // field to indicate if the report is for the user as event creator or as event participant
    protected boolean asEventCreator;
}
