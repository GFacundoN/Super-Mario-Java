package Entity;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Castle {
    private GamePanel gp;
    public int worldX, worldY;
    private BufferedImage castleSprite;
    private int width, height;
    
    public Castle(GamePanel gp, int x, int y) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        
        try {
            castleSprite = ImageIO.read(getClass().getResourceAsStream("/res/castillo.png"));
            if (castleSprite != null) {
                this.width = gp.tileSize * 6;
                this.height = gp.tileSize * 6;
            }
        } catch (Exception e) {
            System.err.println("⚠️ No se pudo cargar castillo.png");
            castleSprite = null;
            this.width = gp.tileSize * 4;
            this.height = gp.tileSize * 5;
        }
    }
    
    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        int drawX = worldX - cameraX;
        int drawY = worldY - cameraY;
        
        if (castleSprite != null) {
            g2.drawImage(castleSprite, drawX, drawY, width, height, null);
        } else {
            g2.setColor(java.awt.Color.GRAY);
            g2.fillRect(drawX, drawY, width, height);
            g2.setColor(java.awt.Color.BLACK);
            g2.drawRect(drawX, drawY, width, height);
        }
    }
}
