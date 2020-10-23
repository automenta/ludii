// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.puzzle;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.puzzle.HashiDesign;

public class HashiStyle extends PuzzleStyle
{
    public HashiStyle(final Container container, final Context context) {
        super(container, context);
        this.containerDesign = new HashiDesign(this, this.boardPlacement);
    }
}
