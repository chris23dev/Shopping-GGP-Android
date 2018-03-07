package com.ggp.theclub.util;

import com.ggp.theclub.BaseTest;
import com.ggp.theclub.model.Movie;
import com.ggp.theclub.model.MovieShowtime;
import com.ggp.theclub.model.MovieTheater;
import com.ggp.theclub.model.TheaterShowtime;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MovieUtilsTest extends BaseTest {
    private final String fandangoUrlFormat = "http://www.fandango.com/redirect.aspx?mid=%s&tid=%s&date=%s";
    private final String fandangoDateTimeFormat = "yyyy-MM-dd+k:ma";

    private MovieTheater movieTheater;
    private Movie movie;
    private List<Movie> movieList;
    private MovieShowtime movieShowtime;
    private List<MovieShowtime> movieShowtimes;

    private int mockFandangoId = 9000;
    private String mockTheaterName = "AMC 9000";
    private String mockTmsId = "testId";
    private String mockTheaterUrl = "www.theater.com";
    private String mockMovieTitle = "Star Trek: Order of the Phoenix";
    private String mockMovieTitle2 = "The ABC Movie";
    private String mockMovieTitle3 = "The Zebra";

    @Before
    public void setup() throws Exception {
        super.setup();
        movieTheater = new MovieTheater();
        movieTheater.setTmsId(mockTmsId);
        movieTheater.setTheaterUrl(mockTheaterUrl);
        movieTheater.setName(mockTheaterName);

        Date date = new Date();
        movieShowtime = new MovieShowtime();
        movieShowtime.setMovieShowtime(date);

        movieShowtimes = new ArrayList<>();
        movieShowtimes.add(movieShowtime);

        movie = getMockMovie(0);

        movieList = new ArrayList<>();
        movieList.add(movie);
        movieTheater.setMovies(movieList);
    }
    @Test
    public void testBuildFandangoUrl() {
        String purchaseTicketsUrl = MovieUtils.buildPurchaseTicketsUrl(movie, movieShowtime, movieTheater.getTheaterUrl(), movieTheater.getTmsId());
        SimpleDateFormat dateFormat = new SimpleDateFormat(fandangoDateTimeFormat);
        String formattedDateTime = dateFormat.format(movieShowtime.getMovieShowtime());
        String expectedFandangoUrl = String.format(fandangoUrlFormat, mockFandangoId, mockTmsId, formattedDateTime);
        assertEquals(expectedFandangoUrl, purchaseTicketsUrl);
    }

    @Test
    public void testBuildFandangoExceptionUrl() {
        movieTheater.setTmsId("");
        assertEquals(MovieUtils.buildPurchaseTicketsUrl(movie, movieShowtime, movieTheater.getTheaterUrl(), movieTheater.getTmsId()), mockTheaterUrl);
        
        movieTheater.setTmsId(null);
        assertEquals(MovieUtils.buildPurchaseTicketsUrl(movie, movieShowtime, movieTheater.getTheaterUrl(), movieTheater.getTmsId()), mockTheaterUrl);
    }

    @Test
    public void testGetMoviesWithShowtimes() {
        List<TheaterShowtime> theaterShowtimeList = new ArrayList<>();
        theaterShowtimeList.add(new TheaterShowtime(movieTheater, movieShowtimes));
        movieList.get(0).setTheaterShowtimes(theaterShowtimeList);

        assertEquals(MovieUtils.getMoviesWithTheaterShowtimes(movieList, new LocalDate()).size(), 1);
        assertEquals(MovieUtils.getMoviesWithTheaterShowtimes(movieList, new LocalDate().plusDays(1)).size(), 0);

        MovieShowtime showtime = new MovieShowtime();
        showtime.setMovieShowtime(new LocalDate().plusDays(1).toDate());
        movieShowtimes = new ArrayList<>();
        movieShowtimes.add(showtime);

        theaterShowtimeList = new ArrayList<>();
        theaterShowtimeList.add(new TheaterShowtime(new MovieTheater(), movieShowtimes));

        movie = new Movie();
        movie.setTheaterShowtimes(theaterShowtimeList);
        movieList.add(movie);

        assertEquals(MovieUtils.getMoviesWithTheaterShowtimes(movieList, new LocalDate().plusDays(1)).size(), 1);
    }

    @Test
    public void testSortMovies() {
        movie = new Movie();
        movie.setTitle(mockMovieTitle3);
        movieList.add(movie);
        movie = new Movie();
        movie.setTitle(mockMovieTitle2);
        movieList.add(movie);

        assertEquals(movieList.size(), 3);
        assertEquals(movieList.get(0).getTitle(), mockMovieTitle);
        assertEquals(movieList.get(1).getTitle(), mockMovieTitle3);
        assertEquals(movieList.get(2).getTitle(), mockMovieTitle2);
        movieList = MovieUtils.sortMovies(movieList);
        assertEquals(movieList.get(0).getTitle(), mockMovieTitle2);
        assertEquals(movieList.get(1).getTitle(), mockMovieTitle);
        assertEquals(movieList.get(2).getTitle(), mockMovieTitle3);
    }

    @Test
    public void testGetShowtimesForDate() {
        assertEquals(MovieUtils.getShowtimesForDate(movieShowtimes, LocalDate.now()).get(0), movieShowtime);
        assertEquals(MovieUtils.getShowtimesForDate(movieShowtimes, LocalDate.now().plusDays(1)).isEmpty(), true);
    }

    @Test
    public void testGetUniqueMovies() {
        List<MovieTheater> movieTheaterList = new ArrayList<>();
        movieTheaterList.add(movieTheater);
        movieTheaterList.add(movieTheater);
        assertEquals(MovieUtils.getUniqueMovies(movieTheaterList).size(), 1);

        movie = new Movie();
        movie.setId(2);
        movieTheaterList.get(0).getMovies().add(movie);
        assertEquals(MovieUtils.getUniqueMovies(movieTheaterList).size(), 2);
    }

    @Test
    public void testPopulateTheaterShowtimes() {
        List<MovieTheater> movieTheaterList = new ArrayList<>();
        movieTheaterList.add(movieTheater);
        movieTheater = new MovieTheater();
        movieTheater.setMovies(getMockMovieList(2));
        movieTheaterList.add(movieTheater);

        movieTheaterList.get(0).getMovies().get(0).setShowtimes(getMockShowtimes(1));
        MovieUtils.populateTheaterShowtimes(movie, movieTheaterList);

        assertEquals(movie.getTheaterShowtimes().size(), 2);
        assertEquals(movie.getTheaterShowtimes().get(0).getShowtimes().size(), 1);
        assertEquals(movie.getTheaterShowtimes().get(1).getShowtimes().size(), 1);

        movieTheaterList.get(1).getMovies().get(0).setShowtimes(getMockShowtimes(2));
        movie = getMockMovie(0);
        MovieUtils.populateTheaterShowtimes(movie, movieTheaterList);

        assertEquals(movie.getTheaterShowtimes().size(), 2);
        assertEquals(movie.getTheaterShowtimes().get(0).getShowtimes().size(), 1);
        assertEquals(movie.getTheaterShowtimes().get(1).getShowtimes().size(), 2);
    }

    private List<Movie> getMockMovieList(int numMovies) {
        List<Movie> mockMovieList = new ArrayList<>();
        for(int i = 0; i < numMovies; i++) {
            mockMovieList.add(getMockMovie(i));
        }
        return mockMovieList;
    }

    private Movie getMockMovie(Integer id) {
        Movie mockMovie = new Movie();
        mockMovie.setTitle(mockMovieTitle);
        mockMovie.setFandangoId(mockFandangoId);
        mockMovie.setShowtimes(movieShowtimes);
        if(id != null) {
            mockMovie.setId(id);
        }
        return mockMovie;
    }

    private List<MovieShowtime> getMockShowtimes(int numShowtimes) {
        List<MovieShowtime> mockMovieShowtimes = new ArrayList<>();
        for(int i = 0; i < numShowtimes; i++) {
            Date date = LocalDateTime.now().plusHours(1).toDate();
            MovieShowtime movieShowtime = new MovieShowtime();
            movieShowtime.setMovieShowtime(date);
            mockMovieShowtimes.add(movieShowtime);
        }

        return mockMovieShowtimes;
    }
}
