package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = "id")
public class Category <T extends Category> {

    @Getter @Setter private int id;
    @Getter @Setter private int parentId;
    @Getter @Setter private String code;
    @Getter @Setter private String label;
    @Getter @Setter private List<T> childCategories = new ArrayList<>();

    public Category() { }

    public Category(Category category) {
        id = category.getId();
        parentId = category.getParentId();
        code = category.getCode();
        label = category.getLabel();
        childCategories = category.getChildCategories();
    }
}