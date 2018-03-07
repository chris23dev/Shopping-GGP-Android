package com.ggp.theclub.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by avishek.das on 1/15/16.
 */
public class GoogleMapsResponse {
    @Getter @Setter List<MapsAddress> results;
}
