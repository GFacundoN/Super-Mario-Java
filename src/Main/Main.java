package Main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window  .setResizable(false);
        window.setTitle("Super Mario Java");

        // Cargar la imagen del icono
        try {
            ImageIcon logoIcon = new ImageIcon("/res/icon.png");
            window.setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}
