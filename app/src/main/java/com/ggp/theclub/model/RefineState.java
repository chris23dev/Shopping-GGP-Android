package com.ggp.theclub.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class RefineState implements Serializable {
    @Getter @Setter private String saleCategoryCode;
    @Getter @Setter private boolean favoritesSelected;
    @Getter @Setter private Set<Integer> selectedTenantIds = new HashSet<>();
    @Getter @Setter private RefineSort refineSort = RefineSort.DEFAULT;

    public enum RefineSort { DEFAULT, ASCENDING, DESCENDING }
}