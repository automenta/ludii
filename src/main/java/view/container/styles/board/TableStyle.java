// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.components.board.TableComponents;
import view.container.aspects.designs.board.TableDesign;
import view.container.aspects.placement.Board.TablePlacement;
import view.container.styles.BoardStyle;

public class TableStyle extends BoardStyle
{
    public TableStyle(final Container container) {
        super(container);
        final TablePlacement backgammonPlacement = new TablePlacement(this);
        this.containerPlacement = backgammonPlacement;
        this.containerDesign = new TableDesign(this, backgammonPlacement);
        this.containerComponents = new TableComponents(this);
    }
}
