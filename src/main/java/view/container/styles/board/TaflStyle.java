// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.TaflDesign;
import view.container.styles.BoardStyle;

public class TaflStyle extends BoardStyle
{
    public TaflStyle(final Container container) {
        super(container);
        this.containerDesign = new TaflDesign(this, this.boardPlacement);
    }
}
