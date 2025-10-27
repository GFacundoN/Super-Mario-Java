package Main;

import Entity.Enemy;
import Entity.Player;
import Entity.PowerUp;
import Entity.BrickParticle;
import Entity.CoinAnimation;
import Entity.BlockBump;
import Entity.Fireball;
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

    // Ajustes de pantalla
    public final int originalTileSize = 16;
    final int scale = 3;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 24;
    public final int maxScreenRow = 15;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // Dimensiones del mapa
    public final int maxWorldCol = 204;
    public final int maxWorldRow = 15;

    // Variables de la cámara
    int cameraX = 0;
    int cameraY = 0;

    // Frames por segundo
    int FPS = 60;

    // Manager de tiles y manejo de input
    public TileManager tileM;
    public KeyHandler keyH = new KeyHandler();
    public SpriteManager spriteManager; // Gestor de sprites
    Thread gameThread;

    // Jugador, enemigos y sistema de vidas
    public Player player; // Inicializar después de SpriteManager
    public ArrayList<Enemy> enemies = new ArrayList<>();
    private int lives = 3;
    private boolean isGameOver = false;
    public boolean isDisplayingLives = false;
    private long lifeDisplayStartTime;
    private final int lifeDisplayDuration = 4000; // Duración en milisegundos

    // Variables para la pantalla de Game Over
    private long gameOverStartTime = 0; // Marca el tiempo en que comienza la pantalla de Game Over
    private final int gameOverDelay = 5000; // 5 segundos de espera en Game Over
    
    private Font gameFont;

    public boolean isInMenu = true; // Estado del menú
    public String playerName = ""; // Nombre del jugador
    public int coinCount = 0; // Contador de monedas
    private Image coinImage;   // Imagen de la moneda
    private Image nubeImage;
    
    // Sistema de power-ups
    public ArrayList<PowerUp> powerUps = new ArrayList<>();
    public int luckyBlocksHit = 0; // Contador de lucky blocks golpeados
    
    // Sistema de partículas
    public ArrayList<BrickParticle> brickParticles = new ArrayList<>();
    
    // Efectos clásicos del Mario original
    public ArrayList<CoinAnimation> coinAnimations = new ArrayList<>();
    public ArrayList<BlockBump> blockBumps = new ArrayList<>();
    public ArrayList<Fireball> fireballs = new ArrayList<>();
    
    // Timer del nivel (como en Mario original)
    public int levelTimer = 400; // Segundos
    private int timerCounter = 0; // Contador para decrementar cada segundo (60 frames)
    
    private boolean isVictory = false; // Variable para verificar si el jugador ha ganado
    private long victoryStartTime = 0; // Tiempo en que se alcanza la victoria
    private final int victoryDelay = 5000; // 5 segundos antes de volver al menú

    // Clase Menu
    private Menu menu;

    // Agregar una lista de enemigos iniciales para reiniciarlos
    private ArrayList<Point> initialEnemyPositions = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Cargar sprites primero
        spriteManager = new SpriteManager();
        
        // Ahora sí inicializar el jugador (después de SpriteManager)
        player = new Player(this, keyH);
        
        // Inicializar el menú
        menu = new Menu(this);

        tileM = new TileManager(this);

        // Agregar enemigos en posiciones predeterminadas y almacenar sus posiciones iniciales
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

try {
    // Carga de la fuente desde los recursos
    InputStream fontStream = getClass().getResourceAsStream("/res/PressStart2P.ttf");
    if (fontStream == null) {
        throw new IOException("No se encontró la fuente PressStart2P.ttf");
    }
    gameFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.PLAIN, 24);

    // Registro de la fuente en el entorno gráfico
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    ge.registerFont(gameFont);

    // Carga de imágenes desde los recursos
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
    // Fuente y/o imágenes de fallback en caso de error
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
                // Debug: Mostrar FPS en consola (comentar en producción para mejor rendimiento)
                // System.out.println("FPS:" + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (isGameOver) {
            // Verificar si han pasado 5 segundos desde que apareció la pantalla de Game Over
            if (System.currentTimeMillis() - gameOverStartTime > gameOverDelay) {
                resetGame();  // Reiniciar el juego y volver al menú principal
            }
            return; // No hacer más actualizaciones mientras está en Game Over
        }

        if (isDisplayingLives) {
            return;
        }

        if (isInMenu) {
            if (menu.isChangingName()) {
                updateNameChange(); // Manejar la lógica de cambiar nombre
            } else {
                menu.updateMenu(); // Maneja la lógica del menú
            }
        } else {
            player.update();
            
            // Verificar si Mario ha caído por debajo del límite inferior del mapa (fila 15)
            if (!player.isDead() && player.worldY > maxWorldRow * tileSize) {
                player.die();
                // La animación de muerte se encargará del resto
            }
            
            if (player.worldX >= tileSize * 200) {
                isVictory = true;
                victoryStartTime = System.currentTimeMillis(); // Marca el inicio de la pantalla de victoria
                return; // No actualices más el juego una vez que ganaste
            }


            // Solo actualizar enemigos y power-ups si Mario NO está muerto
            if (!player.isDead()) {
                for (Enemy enemy : enemies) {
                    enemy.update();
                }
                
                // Actualizar power-ups
                for (PowerUp powerUp : powerUps) {
                    powerUp.update();
                }
            }
            
            // Actualizar partículas
            Iterator<BrickParticle> particleIterator = brickParticles.iterator();
            while (particleIterator.hasNext()) {
                BrickParticle particle = particleIterator.next();
                particle.update();
                if (!particle.isActive) {
                    particleIterator.remove();
                }
            }
            
            // Actualizar animaciones de monedas
            Iterator<CoinAnimation> coinIterator = coinAnimations.iterator();
            while (coinIterator.hasNext()) {
                CoinAnimation coin = coinIterator.next();
                coin.update();
                if (!coin.isActive) {
                    coinIterator.remove();
                }
            }
            
            // Actualizar animaciones de bloques saltando
            Iterator<BlockBump> blockIterator = blockBumps.iterator();
            while (blockIterator.hasNext()) {
                BlockBump block = blockIterator.next();
                block.update();
                if (!block.isActive) {
                    blockIterator.remove();
                }
            }
            
            // Actualizar fireballs
            Iterator<Fireball> fireballIterator = fireballs.iterator();
            while (fireballIterator.hasNext()) {
                Fireball fireball = fireballIterator.next();
                fireball.update();
                
                // Verificar colisiones con enemigos
                for (Enemy enemy : enemies) {
                    if (fireball.checkEnemyCollision(enemy)) {
                        enemy.die();
                        fireball.isActive = false;
                        player.killsCont++;
                        break;
                    }
                }
                
                // Remover fireballs inactivas
                if (!fireball.isActive) {
                    fireballIterator.remove();
                }
            }
            
            // Actualizar timer del nivel
            timerCounter++;
            if (timerCounter >= 60) { // 60 frames = 1 segundo
                if (levelTimer > 0) {
                    levelTimer--;
                }
                timerCounter = 0;
                
                // Game Over si se acaba el tiempo
                if (levelTimer <= 0 && !player.isDead()) {
                    player.die();
                }
            }
            
            // Eliminar enemigos muertos después de la iteración
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

        if (isDisplayingLives) {
            drawLifeDisplay(g2);
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

        if (isGameOver) {
            drawGameOver(g2);
            return;
        }

        // Verificar si el jugador ha ganado
        if (isVictory) {
            drawVictoryScreen(g2);
            // Volver al menú principal después de 5 segundos
            if (System.currentTimeMillis() - victoryStartTime > victoryDelay) {
                resetGame();
            }
            return;
        }

        if (isInMenu) {
            if (menu.isChangingName()) {
                drawNameChange(g2);
            } else {
                menu.drawMenu(g2);
            }
        } else {
            // Dibujar tiles normales
            tileM.draw(g2, cameraX, cameraY);
            
            // Redibujar bloques que están saltando con offset
            for (BlockBump blockBump : blockBumps) {
                int tileNum = tileM.mapTileNum[blockBump.tileCol][blockBump.tileRow];
                int x = blockBump.tileCol * tileSize - cameraX;
                int y = blockBump.tileRow * tileSize - cameraY + (int)blockBump.getOffsetY();
                tileM.drawSingleTile(g2, tileNum, x, y, tileSize);
            }
            
            // Dibujar power-ups después del mapa (encima del cielo)
            for (PowerUp powerUp : powerUps) {
                powerUp.draw(g2, cameraX, cameraY);
            }
            
            // Redibujar el lucky block encima del hongo si está spawneando
            for (PowerUp powerUp : powerUps) {
                if (powerUp.isSpawning()) {
                    // Obtener la posición del bloque
                    int blockCol = powerUp.worldX / tileSize;
                    int blockRow = (powerUp.worldY + tileSize) / tileSize; // Bloque arriba del hongo
                    
                    // Redibujar solo ese tile específico
                    if (blockCol >= 0 && blockCol < maxWorldCol && blockRow >= 0 && blockRow < maxWorldRow) {
                        int tileNum = tileM.mapTileNum[blockCol][blockRow];
                        if (tileNum == 3) { // Lucky block roto
                            int x = blockCol * tileSize - cameraX;
                            int y = blockRow * tileSize - cameraY;
                            tileM.drawSingleTile(g2, tileNum, x, y, tileSize);
                        }
                    }
                }
            }
            
            player.draw(g2, cameraX, cameraY);
            for (Enemy enemy : enemies) {
                enemy.draw(g2, cameraX, cameraY);
            }
            
            // Dibujar partículas de ladrillos
            for (BrickParticle particle : brickParticles) {
                particle.draw(g2, cameraX, cameraY);
            }
            
            // Dibujar animaciones de monedas
            for (CoinAnimation coin : coinAnimations) {
                coin.draw(g2, cameraX, cameraY);
            }
            
            // Dibujar fireballs
            for (Fireball fireball : fireballs) {
                fireball.draw(g2, cameraX, cameraY);
            }
            
            // Dibujar HUD y otros elementos de interfaz
            g2.setFont(gameFont.deriveFont(Font.PLAIN, 24));
            g2.setColor(Color.white);
            g2.drawString(playerName, 32, 50);

            // Dibujar el contador de monedas en la parte superior central
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
            
            // Timer (arriba derecha) - Color rojo si queda poco tiempo
            g2.setFont(gameFont.deriveFont(Font.PLAIN, 20));
            if (levelTimer <= 100) {
                g2.setColor(Color.RED); // Rojo cuando queda poco tiempo
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString("TIME", screenWidth - 150, 30);
            g2.drawString(String.format("%03d", levelTimer), screenWidth - 150, 55);
        }

        g2.dispose();
    }


    // Actualización de la posición de la cámara según el jugador
    public void updateCamera() {
        cameraX = player.worldX - (screenWidth / 2) + (tileSize / 2);
        cameraY = player.worldY - (screenHeight / 2) + (tileSize / 2);

        cameraX = Math.max(0, Math.min(cameraX, (maxWorldCol * tileSize) - screenWidth));
        cameraY = Math.max(0, Math.min(cameraY, (maxWorldRow * tileSize) - screenHeight));
    }

    // Manejo de colisiones del jugador con los enemigos
    public void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (player.collisionBounds.intersects(enemy.collisionBounds) && enemy.isAlive) {
                // Calcular la parte inferior de Mario y la parte superior del enemigo
                int marioBottom = (int)(player.worldY + player.collisionBounds.height);
                int enemyTop = enemy.worldY;
                
                // Calcular el centro horizontal de Mario y del enemigo
                int marioCenterX = (int)(player.worldX + player.collisionBounds.width / 2);
                int enemyCenterX = enemy.worldX + enemy.collisionBounds.width / 2;
                
                // Distancia horizontal entre centros
                int horizontalDistance = Math.abs(marioCenterX - enemyCenterX);
                
                // Mario mata al enemigo si:
                // 1. Está cayendo (velocityY > 0)
                // 2. La parte inferior de Mario está cerca de la parte superior del enemigo
                // 3. Mario está razonablemente centrado sobre el enemigo (no en los extremos)
                if (player.velocityY > 0 && 
                    marioBottom <= enemyTop + 10 && 
                    horizontalDistance < tileSize * 0.6) { // 60% del ancho del tile
                    
                    enemy.die();
                    player.velocityY = -5; // Pequeño rebote
                } else {
                    // Colisión lateral o desde abajo - Mario muere
                    player.die();
                    // No hacer nada más aquí, la animación se encargará
                    break;
                }
            }
        }
    }
    
    // Método llamado cuando termina la animación de muerte de Mario
    public void onPlayerDeathAnimationComplete() {
        lives--;
        if (lives > 0) {
            isDisplayingLives = true;
            lifeDisplayStartTime = System.currentTimeMillis();
            resetLevel(); // Reiniciar el nivel cuando Mario pierde una vida
        } else {
            isGameOver = true;
            gameOverStartTime = System.currentTimeMillis(); // Iniciar el temporizador de Game Over
        }
    }

    // Método para reiniciar el nivel completo
    public void resetLevel() {
        // Reiniciar los enemigos a su posición original
        enemies.clear();
        for (int i = 0; i < initialEnemyPositions.size(); i++) {
            Point pos = initialEnemyPositions.get(i);
            enemies.add(new Enemy(this, pos.x, pos.y)); // Volver a colocar enemigos
        }

        // Reiniciar el jugador
        player.setDefaultValues();
        player.killsCont = 0; // Resetear contador de kills

        // Reiniciar el estado de los bloques interactivos
        tileM.resetInteractiveTiles();

        // Reiniciar el contador de monedas y power-ups
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

    // Método para reiniciar el juego completo
    public void resetGame() {
        isGameOver = false; // Salir del estado de Game Over
        isVictory = false;  // Salir del estado de victoria
        lives = 3;          // Restablecer las vidas del jugador
        player.setDefaultValues(); // Restablecer los valores del jugador
        player.killsCont = 0; // Resetear contador de kills
        resetLevel();       // Reiniciar el nivel
        isInMenu = true;    // Volver al menú principal
    }

    // Función para manejar la actualización del nombre del jugador
    public void updateNameChange() {
        if (keyH.enterPressed) {
            // Confirmar y salir del modo de cambiar nombre
            menu.setChangingName(false);  // Ahora manejamos el cambio de nombre desde la clase Menu
            keyH.enterPressed = false;
        } else {
            if (keyH.lastKeyPressed != '\0') {
                // Soporte para borrar texto con Backspace
                if (keyH.lastKeyPressed == '\b' && !playerName.isEmpty()) {
                    playerName = playerName.substring(0, playerName.length() - 1);
                } 
                // Agregar caracteres normales si no es Backspace y no excede el límite de 15 caracteres
                else if (playerName.length() < 15 && keyH.lastKeyPressed != '\b') {
                    playerName += keyH.lastKeyPressed;
                }
                keyH.lastKeyPressed = '\0'; // Limpiar la última tecla para evitar repeticiones
            }
        }
    }

    // Función para dibujar la pantalla de vidas después de que el jugador pierde una vida
    public void drawLifeDisplay(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.white);
        g2.setFont(gameFont);

        // Dibujar "World 1-1"
        String worldText = "WORLD 1-1";
        int worldTextWidth = g2.getFontMetrics().stringWidth(worldText);
        g2.drawString(worldText, (screenWidth - worldTextWidth) / 2, screenHeight / 2 - 50);

        // Dibujar la imagen de Mario y las vidas restantes
        int imageX = (screenWidth / 2) - 75;
        int imageY = (screenHeight / 2);
        g2.drawImage(player.StartRight, imageX, imageY, tileSize, tileSize, null);

        // Dibujar el texto de "x" y la cantidad de vidas
        String livesText = " X " + lives;
        int livesTextWidth = g2.getFontMetrics().stringWidth(livesText);
        g2.drawString(livesText, imageX + tileSize + 10, imageY + tileSize / 2 + 15);
    }

    // Función para dibujar la pantalla de Game Over
    public void drawGameOver(Graphics2D g2) {
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        g2.setColor(Color.white);
        g2.setFont(gameFont);
        String text = "GAME OVER";
        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
    }

    // Método para spawner un power-up (por defecto hongo)
    public void spawnMushroom(int x, int y) {
        PowerUp powerUp = new PowerUp(this, x, y, PowerUp.PowerUpType.MUSHROOM);
        powerUps.add(powerUp);
        System.out.println("¡Hongo spawneado!");
    }
    
    // Método para spawner Fire Flower
    public void spawnFireFlower(int x, int y) {
        PowerUp powerUp = new PowerUp(this, x, y, PowerUp.PowerUpType.FIRE_FLOWER);
        powerUps.add(powerUp);
        System.out.println("🔥 ¡Fire Flower spawneada!");
    }
    
    // Método para spawner partículas de ladrillo roto
    public void spawnBrickParticles(int x, int y) {
        // Crear 4 partículas que vuelan en diferentes direcciones
        Color brickColor = new Color(139, 69, 19); // Color marrón ladrillo
        
        // Partícula arriba-izquierda
        brickParticles.add(new BrickParticle(this, x + tileSize/4, y + tileSize/4, -3, -8, brickColor));
        
        // Partícula arriba-derecha
        brickParticles.add(new BrickParticle(this, x + 3*tileSize/4, y + tileSize/4, 3, -8, brickColor));
        
        // Partícula abajo-izquierda
        brickParticles.add(new BrickParticle(this, x + tileSize/4, y + 3*tileSize/4, -2, -6, brickColor));
        
        // Partícula abajo-derecha
        brickParticles.add(new BrickParticle(this, x + 3*tileSize/4, y + 3*tileSize/4, 2, -6, brickColor));
    }
    
    // Método para verificar colisiones con power-ups
    public void checkPowerUpCollisions() {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            // Solo colisionar si NO está spawneando (ya salió del bloque completamente)
            if (powerUp.isActive && !powerUp.isCollected && !powerUp.isSpawning()) {
                if (player.collisionBounds.intersects(powerUp.collisionBounds)) {
                    // Aplicar el power-up según su tipo
                    if (powerUp.type == PowerUp.PowerUpType.FIRE_FLOWER) {
                        player.powerUpFire(); // Dar poder de fuego
                    } else {
                        player.powerUp(); // Hacer a Mario grande (hongo)
                    }
                    powerUp.collect();
                    iterator.remove();
                }
            }
        }
    }
    
    // Función para dibujar el nombre del jugador durante el cambio de nombre
    public void drawNameChange(Graphics2D g2) {
        g2.setFont(gameFont);
        g2.setColor(Color.white);

        // Mostrar el texto "INGRESE SU NOMBRE:"
        String prompt = "INGRESE SU NOMBRE:";
        int promptWidth = g2.getFontMetrics().stringWidth(prompt);
        g2.drawString(prompt, (screenWidth - promptWidth) / 2, screenHeight / 2 - 50);

        // Dibujar el nombre que se está ingresando en tiempo real
        String displayedName = playerName.isEmpty() ? "_" : playerName; // Si está vacío, mostrar un cursor
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

        // Mostrar las vidas restantes
        String livesText = "Vidas restantes: " + lives;
        int livesTextWidth = g2.getFontMetrics().stringWidth(livesText);
        g2.drawString(livesText, (screenWidth - livesTextWidth) / 2, screenHeight / 2 - 50);

        // Mostrar la cantidad de monedas obtenidas
        String coinsText = "Monedas obtenidas: " + coinCount;
        int coinsTextWidth = g2.getFontMetrics().stringWidth(coinsText);
        g2.drawString(coinsText, (screenWidth - coinsTextWidth) / 2, screenHeight / 2);

        // Mostrar la cantidad de enemigos eliminados
        String enemiesText = "Enemigos eliminados: " + player.killsCont;
        int enemiesTextWidth = g2.getFontMetrics().stringWidth(enemiesText);
        g2.drawString(enemiesText, (screenWidth - enemiesTextWidth) / 2, screenHeight / 2 + 50);
    }


}
