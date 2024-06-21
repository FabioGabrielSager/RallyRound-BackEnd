package org.fs.rallyroundbackend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantSummary;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class PrivateChatResponse {
    private UUID chatId;
    private ParticipantSummary partnerInfo;
}
