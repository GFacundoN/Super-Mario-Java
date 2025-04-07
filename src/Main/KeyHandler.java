package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    
    public boolean upPressed, downPressed, leftPressed, rightPressed, shiftPressed, enterPressed;
    public char lastKeyPressed = '\0'; // Última tecla presionada
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = true;  // Shift presionado
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
        }
     // Guardar la última tecla presionada como carácter (para nombres)
        if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '\b') {
            lastKeyPressed = e.getKeyChar();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_SHIFT) {
            shiftPressed = false;  // Shift soltado
        }
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = false;
        }
    }
}
