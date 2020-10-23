// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.AgonDesign;
import view.container.styles.BoardStyle;

public class AgonStyle extends BoardStyle
{
    public AgonStyle(final Container container) {
        super(container);
        this.containerDesign = new AgonDesign(this, this.boardPlacement);
    }
}
