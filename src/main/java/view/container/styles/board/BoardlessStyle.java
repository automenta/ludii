// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.BoardlessDesign;
import view.container.aspects.placement.Board.BoardlessPlacement;
import view.container.styles.BoardStyle;

public class BoardlessStyle extends BoardStyle
{
    public BoardlessStyle(final Container container) {
        super(container);
        final BoardlessPlacement boardlessPlacement = new BoardlessPlacement(this);
        this.containerPlacement = boardlessPlacement;
        this.containerDesign = new BoardlessDesign(this, boardlessPlacement);
    }
}
