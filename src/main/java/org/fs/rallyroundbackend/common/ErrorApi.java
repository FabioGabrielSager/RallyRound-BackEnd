package org.fs.rallyroundbackend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Generated
public class ErrorApi {
    private String timestamp;
    private String error;
    private String message;
}
