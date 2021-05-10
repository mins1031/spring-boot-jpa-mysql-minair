package com.minair.minair.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Roles {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    public Roles(String role) {
        this.role = role;
    }
}
