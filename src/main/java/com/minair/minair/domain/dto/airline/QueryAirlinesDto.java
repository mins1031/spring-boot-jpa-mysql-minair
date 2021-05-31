package com.minair.minair.domain.dto.airline;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minair.minair.domain.dto.PageDto;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.SafeHtml;

@Data
@Builder
public class QueryAirlinesDto <T>{

    private T airlineList;
    private PageDto pageDto;
}
