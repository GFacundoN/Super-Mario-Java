package Entity;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
    
    public Fireball(GamePanel gp, double x, double y, boolean facingRight) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.velocityX = facingRight ? 8 : -8; // Velocidad horizontal
        this.velocityY = -3; // Pequeño impulso hacia arriba
        this.collisionBounds = new Rectangle((int)x, (int)y, gp.tileSize / 2, gp.tileSize / 2);
    }
    
    public void update() {
        // Aplicar física
        velocityY += gravity;
        worldX += velocityX;
        worldY += velocityY;
        
        // Actualizar bounds
        collisionBounds.x = (int) worldX;
        collisionBounds.y = (int) worldY;
        
        // Verificar colisión con el suelo para rebotar
        if (checkGroundCollision()) {
            velocityY = -4; // Rebote
            bounceCount++;
            
            if (bounceCount >= maxBounces) {
                isActive = false; // Desaparecer después de 3 rebotes
            }
        }
        
        // Verificar colisión con paredes
        if (checkWallCollision()) {
            isActive = false;
        }
        
        // Incrementar lifetime
        lifetime++;
        if (lifetime >= maxLifetime) {
            isActive = false;
        }
        
        // Animación
        animationCounter++;
        if (animationCounter > 4) {
            animationFrame = (animationFrame + 1) % 4;
            animationCounter = 0;
        }
        
        // Desactivar si sale de la pantalla
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
            
            // Dibujar fireball (círculo rojo/naranja animado)
            Color color1 = new Color(255, 100, 0); // Naranja
            Color color2 = new Color(255, 200, 0); // Amarillo
            
            // Alternar colores para efecto de fuego
            Color currentColor = (animationFrame % 2 == 0) ? color1 : color2;
            
            g2.setColor(currentColor);
            g2.fillOval(drawX, drawY, size, size);
            
            // Agregar brillo en el centro
            g2.setColor(Color.WHITE);
            g2.fillOval(drawX + size/4, drawY + size/4, size/2, size/2);
        }
    }
    
    public boolean checkEnemyCollision(Enemy enemy) {
        if (isActive && enemy.isAlive) {
            return collisionBounds.intersects(enemy.collisionBounds);
        }
        return false;
    }
}
