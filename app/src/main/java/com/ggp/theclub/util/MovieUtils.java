package com.ggp.theclub.util;

import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.MovieShowtime;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.TheaterShowtime;

import org.joda.time.LocalDate;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;


/**
 * Created by avishek.das on 1/6/16.
 */
public class MovieUtils {
    private final static String fandangoUrlFormat = "http://www.fandango.com/redirect.aspx?mid=%s&tid=%s&date=%s";
    private final static String fandangoDateTimeFormat = "yyyy-MM-dd+k:ma";
    private final static int MAX_CALENDAR_DAYS = 7;
    /**
     *
     * @param {Movie} movie
     * @return URL to the fandango purchase site for the movie.
     *         If current mall does not support fandango, returns the theater's website instead
     */
    public static String buildPurchaseTicketsUrl(Movie movie, MovieShowtime showtime, String theaterUrl, String tmsId) {
        if(theaterSupportsFandango(tmsId)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(fandangoDateTimeFormat);
            String formattedDateTime = dateFormat.format(showtime.getMovieShowtime());
            return String.format(fandangoUrlFormat, movie.getFandangoId(), tmsId, formattedDateTime);
        } else {
            return theaterUrl;
        }
    }

    /**
     *
     * @return True if the current mall isn't found in the exceptions list
     */
    public static boolean theaterSupportsFandango(String tmsId) {
        return !StringUtils.isEmpty(tmsId);
    }

    public static List<TheaterShowtime> getTheaterShowtimesForDate(Movie movie, LocalDate date) {
        List<TheaterShowtime> theaterShowtimes = movie.getTheaterShowtimes();
        if(theaterShowtimes != null) {
            theaterShowtimes = StreamSupport.stream(theaterShowtimes)
                    .filter(ts -> !getShowtimesForDate(ts.getShowtimes(), date).isEmpty())
                    .collect(Collectors.toList());
        }

        return theaterShowtimes != null ? theaterShowtimes : new ArrayList<>();
    }

    public static List<MovieShowtime> getShowtimesForDate(List<MovieShowtime> showtimes, LocalDate date) {
        return StreamSupport.stream(showtimes).filter(s -> date.equals(new LocalDate(s.getMovieShowtime()))).collect(Collectors.toList());
    }

    public static List<MovieShowtime> getShowtimesForDate(TheaterShowtime theaterShowtime, LocalDate date) {
        List<MovieShowtime> showtimes = theaterShowtime.getShowtimes();
        if(showtimes != null) {
            showtimes = StreamSupport.stream(showtimes)
                    .filter(s -> date.equals(new LocalDate(s.getMovieShowtime())))
                    .collect(Collectors.toList());
        }

        return showtimes != null ? showtimes : new ArrayList<>();
    }

    public static List<Movie> getMoviesWithTheaterShowtimes(List<Movie> movieList, LocalDate date) {
        List<Movie> filteredMovies = StreamSupport.stream(movieList)
                .filter(movie -> !getTheaterShowtimesForDate(movie, date).isEmpty())
                .collect(Collectors.toList());

        return filteredMovies != null ? filteredMovies : new ArrayList<>();
    }

    public static List<Movie> sortMovies(List<Movie> movies) {
        return StreamSupport.stream(movies).sorted((movie1, movie2) -> {
            Collator collator = Collator.getInstance();
            collator.setStrength(Collator.PRIMARY);
            return Collator.getInstance().compare(StringUtils.getNameForSorting(movie1.getTitle()), StringUtils.getNameForSorting(movie2.getTitle()));
        }).collect(Collectors.toList());
    }

    public static void populateTheaterShowtimes(List<Movie> movieList, List<MovieTheater> movieTheaters) {
        StreamSupport.stream(movieList).forEach(movie -> MovieUtils.populateTheaterShowtimes(movie, movieTheaters));
    }

    /**
     * @modifies: movie
     * @effect: Adds theater showtimes to movie for each theater that it's playing in
     **/
    public static void populateTheaterShowtimes(Movie movie, List<MovieTheater> theaters) {
        StreamSupport.stream(theaters).forEach(theater ->
                StreamSupport.stream(theater.getMovies())
                .filter(m -> m.getId() == movie.getId())
                .forEach(m -> {
                    TheaterShowtime theaterShowtime = new TheaterShowtime(theater, m.getShowtimes());
                    movie.getTheaterShowtimes().add(theaterShowtime);
                })
        );
    }

    public static List<Movie> getUniqueMovies(List<MovieTheater> movieTheaters) {
        List<Movie> movieList = new ArrayList<>();
        Set<Integer> movieIds = new HashSet<>();
        StreamSupport.stream(movieTheaters).forEach(theater -> {
            List<Movie> uniqueMovies = StreamSupport.stream(theater.getMovies()).filter(movie -> !movieIds.contains(movie.getId())).collect(Collectors.toList());
            movieIds.addAll(StreamSupport.stream(uniqueMovies).map(Movie::getId).collect(Collectors.toList()));
            movieList.addAll(uniqueMovies);
        });
        return  movieList;
    }
}
