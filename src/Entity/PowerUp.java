package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;

public class PowerUp {
    GamePanel gp;
    public int worldX, worldY;
    public Rectangle collisionBounds;
    private BufferedImage mushroomImage;
    public boolean isActive = true;
    public boolean isCollected = false;

    // Movimiento
    private int moveSpeed = 2;
    private String direction = "Right";

    // Física
    private double velocityY = 0;
    private final double gravity = 0.26;
    private final double maxFallSpeed = 8.0;

    // Animación de spawn
    private boolean isSpawning = true;
    private int spawnY;
    private int spawnSpeed = 1;

    public PowerUp(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.spawnY = startY - gp.tileSize;
        this.collisionBounds = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        loadImage();
    }

    private void loadImage() {
        try {
            // Cargar imagen individual directamente
            mushroomImage = ImageIO.read(getClass().getResourceAsStream("/res/hongo.png"));
            if (mushroomImage == null) {
                System.err.println("Advertencia: hongo.png no encontrado - usando placeholder rojo");
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo cargar hongo.png - usando placeholder rojo");
            mushroomImage = null;
        }
    }

    public void update() {
        if (!isActive || isCollected) return;

        if (isSpawning) {
            worldY -= spawnSpeed;
            if (worldY <= spawnY) {
                worldY = spawnY;
                isSpawning = false;
            }
            updateCollisionBounds();
            return;
        }

        if (direction.equals("Right")) {
            if (!checkCollision("right")) {
                worldX += moveSpeed;
            } else {
                direction = "Left";
            }
        } else if (direction.equals("Left")) {
            if (!checkCollision("left")) {
                worldX -= moveSpeed;
            } else {
                direction = "Right";
            }
        }

        applyGravity();
        updateCollisionBounds();
    }

    private void applyGravity() {
        velocityY += gravity;
        if (velocityY > maxFallSpeed) {
            velocityY = maxFallSpeed;
        }

        if (!checkCollision("down")) {
            worldY += velocityY;
        } else {
            velocityY = 0;
        }
    }

    private boolean checkCollision(String dir) {
        int futureX = worldX;
        int futureY = worldY;

        if (dir.equals("right")) {
            futureX += moveSpeed;
        } else if (dir.equals("left")) {
            futureX -= moveSpeed;
        } else if (dir.equals("down")) {
            futureY += (int)Math.ceil(velocityY) + 1;
        }

        Rectangle futureBounds = new Rectangle(futureX, futureY, gp.tileSize, gp.tileSize);
        int col = futureX / gp.tileSize;
        int row = futureY / gp.tileSize;

        for (int c = Math.max(0, col - 1); c <= Math.min(gp.maxWorldCol - 1, col + 1); c++) {
            for (int r = Math.max(0, row - 1); r <= Math.min(gp.maxWorldRow - 1, row + 1); r++) {
                int tileNum = gp.tileM.mapTileNum[c][r];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(c * gp.tileSize, r * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void updateCollisionBounds() {
        collisionBounds.x = worldX;
        collisionBounds.y = worldY;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (!isActive || isCollected) return;

        if (mushroomImage != null) {
            g2.drawImage(mushroomImage, worldX - cameraX, worldY - cameraY, gp.tileSize, gp.tileSize, null);
        } else {
            // Placeholder rojo si no hay imagen
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(worldX - cameraX, worldY - cameraY, gp.tileSize, gp.tileSize);
        }
    }

    public void collect() {
        isCollected = true;
        isActive = false;
    }
    
    // Método para verificar si está spawneando
    public boolean isSpawning() {
        return isSpawning;
    }
}