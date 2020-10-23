// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.ScriptaDesign;
import view.container.styles.BoardStyle;

public class ScriptaStyle extends BoardStyle
{
    public ScriptaStyle(final Container container) {
        super(container);
        this.containerDesign = new ScriptaDesign(this, this.boardPlacement);
    }
}
