package com.ggp.theclub.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class TheaterShowtime {
    @Getter @Setter private List<MovieShowtime> showtimes;
    @Getter @Setter private String theaterName;
    @Getter @Setter private String theaterUrl;
    @Getter @Setter private String tmsId;

    public TheaterShowtime(MovieTheater theater, List<MovieShowtime> showtimes) {
        setTheaterName(theater.getName());
        setTheaterUrl(theater.getTheaterUrl());
        setTmsId(theater.getTmsId());
        setShowtimes(showtimes);
    }
}
