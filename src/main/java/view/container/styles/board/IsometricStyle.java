// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.IsometricDesign;
import view.container.styles.BoardStyle;

public class IsometricStyle extends BoardStyle
{
    public IsometricStyle(final Container container) {
        super(container);
        this.containerDesign = new IsometricDesign(this, this.boardPlacement);
    }
}
