// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board.puzzle;

import game.equipment.container.Container;
import util.Context;
import view.container.aspects.designs.board.puzzle.KakuroDesign;

public class KakuroStyle extends PuzzleStyle
{
    public KakuroStyle(final Container container, final Context context) {
        super(container, context);
        this.containerDesign = new KakuroDesign(this, this.boardPlacement);
    }
}
