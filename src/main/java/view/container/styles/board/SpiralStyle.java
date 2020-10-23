// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.SpiralDesign;
import view.container.styles.BoardStyle;

public class SpiralStyle extends BoardStyle
{
    public SpiralStyle(final Container container) {
        super(container);
        this.containerDesign = new SpiralDesign(this);
    }
}
