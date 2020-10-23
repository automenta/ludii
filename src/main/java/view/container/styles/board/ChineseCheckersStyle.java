// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.ChineseCheckersDesign;
import view.container.styles.BoardStyle;

public class ChineseCheckersStyle extends BoardStyle
{
    public ChineseCheckersStyle(final Container container) {
        super(container);
        this.containerDesign = new ChineseCheckersDesign(this, this.boardPlacement);
    }
}
