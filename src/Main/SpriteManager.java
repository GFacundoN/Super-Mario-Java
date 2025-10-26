
package Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteManager {
    private BufferedImage spritesheet;

    // Tamaño de cada sprite en el sheet (16x16 píxeles en el original)
    private final int SPRITE_SIZE = 16;

    // Mario pequeño sprites
    public BufferedImage smallMarioIdleRight;
    public BufferedImage smallMarioIdleLeft;
    public BufferedImage smallMarioWalk1Right;
    public BufferedImage smallMarioWalk2Right;
    public BufferedImage smallMarioWalk3Right;
    public BufferedImage smallMarioWalk1Left;
    public BufferedImage smallMarioWalk2Left;
    public BufferedImage smallMarioWalk3Left;
    public BufferedImage smallMarioJumpRight;
    public BufferedImage smallMarioJumpLeft;

    // Mario grande sprites
    public BufferedImage bigMarioIdleRight;
    public BufferedImage bigMarioIdleLeft;
    public BufferedImage bigMarioWalk1Right;
    public BufferedImage bigMarioWalk2Right;
    public BufferedImage bigMarioWalk3Right;
    public BufferedImage bigMarioWalk1Left;
    public BufferedImage bigMarioWalk2Left;
    public BufferedImage bigMarioWalk3Left;
    public BufferedImage bigMarioJumpRight;
    public BufferedImage bigMarioJumpLeft;

    // Power-ups
    public BufferedImage mushroom;

    public SpriteManager() {
        loadSpritesheet();
        loadSprites();
    }

    private void loadSpritesheet() {
        try {
            // Intentar cargar el nuevo spritesheet primero
            spritesheet = ImageIO.read(getClass().getResourceAsStream("/res/mario_spritesheet.png"));
            if (spritesheet == null) {
                // Fallback al spritesheet anterior
                spritesheet = ImageIO.read(getClass().getResourceAsStream("/res/NES - Super Mario Bros. - Playable Characters - Mario & Luigi.png"));
            }
            System.out.println("Spritesheet cargado exitosamente");
        } catch (IOException e) {
            System.err.println("Error al cargar spritesheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSprites() {
        if (spritesheet == null) {
            System.err.println("No se puede cargar sprites: spritesheet es null");
            return;
        }

        // Nuevo spritesheet:
        // Fila 0-1: Mario GRANDE (Super Mario) - 16x32 píxeles (ocupa 2 filas)
        // Fila 2: Mario PEQUEÑO (Small Mario) - 16x16 píxeles
        // Sprites del medio: transición (ignorar por ahora)
        
        try {
            // Mario PEQUEÑO - Fila 2 (tercera fila) - 16x16 píxeles cada sprite
            // Y = 32 (porque Mario grande ocupa 32px de alto)
            smallMarioIdleRight = getSpriteAbsolute(0, 32, 16, 16);    // Idle
            smallMarioWalk1Right = getSpriteAbsolute(16, 32, 16, 16);  // Walk 1
            smallMarioWalk2Right = getSpriteAbsolute(32, 32, 16, 16);  // Walk 2  
            smallMarioWalk3Right = getSpriteAbsolute(48, 32, 16, 16);  // Walk 3
            smallMarioJumpRight = getSpriteAbsolute(80, 32, 16, 16);   // Jump
            
            // Voltear para obtener sprites izquierda
            smallMarioIdleLeft = flipHorizontal(smallMarioIdleRight);
            smallMarioWalk1Left = flipHorizontal(smallMarioWalk1Right);
            smallMarioWalk2Left = flipHorizontal(smallMarioWalk2Right);
            smallMarioWalk3Left = flipHorizontal(smallMarioWalk3Right);
            smallMarioJumpLeft = flipHorizontal(smallMarioJumpRight);
            
            // Mario GRANDE - Fila 0 (primera fila) - 16x32 píxeles cada sprite
            // Y = 0 (empieza en fila 0, ocupa hasta fila 1)
            bigMarioIdleRight = getSpriteAbsolute(0, 0, 16, 32);    // Idle
            bigMarioWalk1Right = getSpriteAbsolute(16, 0, 16, 32);  // Walk 1
            bigMarioWalk2Right = getSpriteAbsolute(32, 0, 16, 32);  // Walk 2
            bigMarioWalk3Right = getSpriteAbsolute(48, 0, 16, 32);  // Walk 3
            bigMarioJumpRight = getSpriteAbsolute(80, 0, 16, 32);   // Jump
            
            // Voltear para obtener sprites izquierda
            bigMarioIdleLeft = flipHorizontal(bigMarioIdleRight);
            bigMarioWalk1Left = flipHorizontal(bigMarioWalk1Right);
            bigMarioWalk2Left = flipHorizontal(bigMarioWalk2Right);
            bigMarioWalk3Left = flipHorizontal(bigMarioWalk3Right);
            bigMarioJumpLeft = flipHorizontal(bigMarioJumpRight);
            
            // Hongo - buscar en la sección de items (aproximadamente fila 6-7)
            mushroom = getSpriteAbsolute(0, 112, 16, 16); // Ajustar según ubicación real
            
            System.out.println("Sprites cargados exitosamente desde nuevo spritesheet");
        } catch (Exception e) {
            System.err.println("Error al cargar sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para obtener sprite usando coordenadas absolutas en píxeles
    private BufferedImage getSpriteAbsolute(int x, int y, int width, int height) {
        try {
            // Verificar límites
            if (x + width > spritesheet.getWidth() || y + height > spritesheet.getHeight()) {
                System.err.println("Sprite fuera de límites: (" + x + "," + y + ") tamaño (" + width + "," + height + ")");
                return createPlaceholder(width, height);
            }
            
            return spritesheet.getSubimage(x, y, width, height);
        } catch (Exception e) {
            System.err.println("Error al obtener sprite: " + e.getMessage());
            return createPlaceholder(width, height);
        }
    }

    // Obtener sprite de 16x16 desde el spritesheet
    private BufferedImage getSprite(int col, int row) {
        return getSprite(col, row, SPRITE_SIZE, SPRITE_SIZE);
    }

    // Obtener sprite con tamaño personalizado
    private BufferedImage getSprite(int col, int row, int width, int height) {
        try {
            int x = col * SPRITE_SIZE;
            int y = row * SPRITE_SIZE;

            // Verificar límites
            if (x + width > spritesheet.getWidth() || y + height > spritesheet.getHeight()) {
                System.err.println("Sprite fuera de límites: (" + x + "," + y + ") tamaño (" + width + "," + height + ")");
                return createPlaceholder(width, height);
            }

            return spritesheet.getSubimage(x, y, width, height);
        } catch (Exception e) {
            System.err.println("Error al obtener sprite: " + e.getMessage());
            return createPlaceholder(width, height);
        }
    }

    // Voltear sprite horizontalmente
    private BufferedImage flipHorizontal(BufferedImage img) {
        if (img == null) return null;

        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, img.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flipped.setRGB(width - 1 - x, y, img.getRGB(x, y));
            }
        }

        return flipped;
    }

    // Crear placeholder si falla la carga
    private BufferedImage createPlaceholder(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                placeholder.setRGB(x, y, 0xFFFF00FF); // Magenta
            }
        }
        return placeholder;
    }
}