package Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class MusicManager {
    private Clip currentMusic;
    private boolean musicEnabled = true;
    private FloatControl volumeControl;
    private String currentMusicPath;
    
    public static final String GROUND_THEME = "/res/sounds/01.-Ground-Theme.wav";
    
    public void playMusic(String musicPath, boolean loop) {
        if (!musicEnabled) {
            return;
        }
        
        if (musicPath.equals(currentMusicPath) && currentMusic != null) {
            return;
        }
        
        try {
            stopMusic();
            
            URL musicURL = getClass().getResource(musicPath);
            if (musicURL == null) {
                System.err.println("⚠️ Música no encontrada: " + musicPath);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicURL);
            currentMusic = AudioSystem.getClip();
            currentMusic.open(audioStream);
            
            if (currentMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) currentMusic.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-10.0f);
            }
            
            if (loop) {
                currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                currentMusic.start();
            }
            
            currentMusicPath = musicPath;
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("⚠️ Error al reproducir música: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopMusic() {
        if (currentMusic != null) {
            if (currentMusic.isRunning()) {
                currentMusic.stop();
            }
            currentMusic.close();
            currentMusic = null;
            currentMusicPath = null;
        }
    }
    
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
        }
    }
    
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isRunning()) {
            currentMusic.start();
        }
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            stopMusic();
        }
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isRunning();
    }
    
    public void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float value = min + (max - min) * volume;
            volumeControl.setValue(value);
        }
    }
}
