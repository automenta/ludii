// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.SnakesAndLaddersDesign;
import view.container.styles.BoardStyle;

public class SnakesAndLaddersStyle extends BoardStyle
{
    public SnakesAndLaddersStyle(final Container container) {
        super(container);
        this.containerDesign = new SnakesAndLaddersDesign(this, this.boardPlacement);
    }
}
