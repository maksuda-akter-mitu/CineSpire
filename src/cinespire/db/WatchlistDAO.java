package cinespire.db;

import cinespire.model.WatchlistEntry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WatchlistDAO {

    /** Add a movie to a user's watchlist (status = 'To Watch'). */
    public static boolean addToWatchlist(int userId, int movieId) {
        // Prevent duplicates
        String check = "SELECT 1 FROM watchlists WHERE user_id=? AND movie_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(check)) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return false; // already in watchlist
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO watchlists (user_id, movie_id, status) VALUES (?,?,'To Watch')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Get all watchlist entries for a user (joined with movie data). */
    public static List<WatchlistEntry> getWatchlist(int userId) {
        List<WatchlistEntry> list = new ArrayList<>();
        String sql = """
                SELECT w.watchlist_id, w.user_id, w.movie_id,
                       m.title, m.genre, m.release_year, m.director,
                       w.status, COALESCE(w.user_rating, 0) AS user_rating,
                       COALESCE(w.user_review, '') AS user_review
                FROM watchlists w
                JOIN movies m ON w.movie_id = m.movie_id
                WHERE w.user_id = ?
                ORDER BY w.watch_date DESC
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new WatchlistEntry(
                        rs.getInt("watchlist_id"),
                        rs.getInt("user_id"),
                        rs.getInt("movie_id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("release_year"),
                        rs.getString("director"),
                        rs.getString("status"),
                        rs.getInt("user_rating"),
                        rs.getString("user_review")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Mark a movie as watched, save rating and review. */
    public static boolean markWatched(int watchlistId, int rating, String review) {
        String sql = "UPDATE watchlists SET status='Watched', user_rating=?, user_review=?, watch_date=NOW() WHERE watchlist_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, review);
            ps.setInt(3, watchlistId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Remove an entry from the watchlist. */
    public static boolean removeFromWatchlist(int watchlistId) {
        String sql = "DELETE FROM watchlists WHERE watchlist_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, watchlistId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Stats: total watched count for a user. */
    public static int countWatched(int userId) {
        String sql = "SELECT COUNT(*) FROM watchlists WHERE status='Watched' AND user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Stats: total "to watch" count for a user. */
    public static int countToWatch(int userId) {
        String sql = "SELECT COUNT(*) FROM watchlists WHERE status='To Watch' AND user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Stats: top-rated movies for a user (rating = 5). */
    public static List<String> getTopRatedMovies(int userId) {
        List<String> titles = new ArrayList<>();
        String sql = """
                SELECT m.title FROM watchlists w
                JOIN movies m ON w.movie_id = m.movie_id
                WHERE w.user_id=? AND w.user_rating=5
                ORDER BY w.watch_date DESC LIMIT 5
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) titles.add(rs.getString("title"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    /** Stats: average rating given by a user. */
    public static double getAverageRating(int userId) {
        String sql = "SELECT AVG(user_rating) FROM watchlists WHERE user_id=? AND user_rating > 0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
