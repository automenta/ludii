// 
// Decompiled by Procyon v0.5.36
// 

package utils.experiments;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;

public abstract class InterruptableExperiment
{
    protected boolean interrupted;
    protected final long experimentStartTime;
    protected final long maxWallTimeMs;
    
    public InterruptableExperiment(final boolean useGUI) {
        this(useGUI, -1);
    }
    
    public InterruptableExperiment(final boolean useGUI, final int maxWallTime) {
        this.interrupted = false;
        JFrame frame = null;
        this.experimentStartTime = System.currentTimeMillis();
        this.maxWallTimeMs = 60000L * maxWallTime;
        if (useGUI) {
            frame = new JFrame("Ludii Interruptible Experiment");
            frame.setDefaultCloseOperation(0);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    InterruptableExperiment.this.interrupted = true;
                }
            });
            try {
                final URL resource = this.getClass().getResource("/ludii-logo-100x100.png");
                final BufferedImage image = ImageIO.read(resource);
                frame.setIconImage(image);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            final JPanel panel = new JPanel(new GridLayout());
            final JButton interruptButton = new JButton("Interrupt Experiment");
            interruptButton.addActionListener(e -> InterruptableExperiment.this.interrupted = true);
            panel.add(interruptButton);
            frame.setContentPane(panel);
            frame.setSize(600, 250);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
        try {
            this.runExperiment();
        }
        finally {
            if (frame != null) {
                frame.dispose();
            }
        }
    }
    
    public abstract void runExperiment();
    
    public void checkWallTime(final double safetyBuffer) {
        if (this.maxWallTimeMs > 0L) {
            final long terminateAt = (long)(this.experimentStartTime + (1.0 - safetyBuffer) * this.maxWallTimeMs);
            if (System.currentTimeMillis() >= terminateAt) {
                this.interrupted = true;
            }
        }
    }
    
    public void logLine(final PrintWriter logWriter, final String line) {
        if (logWriter != null) {
            logWriter.println(String.format("[%s]: %s", LocalDateTime.now(), line));
        }
    }
}
