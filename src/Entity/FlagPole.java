package Entity;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class FlagPole {
    private GamePanel gp;
    private int worldX, worldY;
    private int flagHeight;
    private boolean flagTouched = false;
    private int flagY;
    private int flagSpeed = 3;
    
    private final int poleWidth = 14;
    private final int poleHeight;
    private final int flagWidth = 14;
    private final int flagInitialHeight = 97;
    
    private BufferedImage[] flagSprites = new BufferedImage[5];
    private int currentFlagFrame = 0;
    private int flagAnimationCounter = 0;
    private boolean useSprites = false;
    
    public Rectangle collisionBounds;
    
    public FlagPole(GamePanel gp, int worldX, int worldY) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.poleHeight = 97 * 4;
        this.flagY = worldY + 20;
        this.collisionBounds = new Rectangle(worldX, worldY, 14, poleHeight);
        loadSprites();
    }
    
    private void loadSprites() {
        try {
            for (int i = 0; i < 5; i++) {
                flagSprites[i] = ImageIO.read(getClass().getResourceAsStream("/res/flag" + (i + 1) + ".png"));
            }
            useSprites = true;
        } catch (IOException | NullPointerException e) {
            useSprites = false;
        }
    }
    
    public void update() {
        int maxFlagY = 576 - (97 * 3) + 30;
        if (flagTouched && flagY < maxFlagY) {
            flagY += flagSpeed;
        }
        
        if (useSprites) {
            flagAnimationCounter++;
            if (flagAnimationCounter >= 15) {
                currentFlagFrame = (currentFlagFrame + 1) % 5;
                flagAnimationCounter = 0;
            }
        }
    }
    
    public void touchFlag() {
        flagTouched = true;
    }
    
    public boolean isFlagAtBottom() {
        int maxFlagY = 576 - (97 * 3) + 30;
        return flagY >= maxFlagY;
    }
    
    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        int screenX = worldX - cameraX;
        int screenY = worldY - cameraY;
        
        if (useSprites && flagSprites[0] != null) {
            int flagScreenY = flagY - cameraY;
            BufferedImage currentFlag = flagSprites[currentFlagFrame];
            
            int scaledWidth = 14 * 3;
            int scaledHeight = 97 * 3;
            
            g2.drawImage(currentFlag, screenX - 10, flagScreenY, scaledWidth, scaledHeight, null);
        } else {
            g2.setColor(new Color(200, 200, 200));
            g2.fillRect(screenX, screenY, poleWidth, poleHeight);
            
            int flagScreenY = flagY - cameraY;
            g2.setColor(Color.RED);
            g2.fillRect(screenX + poleWidth, flagScreenY, flagWidth, flagInitialHeight);
            
            g2.setColor(Color.WHITE);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i + j) % 2 == 0) {
                        g2.fillRect(
                            screenX + poleWidth + (i * flagWidth / 3), 
                            flagScreenY + (j * flagInitialHeight / 3),
                            flagWidth / 3,
                            flagInitialHeight / 3
                        );
                    }
                }
            }
        }
    }
    
    public int getWorldX() {
        return worldX;
    }
    
    public int getWorldY() {
        return worldY;
    }
    
    public boolean isTouched() {
        return flagTouched;
    }
    
    public void reset() {
        flagTouched = false;
        flagY = worldY + 20;
        currentFlagFrame = 0;
        flagAnimationCounter = 0;
    }
}
