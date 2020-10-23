// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.DesktopApp;
import app.display.views.tools.ToolButton;
import app.game.GameRestart;
import game.types.play.ModeType;
import manager.Manager;
import manager.network.SettingsNetwork;
import manager.utils.SettingsManager;
import util.Move;
import util.SettingsVC;
import util.locations.FullLocation;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

public class ButtonPlayPause extends ToolButton
{
    public ButtonPlayPause(final int cx, final int cy, final int sx, final int sy) {
        super("PlayPause", cx, cy, sx, sy);
        this.tooltipMessage = "Player/Pause";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        GeneralPath path = new GeneralPath();
        if (SettingsManager.agentsPaused) {
            path.moveTo((this.cx + 9), this.cy);
            path.lineTo((this.cx - 7), (this.cy - 9));
            path.lineTo((this.cx - 7), (this.cy + 9));
        }
        else {
            path.moveTo((this.cx - 7), (this.cy + 9));
            path.lineTo((this.cx - 7), (this.cy - 9));
            path.lineTo((this.cx - 2), (this.cy - 9));
            path.lineTo((this.cx - 2), (this.cy + 9));
            g2d.fill(path);
            path = new GeneralPath();
            path.moveTo((this.cx + 2), (this.cy + 9));
            path.lineTo((this.cx + 2), (this.cy - 9));
            path.lineTo((this.cx + 7), (this.cy - 9));
            path.lineTo((this.cx + 7), (this.cy + 9));
        }
        g2d.fill(path);
    }
    
    @Override
    protected boolean isEnabled() {
        if (Manager.ref().context().game().mode().mode() == ModeType.Simulation) {
            return true;
        }
        boolean AnyAIPlayer = false;
        for (int i = 0; i < DesktopApp.aiSelected().length; ++i) {
            if (DesktopApp.aiSelected()[i].ai() != null) {
                AnyAIPlayer = true;
            }
        }
        return AnyAIPlayer && SettingsNetwork.getActiveGameId() == 0;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            if (Manager.savedTrial() != null) {
                final List<Move> tempActions = new ArrayList<>(Manager.ref().context().trial().moves());
                GameRestart.restartGame(false);
                for (int i = Manager.ref().context().trial().moves().size(); i < tempActions.size(); ++i) {
                    Manager.ref().makeSavedMove(tempActions.get(i));
                }
                SettingsManager.agentsPaused = false;
                Manager.ref().nextMove(false);
                SettingsVC.selectedLocation = new FullLocation(-1);
            }
            else if (!SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = true;
                Manager.ref().interruptAI();
                SettingsVC.selectedLocation = new FullLocation(-1);
            }
            else if (SettingsManager.agentsPaused) {
                SettingsManager.agentsPaused = false;
                Manager.ref().nextMove(false);
                SettingsVC.selectedLocation = new FullLocation(-1);
            }
            if (!SettingsManager.agentsPaused) {
                SettingsVC.sandboxMode = false;
            }
        }
    }
}
