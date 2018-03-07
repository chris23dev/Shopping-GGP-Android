package com.ggp.theclub.model;

import com.ggp.theclub.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

public class Address {
    @Getter @Setter private String line1;
    @Getter @Setter private String city;
    @Getter @Setter private String state;
    @Getter @Setter private String zip;

    public String getCityAndState() {
        if (!StringUtils.isEmpty(city) && !StringUtils.isEmpty(state)) {
            return String.format("%s, %s", city, state);
        }
        return null;
    }
}