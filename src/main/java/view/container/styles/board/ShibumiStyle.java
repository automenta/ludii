// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.ShibumiDesign;
import view.container.aspects.placement.Board.PyramidalPlacement;
import view.container.styles.BoardStyle;

public class ShibumiStyle extends BoardStyle
{
    public ShibumiStyle(final Container container) {
        super(container);
        final PyramidalPlacement pyramidalPlacement = new PyramidalPlacement(this);
        this.containerPlacement = pyramidalPlacement;
        this.containerDesign = new ShibumiDesign(this, pyramidalPlacement);
    }
}
