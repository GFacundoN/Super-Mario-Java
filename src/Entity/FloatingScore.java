package Entity;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class FloatingScore {
    private GamePanel gp;
    public double worldX, worldY;
    private double velocityY;
    private int score;
    private int lifeTimer;
    private final int maxLife = 40; // ~0.6 segundos
    public boolean isActive;
    private Color color;
    
    public FloatingScore(GamePanel gp, double x, double y, int score) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.score = score;
        this.velocityY = -1.5; // Sube lentamente
        this.lifeTimer = 0;
        this.isActive = true;
        
        // Color amarillo como en el Mario original
        this.color = new Color(255, 255, 0);
    }
    
    public void update() {
        // Mover hacia arriba
        worldY += velocityY;
        
        // Incrementar timer
        lifeTimer++;
        
        // Desactivar cuando termina
        if (lifeTimer >= maxLife) {
            isActive = false;
        }
    }
    
    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (isActive) {
            int drawX = (int) worldX - cameraX;
            int drawY = (int) worldY - cameraY;
            
            // Calcular transparencia (fade out al final)
            float alpha = 1.0f;
            if (lifeTimer > maxLife - 15) {
                alpha = (maxLife - lifeTimer) / 15.0f;
            }
            
            // Aplicar transparencia
            if (alpha < 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            }
            
            // Dibujar el texto
            g2.setColor(color);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.drawString(String.valueOf(score), drawX, drawY);
            
            // Restaurar composición normal
            if (alpha < 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}
