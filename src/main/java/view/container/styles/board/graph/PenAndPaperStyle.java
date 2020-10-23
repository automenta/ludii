// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.graph;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.components.board.PenAndPaperComponents;
import view.container.aspects.designs.board.graph.PenAndPaperDesign;

public class PenAndPaperStyle extends GraphStyle
{
    public PenAndPaperStyle(final Container container, final Context context) {
        super(container, context);
        final PenAndPaperDesign boardDesign = new PenAndPaperDesign(this, this.boardPlacement);
        this.containerDesign = boardDesign;
        this.containerComponents = new PenAndPaperComponents(this, boardDesign);
    }
}
