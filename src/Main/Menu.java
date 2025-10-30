package Main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Menu {

    private GamePanel gp;
    private String[] menuOptions = {"EMPEZAR EL JUEGO", "ELEGIR NOMBRE", "SALIR"}; // Opciones del menú
    private int selectedOption = 0; // Opción seleccionada
    private Image backgroundImage; // Imagen de fondo del menú
    private Image selectorSprite;  // Sprite del selector
    private Font gameFont;
    private boolean isChangingName = false; // Si el usuario está en el modo de cambiar nombre

    public Menu(GamePanel gp) {
        this.gp = gp;

        // Cargar recursos relacionados con el menú
        try {
            // Carga de imágenes desde los recursos
            InputStream fondoStream = getClass().getResourceAsStream("/res/fondoMenu.jpg");
            if (fondoStream == null) {
                throw new IOException("No se encontró la imagen fondoMenu.jpg");
            }
            backgroundImage = ImageIO.read(fondoStream);

            InputStream selectorStream = getClass().getResourceAsStream("/res/selector.jpg");
            if (selectorStream == null) {
                throw new IOException("No se encontró la imagen selector.jpg");
            }
            selectorSprite = ImageIO.read(selectorStream);

            // Carga de la fuente desde los recursos
            InputStream fontStream = getClass().getResourceAsStream("/res/PressStart2P.ttf");
            if (fontStream == null) {
                throw new IOException("No se encontró la fuente PressStart2P.ttf");
            }
            gameFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 24);

    } catch (IOException | FontFormatException e) {
    e.printStackTrace();
    // Fuente de fallback en caso de error
    gameFont = new Font("SansSerif", Font.PLAIN, 16);
}

    }

    public void drawMenu(Graphics2D g2) {
        // Dibujar el fondo del menú
        g2.drawImage(backgroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);

        g2.setFont(gameFont);
        g2.setColor(java.awt.Color.white);

        for (int i = 0; i < menuOptions.length; i++) {
            String option = menuOptions[i];
            int textWidth = g2.getFontMetrics().stringWidth(option);
            int x = (gp.screenWidth - textWidth) / 2;
            int y = 100 + gp.screenHeight / 2 + i * 55;

            if (i == selectedOption) {
                g2.drawImage(selectorSprite, x - 50, y - 30, 30, 30, null); // Dibujar el selector
            }

            g2.drawString(option, x, y);
        }
    }

    public void updateMenu() {
        // Navegar por el menú usando teclas arriba y abajo
        if (gp.keyH.upPressed) {
            selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
            gp.keyH.upPressed = false;
        }
        if (gp.keyH.downPressed) {
            selectedOption = (selectedOption + 1) % menuOptions.length;
            gp.keyH.downPressed = false;
        }
        // Confirmar opción con Enter
        if (gp.keyH.enterPressed) {
            selectMenuOption();
            gp.keyH.enterPressed = false;
        }
    }

    public void selectMenuOption() {
        if (selectedOption == 0) {
            if (gp.playerName.isEmpty()) {
                gp.playerName = "MARIO";
            }
            gp.startNewGame();
            gp.isInMenu = false;
        } else if (selectedOption == 1) {
            isChangingName = true; // Cambiar a modo de cambio de nombre
        } else if (selectedOption == 2) {
            System.exit(0); // Salir del juego
        }
    }

    public boolean isChangingName() {
        return isChangingName;
    }

    public void setChangingName(boolean changingName) {
        isChangingName = changingName;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isInMenu() {
        return gp.isInMenu;
    }

    public void setInMenu(boolean inMenu) {
        gp.isInMenu = inMenu;
    }
}
