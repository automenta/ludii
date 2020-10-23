// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.components.board.MancalaComponents;
import view.container.aspects.designs.board.MancalaDesign;
import view.container.styles.BoardStyle;

public class MancalaStyle extends BoardStyle
{
    public MancalaStyle(final Container container) {
        super(container);
        this.containerDesign = new MancalaDesign(this);
        this.containerComponents = new MancalaComponents(this);
    }
}
