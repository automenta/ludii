// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.views.tools.ToolButton;
import game.rules.play.moves.Moves;
import manager.Manager;
import manager.network.SettingsNetwork;
import manager.referee.UserMoveHandler;
import manager.utils.ContextSnapshot;
import util.Context;
import util.Move;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class ButtonPass extends ToolButton
{
    public ButtonPass(final int cx, final int cy, final int sx, final int sy) {
        super("Pass", cx, cy, sx, sy);
        this.tooltipMessage = "Pass/End Move";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        g2d.setStroke(new BasicStroke(3.0f, 0, 1));
        final GeneralPath path = new GeneralPath();
        path.moveTo((this.cx - 15), (this.cy + 10));
        path.curveTo((this.cx - 15), (this.cy + 0), (this.cx - 8), (this.cy - 7), (this.cx + 2), (this.cy - 7));
        path.lineTo((this.cx + 0), (this.cy - 12));
        path.lineTo((this.cx + 15), (this.cy - 5));
        path.lineTo((this.cx + 0), (this.cy + 2));
        path.lineTo((this.cx + 2), (this.cy - 3));
        path.curveTo((this.cx - 7), (this.cy - 3), (this.cx - 13), (this.cy + 6), (this.cx - 15), (this.cy + 10));
        g2d.fill(path);
    }
    
    @Override
    protected boolean isEnabled() {
        boolean canPass = false;
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        for (final Move m : legal.moves()) {
            if (m.isPass() && (SettingsNetwork.getNetworkPlayerNumber() == m.mover() || SettingsNetwork.getNetworkPlayerNumber() == 0)) {
                canPass = true;
            }
            if (m.containsNextInstance()) {
                canPass = true;
            }
        }
        if (legal.moves().isEmpty() && !ContextSnapshot.getContext().trial().over() && Manager.savedTrial() != null && !Manager.savedTrial().moves().isEmpty()) {
            canPass = true;
        }
        if (canPass) {
            this.showPossibleMovesTemporaryMessage();
            return true;
        }
        return false;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            UserMoveHandler.passMove(ContextSnapshot.getContext().state().mover());
        }
    }
}
