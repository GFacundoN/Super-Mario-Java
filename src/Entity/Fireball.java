package Entity;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Fireball {
    private GamePanel gp;
    public double worldX, worldY;
    private double velocityX;
    private double velocityY;
    private double gravity = 0.3;
    private int bounceCount = 0;
    private final int maxBounces = 3;
    public boolean isActive = true;
    public Rectangle collisionBounds;
    private int lifetime = 0;
    private final int maxLifetime = 180; // 3 segundos
    private int animationFrame = 0;
    private int animationCounter = 0;
    private BufferedImage[] fireballSprites = new BufferedImage[4];
    private int currentFrame = 0;
    
    public Fireball(GamePanel gp, double x, double y, boolean facingRight) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.velocityX = facingRight ? 8 : -8;
        this.velocityY = -3;
        this.collisionBounds = new Rectangle((int)x, (int)y, gp.tileSize / 2, gp.tileSize / 2);
        
        // Cargar sprites de fireball (4 frames para animación)
        try {
            fireballSprites[0] = ImageIO.read(getClass().getResourceAsStream("/res/fireball.png"));
            fireballSprites[1] = ImageIO.read(getClass().getResourceAsStream("/res/fireball2.png"));
            fireballSprites[2] = ImageIO.read(getClass().getResourceAsStream("/res/fireball3.png"));
            fireballSprites[3] = ImageIO.read(getClass().getResourceAsStream("/res/fireball4.png"));
        } catch (Exception e) {
            System.err.println("⚠️ No se pudieron cargar sprites de fireball");
            fireballSprites[0] = null;
        }
    }
    
    public void update() {
        velocityY += gravity;
        worldX += velocityX;
        worldY += velocityY;
        
        collisionBounds.x = (int) worldX;
        collisionBounds.y = (int) worldY;
        
        if (checkGroundCollision()) {
            velocityY = -4; // Rebote
            bounceCount++;
            
            if (bounceCount >= maxBounces) {
                isActive = false;
            }
        }
        
        if (checkWallCollision()) {
            isActive = false;
        }
        
        lifetime++;
        if (lifetime >= maxLifetime) {
            isActive = false;
        }
        
        animationCounter++;
        if (animationCounter > 2) {
            currentFrame = (currentFrame + 1) % 4;
            animationCounter = 0;
        }
        
        if (worldY > gp.screenHeight || worldX < 0 || worldX > gp.maxWorldCol * gp.tileSize) {
            isActive = false;
        }
    }
    
    private boolean checkGroundCollision() {
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = ((int) worldY + gp.tileSize / 2) / gp.tileSize;
        
        if (playerRow >= 0 && playerRow < gp.tileM.mapTileNum[0].length &&
            playerCol >= 0 && playerCol < gp.tileM.mapTileNum.length) {
            
            int tileNum = gp.tileM.mapTileNum[playerCol][playerRow];
            if (gp.tileM.tile[tileNum].collision && velocityY > 0) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkWallCollision() {
        int playerCol = ((int) worldX + (velocityX > 0 ? gp.tileSize / 2 : 0)) / gp.tileSize;
        int playerRow = (int) worldY / gp.tileSize;
        
        if (playerRow >= 0 && playerRow < gp.tileM.mapTileNum[0].length &&
            playerCol >= 0 && playerCol < gp.tileM.mapTileNum.length) {
            
            int tileNum = gp.tileM.mapTileNum[playerCol][playerRow];
            return gp.tileM.tile[tileNum].collision;
        }
        return false;
    }
    
    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (isActive) {
            int drawX = (int) worldX - cameraX;
            int drawY = (int) worldY - cameraY;
            int size = gp.tileSize / 2;
            
            // Usar sprite animado si está cargado
            if (fireballSprites[0] != null) {
                g2.drawImage(fireballSprites[currentFrame], drawX, drawY, size, size, null);
            } else {
                Color color1 = new Color(255, 100, 0);
                Color color2 = new Color(255, 200, 0);
                
                Color currentColor = (animationFrame % 2 == 0) ? color1 : color2;
                
                g2.setColor(currentColor);
                g2.fillOval(drawX, drawY, size, size);
                
                g2.setColor(Color.WHITE);
                g2.fillOval(drawX + size/4, drawY + size/4, size/2, size/2);
            }
        }
    }
    
    public boolean checkEnemyCollision(Enemy enemy) {
        if (isActive && enemy.isAlive) {
            return collisionBounds.intersects(enemy.collisionBounds);
        }
        return false;
    }
}
