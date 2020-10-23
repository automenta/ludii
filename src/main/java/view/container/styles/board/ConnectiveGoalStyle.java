// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.ConnectiveGoalDesign;
import view.container.styles.BoardStyle;

public class ConnectiveGoalStyle extends BoardStyle
{
    public ConnectiveGoalStyle(final Container container) {
        super(container);
        this.containerDesign = new ConnectiveGoalDesign(this, this.boardPlacement);
    }
}
