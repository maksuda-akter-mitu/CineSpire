package cinespire.model;

public class Movie {
    private int    movieId;
    private String title;
    private String genre;
    private int    releaseYear;
    private String director;

    public Movie(int movieId, String title, String genre, int releaseYear, String director) {
        this.movieId     = movieId;
        this.title       = title;
        this.genre       = genre;
        this.releaseYear = releaseYear;
        this.director    = director;
    }

    public int    getMovieId()    { return movieId; }
    public String getTitle()      { return title; }
    public String getGenre()      { return genre; }
    public int    getReleaseYear(){ return releaseYear; }
    public String getDirector()   { return director; }

    @Override
    public String toString() { return title; }
}
