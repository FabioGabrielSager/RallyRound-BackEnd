package org.fs.rallyroundbackend.dto.mercadopago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MpWebHookNotificationDto {
    private Long id;
    @JsonProperty("live_mode")
    private Boolean liveMode;
    private String type;
    @JsonProperty("date_created")
    private String dateCreated;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("api_version")
    private String apiVersion;
    private String action;
    private MPWebHookNotificationDataDto data;
}
