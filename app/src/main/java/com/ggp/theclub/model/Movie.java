package com.ggp.theclub.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Movie {
    @Getter @Setter private int id;
    @Getter @Setter private String title;
    @Getter @Setter private String mpaaRating;
    @Getter @Setter private String synopsis;
    @Getter @Setter private String director;
    @Getter @Setter private String distributor;
    @Getter @Setter private Integer runTimeInMinutes;
    @Getter @Setter private Integer fandangoId;
    @Getter @Setter private String largePosterImageUrl;
    @Getter @Setter private List<MovieShowtime> showtimes;
    @Getter @Setter private List<String> actors;
    @Getter @Setter private List<String> genres;
    @Getter @Setter private List<TheaterShowtime> theaterShowtimes = new ArrayList<>(); //Populated by the movies screen
}