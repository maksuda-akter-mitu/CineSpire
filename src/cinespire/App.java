package cinespire;

import cinespire.ui.LoginFrame;
import cinespire.ui.UITheme;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        UITheme.apply();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
