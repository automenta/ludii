// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.views.tools.ToolButton;
import game.rules.play.moves.Moves;
import manager.network.SettingsNetwork;
import manager.referee.UserMoveHandler;
import manager.utils.ContextSnapshot;
import util.Context;
import util.Move;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class ButtonSwap extends ToolButton
{
    boolean canSwap;
    
    public ButtonSwap(final int cx, final int cy, final int sx, final int sy) {
        super("Swap", cx, cy, sx, sy);
        this.canSwap = false;
        this.tooltipMessage = "Swap Roles";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        final GeneralPath path = new GeneralPath();
        path.moveTo((float)(this.cx - 6), (float)(this.cy - 3));
        path.lineTo((float)(this.cx - 6), (float)(this.cy - 7));
        path.lineTo((float)(this.cx + 7), (float)(this.cy - 7));
        path.lineTo((float)(this.cx + 5), (float)(this.cy - 12));
        path.lineTo((float)(this.cx + 18), (float)(this.cy - 5));
        path.lineTo((float)(this.cx + 5), (float)(this.cy + 2));
        path.lineTo((float)(this.cx + 7), (float)(this.cy - 3));
        path.lineTo((float)(this.cx - 6), (float)(this.cy - 3));
        path.moveTo((float)(this.cx + 6), (float)(this.cy + 3));
        path.lineTo((float)(this.cx + 6), (float)(this.cy + 7));
        path.lineTo((float)(this.cx - 7), (float)(this.cy + 7));
        path.lineTo((float)(this.cx - 5), (float)(this.cy + 12));
        path.lineTo((float)(this.cx - 18), (float)(this.cy + 5));
        path.lineTo((float)(this.cx - 5), (float)(this.cy - 2));
        path.lineTo((float)(this.cx - 7), (float)(this.cy + 3));
        path.lineTo((float)(this.cx + 6), (float)(this.cy + 3));
        g2d.fill(path);
    }
    
    @Override
    protected boolean isEnabled() {
        this.canSwap = false;
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        for (final Move m : legal.moves()) {
            if (m.isSwap() && (SettingsNetwork.getNetworkPlayerNumber() == m.mover() || SettingsNetwork.getNetworkPlayerNumber() == 0)) {
                this.canSwap = true;
            }
        }
        if (this.canSwap) {
            this.showPossibleMovesTemporaryMessage();
            return true;
        }
        return false;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            UserMoveHandler.swapMove(ContextSnapshot.getContext().state().mover());
        }
    }
}
