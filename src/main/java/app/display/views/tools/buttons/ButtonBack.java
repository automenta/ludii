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

public class ButtonBack extends ToolButton
{
    public ButtonBack(final int cx, final int cy, final int sx, final int sy) {
        super("Back", cx, cy, sx, sy);
        this.tooltipMessage = "Back a Move";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        final GeneralPath path = new GeneralPath();
        path.moveTo((float)(this.cx + 5), (float)(this.cy + 7));
        path.lineTo((float)(this.cx - 5), (float)(this.cy + 0));
        path.lineTo((float)(this.cx + 5), (float)(this.cy - 7));
        g2d.draw(path);
    }
    
    @Override
    protected boolean isEnabled() {
        final Context context = Manager.ref().context();
        final int numInitialPlacementMoves = context.currentInstanceContext().trial().numInitialPlacementMoves();
        return (context.currentSubgameIdx() > 1 || context.trial().moves().size() > numInitialPlacementMoves) && SettingsNetwork.getActiveGameId() == 0;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            final Context context = Manager.ref().context();
            ToolView.jumpToMove(context.trial().moves().size() - 1);
        }
    }
}
