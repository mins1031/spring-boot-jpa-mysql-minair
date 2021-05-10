package com.minair.minair.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuttController {

    //private final AuthService authService;

    @GetMapping("/access")
    @Secured("ROLE_MEMBER")
    public String accessToken(){
        return "access!!";
    }

}
