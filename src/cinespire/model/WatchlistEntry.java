package cinespire.model;

public class WatchlistEntry {
    private int    watchlistId;
    private int    userId;
    private int    movieId;
    private String title;
    private String genre;
    private int    releaseYear;
    private String director;
    private String status;
    private int    userRating;
    private String userReview;

    public WatchlistEntry(int watchlistId, int userId, int movieId,
                          String title, String genre, int releaseYear, String director,
                          String status, int userRating, String userReview) {
        this.watchlistId = watchlistId;
        this.userId      = userId;
        this.movieId     = movieId;
        this.title       = title;
        this.genre       = genre;
        this.releaseYear = releaseYear;
        this.director    = director;
        this.status      = status;
        this.userRating  = userRating;
        this.userReview  = userReview;
    }

    public int    getWatchlistId(){ return watchlistId; }
    public int    getUserId()     { return userId; }
    public int    getMovieId()    { return movieId; }
    public String getTitle()      { return title; }
    public String getGenre()      { return genre; }
    public int    getReleaseYear(){ return releaseYear; }
    public String getDirector()   { return director; }
    public String getStatus()     { return status; }
    public int    getUserRating() { return userRating; }
    public String getUserReview() { return userReview; }
}
