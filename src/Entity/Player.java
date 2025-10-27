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
    public boolean isFire = false; // Mario con poder de fuego
    private int powerUpTransitionTimer = 0; // Timer para animación de transformación
    
    // Estado de agacharse
    public boolean isCrouching = false; // Mario agachado
    
    // Sistema de invulnerabilidad
    private boolean isInvulnerable = false; // Invulnerabilidad temporal después de recibir daño
    private int invulnerabilityTimer = 0;
    private final int INVULNERABILITY_DURATION = 120; // 2 segundos a 60 FPS
    
    // Sistema de disparo
    private int fireballCooldown = 0;
    private final int FIREBALL_COOLDOWN_TIME = 20; // ~0.3 segundos entre disparos

    // Sprites Mario pequeño
    public BufferedImage StartRight, StartLeft, Der1, Der2, Der3, Left1, Left2, Left3, JumpD, JumpL;
    
    // Sprites Mario grande
    public BufferedImage BigStartRight, BigStartLeft, BigDer1, BigDer2, BigDer3, BigLeft1, BigLeft2, BigLeft3, BigJumpD, BigJumpL;
    public BufferedImage BigCrouchRight, BigCrouchLeft; // Sprites de Mario agachado
    
    // Sprites Fire Mario
    public BufferedImage FireStartRight, FireStartLeft, FireDer1, FireDer2, FireDer3, FireLeft1, FireLeft2, FireLeft3, FireJumpD, FireJumpL;
    public BufferedImage FireCrouchRight, FireCrouchLeft;
    public BufferedImage FireThrowRight, FireThrowLeft; // Sprites lanzando fireball
    
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
        isFire = false; // Reiniciar Fire Mario
        powerUpTransitionTimer = 0;
        isCrouching = false; // Reiniciar estado agachado
        isInvulnerable = false; // Reiniciar invulnerabilidad
        invulnerabilityTimer = 0;
        fireballCooldown = 0; // Reiniciar cooldown de fireball
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
            
            // Intentar cargar sprites de agachado (si no existen, usar sprites de Mario pequeño como fallback)
            try {
                BigCrouchRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_der_agachado.png"));
                BigCrouchLeft = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq_agachado.png"));
            } catch (Exception ex) {
                System.out.println("⚠️ Sprites de agachado no encontrados, usando sprites de Mario pequeño como fallback");
                BigCrouchRight = StartRight; // Usar sprite pequeño para evitar compresión
                BigCrouchLeft = StartLeft;
            }
            
            // Fire Mario
            // Idle (quieto)
            FireStartRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der5.png"));
            FireStartLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq5.png"));
            // Caminando
            FireDer1 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der2.png"));
            FireDer2 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der3.png"));
            FireDer3 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der4.png"));
            FireLeft1 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq2.png"));
            FireLeft2 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq3.png"));
            FireLeft3 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq4.png"));
            // Saltando
            FireJumpD = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der1png.png"));
            FireJumpL = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq1.png"));
            // Agachado
            FireCrouchRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der_agachado.png"));
            FireCrouchLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq_agachado.png"));
            // Lanzando fireball
            FireThrowRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der_banana.png"));
            FireThrowLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq_banana.png"));
            
            // Mario muerto
            DeadSprite = ImageIO.read(getClass().getResourceAsStream("/res/mario_dead.png"));
            
            System.out.println("✅ Todos los sprites de Mario cargados correctamente (incluido Fire Mario)");
        } catch (IOException e) {
            System.err.println("⚠️ Error al cargar sprites de Mario");
            e.printStackTrace();
        }
    }

    public void updateCollisionBounds() {
        collisionBounds.x = (int) worldX + 10;
        collisionBounds.y = (int) worldY;
        
        // Ajustar altura de colisión según el tamaño de Mario y si está agachado
        if ((isBig || isFire) && !isCrouching) {
            collisionBounds.height = gp.tileSize * 2; // Mario grande o Fire: 2 tiles de alto
            collisionBounds.y = (int) worldY - gp.tileSize; // Ajustar Y para que los pies estén en el mismo lugar
        } else {
            collisionBounds.height = gp.tileSize; // Mario pequeño o agachado: 1 tile de alto
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

        // Ajustar altura según el tamaño de Mario y si está agachado
        int collisionHeight = (isBig && !isCrouching) ? gp.tileSize * 2 : gp.tileSize;
        int collisionY = (isBig && !isCrouching) ? futureY - gp.tileSize : futureY;
        
        Rectangle futureBounds = new Rectangle(futureX + 10, collisionY, gp.tileSize - 20, collisionHeight);

        // Limitar la búsqueda de tiles en torno al jugador para mejorar el rendimiento
        // Para Mario grande (no agachado), necesitamos buscar también en la fila superior
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = (int) worldY / gp.tileSize;
        int topRow = (isBig && !isCrouching) ? (int)(worldY - gp.tileSize) / gp.tileSize : playerRow;
        
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
                            // Para Mario grande (no agachado), verificar colisión con la cabeza (parte superior)
                            int marioTop = (isBig && !isCrouching) ? (int)worldY - gp.tileSize : (int)worldY;
                            int tileBottom = tileBounds.y + tileBounds.height;
                            
                            // Solo si la cabeza de Mario está golpeando el bloque desde abajo
                            if (marioTop < tileBottom && marioTop + (int)Math.abs(velocityY) >= tileBounds.y) {
                                worldY = tileBounds.y + tileBounds.height + ((isBig && !isCrouching) ? gp.tileSize : 0); // Ajustar Y para no "escalar"
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
        
        // Actualizar timer de invulnerabilidad
        if (isInvulnerable) {
            invulnerabilityTimer++;
            if (invulnerabilityTimer >= INVULNERABILITY_DURATION) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
            }
        }
        
        // Actualizar cooldown de fireball
        if (fireballCooldown > 0) {
            fireballCooldown--;
        }
        
        // Disparar fireball (tecla X) - solo si es Fire Mario
        if (isFire && keyH.xPressed && fireballCooldown == 0) {
            shootFireball();
            fireballCooldown = FIREBALL_COOLDOWN_TIME;
        }
        
        // Manejar agacharse (solo si es grande, está en el suelo y no está saltando)
        if (isBig && keyH.downPressed && !jumping && !falling) {
            isCrouching = true;
        } else {
            // Si estaba agachado y deja de estarlo, restaurar la dirección normal
            if (isCrouching) {
                if (direction.equals("CrouchRight")) {
                    direction = "StartRight";
                } else if (direction.equals("CrouchLeft")) {
                    direction = "StartLeft";
                }
            }
            isCrouching = false;
        }
        
        // Actualizar el área de colisión
        updateCollisionBounds();

        checkEnemyCollision();

        // Movimiento horizontal (no se puede mover si está agachado)
        if (!isCrouching) {
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
                // Solo cambiar a estado idle si NO está saltando o cayendo
                if (!jumping && !falling) {
                    if (direction.equals("Right")) {
                        direction = "StartRight";
                    } else if (direction.equals("Left")) {
                        direction = "StartLeft";
                    }
                }
            }
        } else {
            // Si está agachado, mantener la dirección de agachado basándose en la última dirección
            if (direction.equals("Right") || direction.equals("StartRight") || direction.equals("JumpD") || direction.equals("CrouchRight")) {
                direction = "CrouchRight";
            } else if (direction.equals("Left") || direction.equals("StartLeft") || direction.equals("JumpL") || direction.equals("CrouchLeft")) {
                direction = "CrouchLeft";
            }
        }

        // Ajuste de velocidad si se presiona Shift
        speed = keyH.shiftPressed ? sprintSpeed : normalSpeed;

        if (!isDead) {
            // Saltar solo si está en el suelo o en una plataforma y no está agachado
            if (keyH.upPressed && !jumping && !falling && !isCrouching && (onPlatform || !isAirBelow())) {
                jumping = true;
                falling = false;
                velocityY = jumpSpeed;
                onPlatform = false; // Desactivar onPlatform al saltar
            }

            // Lógica de salto y caída
            if (jumping) {
                // Salto variable: si se suelta el botón mientras sube, aplicar más gravedad
                if (velocityY < 0 && !keyH.upPressed) {
                    velocityY += gravity * 2; // Doble gravedad si suelta el botón
                } else {
                    velocityY += gravity; // Gravedad normal
                }
                
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
                    killsCont = 0; // Resetear combo al tocar el suelo
                    // Cambiar la animación al estado inicial (quieto en suelo) manteniendo la dirección
                    if (direction.equals("JumpD")) {
                        direction = "StartRight";
                    } else if (direction.equals("JumpL")) {
                        direction = "StartLeft";
                    }
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
                    killsCont = 0; // Resetear combo al tocar el suelo
                    // Cambiar la animación al estado inicial si estaba en animación de salto
                    if (direction.equals("JumpD")) {
                        direction = "StartRight";
                    } else if (direction.equals("JumpL")) {
                        direction = "StartLeft";
                    }
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
        
        // Calcular la posición de la cabeza de Mario (parte superior del collisionBounds)
        int headY = (isBig && !isCrouching) ? (int)worldY - gp.tileSize : (int)worldY;
        int playerRow = headY / gp.tileSize;
        
        // Ajustar playerRow para que apunte al bloque justo encima de la cabeza
        if (velocityY < 0) {
            playerRow = (headY + (int)velocityY) / gp.tileSize;
        }
        
        // Verificar que playerRow está dentro de los límites
        if (playerRow < 0 || playerRow >= gp.tileM.mapTileNum[0].length) {
            return;
        }
        
        // Calcular el rango de columnas que la cabeza de Mario puede estar tocando
        // collisionBounds tiene un offset de +10 y ancho de tileSize - 20
        int leftX = (int)worldX + 10;
        int rightX = (int)worldX + gp.tileSize - 10;
        
        int leftCol = leftX / gp.tileSize;
        int rightCol = rightX / gp.tileSize;
        
        // Verificar todos los tiles que la cabeza de Mario puede estar tocando
        for (int col = leftCol; col <= rightCol; col++) {
            // Verificar límites de columna
            if (col < 0 || col >= gp.tileM.mapTileNum.length) {
                continue;
            }
            
            int tileNum = gp.tileM.mapTileNum[col][playerRow];

            // Detecta si es un Lucky Block y cambia su estado
            if (tileNum == 2) { // Lucky block
                gp.tileM.mapTileNum[col][playerRow] = 3; // Cambia a bloque roto
                
                // Crear animación de bloque saltando
                gp.blockBumps.add(new BlockBump(gp, col, playerRow));
                
                // Sistema de spawneo de power-ups
                if (gp.luckyBlocksHit == 2) { // Tercer bloque → Hongo
                    gp.spawnMushroom(col * gp.tileSize, playerRow * gp.tileSize);
                } else if (gp.luckyBlocksHit == 3) { // Cuarto bloque → Fire Flower
                    gp.spawnFireFlower(col * gp.tileSize, playerRow * gp.tileSize);
                } else {
                    gp.coinCount++;
                    // Crear animación de moneda saliendo del bloque
                    gp.coinAnimations.add(new CoinAnimation(gp, col * gp.tileSize, playerRow * gp.tileSize));
                }
                gp.luckyBlocksHit++;
                return; // Salir después de golpear un bloque
            }
            
            // Manejo de ladrillos (tile 1)
            if (tileNum == 1) {
                if (isBig) {
                    // Mario grande rompe el ladrillo
                    gp.tileM.mapTileNum[col][playerRow] = 5; // Cambiar a cielo (romper)
                    gp.spawnBrickParticles(col * gp.tileSize, playerRow * gp.tileSize); // Crear partículas
                    
                    // Hacer que Mario rebote hacia abajo
                    velocityY = 2; // Pequeño rebote hacia abajo
                    return; // Salir después de romper un bloque
                } else {
                    // Mario pequeño golpea el bloque (no lo rompe)
                    gp.blockBumps.add(new BlockBump(gp, col, playerRow));
                }
            }
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
                } else if (!isInvulnerable) {
                    // Mario es golpeado por el enemigo (solo si no es invulnerable)
                    die();
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
        } else if (isFire) {
            // Fire Mario - usar sprites de fuego (misma lógica que Mario grande)
            if (isCrouching) {
                // Mario agachado: altura de 1 tile
                drawHeight = gp.tileSize;
                drawY = (int) worldY - cameraY;
            } else {
                // Fire Mario normal: altura de 2 tiles
                drawHeight = gp.tileSize * 2;
                drawY = (int) worldY - cameraY - gp.tileSize;
            }
            
            switch (direction) {
                case "StartRight":
                    image = FireStartRight;
                    break;
                case "StartLeft":
                    image = FireStartLeft;
                    break;
                case "Right":
                    if (spriteNum == 1) {
                        image = FireDer1;
                    } else if (spriteNum == 2) {
                        image = FireDer2;
                    } else if (spriteNum == 3) {
                        image = FireDer3;
                    }
                    break;
                case "Left":
                    if (spriteNum == 1) {
                        image = FireLeft1;
                    } else if (spriteNum == 2) {
                        image = FireLeft2;
                    } else if (spriteNum == 3) {
                        image = FireLeft3;
                    }
                    break;
                case "JumpD":
                    image = FireJumpD;
                    break;
                case "JumpL":
                    image = FireJumpL;
                    break;
                case "CrouchRight":
                    image = FireCrouchRight;
                    break;
                case "CrouchLeft":
                    image = FireCrouchLeft;
                    break;
            }
        } else if (isBig) {
            // Mario grande - usar sprites grandes
            if (isCrouching) {
                // Mario agachado: altura de 1 tile
                drawHeight = gp.tileSize;
                drawY = (int) worldY - cameraY;
            } else {
                // Mario grande normal: altura de 2 tiles
                drawHeight = gp.tileSize * 2;
                drawY = (int) worldY - cameraY - gp.tileSize;
            }
            
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
                case "CrouchRight":
                    image = BigCrouchRight;
                    break;
                case "CrouchLeft":
                    image = BigCrouchLeft;
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

        // Efecto de parpadeo cuando es invulnerable (dibujar solo en frames pares)
        if (!isInvulnerable || (invulnerabilityTimer / 5) % 2 == 0) {
            g2.drawImage(image, (int) worldX - cameraX, drawY, gp.tileSize, drawHeight, null);
        }
    }
    
    public void die() {
        // Evitar múltiples llamadas
        if (isDead || isInvulnerable) {
            return; // Ya está muerto o es invulnerable, no hacer nada
        }
        
        if (isFire) {
            // Fire Mario → Mario Grande
            isFire = false;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            System.out.println("Fire Mario perdió el poder de fuego");
        } else if (isBig) {
            // Mario Grande → Mario Pequeño
            isBig = false;
            isCrouching = false; // Desactivar agacharse al hacerse pequeño
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            System.out.println("Mario se hizo pequeño e invulnerable temporalmente");
        } else {
            // Mario Pequeño → Muerto
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
    
    // Método para disparar fireball
    private void shootFireball() {
        // Calcular posición de spawn del fireball
        double fireballX = worldX + gp.tileSize / 2;
        double fireballY = (isBig || isFire) ? worldY - gp.tileSize / 2 : worldY + gp.tileSize / 4;
        
        // Determinar dirección según el sprite actual
        boolean facingRight = direction.contains("Right") || direction.contains("Der") || direction.equals("JumpD");
        
        // Crear fireball y agregarlo a GamePanel
        gp.fireballs.add(new Fireball(gp, fireballX, fireballY, facingRight));
        System.out.println("🔥 Fireball lanzado!");
    }
    
    public void powerUpFire() {
        if (!isFire) {
            isBig = true; // Fire Mario también es grande
            isFire = true;
            System.out.println("🔥 Mario obtuvo el poder de fuego!");
        }
    }
    
    // Método para verificar si el jugador está muerto
    public boolean isDead() {
        return isDead;
    }
}

