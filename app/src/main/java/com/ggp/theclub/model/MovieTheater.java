package com.ggp.theclub.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MovieTheater extends Tenant {
    @Getter @Setter private List<Movie> movies;
    @Getter @Setter private String theaterUrl;
    @Getter @Setter private String tmsId;
}
