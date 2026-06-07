package cinespire.ui;

import cinespire.db.UserDAO;
import cinespire.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private final JTextField     tfUsername = UITheme.styledField(20);
    private final JPasswordField pfPassword = UITheme.styledPasswordField(20);
    private final JLabel         lblStatus  = UITheme.mutedLabel(" ");

    public LoginFrame() {
        setTitle("CineSpire — Sign In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(440, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildPanel(), BorderLayout.CENTER);
    }

    private JPanel buildPanel() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x3A, 0x50)),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)));

        // Logo / title
        JLabel logo = new JLabel("  CineSpire");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = UITheme.mutedLabel("Your personal cinema companion");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form
        JLabel lUser = UITheme.bodyLabel("Username");
        lUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfUsername.setMaximumSize(new Dimension(340, 38));

        JLabel lPass = UITheme.bodyLabel("Password");
        lPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        pfPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        pfPassword.setMaximumSize(new Dimension(340, 38));

        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setForeground(UITheme.DANGER);

        JButton btnLogin    = UITheme.accentButton("Sign In");
        JButton btnRegister = UITheme.ghostButton("Create Account");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(340, 38));
        btnRegister.setMaximumSize(new Dimension(340, 38));

        btnLogin.addActionListener(this::doLogin);
        btnRegister.addActionListener(this::doRegister);

        // Allow Enter key on password field to trigger login
        pfPassword.addActionListener(this::doLogin);
        tfUsername.addActionListener(e -> pfPassword.requestFocus());

        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(lUser);
        card.add(Box.createVerticalStrut(6));
        card.add(tfUsername);
        card.add(Box.createVerticalStrut(14));
        card.add(lPass);
        card.add(Box.createVerticalStrut(6));
        card.add(pfPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(18));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRegister);

        root.add(card);
        return root;
    }

    private void doLogin(ActionEvent e) {
        String user = tfUsername.getText().trim();
        String pass = new String(pfPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            setStatus("Please fill in all fields.", false);
            return;
        }
        User u = UserDAO.login(user, pass);
        if (u != null) {
            lblStatus.setText(" ");
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardFrame(u).setVisible(true));
        } else {
            setStatus("Invalid username or password.", false);
        }
    }

    private void doRegister(ActionEvent e) {
        String user = tfUsername.getText().trim();
        String pass = new String(pfPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            setStatus("Please fill in all fields.", false);
            return;
        }
        if (user.length() < 3) {
            setStatus("Username must be at least 3 characters.", false);
            return;
        }
        if (pass.length() < 4) {
            setStatus("Password must be at least 4 characters.", false);
            return;
        }
        boolean ok = UserDAO.register(user, pass);
        if (ok) {
            setStatus("Account created! You can now sign in.", true);
        } else {
            setStatus("Username already taken. Try another.", false);
        }
    }

    private void setStatus(String msg, boolean success) {
        lblStatus.setText(msg);
        lblStatus.setForeground(success ? UITheme.SUCCESS : UITheme.DANGER);
    }
}
