package com.minair.minair.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDto {
    //startPage,endPage, prev,next,total,offset,limit
    //startPage = getNumber(), endPage = getTotalPage, prev = hasPrevious(),
    //next = hasNext() , total = getTotalElements, offset = 선택된 페이지. 디폴트 0,
    //limit = 10;
    private int startPage;
    private int endPage;
    private boolean prev,next;
    private Long total;
    private int realEndPage;
    private int offset;
    private int limit;

    public PageDto(int offset,int limit,Long total,int totalPage){

        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.endPage = (int)(Math.ceil(offset/10.0)) * 10;
        this.startPage = endPage - 9;
        this.realEndPage = totalPage;

        if (realEndPage < this.endPage)
            this.endPage = realEndPage;

        this.prev = this.startPage > 1;
        this.next = this.endPage < realEndPage;
    }

}
