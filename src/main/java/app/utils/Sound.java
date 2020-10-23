// 
// Decompiled by Procyon v0.5.36
// 

package app.utils;

import app.DesktopApp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class Sound
{
    public static synchronized void playSound(final String soundName) {
        final String soundPath = "/" + soundName + ".wav";
        new Thread(() -> {
            try {
                final Clip clip = AudioSystem.getClip();
                final InputStream is = DesktopApp.class.getResourceAsStream(soundPath);
                final BufferedInputStream bufferedIS = new BufferedInputStream(is);
                final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIS);
                clip.open(audioInputStream);
                clip.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
