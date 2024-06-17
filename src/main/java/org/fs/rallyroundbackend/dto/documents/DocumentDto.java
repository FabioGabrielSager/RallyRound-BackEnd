package org.fs.rallyroundbackend.dto.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDto {
    private String title;
    private String content;
    private String contentType;
}
