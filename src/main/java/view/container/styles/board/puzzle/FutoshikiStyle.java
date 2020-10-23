// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.puzzle;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.puzzle.FutoshikiDesign;
import view.container.styles.board.graph.GraphStyle;

public class FutoshikiStyle extends GraphStyle
{
    public FutoshikiStyle(final Container container, final Context context) {
        super(container, context);
        this.containerDesign = new FutoshikiDesign(this, this.boardPlacement);
    }
}
