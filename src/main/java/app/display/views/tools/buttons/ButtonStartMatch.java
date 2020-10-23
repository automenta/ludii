// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.views.tools.ToolButton;
import app.display.views.tools.ToolView;
import manager.Manager;
import manager.network.SettingsNetwork;
import util.Context;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class ButtonStartMatch extends ToolButton
{
    public ButtonStartMatch(final int cx, final int cy, final int sx, final int sy) {
        super("Start", cx, cy, sx, sy);
        this.tooltipMessage = "Back to Start";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        GeneralPath path = new GeneralPath();
        path.moveTo((this.cx + 10), (this.cy + 7));
        path.lineTo((this.cx + 0), (this.cy + 0));
        path.lineTo((this.cx + 10), (this.cy - 7));
        g2d.draw(path);
        g2d.setStroke(new BasicStroke(2.0f, 0, 1));
        path = new GeneralPath();
        path.moveTo((this.cx - 4), (this.cy + 9));
        path.lineTo((this.cx - 4), (this.cy - 9));
        g2d.draw(path);
        path = new GeneralPath();
        path.moveTo((this.cx - 8), (this.cy + 9));
        path.lineTo((this.cx - 8), (this.cy - 9));
        g2d.draw(path);
    }
    
    @Override
    protected boolean isEnabled() {
        final Context context = Manager.ref().context();
        final int numInitialPlacementMoves = context.currentInstanceContext().trial().numInitialPlacementMoves();
        return context.isAMatch() && (context.currentSubgameIdx() > 1 || context.trial().moves().size() > numInitialPlacementMoves) && SettingsNetwork.getActiveGameId() == 0;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            ToolView.jumpToMove(0);
        }
    }
}
