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
    private BufferedImage goombaDer, goombaIzq, goombaMuerto;
    public boolean isAlive = true;
    private int animationCounter = 0;
    private int animationSpeed = 15; // Cambia la imagen cada 15 frames
    public int deathTimer = 0; // Temporizador para mostrar el sprite de muerte
    private final int deathDisplayDuration = 15; // 0.25 segundos a 60 FPS (casi imperceptible)

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
            goombaMuerto = ImageIO.read(getClass().getResourceAsStream("/res/goomba_muerto.png"));
            System.out.println("✅ Sprites de Goomba cargados correctamente");
        } catch (IOException e) {
            System.err.println("⚠️ Error al cargar sprites de Goomba");
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
            // Verificar colisión con bloques y bordes
            if (!checkTileCollision("right") && !isEdgeAhead("right")) {
                worldX += moveSpeed;
            } else {
                direction = "Left";
            }
        } else if (direction.equals("Left")) {
            // Verificar colisión con bloques y bordes
            if (!checkTileCollision("left") && !isEdgeAhead("left")) {
                worldX -= moveSpeed;
            } else {
                direction = "Right";
            }
        }
    }

    // Método para verificar si hay un borde adelante (para evitar caer al vacío)
    private boolean isEdgeAhead(String dir) {
        int checkX = worldX;
        if (dir.equals("right")) {
            checkX += gp.tileSize; // Verificar un tile adelante
        } else if (dir.equals("left")) {
            checkX -= gp.tileSize;
        }
        
        // Verificar si hay suelo debajo de la posición futura
        int checkY = worldY + gp.tileSize + 5; // Un poco más abajo
        return !checkTileCollisionAtPosition(checkX, checkY);
    }
    
    // Método para aplicar gravedad
    private void applyGravity() {
        // Si no está sobre el suelo, aumenta la velocidad de caída
        if (!isOnGround()) {
            fallSpeed += gravity;
            if (fallSpeed > maxFallSpeed) {
                fallSpeed = maxFallSpeed;
            }
            
            // Verificar colisión antes de mover
            int futureY = (int)(worldY + fallSpeed);
            if (!checkTileCollisionAtPosition(worldX, futureY + gp.tileSize)) {
                worldY += fallSpeed;  // Mover hacia abajo solo si no hay colisión
            } else {
                // Ajustar posición al suelo
                int tileRow = (futureY + gp.tileSize) / gp.tileSize;
                worldY = (tileRow * gp.tileSize) - gp.tileSize;
                fallSpeed = 0;
            }
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
        BufferedImage image;
        
        if (!isAlive) {
            // Mostrar sprite de Goomba muerto si el timer aún está activo
            if (deathTimer > 0) {
                image = goombaMuerto;
                // Dibujar el Goomba muy aplastado: 1/4 de alto (casi una línea)
                int deadHeight = gp.tileSize / 4; // Muy bajo (aplastado)
                int deadY = (int) worldY - cameraY + (gp.tileSize - deadHeight); // Ajustar para que esté en el suelo
                g2.drawImage(image, (int) worldX - cameraX, deadY, gp.tileSize, deadHeight, null);
            }
            return;  // No dibujar animación si está muerto
        }

        // Alternar entre sprites para animación
        image = (animationCounter < animationSpeed / 2) ? goombaDer : goombaIzq;

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
