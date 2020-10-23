// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import app.display.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SpinnerFunctions
{
    private static Timer spinTimer;
    static int spinTicks;
    private static final List<Point2D.Double> spinPts;
    static final Rectangle spinRect;
    static MainWindow mainWindow;
    static ActionListener spinTimerAction;
    
    public static void startSpinner() {
        if (SpinnerFunctions.spinTimer != null) {
            return;
        }
        (SpinnerFunctions.spinTimer = new Timer(25, SpinnerFunctions.spinTimerAction)).start();
    }
    
    public static void stopSpinner() {
        if (SpinnerFunctions.spinTimer != null) {
            SpinnerFunctions.spinTimer.stop();
            SpinnerFunctions.spinTimer = null;
        }
        SpinnerFunctions.spinTicks = -1;
        if (SpinnerFunctions.mainWindow != null) {
            SpinnerFunctions.mainWindow.paintImmediately(SpinnerFunctions.spinRect.x - 10, SpinnerFunctions.spinRect.y - 10, SpinnerFunctions.spinRect.x + SpinnerFunctions.spinRect.width + 20, SpinnerFunctions.spinRect.y + SpinnerFunctions.spinRect.height + 20);
        }
    }
    
    public static void initialiseSpinnerGraphics(final MainWindow view) {
        SpinnerFunctions.mainWindow = view;
        final int cx = 20;
        final int cy = 20;
        final double r = 10.0;
        final int numPts = 12;
        for (int n = 0; n < 12; ++n) {
            final double t = n / 11.0;
            final double x = 20.0 + 10.0 * Math.sin(t * 2.0 * 3.141592653589793);
            final double y = 20.0 - 10.0 * Math.cos(t * 2.0 * 3.141592653589793);
            SpinnerFunctions.spinPts.add(new Point2D.Double(x, y));
        }
        SpinnerFunctions.spinRect.setBounds(0, 0, 40, 40);
    }
    
    public static void drawSpinner(final Graphics2D g2d) {
        if (SpinnerFunctions.spinTicks < 0) {
            return;
        }
        final double r = 2.25;
        g2d.setColor(new Color(180, 180, 180));
        for (int n = 0; n < SpinnerFunctions.spinPts.size(); ++n) {
            final int pid = SpinnerFunctions.spinTicks % SpinnerFunctions.spinPts.size();
            final Point2D.Double pt = SpinnerFunctions.spinPts.get(pid);
            final Shape dot = new Arc2D.Double(pt.x - 2.25, pt.y - 2.25, 5.5, 5.5, 0.0, 360.0, 0);
            g2d.fill(dot);
        }
    }
    
    static {
        SpinnerFunctions.spinTimer = null;
        SpinnerFunctions.spinTicks = -1;
        spinPts = new ArrayList<>();
        spinRect = new Rectangle();
        SpinnerFunctions.mainWindow = null;
        SpinnerFunctions.spinTimerAction = e -> {
            ++SpinnerFunctions.spinTicks;
            SpinnerFunctions.mainWindow.paintImmediately(SpinnerFunctions.spinRect.x - 10, SpinnerFunctions.spinRect.y - 10, SpinnerFunctions.spinRect.x + SpinnerFunctions.spinRect.width + 20, SpinnerFunctions.spinRect.y + SpinnerFunctions.spinRect.height + 20);
        };
    }
}
