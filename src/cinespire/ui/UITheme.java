package cinespire.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * CineSpire visual theme — dark cinema palette with amber accents.
 */
public class UITheme {

    // ── Palette ────────────────────────────────────────────────
    public static final Color BG_DARK     = new Color(0x0F, 0x0F, 0x14);
    public static final Color BG_CARD     = new Color(0x1A, 0x1A, 0x24);
    public static final Color BG_INPUT    = new Color(0x22, 0x22, 0x30);
    public static final Color ACCENT      = new Color(0xF5, 0xA6, 0x23);   // amber
    public static final Color ACCENT_DIM  = new Color(0xA8, 0x70, 0x10);
    public static final Color TEXT_MAIN   = new Color(0xF0, 0xEE, 0xE8);
    public static final Color TEXT_MUTED  = new Color(0x88, 0x88, 0x99);
    public static final Color SUCCESS     = new Color(0x4A, 0xD9, 0x95);
    public static final Color DANGER      = new Color(0xE8, 0x4A, 0x5B);
    public static final Color ROW_ALT     = new Color(0x16, 0x16, 0x20);

    // ── Fonts ──────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Georgia", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Georgia", Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  13);

    // ── Global L&F bootstrap ──────────────────────────────────
    public static void apply() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("Panel.background",          BG_DARK);
        UIManager.put("OptionPane.background",     BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_MAIN);
        UIManager.put("Button.background",         BG_INPUT);
        UIManager.put("Button.foreground",         TEXT_MAIN);
        UIManager.put("Label.foreground",          TEXT_MAIN);
        UIManager.put("TextField.background",      BG_INPUT);
        UIManager.put("TextField.foreground",      TEXT_MAIN);
        UIManager.put("TextField.caretForeground", ACCENT);
        UIManager.put("PasswordField.background",  BG_INPUT);
        UIManager.put("PasswordField.foreground",  TEXT_MAIN);
        UIManager.put("PasswordField.caretForeground", ACCENT);
        UIManager.put("TextArea.background",       BG_INPUT);
        UIManager.put("TextArea.foreground",       TEXT_MAIN);
        UIManager.put("TextArea.caretForeground",  ACCENT);
        UIManager.put("ComboBox.background",       BG_INPUT);
        UIManager.put("ComboBox.foreground",       TEXT_MAIN);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", BG_DARK);
        UIManager.put("TabbedPane.background",     BG_CARD);
        UIManager.put("TabbedPane.foreground",     TEXT_MUTED);
        UIManager.put("TabbedPane.selected",       BG_DARK);
        UIManager.put("TabbedPane.selectedForeground", ACCENT);
        UIManager.put("ScrollPane.background",     BG_DARK);
        UIManager.put("Viewport.background",       BG_DARK);
        UIManager.put("Table.background",          BG_DARK);
        UIManager.put("Table.foreground",          TEXT_MAIN);
        UIManager.put("Table.selectionBackground", new Color(0xF5, 0xA6, 0x23, 60));
        UIManager.put("Table.selectionForeground", TEXT_MAIN);
        UIManager.put("Table.gridColor",           new Color(0x2A, 0x2A, 0x38));
        UIManager.put("TableHeader.background",    BG_CARD);
        UIManager.put("TableHeader.foreground",    ACCENT);
    }

    // ── Factory helpers ────────────────────────────────────────

    public static JButton accentButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_DIM);
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT.brighter());
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BG_DARK);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setForeground(BG_DARK); b.setFont(FONT_BTN);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 36));
        return b;
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? new Color(0x2A, 0x2A, 0x38) : BG_INPUT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.setColor(TEXT_MAIN);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFont(FONT_BTN); b.setForeground(TEXT_MAIN);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 36));
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? DANGER.darker() : new Color(0x3A, 0x15, 0x18);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(DANGER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.setColor(TEXT_MAIN);
                g2.setFont(FONT_BTN);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setFont(FONT_BTN); b.setForeground(TEXT_MAIN);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 36));
        return b;
    }

    public static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(BG_INPUT); f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT); f.setFont(FONT_BODY);
        f.setBorder(fieldBorder());
        return f;
    }

    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setBackground(BG_INPUT); f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT); f.setFont(FONT_BODY);
        f.setBorder(fieldBorder());
        return f;
    }

    public static JTextArea styledTextArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setBackground(BG_INPUT); ta.setForeground(TEXT_MAIN);
        ta.setCaretColor(ACCENT); ta.setFont(FONT_BODY);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setBorder(fieldBorder());
        return ta;
    }

    public static JLabel headerLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING); l.setForeground(ACCENT);
        return l;
    }

    public static JLabel bodyLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY); l.setForeground(TEXT_MAIN);
        return l;
    }

    public static JLabel mutedLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL); l.setForeground(TEXT_MUTED);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_MAIN);
        table.setFont(FONT_BODY);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(0x2A, 0x2A, 0x38));
        table.setSelectionBackground(new Color(0xF5, 0xA6, 0x23, 50));
        table.setSelectionForeground(TEXT_MAIN);
        table.setIntercellSpacing(new Dimension(10, 2));

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD);
        header.setForeground(ACCENT);
        header.setFont(FONT_BTN);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? new Color(0xF5, 0xA6, 0x23, 40)
                                  : (row % 2 == 0 ? BG_DARK : ROW_ALT));
                setForeground(TEXT_MAIN);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setFont(FONT_BODY);
                return this;
            }
        });
    }

    public static JScrollPane darkScroll(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0x2A, 0x2A, 0x38)));
        sp.getVerticalScrollBar().setBackground(BG_CARD);
        sp.getHorizontalScrollBar().setBackground(BG_CARD);
        return sp;
    }

    private static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x50), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8));
    }
}
