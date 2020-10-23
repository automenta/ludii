// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.components.board.Connect4Components;
import view.container.aspects.designs.board.Connect4Design;
import view.container.aspects.placement.Board.Connect4Placement;
import view.container.styles.BoardStyle;

public class Connect4Style extends BoardStyle
{
    public Connect4Style(final Container container) {
        super(container);
        final Connect4Placement connect4Placement = new Connect4Placement(this);
        this.containerPlacement = connect4Placement;
        this.containerDesign = new Connect4Design(this, connect4Placement);
        this.containerComponents = new Connect4Components(this);
    }
}
