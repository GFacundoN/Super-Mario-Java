package Entity;

import Main.GamePanel;

public class BrickBump {
    GamePanel gp;
    public int col, row; // Posición del ladrillo en el mapa
    private int offsetY = 0; // Desplazamiento vertical
    private int maxOffset = -10; // Cuánto sube el ladrillo
    private int speed = 2; // Velocidad de la animación
    private boolean movingUp = true;
    public boolean isActive = true;

    public BrickBump(GamePanel gp, int col, int row) {
        this.gp = gp;
        this.col = col;
        this.row = row;
    }

    public void update() {
        if (!isActive) return;

        if (movingUp) {
            offsetY -= speed;
            System.out.println("⬆️ Bump (" + col + ", " + row + ") subiendo - offsetY: " + offsetY);
            if (offsetY <= maxOffset) {
                movingUp = false;
                System.out.println("🔄 Bump alcanzó máximo, ahora baja");
            }
        } else {
            offsetY += speed;
            System.out.println("⬇️ Bump (" + col + ", " + row + ") bajando - offsetY: " + offsetY);
            if (offsetY >= 0) {
                offsetY = 0;
                isActive = false; // Animación terminada
                System.out.println("✅ Bump terminado");
            }
        }
    }

    public int getOffsetY() {
        return offsetY;
    }
}