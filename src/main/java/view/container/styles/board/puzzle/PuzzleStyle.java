// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.puzzle;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.components.board.PuzzleComponents;
import view.container.aspects.designs.board.puzzle.PuzzleDesign;
import view.container.styles.BoardStyle;

public class PuzzleStyle extends BoardStyle
{
    public PuzzleStyle(final Container container, final Context context) {
        super(container);
        final PuzzleDesign puzzleDesign = new PuzzleDesign(this, this.boardPlacement);
        this.containerDesign = puzzleDesign;
        if (context.game().isDeductionPuzzle()) {
            this.containerComponents = new PuzzleComponents(this, puzzleDesign);
        }
    }
}
