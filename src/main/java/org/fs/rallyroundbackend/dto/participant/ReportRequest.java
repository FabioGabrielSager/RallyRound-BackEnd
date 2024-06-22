package org.fs.rallyroundbackend.dto.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.fs.rallyroundbackend.entity.users.participant.ReportMotive;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ReportRequest {
    @NotNull
    protected UUID reportedUserId;
    @NotNull
    protected ReportMotive reportMotive;
    @NotBlank
    protected String description;
    // field to indicate if the report is for the user as event creator or as event participant
    @NotNull
    protected boolean asEventCreator;
}
