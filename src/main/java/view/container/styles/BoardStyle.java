// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles;

import game.equipment.container.Container;
import view.container.BaseContainerStyle;
import view.container.aspects.axes.BoardAxis;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;

public class BoardStyle extends BaseContainerStyle
{
    protected BoardPlacement boardPlacement;
    
    public BoardStyle(final Container container) {
        super(container);
        this.boardPlacement = new BoardPlacement(this);
        this.containerPlacement = this.boardPlacement;
        this.containerAxis = new BoardAxis(this, this.boardPlacement);
        this.containerDesign = new BoardDesign(this, this.boardPlacement);
    }
}
