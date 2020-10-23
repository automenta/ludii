// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.axes.board.SurakartaAxis;
import view.container.aspects.designs.board.SurakartaDesign;
import view.container.aspects.placement.Board.SurakartaPlacement;
import view.container.styles.BoardStyle;

public class SurakartaStyle extends BoardStyle
{
    public SurakartaStyle(final Container container) {
        super(container);
        final SurakartaPlacement surakartaPlacement = new SurakartaPlacement(this);
        this.containerPlacement = surakartaPlacement;
        this.containerAxis = new SurakartaAxis(this, surakartaPlacement);
        this.containerDesign = new SurakartaDesign(this, surakartaPlacement);
    }
}
