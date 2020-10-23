// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.tools.buttons;

import app.display.dialogs.PossibleMovesDialog;
import app.display.views.tools.ToolButton;
import game.rules.play.moves.Moves;
import main.collections.FastArrayList;
import manager.utils.ContextSnapshot;
import util.Context;
import util.Move;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ButtonOther extends ToolButton
{
    FastArrayList<Move> otherPossibleMoves;
    
    public ButtonOther(final int cx, final int cy, final int sx, final int sy) {
        super("Other", cx, cy, sx, sy);
        this.otherPossibleMoves = new FastArrayList<>();
        this.tooltipMessage = "Miscellaneous";
    }
    
    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(this.getButtonColour());
        final double r = 2.75;
        g2d.fill(new Ellipse2D.Double(this.cx - 2.75, this.cy - 2.75 - 9.0, 5.5, 5.5));
        g2d.fill(new Ellipse2D.Double(this.cx - 2.75, this.cy - 2.75, 5.5, 5.5));
        g2d.fill(new Ellipse2D.Double(this.cx - 2.75, this.cy - 2.75 + 9.0, 5.5, 5.5));
        if (this.otherPossibleMoves.size() > 0) {
            this.showPossibleMovesTemporaryMessage();
        }
    }
    
    @Override
    protected boolean isEnabled() {
        this.otherPossibleMoves.clear();
        final Context context = ContextSnapshot.getContext();
        final Moves legal = context.game().moves(context);
        for (final Move m : legal.moves()) {
            if (!m.isPass() && !m.isSwap() && m.isOtherMove()) {
                this.otherPossibleMoves.add(m);
            }
        }
        if (this.otherPossibleMoves.size() > 0) {
            this.showPossibleMovesTemporaryMessage();
            return true;
        }
        return false;
    }
    
    @Override
    public void press() {
        if (this.isEnabled()) {
            PossibleMovesDialog.createAndShowGUI(ContextSnapshot.getContext(), this.otherPossibleMoves);
        }
    }
}
