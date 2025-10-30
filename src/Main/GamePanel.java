package Main;

import Entity.Enemy;
import Entity.Player;
import Entity.PowerUp;
import Entity.BrickParticle;
import Entity.CoinAnimation;
import Entity.BlockBump;
import Entity.Fireball;
import Entity.Castle;
import tile.TileManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.util.Iterator;

public class GamePanel extends JPanel implements Runnable {

    public final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 24;
    public final int maxScreenRow = 15;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 204;
    public final int maxWorldRow = 15;

    int cameraX = 0;
    int cameraY = 0;

    int FPS = 60;

    public TileManager tileM;
    public KeyHandler keyH = new KeyHandler();
    public SpriteManager spriteManager;
    public SoundManager soundManager;
    Thread gameThread;

    public Player player;
    public ArrayList<Enemy> enemies = new ArrayList<>();
    private int lives = 3;
    private boolean isGameOver = false;
    public boolean isDisplayingLives = false;
    private long lifeDisplayStartTime;
    private final int lifeDisplayDuration = 4000;

    private long gameOverStartTime = 0;
    private final int gameOverDelay = 5000;
    
    private Font gameFont;

    public boolean isInMenu = true;
    public String playerName = "";
    public int coinCount = 0;
    private Image coinImage;
    private Image nubeImage;
    
    public ArrayList<PowerUp> powerUps = new ArrayList<>();
    public int luckyBlocksHit = 0;
    
    public ArrayList<BrickParticle> brickParticles = new ArrayList<>();
    
    public ArrayList<CoinAnimation> coinAnimations = new ArrayList<>();
    public ArrayList<BlockBump> blockBumps = new ArrayList<>();
    public ArrayList<Fireball> fireballs = new ArrayList<>();
    
    public int levelTimer = 400;
    private int timerCounter = 0;
    
    public Castle castle;
    
    private boolean isVictory = false;
    private long victoryStartTime = 0;
    private final int victoryDelay = 5000;

    private Menu menu;

    private ArrayList<Point> initialEnemyPositions = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        spriteManager = new SpriteManager();
        soundManager = new SoundManager();
        
        player = new Player(this, keyH);
        
        menu = new Menu(this);

        tileM = new TileManager(this);
        enemies.add(new Enemy(this, 400, 576));
        initialEnemyPositions.add(new Point(400, 576));
        enemies.add(new Enemy(this, 800, 576));
        initialEnemyPositions.add(new Point(800, 576));
        
        enemies.add(new Enemy(this, 1500, 576));
        initialEnemyPositions.add(new Point(1500, 576));
        
        enemies.add(new Enemy(this, 2400, 576));
        initialEnemyPositions.add(new Point(2400, 576));
        enemies.add(new Enemy(this, 2600, 576));
        initialEnemyPositions.add(new Point(2600, 576));
        
        enemies.add(new Enemy(this, 6100, 576));
        initialEnemyPositions.add(new Point(6100, 576));
        enemies.add(new Enemy(this, 6300, 576));
        initialEnemyPositions.add(new Point(6300, 576));
        enemies.add(new Enemy(this, 6400, 576));
        initialEnemyPositions.add(new Point(6400, 576));
        
        enemies.add(new Enemy(this, 8200, 576));
        initialEnemyPositions.add(new Point(8200, 576));
        enemies.add(new Enemy(this, 8400, 576));
        initialEnemyPositions.add(new Point(8400, 576));
        
        int castleX = (maxWorldCol - 6) * tileSize;
        int castleY = 335;
        castle = new Castle(this, castleX, castleY);

try {
    InputStream fontStream = getClass().getResourceAsStream("/res/PressStart2P.ttf");
    if (fontStream == null) {
        throw new IOException("No se encontró la fuente PressStart2P.ttf");
    }
    gameFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 24);

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    ge.registerFont(gameFont);

    InputStream coinStream = getClass().getResourceAsStream("/res/coin.png");
    if (coinStream == null) {
        throw new IOException("No se encontró la imagen coin.png");
    }
    coinImage = ImageIO.read(coinStream);

    InputStream nubeStream = getClass().getResourceAsStream("/res/nube.png");
    if (nubeStream == null) {
        throw new IOException("No se encontró la imagen nube.png");
    }
    nubeImage = ImageIO.read(nubeStream);

} catch (IOException | FontFormatException e) {
    e.printStackTrace();
    gameFont = new Font("SansSerif", Font.PLAIN, 16); 
}

    }
    

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (isGameOver) {
            if (System.currentTimeMillis() - gameOverStartTime > gameOverDelay) {
                resetGame();
            }
            return;
        }

        if (isDisplayingLives) {
            if (System.currentTimeMillis() - lifeDisplayStartTime > lifeDisplayDuration) {
                isDisplayingLives = false;
                if (lives > 0) {
                    player.setDefaultValues();
                } else {
                    isGameOver = true;
                    gameOverStartTime = System.currentTimeMillis();
                }
            }
            return;
        }

        if (isInMenu) {
            if (menu.isChangingName()) {
                updateNameChange();
            } else {
                menu.updateMenu();
            }
        } else {
            player.update();
            
            if (!player.isDead() && player.worldY > maxWorldRow * tileSize) {
                player.dieInstantly();
            }
            
            if (player.worldX >= tileSize * 200) {
                isVictory = true;
                victoryStartTime = System.currentTimeMillis();
                return;
            }


            if (!player.isDead()) {
                for (Enemy enemy : enemies) {
                    enemy.update();
                }
                
                for (PowerUp powerUp : powerUps) {
                    powerUp.update();
                }
            }
            
            Iterator<BrickParticle> particleIterator = brickParticles.iterator();
            while (particleIterator.hasNext()) {
                BrickParticle particle = particleIterator.next();
                particle.update();
                if (!particle.isActive) {
                    particleIterator.remove();
                }
            }
            
            Iterator<CoinAnimation> coinIterator = coinAnimations.iterator();
            while (coinIterator.hasNext()) {
                CoinAnimation coin = coinIterator.next();
                coin.update();
                if (!coin.isActive) {
                    coinIterator.remove();
                }
            }
            
            Iterator<BlockBump> blockIterator = blockBumps.iterator();
            while (blockIterator.hasNext()) {
                BlockBump block = blockIterator.next();
                block.update();
                if (!block.isActive) {
                    blockIterator.remove();
                }
            }
            
            Iterator<Fireball> fireballIterator = fireballs.iterator();
            while (fireballIterator.hasNext()) {
                Fireball fireball = fireballIterator.next();
                fireball.update();
                
                for (Enemy enemy : enemies) {
                    if (fireball.checkEnemyCollision(enemy)) {
                        enemy.die();
                        fireball.isActive = false;
                        player.killsCont++;
                        soundManager.playSound(SoundManager.KICK_KILL);
                        break;
                    }
                }
                
                if (!fireball.isActive) {
                    fireballIterator.remove();
                }
            }
            
            timerCounter++;
            if (timerCounter >= 60) {
                if (levelTimer > 0) {
                    levelTimer--;
                }
                timerCounter = 0;
                
                if (levelTimer <= 0 && !player.isDead()) {
                    player.die();
                }
            }
            
            Iterator<Enemy> iterator = enemies.iterator();
            while (iterator.hasNext()) {
                Enemy enemy = iterator.next();
                if (!enemy.isAlive && enemy.deathTimer <= 0) {
                    iterator.remove();
                }
            }

            checkCollisions();
            checkPowerUpCollisions();
            updateCamera();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (isGameOver) {
            drawGameOver(g2);
            g2.dispose();
            return;
        }

        if (isDisplayingLives) {
            drawLifeDisplay(g2);
            g2.dispose();
            return;
        }

        if (isVictory) {
            drawVictoryScreen(g2);
            if (System.currentTimeMillis() - victoryStartTime > victoryDelay) {
                resetGame();
            }
            g2.dispose();
            return;
        }

        if (isInMenu) {
            if (menu.isChangingName()) {
                drawNameChange(g2);
            } else {
                menu.drawMenu(g2);
            }
        } else {
            tileM.draw(g2, cameraX, cameraY);
            
            for (BlockBump blockBump : blockBumps) {
                int tileNum = tileM.mapTileNum[blockBump.tileCol][blockBump.tileRow];
                int x = blockBump.tileCol * tileSize - cameraX;
                int y = blockBump.tileRow * tileSize - cameraY + (int)blockBump.getOffsetY();
                tileM.drawSingleTile(g2, tileNum, x, y, tileSize);
            }
            
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(g2, cameraX, cameraY);
            }
            
            for (PowerUp powerUp : powerUps) {
                if (powerUp.isSpawning()) {
                    int blockCol = powerUp.worldX / tileSize;
                    int blockRow = (powerUp.worldY + tileSize) / tileSize;
                    
                    if (blockCol >= 0 && blockCol < maxWorldCol && blockRow >= 0 && blockRow < maxWorldRow) {
                        int tileNum = tileM.mapTileNum[blockCol][blockRow];
                        if (tileNum == 3) {
                            int x = blockCol * tileSize - cameraX;
                            int y = blockRow * tileSize - cameraY;
                            tileM.drawSingleTile(g2, tileNum, x, y, tileSize);
                        }
                    }
                }
            }
            
            if (castle != null) {
                castle.draw(g2, cameraX, cameraY);
            }
            
            player.draw(g2, cameraX, cameraY);
            for (Enemy enemy : enemies) {
                enemy.draw(g2, cameraX, cameraY);
            }
            
            for (BrickParticle particle : brickParticles) {
                particle.draw(g2, cameraX, cameraY);
            }
            
            for (CoinAnimation coin : coinAnimations) {
                coin.draw(g2, cameraX, cameraY);
            }
            
            for (Fireball fireball : fireballs) {
                fireball.draw(g2, cameraX, cameraY);
            }
            
            g2.setFont(gameFont.deriveFont(Font.PLAIN, 24));
            g2.setColor(Color.white);
            g2.drawString(playerName, 32, 50);

            g2.setFont(gameFont.deriveFont(Font.PLAIN, 24));
            FontMetrics fm = g2.getFontMetrics();
            int textHeight = fm.getAscent();

            int scaledWidth = textHeight;
            int scaledHeight = textHeight + 5;
            int coinX = screenWidth / 2 - 40;
            int coinY = 50;
            g2.drawImage(coinImage, coinX, coinY - textHeight - 2, scaledWidth, scaledHeight, null);

            String coinText = " X " + coinCount;
            g2.drawString(coinText, coinX + scaledWidth + 10, coinY - 4 + textHeight / 4);
            g2.drawImage(nubeImage, 1100, 16, scaledWidth + 20, scaledHeight + 10, null);
            
            g2.setFont(gameFont.deriveFont(Font.PLAIN, 20));
            if (levelTimer <= 100) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString("TIME", screenWidth - 150, 30);
            g2.drawString(String.format("%03d", levelTimer), screenWidth - 150, 55);
        }

        g2.dispose();
    }

    public void updateCamera() {
        cameraX = player.worldX - (screenWidth / 2) + (tileSize / 2);
        cameraY = player.worldY - (screenHeight / 2) + (tileSize / 2);

        cameraX = Math.max(0, Math.min(cameraX, (maxWorldCol * tileSize) - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, (maxWorldRow * tileSize) - screenHeight));
    }

    public void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (player.collisionBounds.intersects(enemy.collisionBounds) && enemy.isAlive) {
                int marioBottom = (int)(player.worldY + player.collisionBounds.height);
                int enemyTop = enemy.worldY;
                
                int marioCenterX = player.worldX + player.collisionBounds.width / 2;
                int enemyCenterX = enemy.worldX + enemy.collisionBounds.width / 2;
                
                int horizontalDistance = Math.abs(marioCenterX - enemyCenterX);
                
                if (player.velocityY > 0 && 
                    marioBottom <= enemyTop + 10 && 
                    horizontalDistance < tileSize * 0.6) {
                    
                    enemy.die();
                    player.velocityY = -5;
                } else {
                    player.die();
                    break;
                }
            }
        }
    }
    
    public void onPlayerDeathAnimationComplete() {
        lives--;
        if (lives > 0) {
            isDisplayingLives = true;
            lifeDisplayStartTime = System.currentTimeMillis();
            resetLevel();
        } else {
            isGameOver = true;
            gameOverStartTime = System.currentTimeMillis();
            soundManager.playSound(SoundManager.GAME_OVER);
        }
    }

    public void resetLevel() {
        enemies.clear();
        for (int i = 0; i < initialEnemyPositions.size(); i++) {
            Point pos = initialEnemyPositions.get(i);
            enemies.add(new Enemy(this, pos.x, pos.y));
        }

        player.setDefaultValues();
        player.killsCont = 0;

        tileM.resetInteractiveTiles();

        coinCount = 0;
        powerUps.clear();
        luckyBlocksHit = 0;
        brickParticles.clear();
        coinAnimations.clear();
        blockBumps.clear();
        fireballs.clear();
        levelTimer = 400;
        timerCounter = 0;
    }

    public void resetGame() {
        isGameOver = false;
        isVictory = false;
        lives = 3;
        player.setDefaultValues();
        player.killsCont = 0;
        resetLevel();
        isInMenu = true;
    }

    public void updateNameChange() {
        if (keyH.enterPressed) {
            menu.setChangingName(false);
            keyH.enterPressed = false;
        } else {
            if (keyH.lastKeyPressed != '\0') {
                if (keyH.lastKeyPressed == '\b' && !playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                } 
                else if (playerName.length() < 15 && keyH.lastKeyPressed != '\b') {
                    playerName += keyH.lastKeyPressed;
                }
                keyH.lastKeyPressed = '\0';
            }
        }
    }

    public void drawLifeDisplay(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.white);
        g2.setFont(gameFont);

        String worldText = "WORLD 1-1";
        int worldTextWidth = g2.getFontMetrics().stringWidth(worldText);
        g2.drawString(worldText, (screenWidth - worldTextWidth) / 2, screenHeight / 2 - 50);

        int imageX = (screenWidth / 2) - 75;
        int imageY = (screenHeight / 2);
        g2.drawImage(player.StartRight, imageX, imageY, tileSize, tileSize, null);

        String livesText = " X " + lives;
        int livesTextWidth = g2.getFontMetrics().stringWidth(livesText);
        g2.drawString(livesText, imageX + tileSize + 10, imageY + tileSize / 2 + 15);
    }

    public void drawGameOver(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setColor(Color.white);
        g2.setFont(gameFont);
        String text = "GAME OVER";
        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
    }

    public void spawnMushroom(int x, int y) {
        PowerUp powerUp = new PowerUp(this, x, y, PowerUp.PowerUpType.MUSHROOM);
        powerUps.add(powerUp);
        System.out.println("¡Hongo spawneado!");
    }
    
    public void spawnFireFlower(int x, int y) {
        PowerUp powerUp = new PowerUp(this, x, y, PowerUp.PowerUpType.FIRE_FLOWER);
        powerUps.add(powerUp);
        System.out.println("🔥 ¡Fire Flower spawneada!");
    }
    
    public void spawnBrickParticles(int x, int y) {
        Color brickColor = new Color(139, 69, 19);
        
        brickParticles.add(new BrickParticle(this, x + tileSize/4, y + tileSize/4, -3, -8, brickColor));
        
        brickParticles.add(new BrickParticle(this, x + 3*tileSize/4, y + tileSize/4, 3, -8, brickColor));
        
        brickParticles.add(new BrickParticle(this, x + tileSize/4, y + 3*tileSize/4, -2, -6, brickColor));
        
        brickParticles.add(new BrickParticle(this, x + 3*tileSize/4, y + 3*tileSize/4, 2, -6, brickColor));
    }
    
    public void checkPowerUpCollisions() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (powerUp.isActive && !powerUp.isCollected && !powerUp.isSpawning()) {
                if (player.collisionBounds.intersects(powerUp.collisionBounds)) {
                    if (powerUp.type == PowerUp.PowerUpType.FIRE_FLOWER) {
                        player.powerUpFire();
                    } else {
                        player.powerUp();
                    }
                    powerUp.collect();
                    iterator.remove();
                }
            }
        }
    }
    
    public void drawNameChange(Graphics2D g2) {
        g2.setFont(gameFont);
        g2.setColor(Color.white);

        String prompt = "INGRESE SU NOMBRE:";
        int promptWidth = g2.getFontMetrics().stringWidth(prompt);
        g2.drawString(prompt, (screenWidth - promptWidth) / 2, screenHeight / 2 - 50);

        String displayedName = playerName.isEmpty() ? "_" : playerName;
        int nameWidth = g2.getFontMetrics().stringWidth(displayedName);
        g2.drawString(displayedName, (screenWidth - nameWidth) / 2, screenHeight / 2 + 20);
    }
    
    public void drawVictoryScreen(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(gameFont);
        g2.setColor(Color.white);

        String victoryMessage = "¡VICTORIA!";
        int messageWidth = g2.getFontMetrics().stringWidth(victoryMessage);
        g2.drawString(victoryMessage, (screenWidth - messageWidth) / 2, screenHeight / 2 - 100);

        String livesText = "Vidas restantes: " + lives;
        int livesTextWidth = g2.getFontMetrics().stringWidth(livesText);
        g2.drawString(livesText, (screenWidth - livesTextWidth) / 2, screenHeight / 2 - 50);

        String coinsText = "Monedas obtenidas: " + coinCount;
        int coinsTextWidth = g2.getFontMetrics().stringWidth(coinsText);
        g2.drawString(coinsText, (screenWidth - coinsTextWidth) / 2, screenHeight / 2);

        String enemiesText = "Enemigos eliminados: " + player.killsCont;
        int enemiesTextWidth = g2.getFontMetrics().stringWidth(enemiesText);
        g2.drawString(enemiesText, (screenWidth - enemiesTextWidth) / 2, screenHeight / 2 + 50);
    }


}
