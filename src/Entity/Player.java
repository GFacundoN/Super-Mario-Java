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

    boolean jumping = false;
    boolean falling = false;
    public double velocityY = 0;
    double jumpSpeed = -10;
    double gravity = 0.26;
    double maxFallSpeed = 10.0;
    boolean onPlatform = false;

    private final int normalSpeed = 6;
    private final int sprintSpeed = 10;

    public Rectangle collisionBounds;
    
    private boolean isDead = false;
    private boolean deathAnimationStarted = false;
    private int deathAnimationTimer = 0;
    private final int DEATH_JUMP_SPEED = -8;
    private final int DEATH_ANIMATION_DURATION = 180;
    
    public boolean isBig = false;
    public boolean isFire = false;
    private int powerUpTransitionTimer = 0;
    
    public boolean isCrouching = false;
    
    private boolean isInvulnerable = false;
    private int invulnerabilityTimer = 0;
    private final int INVULNERABILITY_DURATION = 120;
    
    private int fireballCooldown = 0;
    private final int FIREBALL_COOLDOWN_TIME = 20;

    public BufferedImage StartRight, StartLeft, Der1, Der2, Der3, Left1, Left2, Left3, JumpD, JumpL;
    public BufferedImage BigStartRight, BigStartLeft, BigDer1, BigDer2, BigDer3, BigLeft1, BigLeft2, BigLeft3, BigJumpD, BigJumpL;
    public BufferedImage BigCrouchRight, BigCrouchLeft;
    public BufferedImage FireStartRight, FireStartLeft, FireDer1, FireDer2, FireDer3, FireLeft1, FireLeft2, FireLeft3, FireJumpD, FireJumpL;
    public BufferedImage FireCrouchRight, FireCrouchLeft;
    public BufferedImage FireThrowRight, FireThrowLeft;
    public BufferedImage DeadSprite;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
        collisionBounds = new Rectangle(worldX, worldY, gp.tileSize, gp.tileSize);
    }

    public void setDefaultValues() {
        worldX = 120;
        worldY = 576;
        speed = 4;
        direction = "StartRight";
        jumping = false;
        falling = false;
        velocityY = 0;
        onPlatform = false;
        isDead = false;
        deathAnimationStarted = false;
        deathAnimationTimer = 0;
        isBig = false;
        isFire = false;
        powerUpTransitionTimer = 0;
        isCrouching = false;
        isInvulnerable = false;
        invulnerabilityTimer = 0;
        fireballCooldown = 0;
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
            
            BigStartRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_der5.png"));
            BigStartLeft = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq5.png"));
            BigDer1 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der2.png"));
            BigDer2 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der3.png"));
            BigDer3 = ImageIO.read(getClass().getResourceAsStream("/res/mario_der4.png"));
            BigLeft1 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq2.png"));
            BigLeft2 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq3.png"));
            BigLeft3 = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq4.png"));
            BigJumpD = ImageIO.read(getClass().getResourceAsStream("/res/mario_der1.png"));
            BigJumpL = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq1.png"));
            
            try {
                BigCrouchRight = ImageIO.read(getClass().getResourceAsStream("/res/mario_der_agachado.png"));
                BigCrouchLeft = ImageIO.read(getClass().getResourceAsStream("/res/mario_izq_agachado.png"));
            } catch (Exception ex) {
                BigCrouchRight = StartRight;
                BigCrouchLeft = StartLeft;
            }
            
            FireStartRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der5.png"));
            FireStartLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq5.png"));
            FireDer1 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der2.png"));
            FireDer2 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der3.png"));
            FireDer3 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der4.png"));
            FireLeft1 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq2.png"));
            FireLeft2 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq3.png"));
            FireLeft3 = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq4.png"));
            FireJumpD = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der1png.png"));
            FireJumpL = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq1.png"));
            FireCrouchRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der_agachado.png"));
            FireCrouchLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq_agachado.png"));
            FireThrowRight = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_der_banana.png"));
            FireThrowLeft = ImageIO.read(getClass().getResourceAsStream("/res/marioconflor_izq_banana.png"));
            
            DeadSprite = ImageIO.read(getClass().getResourceAsStream("/res/mario_dead.png"));
        } catch (IOException e) {
            System.err.println("⚠️ Error al cargar sprites de Mario");
            e.printStackTrace();
        }
    }

    public void updateCollisionBounds() {
        collisionBounds.x = (int) worldX + 10;
        collisionBounds.y = (int) worldY;
        
        if ((isBig || isFire) && !isCrouching) {
            collisionBounds.height = gp.tileSize * 2;
            collisionBounds.y = (int) worldY - gp.tileSize;
        } else {
            collisionBounds.height = gp.tileSize;
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
            futureY += (int)Math.ceil(Math.abs(velocityY)) + 2;
        } else if (direction.equals("up")) {
            futureY += (int)velocityY;
        }

        int collisionHeight = (isBig && !isCrouching) ? gp.tileSize * 2 : gp.tileSize;
        int collisionY = (isBig && !isCrouching) ? futureY - gp.tileSize : futureY;
        
        Rectangle futureBounds = new Rectangle(futureX + 10, collisionY, gp.tileSize - 20, collisionHeight);
        int playerCol = (int) worldX / gp.tileSize;
        int playerRow = (int) worldY / gp.tileSize;
        int topRow = (isBig && !isCrouching) ? (int)(worldY - gp.tileSize) / gp.tileSize : playerRow;
        
        int minRow = Math.max(0, topRow - 1);
        int maxRow = Math.min(gp.tileM.mapTileNum[0].length - 1, playerRow + 1);

        for (int col = Math.max(0, playerCol - 1); col <= Math.min(gp.tileM.mapTileNum.length - 1, playerCol + 1); col++) {
            for (int row = minRow; row <= maxRow; row++) {
                int tileNum = gp.tileM.mapTileNum[col][row];
                if (gp.tileM.tile[tileNum].collision) {
                    Rectangle tileBounds = new Rectangle(col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize);
                    if (futureBounds.intersects(tileBounds)) {
                        if (direction.equals("down")) {
                            int marioBottom = (int)worldY + gp.tileSize;
                            int tileTop = tileBounds.y;
                            
                            int margin = Math.max(5, (int)Math.abs(velocityY) + 3);
                            
                            if (marioBottom <= tileTop + margin && velocityY >= 0) {
                                onPlatform = true;
                                worldY = tileBounds.y - gp.tileSize;
                                velocityY = 0;
                                falling = false;
                                return true;
                            }
                        }
                        else if (direction.equals("up")) {
                            int marioTop = (isBig && !isCrouching) ? (int)worldY - gp.tileSize : (int)worldY;
                            int tileBottom = tileBounds.y + tileBounds.height;
                            
                            if (marioTop < tileBottom && marioTop + (int)Math.abs(velocityY) >= tileBounds.y) {
                                worldY = tileBounds.y + tileBounds.height + ((isBig && !isCrouching) ? gp.tileSize : 0);
                                velocityY = 0;
                                return true;
                            }
                        }
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
        if (isDead) {
            updateDeathAnimation();
            return;
        }
        
        if (isInvulnerable) {
            invulnerabilityTimer++;
            if (invulnerabilityTimer >= INVULNERABILITY_DURATION) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
            }
        }
        
        if (fireballCooldown > 0) {
            fireballCooldown--;
        }
        
        if (isFire && keyH.xPressed && fireballCooldown == 0) {
            shootFireball();
            fireballCooldown = FIREBALL_COOLDOWN_TIME;
        }
        
        if (isBig && keyH.downPressed && !jumping && !falling) {
            isCrouching = true;
        } else {
            if (isCrouching) {
                if (direction.equals("CrouchRight")) {
                    direction = "StartRight";
                } else if (direction.equals("CrouchLeft")) {
                    direction = "StartLeft";
                }
            }
            isCrouching = false;
        }
        
        updateCollisionBounds();

        checkEnemyCollision();

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
                if (!jumping && !falling) {
                    if (direction.equals("Right")) {
                        direction = "StartRight";
                    } else if (direction.equals("Left")) {
                        direction = "StartLeft";
                    }
                }
            }
        } else {
            if (direction.equals("Right") || direction.equals("StartRight") || direction.equals("JumpD") || direction.equals("CrouchRight")) {
                direction = "CrouchRight";
            } else if (direction.equals("Left") || direction.equals("StartLeft") || direction.equals("JumpL") || direction.equals("CrouchLeft")) {
                direction = "CrouchLeft";
            }
        }

        speed = keyH.shiftPressed ? sprintSpeed : normalSpeed;

        if (!isDead) {
            if (keyH.upPressed && !jumping && !falling && !isCrouching && (onPlatform || !isAirBelow())) {
                jumping = true;
                falling = false;
                velocityY = jumpSpeed;
                onPlatform = false;
            }

            if (jumping) {
                if (velocityY < 0 && !keyH.upPressed) {
                    velocityY += gravity * 2;
                } else {
                    velocityY += gravity;
                }
                
                if (velocityY > maxFallSpeed) {
                    velocityY = maxFallSpeed;
                }
                
                if (velocityY < 0 && checkCollision("up")) {
                    velocityY = 0;
                    jumping = false;
                    falling = true;
                }
                
                if (velocityY >= 0 && checkCollision("down")) {
                    jumping = false;
                    falling = false;
                    velocityY = 0;
                    onPlatform = true;
                    killsCont = 0;
                    if (direction.equals("JumpD")) {
                        direction = "StartRight";
                    } else if (direction.equals("JumpL")) {
                        direction = "StartLeft";
                    }
                } else {
                    worldY += velocityY;
                }

                if (direction.equals("Right") || direction.equals("StartRight")) {
                    direction = "JumpD";
                } else if (direction.equals("Left") || direction.equals("StartLeft")) {
                    direction = "JumpL";
                }

                if (worldY < 0) {
                    worldY = 0;
                    velocityY = 0;
                    jumping = false;
                    falling = true;
                }
                
                hitBlockAbove();
            } else if (isAirBelow()) {
                falling = true;
                onPlatform = false;
                velocityY += gravity;
                
                if (velocityY > maxFallSpeed) {
                    velocityY = maxFallSpeed;
                }
                
                if (!checkCollision("down")) {
                    worldY += velocityY;
                } else {
                    falling = false;
                    velocityY = 0;
                    onPlatform = true;
                    killsCont = 0;
                    if (direction.equals("JumpD")) {
                        direction = "StartRight";
                    } else if (direction.equals("JumpL")) {
                        direction = "StartLeft";
                    }
                }
            } else {
                jumping = false;
                falling = false;
                velocityY = 0;

                if (direction.equals("JumpD")) {
                    direction = "StartRight";
                } else if (direction.equals("JumpL")) {
                    direction = "StartLeft";
                }
            }

            if (keyH.leftPressed || keyH.rightPressed) {
                spriteCounter++;

                int animationSpeed = keyH.shiftPressed ? 3 : 5;

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
        if (velocityY >= 0) {
            return;
        }
        
        int headY = (isBig && !isCrouching) ? (int)worldY - gp.tileSize : (int)worldY;
        int playerRow = headY / gp.tileSize;
        
        if (velocityY < 0) {
            playerRow = (headY + (int)velocityY) / gp.tileSize;
        }
        
        if (playerRow < 0 || playerRow >= gp.tileM.mapTileNum[0].length) {
            return;
        }
        
        int leftX = (int)worldX + 10;
        int rightX = (int)worldX + gp.tileSize - 10;
        
        int leftCol = leftX / gp.tileSize;
        int rightCol = rightX / gp.tileSize;
        
        for (int col = leftCol; col <= rightCol; col++) {
            if (col < 0 || col >= gp.tileM.mapTileNum.length) {
                continue;
            }
            
            int tileNum = gp.tileM.mapTileNum[col][playerRow];

            if (tileNum == 2) {
                gp.tileM.mapTileNum[col][playerRow] = 3;
                
                gp.blockBumps.add(new BlockBump(gp, col, playerRow));
                
                if (gp.luckyBlocksHit == 2) {
                    gp.spawnMushroom(col * gp.tileSize, playerRow * gp.tileSize);
                } else if (gp.luckyBlocksHit == 3) {
                    gp.spawnFireFlower(col * gp.tileSize, playerRow * gp.tileSize);
                } else {
                    gp.coinCount++;
                    gp.coinAnimations.add(new CoinAnimation(gp, col * gp.tileSize, playerRow * gp.tileSize));
                }
                gp.luckyBlocksHit++;
                return;
            }
            
            if (tileNum == 1) {
                if (isBig) {
                    gp.tileM.mapTileNum[col][playerRow] = 5;
                    gp.spawnBrickParticles(col * gp.tileSize, playerRow * gp.tileSize);
                    
                    velocityY = 2;
                    return;
                } else {
                    gp.blockBumps.add(new BlockBump(gp, col, playerRow));
                }
            }
        }
    }


    public int killsCont = 0;
    
    public void checkEnemyCollision() {
        for (Enemy enemy : gp.enemies) {
            if (enemy.isAlive && collisionBounds.intersects(enemy.collisionBounds)) {
                if (worldY + gp.tileSize - 10 < enemy.worldY) {
                    enemy.die();
                    killsCont++;
                    jumping = true;
                    velocityY = jumpSpeed / 2;
                } else if (!isInvulnerable) {
                    die();
                }
            }
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        BufferedImage image = null;
        int drawHeight = gp.tileSize;
        int drawY = (int) worldY - cameraY;
        
        if (isDead) {
            image = DeadSprite;
        } else if (isFire) {
            if (isCrouching) {
                drawHeight = gp.tileSize;
                drawY = (int) worldY - cameraY;
            } else {
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
            if (isCrouching) {
                drawHeight = gp.tileSize;
                drawY = (int) worldY - cameraY;
            } else {
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

        if (!isInvulnerable || (invulnerabilityTimer / 5) % 2 == 0) {
            g2.drawImage(image, (int) worldX - cameraX, drawY, gp.tileSize, drawHeight, null);
        }
    }
    
    public void die() {
        if (isDead || isInvulnerable) {
            return;
        }
        
        if (isFire) {
            isFire = false;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            System.out.println("Fire Mario perdió el poder de fuego");
        } else if (isBig) {
            isBig = false;
            isCrouching = false;
            isInvulnerable = true;
            invulnerabilityTimer = 0;
            System.out.println("Mario se hizo pequeño e invulnerable temporalmente");
        } else {
            System.out.println("Mario ha muerto");
            isDead = true;
            deathAnimationStarted = false;
            deathAnimationTimer = 0;
        }
    }
    
    private void updateDeathAnimation() {
        if (!deathAnimationStarted) {
            velocityY = DEATH_JUMP_SPEED;
            deathAnimationStarted = true;
            direction = "JumpD";
        }
        
        velocityY += gravity * 0.7;
        worldY += velocityY;
        
        deathAnimationTimer++;
        
        if (worldY > gp.screenHeight || deathAnimationTimer > DEATH_ANIMATION_DURATION) {
            gp.onPlayerDeathAnimationComplete();
        }
    }
    
    public void powerUp() {
        if (!isBig) {
            isBig = true;
            System.out.println("Mario se hizo grande!");
        }
    }

    public boolean isAirBelow() {
        return !checkCollision("down");
    }
    
    private void shootFireball() {
        double fireballX = worldX + gp.tileSize / 2;
        double fireballY = (isBig || isFire) ? worldY - gp.tileSize / 2 : worldY + gp.tileSize / 4;
        
        boolean facingRight = direction.contains("Right") || direction.contains("Der") || direction.equals("JumpD");
        
        gp.fireballs.add(new Fireball(gp, fireballX, fireballY, facingRight));
    }
    
    public void powerUpFire() {
        if (!isFire) {
            isBig = true;
            isFire = true;
        }
    }
    
    public boolean isDead() {
        return isDead;
    }
}

