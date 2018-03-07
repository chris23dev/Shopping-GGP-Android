package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class FilterProductType {
    @Getter @Setter private String code;
    @Getter @Setter private String description;
    @Getter @Setter private int count;
    @Getter @Setter private List<FilterProductType> children;

    public FilterProductType(String code, String description) {
        this(code, description, 0, new ArrayList<>());
    }

    public FilterProductType(String code, String description, List<FilterProductType> children) {
        this(code, description, 0, children);
    }

    public FilterProductType(String code, String description, int count) {
        this(code, description, count, new ArrayList<>());
    }

    public FilterProductType(String code, String description, int count, List<FilterProductType> children) {
        setCode(code);
        setDescription(description);
        setCount(count);
        setChildren(children);
    }
}