// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.ChessDesign;
import view.container.styles.board.puzzle.PuzzleStyle;

public class ChessStyle extends PuzzleStyle
{
    public ChessStyle(final Container container, final Context context) {
        super(container, context);
        this.containerDesign = new ChessDesign(this, this.boardPlacement);
    }
}
