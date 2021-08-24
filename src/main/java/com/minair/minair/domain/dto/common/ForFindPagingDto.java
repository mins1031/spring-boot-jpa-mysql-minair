package com.minair.minair.domain.dto.common;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForFindPagingDto {

    @Value("1")
    @NotNull
    private int pageNum;
    @Nullable
    private String username;

}
