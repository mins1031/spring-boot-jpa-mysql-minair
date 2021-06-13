package com.minair.minair.common;

import com.minair.minair.domain.Member;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class BasicResource extends EntityModel<Object> {

    public BasicResource(Object object) {
        super(object);
        add(new Link("/").withRel("index"));
        add(new Link("/docs/index").withRel("profile"));
    }
}
