package cinespire.ui;

import cinespire.db.WatchlistDAO;
import cinespire.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StatsPanel extends JPanel {

    private final User user;

   
    private final JLabel lblWatched  = makeStatNum("—");
    private final JLabel lblToWatch  = makeStatNum("—");
    private final JLabel lblAvgRating = makeStatNum("—");
    private final JPanel topRatedBox;

    public StatsPanel(User user) {
        this.user = user;
        setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(UITheme.headerLabel("  Your Cinema Stats"), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(UITheme.BG_DARK);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

       
        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(UITheme.BG_DARK);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cards.add(statCard("  Watched",       lblWatched,  UITheme.SUCCESS));
        cards.add(statCard("  To Watch",       lblToWatch,  UITheme.ACCENT));
        cards.add(statCard("  Avg. Rating",    lblAvgRating, new Color(0x8B, 0xC4, 0xFF)));
        content.add(cards);
        content.add(Box.createVerticalStrut(24));

       
        JLabel lTop = UITheme.headerLabel("  Your 5-Star Picks");
        lTop.setAlignmentX(LEFT_ALIGNMENT);
        content.add(lTop);
        content.add(Box.createVerticalStrut(10));

        topRatedBox = new JPanel();
        topRatedBox.setBackground(UITheme.BG_CARD);
        topRatedBox.setLayout(new BoxLayout(topRatedBox, BoxLayout.Y_AXIS));
        topRatedBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x50)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        topRatedBox.setAlignmentX(LEFT_ALIGNMENT);

        JScrollPane spTop = UITheme.darkScroll(topRatedBox);
        spTop.setAlignmentX(LEFT_ALIGNMENT);
        spTop.setPreferredSize(new Dimension(0, 160));
        content.add(spTop);

       
        content.add(Box.createVerticalStrut(16));
        JButton btnRefresh = UITheme.ghostButton("↻  Refresh Stats");
        btnRefresh.setAlignmentX(LEFT_ALIGNMENT);
        btnRefresh.addActionListener(e -> refresh());
        content.add(btnRefresh);

        add(content, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        int watched   = WatchlistDAO.countWatched(user.getUserId());
        int toWatch   = WatchlistDAO.countToWatch(user.getUserId());
        double avg    = WatchlistDAO.getAverageRating(user.getUserId());
        List<String> top = WatchlistDAO.getTopRatedMovies(user.getUserId());

        lblWatched.setText(String.valueOf(watched));
        lblToWatch.setText(String.valueOf(toWatch));
        lblAvgRating.setText(avg > 0 ? String.format("%.1f / 5", avg) : "N/A");

        topRatedBox.removeAll();
        if (top.isEmpty()) {
            JLabel none = UITheme.mutedLabel("No 5-star movies yet. Start rating!");
            none.setAlignmentX(LEFT_ALIGNMENT);
            topRatedBox.add(none);
        } else {
            for (int i = 0; i < top.size(); i++) {
                JLabel lbl = UITheme.bodyLabel((i + 1) + ".  ⭐⭐⭐⭐⭐  " + top.get(i));
                lbl.setAlignmentX(LEFT_ALIGNMENT);
                topRatedBox.add(lbl);
                if (i < top.size() - 1) topRatedBox.add(Box.createVerticalStrut(6));
            }
        }
        topRatedBox.revalidate();
        topRatedBox.repaint();
    }

   

    private JPanel statCard(String title, JLabel numLabel, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x50)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UITheme.FONT_SMALL); lTitle.setForeground(UITheme.TEXT_MUTED);
        lTitle.setAlignmentX(LEFT_ALIGNMENT);

        numLabel.setForeground(accent);
        numLabel.setAlignmentX(LEFT_ALIGNMENT);

        p.add(lTitle);
        p.add(Box.createVerticalStrut(8));
        p.add(numLabel);
        return p;
    }

    private static JLabel makeStatNum(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Georgia", Font.BOLD, 30));
        return l;
    }
}
