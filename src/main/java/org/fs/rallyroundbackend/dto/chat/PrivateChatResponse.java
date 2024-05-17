package org.fs.rallyroundbackend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.fs.rallyroundbackend.dto.participant.ParticipantResume;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class PrivateChatResponse {
    private UUID chatId;
    private ParticipantResume partnerInfo;
}
