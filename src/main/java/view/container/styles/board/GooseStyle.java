// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.GooseDesign;
import view.container.styles.BoardStyle;

public class GooseStyle extends BoardStyle
{
    public GooseStyle(final Container container) {
        super(container);
        this.containerDesign = new GooseDesign(this, this.boardPlacement);
    }
}
