package cinespire.ui;

import cinespire.db.WatchlistDAO;
import cinespire.model.WatchlistEntry;

import javax.swing.*;
import java.awt.*;

public class ReviewDialog extends JDialog {

    private boolean saved = false;

    public ReviewDialog(Frame parent, WatchlistEntry entry) {
        super(parent, "Mark as Watched — " + entry.getTitle(), true);
        setSize(460, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_CARD);
        setLayout(new BorderLayout());
        buildUI(entry);
    }

    private void buildUI(WatchlistEntry entry) {
      
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        header.setBackground(UITheme.BG_DARK);
        JLabel title = new JLabel(entry.getTitle());
        title.setFont(UITheme.FONT_HEADING); title.setForeground(UITheme.ACCENT);
        JLabel year = UITheme.mutedLabel("(" + entry.getReleaseYear() + ")  •  " + entry.getGenre());
        header.add(title); header.add(year);
        add(header, BorderLayout.NORTH);

      
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));

        JLabel lRating = UITheme.bodyLabel("Your Rating");
        lRating.setAlignmentX(LEFT_ALIGNMENT);

        String[] stars = {"* 1 — Poor", "** 2 — Fair", "*** 3 — Good",
                          "**** 4 — Great", "***** 5 — Masterpiece"};
        JComboBox<String> cbRating = new JComboBox<>(stars);
        cbRating.setBackground(UITheme.BG_INPUT); cbRating.setForeground(UITheme.BG_DARK);
        cbRating.setFont(UITheme.FONT_BODY); cbRating.setAlignmentX(LEFT_ALIGNMENT);
        cbRating.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
     
        if (entry.getUserRating() > 0) cbRating.setSelectedIndex(entry.getUserRating() - 1);

        JLabel lReview = UITheme.bodyLabel("Your Review  (optional)");
        lReview.setAlignmentX(LEFT_ALIGNMENT);

        JTextArea taReview = UITheme.styledTextArea(5, 30);
        taReview.setAlignmentX(LEFT_ALIGNMENT);
        if (!entry.getUserReview().isEmpty()) taReview.setText(entry.getUserReview());
        JScrollPane spReview = UITheme.darkScroll(taReview);
        spReview.setAlignmentX(LEFT_ALIGNMENT);
        spReview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        form.add(lRating);
        form.add(Box.createVerticalStrut(6));
        form.add(cbRating);
        form.add(Box.createVerticalStrut(14));
        form.add(lReview);
        form.add(Box.createVerticalStrut(6));
        form.add(spReview);
        add(form, BorderLayout.CENTER);

       
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        btns.setBackground(UITheme.BG_CARD);
        JButton btnCancel = UITheme.ghostButton("Cancel");
        JButton btnSave   = UITheme.accentButton("Save & Mark Watched");
        btnSave.setPreferredSize(new Dimension(200, 36));
        btns.add(btnCancel); btns.add(btnSave);
        add(btns, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            int rating = cbRating.getSelectedIndex() + 1;
            String review = taReview.getText().trim();
            boolean ok = WatchlistDAO.markWatched(entry.getWatchlistId(), rating, review);
            if (ok) {
                saved = true;
                dispose();
            } else {
                UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.background", new Color(220, 220, 220));
                JOptionPane.showMessageDialog(this, "Failed to save. Check DB connection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public boolean isSaved() { return saved; }
}
