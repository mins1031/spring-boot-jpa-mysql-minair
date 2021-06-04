package com.minair.minair.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.criterion.LikeExpression;
import org.springframework.hateoas.Link;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 응답 개체가 없이 링크만 응답해야 하는 경우에 사용
 * */
public class LinkDto {

    private Link selfLink;
    private Link indexLink;
    private Link profileLink;
}
