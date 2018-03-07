package com.ggp.theclub.model;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

import lombok.Getter;

public class ProductData {
    @SerializedName("types")
    @Getter private Set<ProductType> productTypes;
    @Getter private Set<Brand> brands;
}