package tile;

import java.awt.image.BufferedImage;

public class Tile {
     public BufferedImage Image;
     public boolean collision = false;
     
     public boolean isBrick = false; // Indica si es un bloque de ladrillo
     public boolean isAnimated = false; // Si está siendo animado
     public int animationOffset = 0; // Desplazamiento durante la animación
     public long animationStartTime = 0; // Momento en el que comenzó la animación
}
