package Entity;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CoinAnimation {
    private GamePanel gp;
    public double worldX, worldY;
    private double velocityY;
    private double gravity;
    private int lifeTimer;
    private final int maxLife = 40; // Duración de la animación (menos de 1 segundo)
    public boolean isActive;
    private BufferedImage coinSprite;
    
    public CoinAnimation(GamePanel gp, int x, int y) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.velocityY = -6; // Salta hacia arriba
        this.gravity = 0.3;
        this.lifeTimer = 0;
        this.isActive = true;
        
        // Cargar sprite de moneda
        try {
            coinSprite = ImageIO.read(getClass().getResourceAsStream("/res/coin.png"));
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar sprite de moneda para animación");
        }
    }
    
    public void update() {
        // Aplicar física
        velocityY += gravity;
        worldY += velocityY;
        
        // Incrementar timer
        lifeTimer++;
        
        // Desactivar cuando termina la animación
        if (lifeTimer >= maxLife) {
            isActive = false;
        }
    }
    
    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        if (coinSprite != null && isActive) {
            // Tamaño más pequeño (50% del tile)
            int coinSize = gp.tileSize / 2;
            
            // Centrar la moneda en su posición
            int drawX = (int) worldX - cameraX + (gp.tileSize - coinSize) / 2;
            int drawY = (int) worldY - cameraY + (gp.tileSize - coinSize) / 2;
            
            // Calcular transparencia (fade out al final)
            float alpha = 1.0f;
            if (lifeTimer > maxLife - 10) {
                alpha = (maxLife - lifeTimer) / 10.0f;
            }
            
            // Aplicar transparencia
            if (alpha < 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
            }
            
            g2.drawImage(coinSprite, drawX, drawY, coinSize, coinSize, null);
            
            // Restaurar composición normal
            if (alpha < 1.0f) {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}
