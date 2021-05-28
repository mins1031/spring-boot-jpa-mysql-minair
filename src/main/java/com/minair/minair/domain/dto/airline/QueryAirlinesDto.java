package com.minair.minair.domain.dto.airline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.SafeHtml;

@Data
@Builder
public class QueryAirlinesDto <T>{

    private T airlineList;
    @JsonProperty("totalPage")
    private int totalPage;
    private Long totalElements;
}
