// Player.java
package Entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import Main.GamePanel;
import Main.KeyHandler;
import Main.SpriteManager;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;

    // Variables para el salto
    boolean jumping = false;
    boolean falling = false;
    public double velocityY = 0; // Público para que GamePanel pueda acceder
    double jumpSpeed = -10; // Velocidad inicial del salto
    double gravity = 0.26; // Gravedad que afecta al salto
    double maxFallSpeed = 10.0; // Velocidad máxima de caída
    boolean onPlatform = false; // Indica si está sobre una plataforma

    // Velocidades
    private final int normalSpeed = 6;
    private final int sprintSpeed = 10;

    // Área de colisión
    public Rectangle collisionBounds;
    
 // Estado de muerte
    private boolean isDead = false;
    private boolean deathAnimationStarted = false;
    private int deathAnimationTimer = 0;
    private final int DEATH_JUMP_SPEED = -8; // Velocidad del salto de muerte (reducida)
    private final int DEATH_ANIMATION_DURATION = 180; // 3 segundos a 60 FPS
    
    // Estado de power-up
    public boolean isBig = false; // Mario grande o pequeño
    private int powerUpTransitionTimer = 0; // Timer para animación de transformación

    // Sprites Mario pequeño
    public BufferedImage StartRight, StartLeft, Der1, Der2, Der3, Left1, Left2, Left3, JumpD, JumpL;
    
    // Sprites Mario grande
    public BufferedImage BigStartRight, BigStartLeft, BigDer1, BigDer2, BigDer3, BigLeft1, BigLeft2, BigLeft3, BigJumpD, BigJumpL;
    
    // Sprite Mario muerto
    public BufferedImage DeadSprite;

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
        deathAnimationStarted = false;
        deathAnimationTimer = 0;
        isBig = false; // Reiniciar a Mario pequeño
        powerUpTransitionTimer = 0;
    }

    public void getPlayerImage() {
        // Usar imágenes individuales directamente
        try {
            // Mario pequeño
            // Idle (quieto)
            StartRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_start.png"));
            StartLeft = ImageIO.read(getClass().getResourceAsStream("/res/StartLeft.png"));
            // Caminando
            Der1 = ImageIO.read(getClass().getResourceAsStream("/res/Der1.png"));
            Der2 = ImageIO.read(getClass().getResourceAsStream("/res/Der2.png"));
            Der3 = ImageIO.read(getClass().getResourceAsStream("/res/Der3.png"));
            Left1 = ImageIO.read(getClass().getResourceAsStream("/res/Izq1.png"));
            Left2 = ImageIO.read(getClass().getResourceAsStream("/res/Izq2.png"));
            Left3 = ImageIO.read(getClass().getResourceAsStream("/res/Izq3.png"));
            // Saltando
            JumpD = ImageIO.read(getClass().getResourceAsStream("/res/JumpD.png"));
            JumpL = ImageIO.read(getClass().getResourceAsStream("/res/JumpL.png"));
            
            // Mario grande (corregido: idle y jump invertidos)
            // Idle (quieto)
            BigStartRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_der5.png"));
            BigStartLeft = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq5.png"));
            // Caminando
            BigDer1 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der2.png"));
            BigDer2 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der3.png"));
            BigDer3 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der4.png"));
            BigLeft1 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq2.png"));
            BigLeft2 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq3.png"));
            BigLeft3 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq4.png"));
            // Saltando
            BigJumpD = ImageIO.read(getClass().getResourceAsStream("/res/mario_der1.png"));
            BigJumpL = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq1.png"));
            
            // Mario muerto
            DeadSprite = ImageIO.read(getClass().getResourceAsStream("/res/mario_dead.png"));
            
            System.out.println("✅ Todos los sprites de Mario cargados correctamente");
        } catch (IOException e) {
            System.err.println("⚠️ Error al cargar sprites de Mario");
            e.printStackTrace();
        }
    }

    public void updateCollisionBounds() {
        collisionBounds.x = (int) worldX + 10;
        collisionBounds.y = (int) worldY;
        
        // Ajustar altura de colisión según el tamaño de Mario
        if (isBig) {
            collisionBounds.height = gp.tileSize * 2; // Mario grande: 2 tiles de alto
            collisionBounds.y = (int) worldY - gp.tileSize; // Ajustar Y para que los pies estén en el mismo lugar
        } else {
            collisionBounds.height = gp.tileSize; // Mario pequeño: 1 tile de alto
        }
    }

    public boolean checkCollision(String direction) {
        int futureX = (int) worldX;
        int futureY = (int) worldY;

        if (direction.equals("right")) {
            futureX += speed;
        } else if (direction.equals("left")) {
            futureX -= speed;
        } else if (direction.equals("down")) {
            // Usar velocityY + margen para predecir correctamente la caída
            futureY += (int)Math.ceil(Math.abs(velocityY)) + 2;
        } else if (direction.equals("up")) {
            futureY += (int)velocityY; // Usar velocityY cuando sube
        }

        // Ajustar altura según el tamaño de Mario
        int collisionHeight = isBig ? gp.tileSize * 2 : gp.tileSize;
        int collisionY = isBig ? futureY - gp.tileSize : futureY;
        
        Rectangle futureBounds = new Rectangle(futureX + 10, collisionY, gp.tileSize - 20, collisionHeight);

        // Limitar la búsqueda de tiles en torno al jugador para mejorar el rendimiento
        // Para Mario grande, necesitamos buscar también en la fila superior
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = (int) worldY / gp.tileSize;
        int topRow = isBig ? (int)(worldY - gp.tileSize) / gp.tileSize : playerRow;
        
        // Ajustar el rango de búsqueda para incluir la cabeza de Mario grande
        int minRow = Math.max(0, topRow - 1);
        int maxRow = Math.min(gp.tileM.mapTileNum[0].length - 1, playerRow + 1);

        for (int col = Math.max(0, playerCol - 1); col <= Math.min(gp.tileM.mapTileNum.length - 1, playerCol + 1); col++) {
            for (int row = minRow; row <= maxRow; row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        // Si colisiona por abajo
                        if (direction.equals("down")) {
                            // Verificar que Mario viene desde arriba del tile
                            int marioBottom = (int)worldY + gp.tileSize;
                            int tileTop = tileBounds.y;
                            
                            // Margen dinámico basado en la velocidad de caída (mínimo 5, máximo velocityY + 3)
                            int margin = Math.max(5, (int)Math.abs(velocityY) + 3);
                            
                            // Solo colisionar si Mario está cayendo hacia el bloque desde arriba
                            if (marioBottom <= tileTop + margin && velocityY >= 0) {
                                onPlatform = true;
                                worldY = tileBounds.y - gp.tileSize; // Ajustar la posición Y al estar sobre una plataforma
                                velocityY = 0; // Reiniciar la velocidad vertical al colisionar
                                falling = false;
                                return true;
                            }
                        }
                        // Si colisiona por arriba
                        else if (direction.equals("up")) {
                            // Para Mario grande, verificar colisión con la cabeza (parte superior)
                            int marioTop = isBig ? (int)worldY - gp.tileSize : (int)worldY;
                            int tileBottom = tileBounds.y + tileBounds.height;
                            
                            // Solo si la cabeza de Mario está golpeando el bloque desde abajo
                            if (marioTop < tileBottom && marioTop + (int)Math.abs(velocityY) >= tileBounds.y) {
                                worldY = tileBounds.y + tileBounds.height + (isBig ? gp.tileSize : 0); // Ajustar Y para no "escalar"
                                velocityY = 0; // Reiniciar velocidad vertical
                                return true; // Se detecta colisión arriba
                            }
                        }
                        // Colisiones laterales
                        else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void update() {
        // Si está muerto, solo ejecutar animación de muerte
        if (isDead) {
            updateDeathAnimation();
            return; // No ejecutar el resto del update
        }
        
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
                velocityY += gravity; // Aplicar gravedad primero
                
                // Limitar velocidad de caída
                if (velocityY > maxFallSpeed) {
                    velocityY = maxFallSpeed;
                }
                
                // Verificar si colisiona con el techo (cuando sube)
                if (velocityY < 0 && checkCollision("up")) {
                    velocityY = 0; // Detener el salto al colisionar con el techo
                    jumping = false;
                    falling = true; // Comenzar a caer
                }
                
                // Verificar si el jugador colisiona con el suelo o plataforma (cuando cae)
                if (velocityY >= 0 && checkCollision("down")) {
                    jumping = false;
                    falling = false;
                    velocityY = 0;
                    onPlatform = true;
                    // Cambiar la animación al estado inicial (quieto en suelo)
                    direction = direction.equals("JumpD") ? "StartRight" : "StartLeft";
                } else {
                    // Solo mover si NO hay colisión
                    worldY += velocityY;
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
                
                hitBlockAbove();
            } else if (isAirBelow()) {
                // Aplicar la gravedad solo si el jugador no está en una plataforma o en el suelo
                falling = true;
                onPlatform = false; // Ya no está en una plataforma
                velocityY += gravity;
                
                // Limitar velocidad de caída
                if (velocityY > maxFallSpeed) {
                    velocityY = maxFallSpeed;
                }
                
                // Verificar colisión ANTES de mover
                if (!checkCollision("down")) {
                    worldY += velocityY; // Mover al jugador hacia abajo por la gravedad
                } else {
                    // Si hay colisión, detener la caída
                    falling = false;
                    velocityY = 0;
                    onPlatform = true;
                }
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
        // Solo verificar si Mario está subiendo (saltando)
        if (velocityY >= 0) {
            return; // No golpear bloques si está cayendo o quieto
        }
        
        int playerCol = (int) worldX / gp.tileSize;
        // Calcular la posición de la cabeza de Mario (parte superior del collisionBounds)
        int headY = isBig ? (int)worldY - gp.tileSize : (int)worldY;
        int playerRow = headY / gp.tileSize;
        
        // Ajustar playerRow para que apunte al bloque justo encima de la cabeza
        if (velocityY < 0) {
            playerRow = (headY + (int)velocityY) / gp.tileSize;
        }

        // Verificar que playerCol y playerRow están dentro de los límites del array del mapa
        if (playerCol < 0 || playerCol >= gp.tileM.mapTileNum.length || playerRow < 0 || playerRow >= gp.tileM.mapTileNum[0].length) {
            return; // Salir del método si está fuera de los límites
        }

        int tileNum = gp.tileM.mapTileNum[playerCol][playerRow];

        // Detecta si es un Lucky Block y cambia su estado
        if (tileNum == 2) { // Lucky block
            gp.tileM.mapTileNum[playerCol][playerRow] = 3; // Cambia a bloque roto
            
            // Verificar si es el tercer lucky block (spawner hongo)
            if (gp.luckyBlocksHit == 2) { // Tercer bloque (0, 1, 2)
                gp.spawnMushroom(playerCol * gp.tileSize, playerRow * gp.tileSize);
            } else {
                gp.coinCount++;
            }
            gp.luckyBlocksHit++;
        }
        
        // Manejo de ladrillos (tile 1)
        if (tileNum == 1) {
            if (isBig) {
                // Mario grande rompe el ladrillo
                gp.tileM.mapTileNum[playerCol][playerRow] = 5; // Cambiar a cielo (romper)
                gp.spawnBrickParticles(playerCol * gp.tileSize, playerRow * gp.tileSize); // Crear partículas
                
                
                // Hacer que Mario rebote hacia abajo
                velocityY = 2; // Pequeño rebote hacia abajo
            }
            // Mario pequeño no puede romper ladrillos
        }
    }


    public int killsCont = 0; // Contador de enemigos eliminados
    
    public void checkEnemyCollision() {
        for (Enemy enemy : gp.enemies) {  // Supongo que tienes una lista de enemigos en GamePanel
            if (enemy.isAlive && collisionBounds.intersects(enemy.collisionBounds)) {
                if (worldY + gp.tileSize - 10 < enemy.worldY) {  // Mario está por encima del enemigo
                    enemy.die();  // Llamar al método die() para activar el timer y mostrar sprite muerto
                    killsCont++;
                    jumping = true;  // Mario puede rebotar tras eliminar al enemigo
                    velocityY = jumpSpeed / 2;  // Mario salta al golpear desde arriba
                }
            }
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        BufferedImage image = null;
        int drawHeight = gp.tileSize;
        int drawY = (int) worldY - cameraY;
        
        // Si está muerto, usar sprite de muerte
        if (isDead) {
            image = DeadSprite;
        } else if (isBig) {
            // Mario grande - usar sprites grandes
            drawHeight = gp.tileSize * 2; // Mario grande es el doble de alto
            drawY = (int) worldY - cameraY - gp.tileSize; // Ajustar posición para que los pies estén en el mismo lugar
            
            switch (direction) {
                case "StartRight":
                    image = BigStartRight;
                    break;
                case "StartLeft":
                    image = BigStartLeft;
                    break;
                case "Right":
                    if (spriteNum == 1) {
                        image = BigDer1;
                    } else if (spriteNum == 2) {
                        image = BigDer2;
                    } else if (spriteNum == 3) {
                        image = BigDer3;
                    }
                    break;
                case "Left":
                    if (spriteNum == 1) {
                        image = BigLeft1;
                    } else if (spriteNum == 2) {
                        image = BigLeft2;
                    } else if (spriteNum == 3) {
                        image = BigLeft3;
                    }
                    break;
                case "JumpD":
                    image = BigJumpD;
                    break;
                case "JumpL":
                    image = BigJumpL;
                    break;
            }
        } else {
            // Mario pequeño - usar sprites pequeños
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
        }

        g2.drawImage(image, (int) worldX - cameraX, drawY, gp.tileSize, drawHeight, null);
    }
    
    public void die() {
        // Evitar múltiples llamadas
        if (isDead) {
            return; // Ya está muerto, no hacer nada
        }
        
        if (isBig) {
            // Si es grande, solo se hace pequeño
            isBig = false;
            System.out.println("Mario se hizo pequeño");
        } else {
            // Si es pequeño, muere
            System.out.println("Mario ha muerto");
            isDead = true;
            deathAnimationStarted = false; // Resetear para iniciar animación
            deathAnimationTimer = 0;
        }
    }
    
    // Animación de muerte de Mario
    private void updateDeathAnimation() {
        if (!deathAnimationStarted) {
            // Iniciar animación: salto hacia arriba
            velocityY = DEATH_JUMP_SPEED;
            deathAnimationStarted = true;
            direction = "JumpD"; // Usar sprite de salto
        }
        
        // Aplicar gravedad reducida para animación de muerte más lenta
        velocityY += gravity * 0.7; // 70% de la gravedad normal
        worldY += velocityY;
        
        // Incrementar timer
        deathAnimationTimer++;
        
        // Cuando Mario cae fuera de la pantalla o pasa el tiempo, notificar a GamePanel
        if (worldY > gp.screenHeight || deathAnimationTimer > DEATH_ANIMATION_DURATION) {
            // La animación terminó, GamePanel manejará el resto
            gp.onPlayerDeathAnimationComplete();
        }
    }
    
    public void powerUp() {
        if (!isBig) {
            isBig = true;
            System.out.println("Mario se hizo grande!");
            // Opcional: agregar animación de transformación
        }
    }

    // Método para verificar si hay aire debajo del jugador
    public boolean isAirBelow() {
        return !checkCollision("down");
    }
    
    // Método para verificar si el jugador está muerto
    public boolean isDead() {
        return isDead;
    }
}

