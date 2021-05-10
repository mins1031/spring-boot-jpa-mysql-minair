package com.minair.minair.domain.notEntity;

public enum Departure {
    ICN("인천"),JEJU("제주"),DAE("대구"),BUS("부산"),GWANG("광주");

    final private String city;

    Departure(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}
