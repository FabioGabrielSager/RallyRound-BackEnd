package org.fs.rallyroundbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagedResponse {
    protected int page;
    protected int pageSize;
    protected long totalElements;
}
