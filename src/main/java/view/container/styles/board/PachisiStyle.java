// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.PachisiDesign;
import view.container.aspects.placement.Board.PachisiPlacement;
import view.container.styles.BoardStyle;

public class PachisiStyle extends BoardStyle
{
    public PachisiStyle(final Container container) {
        super(container);
        final PachisiPlacement pachisiPlacement = new PachisiPlacement(this);
        this.containerPlacement = pachisiPlacement;
        this.containerDesign = new PachisiDesign(this, pachisiPlacement);
    }
}
