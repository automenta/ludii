// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools;

import app.DesktopApp;
import app.display.views.View;
import app.display.views.tools.buttons.*;
import app.game.GameRestart;
import app.utils.SettingsDesktop;
import manager.Manager;
import manager.utils.SettingsManager;
import util.Context;
import util.SettingsVC;
import util.Trial;
import util.locations.FullLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ToolView extends View
{
    public List<ToolButton> buttons;
    public static final int PASS_BUTTON_INDEX = 0;
    public static final int SWAP_BUTTON_INDEX = 1;
    public static final int OTHER_BUTTON_INDEX = 2;
    public static final int STARTMATCH_BUTTON_INDEX = 4;
    public static final int START_BUTTON_INDEX = 5;
    public static final int BACK_BUTTON_INDEX = 6;
    public static final int PLAY_BUTTON_INDEX = 7;
    public static final int FORWARD_BUTTON_INDEX = 8;
    public static final int END_BUTTON_INDEX = 9;
    public static final int ENDMATCH_BUTTON_INDEX = 10;
    public static final int SETTINGS_BUTTON_INDEX = 12;
    public static final int INFO_BUTTON_INDEX = 13;
    
    public ToolView() {
        this.buttons = new ArrayList<>();
        final int toolHeight = 40;
        int startX;
        final int boardSize = startX = DesktopApp.view().boardSize();
        final int startY = DesktopApp.view().getHeight() - 40 - 6;
        int width = DesktopApp.view().getWidth() - boardSize;
        final int height = 40;
        if (DesktopApp.view().getWidth() < DesktopApp.view().getHeight()) {
            startX = 0;
            width = DesktopApp.view().getWidth();
        }
        this.placement.setBounds(startX, startY, width, 40);
        int cx = 0;
        int cy = this.placement.y + 20;
        final int scaleUpFactor = 30;
        cy = cy + 30 - 30;
        final int sx = 40;
        final int sy = 40;
        this.buttons.add(new ButtonPass(cx, cy, 40, 40));
        this.buttons.add(new ButtonSwap(cx, cy, 40, 40));
        this.buttons.add(new ButtonOther(cx, cy, 40, 40));
        this.buttons.add(null);
        this.buttons.add(new ButtonStartMatch(cx, cy, 40, 40));
        this.buttons.add(new ButtonStart(cx, cy, 40, 40));
        this.buttons.add(new ButtonBack(cx, cy, 40, 40));
        this.buttons.add(new ButtonPlayPause(cx, cy, 40, 40));
        this.buttons.add(new ButtonForward(cx, cy, 40, 40));
        this.buttons.add(new ButtonEnd(cx, cy, 40, 40));
        this.buttons.add(new ButtonEndMatch(cx, cy, 40, 40));
        this.buttons.add(null);
        this.buttons.add(new ButtonSettings(cx, cy, 40, 40));
        this.buttons.add(new ButtonInfo(cx, cy, 40, 40));
        this.buttons.add(null);
        final double spacing = this.placement.width / (double)this.buttons.size();
        for (int b = 0; b < this.buttons.size(); ++b) {
            if (this.buttons.get(b) != null) {
                cx = this.placement.x + (int)((b + 0.25) * spacing) + 10;
                this.buttons.get(b).setPosition(cx, cy);
            }
        }
        DesktopApp.frame().buttons = this.buttons;
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        for (final ToolButton button : this.buttons) {
            if (button != null) {
                button.draw(g2d);
            }
        }
        this.paintDebug(g2d, Color.BLUE);
    }
    
    public void clickAt(final Point pixel) {
        for (final ToolButton button : this.buttons) {
            if (button != null && button.hit(pixel.x, pixel.y)) {
                button.press();
            }
        }
    }
    
    @Override
    public void mouseOverAt(final Point pixel) {
        for (final ToolButton button : this.buttons) {
            if (button == null) {
                continue;
            }
            if (button.hit(pixel.x, pixel.y)) {
                if (button.mouseOver()) {
                    continue;
                }
                button.setMouseOver(true);
                DesktopApp.view().repaint(button.rect());
            }
            else {
                if (!button.mouseOver()) {
                    continue;
                }
                button.setMouseOver(false);
                DesktopApp.view().repaint(button.rect());
            }
        }
    }
    
    public static void jumpToMove(final int moveToJumpTo) {
        final BufferedImage image = new BufferedImage(DesktopApp.view().getWidth(), DesktopApp.view().getHeight(), 1);
        final Graphics2D graphics2D = image.createGraphics();
        DesktopApp.view().paint(graphics2D);
        SettingsDesktop.jumpingMoveSavedImage = image;
        SettingsManager.agentsPaused = true;
        Manager.ref().interruptAI();
        final Context context = Manager.ref().context();
        if (Manager.savedTrial() == null) {
            final Trial savedTrial = new Trial(context.trial());
            DesktopApp.setSavedTrial(savedTrial);
        }
        GameRestart.clearBoard();
        int moveToJumpToWithSetup;
        if (moveToJumpTo == 0) {
            moveToJumpToWithSetup = context.currentInstanceContext().trial().numInitialPlacementMoves();
        }
        else {
            moveToJumpToWithSetup = moveToJumpTo;
        }
        for (int i = context.trial().moves().size(); i < moveToJumpToWithSetup; ++i) {
            Manager.ref().makeSavedMove(Manager.savedTrial().moves().get(i));
            final int moveNumber = context.currentInstanceContext().trial().numMoves() - 1;
            if (context.trial().over() || (context.isAMatch() && moveNumber < context.currentInstanceContext().trial().numInitialPlacementMoves())) {
                DesktopApp.playerApp().gameOverTasks();
            }
        }
        context.game().incrementGameStartCount();
        EventQueue.invokeLater(() -> {
            Manager.app.updateTabs(context);
            SettingsDesktop.jumpingMoveSavedImage = null;
            return;
        });
        SettingsVC.selectedLocation = new FullLocation(-1);
    }
}
