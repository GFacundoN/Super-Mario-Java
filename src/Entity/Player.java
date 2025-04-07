// Player.java
package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;
import Main.KeyHandler;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    // Variables para el salto
    boolean jumping = false;
    boolean falling = false;
    double velocityY = 0;
    double jumpSpeed = -10; // Velocidad inicial del salto
    double gravity = 0.26; // Gravedad que afecta al salto
    boolean onPlatform = false; // Indica si está sobre una plataforma

    // Velocidades
    private final int normalSpeed = 6;
    private final int sprintSpeed = 10;

    // Área de colisión
    public Rectangle collisionBounds;
    
 // Estado de muerte
    private boolean isDead = false;

    
    


    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
        collisionBounds = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize); // Ajusta la colisión
    }

    public void setDefaultValues() {
        worldX = 120;
        worldY = 576; // Posicionar al jugador en el suelo
        speed = 4;
        direction = "StartRight";
        jumping = false;
        falling = false;
        velocityY = 0;
        onPlatform = false;
        isDead = false; // Reiniciar estado de muerte
    }

    public void getPlayerImage() {
        try {
            StartRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_start.png"));
            StartLeft = ImageIO.read(getClass().getResourceAsStream("/res/StartLeft.png"));
            Der1 = ImageIO.read(getClass().getResourceAsStream("/res/Der1.png"));
            Der2 = ImageIO.read(getClass().getResourceAsStream("/res/Der2.png"));
            Der3 = ImageIO.read(getClass().getResourceAsStream("/res/Der3.png"));
            Left1 = ImageIO.read(getClass().getResourceAsStream("/res/Izq1.png"));
            Left2 = ImageIO.read(getClass().getResourceAsStream("/res/Izq2.png"));
            Left3 = ImageIO.read(getClass().getResourceAsStream("/res/Izq3.png"));
            JumpD = ImageIO.read(getClass().getResourceAsStream("/res/JumpD.png"));
            JumpL = ImageIO.read(getClass().getResourceAsStream("/res/JumpL.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateCollisionBounds() {
        collisionBounds.x = (int) worldX + 10;
        collisionBounds.y = (int) worldY;
    }

    public boolean checkCollision(String direction) {
        int futureX = (int) worldX;
        int futureY = (int) worldY;

        if (direction.equals("right")) {
            futureX += speed;
        } else if (direction.equals("left")) {
            futureX -= speed;
        } else if (direction.equals("down")) {
            futureY += gravity;
        } else if (direction.equals("up")) {
            futureY -= speed; // Movemos hacia arriba al verificar colisión arriba
        }

        Rectangle futureBounds = new Rectangle(futureX + 10, futureY, gp.tileSize - 20, gp.tileSize);

        // Limitar la búsqueda de tiles en torno al jugador para mejorar el rendimiento
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = (int) worldY / gp.tileSize;

        for (int col = Math.max(0, playerCol - 1); col <= Math.min(gp.tileM.mapTileNum.length - 1, playerCol + 1); col++) {
            for (int row = Math.max(0, playerRow - 1); row <= Math.min(gp.tileM.mapTileNum[0].length - 1, playerRow + 1); row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        // Si colisiona por abajo
                        if (direction.equals("down") && (futureY + gp.tileSize > tileBounds.y) && (worldY < tileBounds.y)) {
                            onPlatform = true;
                            worldY = tileBounds.y - gp.tileSize; // Ajustar la posición Y al estar sobre una plataforma
                            velocityY = 0; // Reiniciar la velocidad vertical al colisionar
                            return true;
                        }
                        // Si colisiona por arriba
                        if (direction.equals("up")) {
                            // Solo si el jugador está dentro del área de colisión del bloque
                            if (futureY + gp.tileSize > tileBounds.y && futureY < tileBounds.y + tileBounds.height) {
                                worldY = tileBounds.y + tileBounds.height; // Ajustar Y para no "escalar"
                                velocityY = 0; // Reiniciar velocidad vertical
                                return true; // Se detecta colisión arriba
                            }
                        }
                        return true; // Colisión normal
                    }
                }
            }
        }
        return false;
    }

    public void update() {
        // Actualizar el área de colisión
        updateCollisionBounds();

        checkEnemyCollision();

        if (keyH.rightPressed) {
            direction = "Right";
            if (!checkCollision("right")) {
                worldX += speed;
            }
        } else if (keyH.leftPressed) {
            direction = "Left";
            if (!checkCollision("left")) {
                worldX -= speed;
            }
        } else {
            if (direction.equals("Right") || direction.equals("JumpD")) {
                direction = "StartRight";
            } else if (direction.equals("Left") || direction.equals("JumpL")) {
                direction = "StartLeft";
            }
        }

        // Ajuste de velocidad si se presiona Shift
        speed = keyH.shiftPressed ? sprintSpeed : normalSpeed;

        if (!isDead) {
            // Saltar solo si está en el suelo o en una plataforma
            if (keyH.upPressed && !jumping && !falling && (onPlatform || !isAirBelow())) {
                jumping = true;
                falling = false;
                velocityY = jumpSpeed;
                onPlatform = false; // Desactivar onPlatform al saltar
            }

            // Lógica de salto y caída
            if (jumping) {
                worldY += velocityY; // Aplicar el salto
                velocityY += gravity; // Aplicar gravedad

                // Verificar si colisiona con el techo
                if (checkCollision("up")) {
                    velocityY = 0; // Detener el salto al colisionar con el techo
                    jumping = false;
                    falling = true; // Comenzar a caer
                }

                // Cambiar la animación durante el salto
                if (direction.equals("Right") || direction.equals("StartRight")) {
                    direction = "JumpD"; // Cambiar el sprite de salto a derecha si está mirando a la derecha
                } else if (direction.equals("Left") || direction.equals("StartLeft")) {
                    direction = "JumpL"; // Cambiar el sprite de salto a izquierda si está mirando a la izquierda
                }

                // Evitar que Mario vaya más arriba del índice 0
                if (worldY < 0) {
                    worldY = 0;  // Limitar para que no salga del mapa
                    velocityY = 0;  // Detener la subida
                    jumping = false; // Detener el salto
                    falling = true;  // Comenzar a caer
                }

                // Verificar si el jugador colisiona con el suelo o plataforma
                if (checkCollision("down") || !isAirBelow()) {
                    jumping = false;
                    falling = false;
                    velocityY = 0;
                    onPlatform = true;

                    // Cambiar la animación al estado inicial (quieto en suelo)
                    direction = direction.equals("JumpD") ? "StartRight" : "StartLeft";
                }
                hitBlockAbove();
            } else if (!checkCollision("down") && isAirBelow()) {
                // Aplicar la gravedad solo si el jugador no está en una plataforma o en el suelo
                falling = true;
                velocityY += gravity;
                worldY += velocityY; // Mover al jugador hacia abajo por la gravedad
            } else {
                // Si no está cayendo ni saltando, asegurar que la velocidad se detenga
                jumping = false;
                falling = false;
                velocityY = 0;

                // Cambiar la animación al estado inicial (quieto en suelo)
                if (direction.equals("JumpD")) {
                    direction = "StartRight";
                } else if (direction.equals("JumpL")) {
                    direction = "StartLeft";
                }
            }

            // Animación del personaje al moverse
            if (keyH.leftPressed || keyH.rightPressed) {
                spriteCounter++;

                // Ajusta la velocidad del cambio de sprite dependiendo de si está sprintando o caminando
                int animationSpeed = keyH.shiftPressed ? 3 : 5; // Más rápido si se está sprintando

                if (spriteCounter > animationSpeed) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else if (spriteNum == 2) {
                        spriteNum = 3;
                    } else if (spriteNum == 3) {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            }
        }
    }


    public void hitBlockAbove() {
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = (int) (worldY - gp.tileSize) / gp.tileSize;

        // Verificar que playerCol y playerRow están dentro de los límites del array del mapa
        if (playerCol < 0 || playerCol >= gp.tileM.mapTileNum.length || playerRow < 0 || playerRow >= gp.tileM.mapTileNum[0].length) {
            return; // Salir del método si está fuera de los límites
        }

        int tileNum = gp.tileM.mapTileNum[playerCol][playerRow];

        // Detecta si es un Lucky Block y cambia su estado
        if (tileNum == 2) { // Lucky block
            gp.tileM.mapTileNum[playerCol][playerRow] = 3; // Cambia a bloque roto
            gp.coinCount++;
        }
    }


    public int killsCont;
    public void checkEnemyCollision() {
        for (Enemy enemy : gp.enemies) {  // Supongo que tienes una lista de enemigos en GamePanel
            if (enemy.isAlive && collisionBounds.intersects(enemy.collisionBounds)) {
                if (worldY + gp.tileSize - 10 < enemy.worldY) {  // Mario está por encima del enemigo
                    enemy.isAlive = false;  // El enemigo muere
                    killsCont++;
                    jumping = true;  // Mario puede rebotar tras eliminar al enemigo
                    velocityY = jumpSpeed / 2;  // Mario salta al golpear desde arriba
                }
            }
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        BufferedImage image = null;
        
            switch (direction) {
                case "StartRight":
                    image = StartRight;
                    break;
                case "StartLeft":
                    image = StartLeft;
                    break;
                case "Right":
                    if (spriteNum == 1) {
                        image = Der1;
                    } else if (spriteNum == 2) {
                        image = Der2;
                    } else if (spriteNum == 3) {
                        image = Der3;
                    }
                    break;
                case "Left":
                    if (spriteNum == 1) {
                        image = Left1;
                    } else if (spriteNum == 2) {
                        image = Left2;
                    } else if (spriteNum == 3) {
                        image = Left3;
                    }
                    break;
                case "JumpD":
                    image = JumpD;
                    break;
                case "JumpL":
                    image = JumpL;
                    break;
            }

        g2.drawImage(image, (int) worldX - cameraX, (int) worldY - cameraY, gp.tileSize, gp.tileSize, null);
    }
    
    public void die() {
        System.out.println("Mario ha muerto");
        isDead = true; // Activar estado de muerte
    }

    // Método para verificar si hay aire debajo del jugador
    public boolean isAirBelow() {
        return !checkCollision("down");
    }
}

