// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.JanggiDesign;
import view.container.styles.BoardStyle;

public class JanggiStyle extends BoardStyle
{
    public JanggiStyle(final Container container) {
        super(container);
        this.containerDesign = new JanggiDesign(this, this.boardPlacement);
    }
}
