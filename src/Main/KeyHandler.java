package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    
    public boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed, enterPressed, xPressed;
    public char lastKeyPressed = '\0'; // Última tecla presionada
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // WASD y flechas para movimiento
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = true;  // Shift presionado
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
        if (code == KeyEvent.VK_X) {
            xPressed = true;  // Tecla X para disparar
        }
        
        // Guardar la última tecla presionada como carácter (para nombres)
        // Permitir letras, números, espacios y backspace
        if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '\b' || e.getKeyChar() == ' ') {
            lastKeyPressed = e.getKeyChar();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        // WASD y flechas para movimiento
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;  // Shift soltado
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
        if (code == KeyEvent.VK_X) {
            xPressed = false;
        }
    }
}
