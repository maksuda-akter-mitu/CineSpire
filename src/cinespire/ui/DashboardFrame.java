package cinespire.ui;

import cinespire.db.MovieDAO;
import cinespire.db.WatchlistDAO;
import cinespire.model.Movie;
import cinespire.model.User;
import cinespire.model.WatchlistEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final User user;

    // Discover tab
    private DefaultTableModel discoverModel;
    private JTable discoverTable;

    // Watchlist tab
    private DefaultTableModel watchlistModel;
    private JTable watchlistTable;

    // Stats tab
    private StatsPanel statsPanel;

    public DashboardFrame(User user) {
        this.user = user;
        setTitle("CineSpire  —  Welcome, " + user.getUsername() + "!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(760, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
    }

    // ─── Top bar ─────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x3A, 0x3A, 0x50)));

        JLabel logo = new JLabel("  CineSpire");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.ACCENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 0));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 10));
        right.setOpaque(false);
        JLabel userLbl = UITheme.mutedLabel("  " + user.getUsername());
        JButton btnLogout = UITheme.ghostButton("Log Out");
        btnLogout.setPreferredSize(new Dimension(100, 30));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        right.add(userLbl);
        right.add(btnLogout);

        bar.add(logo, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ─── Tabs ─────────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_CARD);
        tabs.setForeground(UITheme.TEXT_MUTED);
        tabs.setFont(UITheme.FONT_BTN);

        tabs.addTab("  Discover", buildDiscoverPanel());
        tabs.addTab("  My Watchlist", buildWatchlistPanel());
        tabs.addTab("  Add Movie", buildAddMoviePanel());
        tabs.addTab("  Stats", buildStatsPanel());

        // Refresh watchlist & stats when switching tabs
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 1) {
                refreshWatchlist();
            }
            if (idx == 3 && statsPanel != null) {
                statsPanel.refresh();
            }
        });

        return tabs;
    }

    // ─── Discover Panel ───────────────────────────────────────────────────────
    private JPanel buildDiscoverPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        header.add(UITheme.headerLabel("  All Movies"), BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headerRight.setOpaque(false);
        JButton btnRefresh = UITheme.ghostButton(" Refresh");
        btnRefresh.setPreferredSize(new Dimension(110, 30));
        btnRefresh.addActionListener(e -> refreshDiscover());
        JButton btnAdd = UITheme.accentButton("+ Add to Watchlist");
        btnAdd.setPreferredSize(new Dimension(170, 30));
        btnAdd.addActionListener(e -> addSelectedToWatchlist());
        headerRight.add(btnRefresh);
        headerRight.add(btnAdd);
        header.add(headerRight, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Title", "Genre", "Year", "Director"};
        discoverModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        discoverTable = new JTable(discoverModel);
        UITheme.styleTable(discoverTable);
        discoverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Column widths
        discoverTable.getColumnModel().getColumn(0).setMaxWidth(40);
        discoverTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        discoverTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        discoverTable.getColumnModel().getColumn(3).setMaxWidth(70);

        // Double-click = add to watchlist
        discoverTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSelectedToWatchlist();
                }
            }
        });

        JScrollPane sp = UITheme.darkScroll(discoverTable);
        panel.add(sp, BorderLayout.CENTER);

        JLabel hint = UITheme.mutedLabel("  Double-click a row or select + click '+ Add to Watchlist'");
        hint.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        panel.add(hint, BorderLayout.SOUTH);

        refreshDiscover();
        return panel;
    }

    private void refreshDiscover() {
        discoverModel.setRowCount(0);
        List<Movie> movies = MovieDAO.getAllMovies();
        int i = 1;
        for (Movie m : movies) {
            discoverModel.addRow(new Object[]{
                i++, m.getTitle(), m.getGenre(), m.getReleaseYear(), m.getDirector()
            });
        }
    }

    private void addSelectedToWatchlist() {
        int row = discoverTable.getSelectedRow();
        if (row < 0) {
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
            JOptionPane.showMessageDialog(this,
                    "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Get movie_id by re-querying (index in getAllMovies matches table row)
        List<Movie> movies = MovieDAO.getAllMovies();
        Movie m = movies.get(row);
        boolean ok = WatchlistDAO.addToWatchlist(user.getUserId(), m.getMovieId());
        if (ok) {
            JDialog dialog = new JDialog(this, "Added", true);
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JLabel msg = new JLabel("\"" + m.getTitle() + "\" added to your watchlist!",SwingConstants.CENTER);
            msg.setIcon(UIManager.getIcon("OptionPane.informationIcon"));

            JButton okButton = new JButton("OK");
            okButton.setFont(new Font("Arial", Font.PLAIN, 15));
            okButton.setBackground(new Color(0x6C, 0x63, 0xFF));
            okButton.setForeground(Color.BLACK);

            okButton.addActionListener(e -> dialog.dispose());

            JPanel btnPanel = new JPanel();
            btnPanel.add(okButton);

            dialog.add(msg, BorderLayout.CENTER);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } else {

            
                     JDialog dialog = new JDialog(this, "Already Added", true);
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            JLabel msg = new JLabel("\"" + m.getTitle() + "\" is already in your watchlist.",SwingConstants.CENTER);
            msg.setIcon(UIManager.getIcon("OptionPane.warningIcon"));

            JButton okButton = new JButton("OK");
            okButton.setFont(new Font("Arial", Font.PLAIN, 15));
          
            okButton.setForeground(Color.BLACK);

            okButton.addActionListener(e -> dialog.dispose());

            JPanel btnPanel = new JPanel();
            btnPanel.add(okButton);

            dialog.add(msg, BorderLayout.CENTER);
            dialog.add(btnPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);
            
            
        }
    }

    // ─── Watchlist Panel ──────────────────────────────────────────────────────
    private JPanel buildWatchlistPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        header.add(UITheme.headerLabel("  My Watchlist"), BorderLayout.WEST);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton btnMarkWatched = UITheme.accentButton(" Mark as Watched");
        btnMarkWatched.setPreferredSize(new Dimension(170, 30));
        btnMarkWatched.addActionListener(e -> markSelectedWatched());

        JButton btnRemove = UITheme.dangerButton(" Remove");
        btnRemove.setPreferredSize(new Dimension(100, 30));
        btnRemove.addActionListener(e -> removeSelectedEntry());

        JButton btnRefresh = UITheme.ghostButton(" Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(e -> refreshWatchlist());

        btnRow.add(btnRefresh);
        btnRow.add(btnRemove);
        btnRow.add(btnMarkWatched);
        header.add(btnRow, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Title", "Genre", "Year", "Director", "Status", "Rating", "Review"};
        watchlistModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        watchlistTable = new JTable(watchlistModel);
        UITheme.styleTable(watchlistTable);
        watchlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        watchlistTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        watchlistTable.getColumnModel().getColumn(2).setMaxWidth(65);
        watchlistTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        watchlistTable.getColumnModel().getColumn(5).setPreferredWidth(65);

        // Double-click = mark watched
        watchlistTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    markSelectedWatched();
                }
            }
        });

        JScrollPane sp = UITheme.darkScroll(watchlistTable);
        panel.add(sp, BorderLayout.CENTER);

        JLabel hint = UITheme.mutedLabel("  Select a movie and click ' Mark as Watched' to rate & review");
        hint.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        panel.add(hint, BorderLayout.SOUTH);

        refreshWatchlist();
        return panel;
    }

    private void refreshWatchlist() {
        if (watchlistModel == null) {
            return;
        }
        watchlistModel.setRowCount(0);
        List<WatchlistEntry> entries = WatchlistDAO.getWatchlist(user.getUserId());
        for (WatchlistEntry e : entries) {
            String ratingStr = e.getUserRating() > 0
                    ? "*".repeat(e.getUserRating())
                    : "—";
            watchlistModel.addRow(new Object[]{
                e.getTitle(), e.getGenre(), e.getReleaseYear(), e.getDirector(),
                e.getStatus(), ratingStr,
                e.getUserReview().isBlank() ? "—" : e.getUserReview()
            });
        }
    }

    private void markSelectedWatched() {
        int row = watchlistTable.getSelectedRow();
        if (row < 0) {
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
            JOptionPane.showMessageDialog(this,
                    "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<WatchlistEntry> entries = WatchlistDAO.getWatchlist(user.getUserId());
        WatchlistEntry entry = entries.get(row);

        ReviewDialog dialog = new ReviewDialog(this, entry);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshWatchlist();
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
            JOptionPane.showMessageDialog(this,
                    "\"" + entry.getTitle() + "\" marked as Watched!",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeSelectedEntry() {
        int row = watchlistTable.getSelectedRow();
        if (row < 0) {
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
            JOptionPane.showMessageDialog(this,
                    "Please select a movie first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<WatchlistEntry> entries = WatchlistDAO.getWatchlist(user.getUserId());
        WatchlistEntry entry = entries.get(row);

        UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + entry.getTitle() + "\" from your watchlist?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
       
        
        if (confirm == JOptionPane.YES_OPTION) {
            WatchlistDAO.removeFromWatchlist(entry.getWatchlistId());
            refreshWatchlist();
        }
        
    }

    // ─── Add Movie Panel ──────────────────────────────────────────────────────
    private JPanel buildAddMoviePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x50)),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));

        JLabel heading = UITheme.headerLabel("  Add a New Movie to the Database");
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lTitle = UITheme.bodyLabel("Title *");
        lTitle.setAlignmentX(LEFT_ALIGNMENT);
        JTextField tfTitle = UITheme.styledField(30);
        tfTitle.setAlignmentX(LEFT_ALIGNMENT);
        tfTitle.setMaximumSize(new Dimension(400, 36));

        JLabel lGenre = UITheme.bodyLabel("Genre");
        lGenre.setAlignmentX(LEFT_ALIGNMENT);
        String[] genres = {"Action", "Animation", "Comedy", "Crime", "Documentary",
            "Drama", "Horror", "Romance", "Sci-Fi", "Thriller", "Other"};
        JComboBox<String> cbGenre = new JComboBox<>(genres);
        cbGenre.setAlignmentX(LEFT_ALIGNMENT);
        cbGenre.setMaximumSize(new Dimension(400, 36));
        cbGenre.setBackground(UITheme.BG_INPUT);
        cbGenre.setForeground(UITheme.BG_INPUT);
        cbGenre.setFont(UITheme.FONT_BODY);

        JLabel lYear = UITheme.bodyLabel("Release Year");
        lYear.setAlignmentX(LEFT_ALIGNMENT);
        JTextField tfYear = UITheme.styledField(6);
        tfYear.setAlignmentX(LEFT_ALIGNMENT);
        tfYear.setMaximumSize(new Dimension(120, 36));

        JLabel lDir = UITheme.bodyLabel("Director");
        lDir.setAlignmentX(LEFT_ALIGNMENT);
        JTextField tfDir = UITheme.styledField(30);
        tfDir.setAlignmentX(LEFT_ALIGNMENT);
        tfDir.setMaximumSize(new Dimension(400, 36));

        JLabel lblStatus = UITheme.mutedLabel(" ");
        lblStatus.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnSave = UITheme.accentButton(" Save Movie");
        btnSave.setAlignmentX(LEFT_ALIGNMENT);
        btnSave.setMaximumSize(new Dimension(180, 38));

        btnSave.addActionListener(e -> {
            String title = tfTitle.getText().trim();
            String genre = (String) cbGenre.getSelectedItem();
            String yearTx = tfYear.getText().trim();
            String dir = tfDir.getText().trim();

            if (title.isEmpty()) {
                lblStatus.setText("Title is required.");
                lblStatus.setForeground(UITheme.DANGER);
                return;
            }
            int year = 0;
            if (!yearTx.isEmpty()) {
                try {
                    year = Integer.parseInt(yearTx);
                } catch (NumberFormatException ex) {
                    lblStatus.setText("Year must be a number.");
                    lblStatus.setForeground(UITheme.DANGER);
                    return;
                }
            }
            boolean ok = MovieDAO.addMovie(title, genre, year, dir);
            if (ok) {
                lblStatus.setText("✔  Movie added successfully!");
                lblStatus.setForeground(UITheme.SUCCESS);
                tfTitle.setText("");
                tfYear.setText("");
                tfDir.setText("");
                refreshDiscover();
            } else {
                lblStatus.setText("Failed to save. Check DB connection.");
                lblStatus.setForeground(UITheme.DANGER);
            }
        });

        card.add(heading);
        card.add(Box.createVerticalStrut(20));
        card.add(lTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(tfTitle);
        card.add(Box.createVerticalStrut(12));
        card.add(lGenre);
        card.add(Box.createVerticalStrut(6));
        card.add(cbGenre);
        card.add(Box.createVerticalStrut(12));
        card.add(lYear);
        card.add(Box.createVerticalStrut(6));
        card.add(tfYear);
        card.add(Box.createVerticalStrut(12));
        card.add(lDir);
        card.add(Box.createVerticalStrut(6));
        card.add(tfDir);
        card.add(Box.createVerticalStrut(16));
        card.add(btnSave);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);

        panel.add(card);
        return panel;
    }

    // ─── Stats Panel ──────────────────────────────────────────────────────────
    private JPanel buildStatsPanel() {
        statsPanel = new StatsPanel(user);
        return statsPanel;
    }
}
