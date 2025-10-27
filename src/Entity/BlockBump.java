package Entity;

import Main.GamePanel;

public class BlockBump {
    private GamePanel gp;
    public int tileCol, tileRow;
    private double offsetY; // Desplazamiento vertical del bloque
    private double velocityY;
    private int animationTimer;
    private final int maxAnimation = 15; // Duración de la animación
    public boolean isActive;
    
    public BlockBump(GamePanel gp, int tileCol, int tileRow) {
        this.gp = gp;
        this.tileCol = tileCol;
        this.tileRow = tileRow;
        this.offsetY = 0;
        this.velocityY = -3; // Velocidad inicial hacia arriba
        this.animationTimer = 0;
        this.isActive = true;
    }
    
    public void update() {
        // Mover el bloque hacia arriba y luego hacia abajo
        offsetY += velocityY;
        velocityY += 0.5; // Gravedad suave
        
        animationTimer++;
        
        // Cuando el bloque vuelve a su posición original
        if (offsetY >= 0) {
            offsetY = 0;
            isActive = false;
        }
        
        // Timeout por si acaso
        if (animationTimer >= maxAnimation) {
            offsetY = 0;
            isActive = false;
        }
    }
    
    public double getOffsetY() {
        return offsetY;
    }
}
