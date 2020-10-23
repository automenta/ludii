// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.ShogiDesign;
import view.container.styles.BoardStyle;

public class ShogiStyle extends BoardStyle
{
    public ShogiStyle(final Container container) {
        super(container);
        this.containerDesign = new ShogiDesign(this, this.boardPlacement);
    }
}
