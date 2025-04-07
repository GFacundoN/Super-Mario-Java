package Main;

import Entity.Enemy;
import Entity.Player;
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
    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    
    // Hilo para ejecutar el juego
    Thread gameThread;

    // Jugador, enemigos y sistema de vidas
    public Player player = new Player(this, keyH);
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

        // Inicializar el menú
        menu = new Menu(this);

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
                System.out.println("FPS:" + drawCount);
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
            if (player.worldY > maxWorldRow * tileSize) {
                player.die();
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
            
            if (player.worldX >= tileSize * 200) {
                isVictory = true;
                victoryStartTime = System.currentTimeMillis(); // Marca el inicio de la pantalla de victoria
                return; // No actualices más el juego una vez que ganaste
            }


            for (Enemy enemy : enemies) {
                enemy.update();
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
            tileM.draw(g2, cameraX, cameraY);
            player.draw(g2, cameraX, cameraY);
            for (Enemy enemy : enemies) {
                enemy.draw(g2, cameraX, cameraY);
            }

            // Dibujar la cuadrícula de tiles
            g2.setColor(new Color(255, 255, 255, 100)); // Color blanco semi-transparente

            // Dibujar líneas verticales de la cuadrícula
            for (int x = 0; x <= screenWidth; x += tileSize) {
                int drawX = x - (cameraX % tileSize); // Ajustar según la posición de la cámara
                g2.drawLine(drawX, 0, drawX, screenHeight);
            }

            // Dibujar líneas horizontales de la cuadrícula
            for (int y = 0; y <= screenHeight; y += tileSize) {
                int drawY = y - (cameraY % tileSize); // Ajustar según la posición de la cámara
                g2.drawLine(0, drawY, screenWidth, drawY);
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
                if (player.worldY + player.collisionBounds.height - 1 < enemy.worldY) {
                    enemy.die();
                    player.worldY -= tileSize / 4;
                } else {
                    player.die();
                    lives--;
                    if (lives > 0) {
                        isDisplayingLives = true;
                        lifeDisplayStartTime = System.currentTimeMillis();
                        resetLevel(); // Reiniciar el nivel cuando Mario pierde una vida
                    } else {
                        isGameOver = true;
                        gameOverStartTime = System.currentTimeMillis(); // Iniciar el temporizador de Game Over
                    }
                    break;
                }
            }
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

        // Reiniciar el estado de los bloques interactivos
        tileM.resetInteractiveTiles();

        // Reiniciar el contador de monedas
        coinCount = 0;
    }

    // Método para reiniciar el juego completo
    public void resetGame() {
        isGameOver = false; // Salir del estado de Game Over
        lives = 3;          // Restablecer las vidas del jugador
        player.setDefaultValues(); // Restablecer los valores del jugador
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
