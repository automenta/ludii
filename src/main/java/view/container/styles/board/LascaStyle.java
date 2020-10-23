// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.LascaDesign;
import view.container.styles.BoardStyle;

public class LascaStyle extends BoardStyle
{
    public LascaStyle(final Container container) {
        super(container);
        this.containerDesign = new LascaDesign(this, this.boardPlacement);
    }
}
