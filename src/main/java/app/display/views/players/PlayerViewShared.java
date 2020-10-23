// 
// Decompiled by Procyon v0.5.36
// 

package app.display.views.players;

import manager.utils.ContextSnapshot;
import util.Context;

import java.awt.*;

public class PlayerViewShared extends PlayerViewUser
{
    public PlayerViewShared(final Rectangle rect, final int pid, final PlayerView playerView) {
        super(rect, pid, playerView);
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        if (this.hand != null) {
            final Context context = ContextSnapshot.getContext();
            final Rectangle containerPlacement = new Rectangle(this.placement.x, this.placement.y - this.placement.height / 2, this.placement.width, this.placement.height);
            this.playerView.paintHand(g2d, context, containerPlacement, this.hand.index());
        }
        this.paintDebug(g2d, Color.ORANGE);
    }
}
