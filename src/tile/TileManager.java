package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Main.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][]; 
    private ImageIcon gifIcon; // Se mantiene el ImageIcon para manejar el GIF

    // Agregar un array para guardar el estado inicial del mapa
    private int[][] initialMapTileNum; 


    public TileManager(GamePanel gp){
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        initialMapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; // Inicializar el array de estado inicial

        getTileImage();
        loadMap("/res/map01.txt");

    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].Image = ImageIO.read(getClass().getResourceAsStream("/res/piso.png"));
            tile[0].collision = true;

            tile[1] = new Tile();
            tile[1].Image = ImageIO.read(getClass().getResourceAsStream("/res/muro.png"));
            tile[1].collision = true;

            // Usar ImageIcon para cargar el GIF animado
            tile[2] = new Tile();
            gifIcon = new ImageIcon(getClass().getResource("/res/lucky.gif")); 
            tile[2].collision = true;

            tile[3] = new Tile();
            tile[3].Image = ImageIO.read(getClass().getResourceAsStream("/res/lucky_roto.png"));
            tile[3].collision = true;

            tile[4] = new Tile();
            tile[4].Image = ImageIO.read(getClass().getResourceAsStream("/res/escalera.png"));
            tile[4].collision = true;
            
            tile[5] = new Tile();
            tile[5].Image = ImageIO.read(getClass().getResourceAsStream("/res/cielo.png"));

            tile[6] = new Tile();
            tile[6].Image = ImageIO.read(getClass().getResourceAsStream("/res/cañeria_arriba_izq.png"));
            tile[6].collision = true;

            tile[7] = new Tile();
            tile[7].Image = ImageIO.read(getClass().getResourceAsStream("/res/cañeria_abajo_izq.png"));
            tile[7].collision = true;

            tile[8] = new Tile();
            tile[8].Image = ImageIO.read(getClass().getResourceAsStream("/res/cañeria_arriba_der.png"));
            tile[8].collision = true;

            tile[9] = new Tile();
            tile[9].Image = ImageIO.read(getClass().getResourceAsStream("/res/cañeria_abajo_der.png"));
            tile[9].collision = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Modificar la carga del mapa para almacenar el estado inicial del nivel
    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.out.println("Map file not found: " + filePath);
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            for (int row = 0; row < gp.maxWorldRow; row++) {
                String line = br.readLine();
                String[] numbers = line.split(" ");

                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    initialMapTileNum[col][row] = num; // Guardar el estado inicial
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para restablecer los tiles interactivos a su estado inicial
    public void resetInteractiveTiles() {
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                // Restablecer los lucky blocks o cualquier tile interactivo
                mapTileNum[col][row] = initialMapTileNum[col][row];
            }
        }
    }

    public void draw(Graphics2D g2, int cameraX, int cameraY) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - cameraX;
            int screenY = worldY - cameraY;

            if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {

                if (tileNum == 2) {
                    g2.drawImage(gifIcon.getImage(), screenX, screenY, gp.tileSize, gp.tileSize, null);

                } else {
                    g2.drawImage(tile[tileNum].Image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }

            }
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
    
    // Método para dibujar un solo tile en una posición específica
    public void drawSingleTile(Graphics2D g2, int tileNum, int screenX, int screenY, int size) {
        if (tileNum == 2) {
            g2.drawImage(gifIcon.getImage(), screenX, screenY, size, size, null);
        } else {
            g2.drawImage(tile[tileNum].Image, screenX, screenY, size, size, null);
        }
    }
}
