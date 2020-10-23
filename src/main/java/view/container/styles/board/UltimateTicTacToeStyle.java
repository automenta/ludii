// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.UltimateTicTacToeDesign;
import view.container.styles.BoardStyle;

public class UltimateTicTacToeStyle extends BoardStyle
{
    public UltimateTicTacToeStyle(final Container container) {
        super(container);
        this.containerDesign = new UltimateTicTacToeDesign(this, this.boardPlacement);
    }
}
