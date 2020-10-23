// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.components.board.BackgammonComponents;
import view.container.aspects.designs.board.BackgammonDesign;
import view.container.aspects.placement.Board.BackgammonPlacement;
import view.container.styles.BoardStyle;

public class BackgammonStyle extends BoardStyle
{
    public BackgammonStyle(final Container container) {
        super(container);
        final BackgammonPlacement backgammonPlacement = new BackgammonPlacement(this);
        this.containerPlacement = backgammonPlacement;
        this.containerDesign = new BackgammonDesign(this, backgammonPlacement);
        this.containerComponents = new BackgammonComponents(this);
    }
}
