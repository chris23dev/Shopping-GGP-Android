package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class FilterCategory {
    @Getter @Setter private String code;
    @Getter @Setter private String description;
    @Getter @Setter private int count;
    @Getter @Setter private List<FilterCategory> children;

    public FilterCategory(String code, String description, int count) {
        this(code, description, count, new ArrayList<>());
    }

    public FilterCategory(String code, String description, int count, List<FilterCategory> children) {
        setCode(code);
        setDescription(description);
        setCount(count);
        setChildren(children);
    }
}