// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.HoundsAndJackalsDesign;
import view.container.aspects.placement.Board.HoundsAndJackalsPlacement;
import view.container.styles.BoardStyle;

public class HoundsAndJackalsStyle extends BoardStyle
{
    public HoundsAndJackalsStyle(final Container container) {
        super(container);
        final HoundsAndJackalsPlacement houndsAndJackalsPlacement = new HoundsAndJackalsPlacement(this);
        this.containerPlacement = houndsAndJackalsPlacement;
        this.containerDesign = new HoundsAndJackalsDesign(this, houndsAndJackalsPlacement);
    }
}
