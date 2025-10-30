package Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<String, Clip> soundClips = new HashMap<>();
    private boolean soundEnabled = true;

    // Nombres de los sonidos del juego
    public static final String JUMP = "jump";
    public static final String JUMP_SMALL = "jumpsmall";
    public static final String COIN = "coin";
    public static final String POWER_UP = "powerup";
    public static final String ITEM_APPEAR = "item";
    public static final String BRICK_BREAK = "brick";
    public static final String BUMP = "bump";
    public static final String KICK_KILL = "kickkill";
    public static final String FIREBALL = "fireball";
    public static final String DEATH = "death";
    public static final String GAME_OVER = "gameover";
    public static final String PIPE_POWER_DOWN = "pipepowerdown";

    public SoundManager() {
        loadSounds();
    }

    /**
     * Carga todos los sonidos del juego
     */
    private void loadSounds() {
        loadSound(JUMP, "/res/sounds/jump.wav");
        loadSound(JUMP_SMALL, "/res/sounds/jumpsmall.wav");
        loadSound(COIN, "/res/sounds/coin.wav");
        loadSound(POWER_UP, "/res/sounds/powerup.wav");
        loadSound(ITEM_APPEAR, "/res/sounds/item.wav");
        loadSound(BRICK_BREAK, "/res/sounds/brick.wav");
        loadSound(BUMP, "/res/sounds/bump.wav");
        loadSound(KICK_KILL, "/res/sounds/kickkill.wav");
        loadSound(FIREBALL, "/res/sounds/fireball.wav");
        loadSound(DEATH, "/res/sounds/death.wav");
        loadSound(GAME_OVER, "/res/sounds/gameover.wav");
        loadSound(PIPE_POWER_DOWN, "/res/sounds/pipepowerdown.wav");
    }

    /**
     * Carga un sonido individual desde un archivo
     */
    private void loadSound(String name, String path) {
        try {
            URL soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.err.println("⚠️ No se encontró el archivo de sonido: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            
            soundClips.put(name, clip);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Error al cargar sonido " + name + ": " + e.getMessage());
        }
    }

    /**
     * Reproduce un efecto de sonido
     */
    public void playSound(String soundName) {
        if (!soundEnabled) {
            return;
        }
        
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            // Reproducir en un nuevo thread para evitar lag
            new Thread(() -> {
                synchronized (clip) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    clip.start();
                }
            }).start();
        } else {
            System.err.println("⚠️ Sonido no encontrado: " + soundName);
        }
    }

    /**
     * Detiene un sonido específico
     */
    public void stopSound(String soundName) {
        Clip clip = soundClips.get(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    /**
     * Detiene todos los sonidos
     */
    public void stopAllSounds() {
        for (Clip clip : soundClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
                clip.setFramePosition(0);
            }
        }
    }

    /**
     * Habilita o deshabilita los efectos de sonido
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    /**
     * Verifica si los sonidos están habilitados
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Libera los recursos de audio
     */
    public void dispose() {
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundClips.clear();
    }
}
