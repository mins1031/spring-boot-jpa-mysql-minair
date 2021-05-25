package com.minair.minair.common;


import com.minair.minair.controller.HomeController;
import org.hibernate.EntityMode;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorResource extends EntityModel<Errors> {

    public ErrorResource(Errors errors, Link... links) {
        super(errors,links);
        add(linkTo(methodOn(HomeController.class).index()).withRel("index"));
    }
}
