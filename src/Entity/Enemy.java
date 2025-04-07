package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;

public class Enemy extends Entity {
    GamePanel gp;
    public Rectangle collisionBounds;
    private int moveSpeed = 2;
    private BufferedImage goombaDer, goombaIzq;
    public boolean isAlive = true;
    private int animationCounter = 0;
    private int animationSpeed = 15; // Cambia la imagen cada 15 frames
    public int deathTimer = 0; // Temporizador para mostrar el sprite de muerte
    private final int deathDisplayDuration = 60; // 1 segundo a 60 FPS

    // Posición inicial
    private int initialX, initialY;

    // Gravedad
    private final double gravity = 0.5; // Fuerza de la gravedad
    private double fallSpeed = 0; // Velocidad de caída actual
    private final double maxFallSpeed = 10; // Velocidad máxima de caída (límite)

    public Enemy(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        this.worldX = startX;
        this.worldY = startY;
        this.initialX = startX;
        this.initialY = startY;
        this.direction = "Right";  // Comienza moviéndose a la derecha
        collisionBounds = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
        getEnemyImage();
    }

    public void getEnemyImage() {
        try {
            goombaDer = ImageIO.read(getClass().getResourceAsStream("/res/goomba_der.png"));
            goombaIzq = ImageIO.read(getClass().getResourceAsStream("/res/goomba_izq.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (!isAlive) {
            if (deathTimer > 0) {
                deathTimer--;  // Disminuir el temporizador de muerte
            }
            return;  // No actualizar si está muerto
        }

        updateCollisionBounds();

        // Aplicar gravedad
        applyGravity();

        // Alternar la animación del goomba
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0; // Reiniciar el contador de animación
        }

        // Movimiento del enemigo
        if (direction.equals("Right")) {
            if (!checkTileCollision("right")) {
                worldX += moveSpeed;
            } else {
                direction = "Left";
            }
        } else if (direction.equals("Left")) {
            if (!checkTileCollision("left")) {
                worldX -= moveSpeed;
            } else {
                direction = "Right";
            }
        }
    }

    // Método para aplicar gravedad
    private void applyGravity() {
        // Si no está sobre el suelo, aumenta la velocidad de caída
        if (!isOnGround()) {
            fallSpeed += gravity;
            if (fallSpeed > maxFallSpeed) {
                fallSpeed = maxFallSpeed;
            }
            worldY += fallSpeed;  // Mover hacia abajo
        } else {
            fallSpeed = 0;  // Si está en el suelo, detener la caída
        }
    }

    // Método para verificar si el Goomba está sobre un bloque con colisión
    private boolean isOnGround() {
        // Revisar si hay colisión en el bloque justo debajo del Goomba
        int futureY = (int) (worldY + fallSpeed + gp.tileSize); // Revisar la siguiente posición en Y, un tile más abajo
        return checkTileCollisionAtPosition((int) worldX, futureY); // Si hay colisión, está en el suelo
    }

    // Método para verificar colisiones en una posición específica
    private boolean checkTileCollisionAtPosition(int x, int y) {
        Rectangle futureBounds = new Rectangle(x, y, gp.tileSize, gp.tileSize);

        // Obtener las coordenadas del tile
        int enemyCol = x / gp.tileSize;
        int enemyRow = y / gp.tileSize;

        for (int col = Math.max(0, enemyCol - 1); col <= Math.min(gp.tileM.mapTileNum.length - 1, enemyCol + 1); col++) {
            for (int row = Math.max(0, enemyRow - 1); row <= Math.min(gp.tileM.mapTileNum[0].length - 1, enemyRow + 1); row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        return true;  // Colisión detectada
                    }
                }
            }
        }
        return false;  // No colisión, por lo tanto, está en el aire
    }

    public boolean checkTileCollision(String direction) {
        int futureX = (int) worldX;
        int futureY = (int) worldY;

        if (direction.equals("right")) {
            futureX += moveSpeed;
        } else if (direction.equals("left")) {
            futureX -= moveSpeed;
        }

        Rectangle futureBounds = new Rectangle(futureX, futureY, gp.tileSize, gp.tileSize);

        // Verificar colisiones con los bloques del mapa
        int enemyCol = futureX / gp.tileSize;
        int enemyRow = futureY / gp.tileSize;

        for (int col = Math.max(0, enemyCol - 1); col <= Math.min(gp.tileM.mapTileNum.length - 1, enemyCol + 1); col++) {
            for (int row = Math.max(0, enemyRow - 1); row <= Math.min(gp.tileM.mapTileNum[0].length - 1, enemyRow + 1); row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        return true;  // Colisión detectada con un bloque
                    }
                }
            }
        }
        return false;
    }

    public void updateCollisionBounds() {
        collisionBounds.x = (int) worldX;
        collisionBounds.y = (int) worldY;
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {

        if (!isAlive) {
            return;  // No dibujar si el enemigo está muerto
        }

        BufferedImage image = (animationCounter < animationSpeed / 2) ? goombaDer : goombaIzq;

        g2.drawImage(image, (int) worldX - cameraX, (int) worldY - cameraY, gp.tileSize, gp.tileSize, null);
    }

    public void die() {
        isAlive = false;
        deathTimer = deathDisplayDuration; // Temporizador de muerte
    }

    public void reset() {
        this.worldX = initialX;
        this.worldY = initialY;
        this.isAlive = true;
        this.deathTimer = 0;
        this.direction = "Right";
    }
}
