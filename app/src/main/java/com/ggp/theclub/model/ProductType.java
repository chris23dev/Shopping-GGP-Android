package com.ggp.theclub.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = "code")
public class ProductType {
    @Getter private String code;
    @Getter private String label;
    private final String SEPARATOR = "/";

    public String getParentCode() {
        return getCode() != null ? getCode().split(SEPARATOR)[0] : null;
    }
}