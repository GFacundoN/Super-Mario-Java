package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import Main.GamePanel;

public class BrickParticle {
    GamePanel gp;
    public double worldX, worldY;
    private double velocityX, velocityY;
    private final double gravity = 0.5;
    private int lifetime = 60; // 1 segundo a 60 FPS
    private int size = 8; // Tamaño de la partícula
    private Color color;
    public boolean isActive = true;

    public BrickParticle(GamePanel gp, int startX, int startY, double velX, double velY, Color color) {
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.velocityX = velX;
        this.velocityY = velY;
        this.color = color;
    }

    public void update() {
        if (!isActive) return;

        // Aplicar velocidad
        worldX += velocityX;
        worldY += velocityY;

        // Aplicar gravedad
        velocityY += gravity;

        // Reducir lifetime
        lifetime--;
        if (lifetime <= 0) {
            isActive = false;
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (!isActive) return;

        g2.setColor(color);
        g2.fillRect((int)worldX - cameraX, (int)worldY - cameraY, size, size);
    }
}