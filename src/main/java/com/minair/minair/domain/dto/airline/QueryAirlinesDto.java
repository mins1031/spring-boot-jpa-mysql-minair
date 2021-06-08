package com.minair.minair.domain.dto.airline;

import com.minair.minair.domain.dto.common.PageDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryAirlinesDto <T>{

    private T airlineList;
    private PageDto pageDto;
}
