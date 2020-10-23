// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.PloyDesign;
import view.container.styles.BoardStyle;

public class PloyStyle extends BoardStyle
{
    public PloyStyle(final Container container) {
        super(container);
        this.containerDesign = new PloyDesign(this, this.boardPlacement);
    }
}
