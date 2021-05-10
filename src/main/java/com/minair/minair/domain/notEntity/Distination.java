package com.minair.minair.domain.notEntity;

public enum Distination {
    ICN("인천"),JEJU("제주"),DAE("대구"),BUS("부산"),GWANG("광주");

    final private String city;

    Distination(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}
