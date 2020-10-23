// 
// Decompiled by Procyon v0.5.36
// 

package app;

import app.display.MainWindow;
import app.display.views.tabs.TabView;
import app.display.views.tools.ToolButton;
import game.util.directions.AbsoluteDirection;
import manager.Manager;
import manager.referee.MoveUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class JFrameList extends JFrame implements KeyListener
{
    private static final long serialVersionUID = 1L;
    public List<ToolButton> buttons;
    public DesktopApp app;
    
    JFrameList(final String appName) {
        super(appName);
        this.buttons = new ArrayList<>();
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == 82) {
            ++MainWindow.currentWalkExtra;
            Manager.app.repaint();
        }
        else if (e.getKeyCode() == 32) {
            DesktopApp.frame().buttons.get(7).press();
        }
        else if (e.getKeyCode() == 37) {
            DesktopApp.frame().buttons.get(6).press();
        }
        else if (e.getKeyCode() == 39) {
            DesktopApp.frame().buttons.get(8).press();
        }
        else if (e.getKeyCode() == 40) {
            DesktopApp.frame().buttons.get(5).press();
        }
        else if (e.getKeyCode() == 38) {
            DesktopApp.frame().buttons.get(9).press();
        }
        else if (e.getKeyCode() == 9) {
            int nextTabIndex = TabView.selected() + 1;
            if (nextTabIndex >= MainWindow.tabPanel().pages().size()) {
                nextTabIndex = 0;
            }
            MainWindow.tabPanel().select(nextTabIndex);
        }
        else if (e.getKeyCode() == 104) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.N);
        }
        else if (e.getKeyCode() == 100) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.W);
        }
        else if (e.getKeyCode() == 98) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.S);
        }
        else if (e.getKeyCode() == 102) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.E);
        }
        else if (e.getKeyCode() == 97) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.SW);
        }
        else if (e.getKeyCode() == 99) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.SE);
        }
        else if (e.getKeyCode() == 103) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.NW);
        }
        else if (e.getKeyCode() == 105) {
            MoveUtil.applyDirectionMove(AbsoluteDirection.NE);
        }
    }
    
    @Override
    public void keyReleased(final KeyEvent arg0) {
    }
    
    @Override
    public void keyTyped(final KeyEvent arg0) {
    }
}
