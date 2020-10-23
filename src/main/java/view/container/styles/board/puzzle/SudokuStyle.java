// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.puzzle;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.puzzle.SudokuDesign;

public class SudokuStyle extends PuzzleStyle
{
    public SudokuStyle(final Container container, final Context context) {
        super(container, context);
        final SudokuDesign sudokuDesign = new SudokuDesign(this, this.boardPlacement);
        this.containerDesign = sudokuDesign;
    }
}
