package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;

public class PowerUp {
    public enum PowerUpType {
        MUSHROOM,
        FIRE_FLOWER
    }
    
    GamePanel gp;
    public int worldX, worldY;
    public Rectangle collisionBounds;
    private BufferedImage powerUpImage;
    public PowerUpType type;
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

    public PowerUp(GamePanel gp, int startX, int startY, PowerUpType type) {
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.type = type;
        this.spawnY = startY - gp.tileSize;
        this.collisionBounds = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        loadImage();
    }
    
    // Constructor legacy para compatibilidad (hongo por defecto)
    public PowerUp(GamePanel gp, int startX, int startY) {
        this(gp, startX, startY, PowerUpType.MUSHROOM);
    }

    private void loadImage() {
        try {
            // Cargar imagen según el tipo
            String imagePath = (type == PowerUpType.MUSHROOM) ? "/res/hongo.png" : "/res/flor.png";
            powerUpImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
            if (powerUpImage == null) {
                System.err.println("Advertencia: " + imagePath + " no encontrado - usando placeholder");
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo cargar imagen de power-up - usando placeholder");
            powerUpImage = null;
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

        // Fire Flower NO se mueve (como en el Mario original)
        if (type == PowerUpType.FIRE_FLOWER) {
            // Solo aplicar gravedad si no está en el suelo
            if (!checkCollision("down")) {
                applyGravity();
            }
            updateCollisionBounds();
            return;
        }

        // Hongo SÍ se mueve
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

        if (powerUpImage != null) {
            g2.drawImage(powerUpImage, worldX - cameraX, worldY - cameraY, gp.tileSize, gp.tileSize, null);
        } else {
            // Placeholder rojo para hongo, naranja para flor
            g2.setColor(type == PowerUpType.MUSHROOM ? java.awt.Color.RED : java.awt.Color.ORANGE);
            g2.fillRect(worldX - cameraX, worldY - cameraY, gp.tileSize, gp.tileSize);
            // Dibujar "F" para Fire Flower
            if (type == PowerUpType.FIRE_FLOWER) {
                g2.setColor(java.awt.Color.WHITE);
                g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
                g2.drawString("F", worldX - cameraX + 12, worldY - cameraY + 24);
            }
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