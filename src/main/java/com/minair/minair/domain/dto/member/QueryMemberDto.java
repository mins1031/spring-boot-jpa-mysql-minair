package com.minair.minair.domain.dto.member;

import com.minair.minair.domain.dto.common.PageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryMemberDto <T> {

    private T memberList;
    private PageDto pageDto;
}
