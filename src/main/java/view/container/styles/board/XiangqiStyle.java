// 
// Decompiled by Procyon v0.5.36
// 

package view.container.styles.board;

import game.equipment.container.Container;
import view.container.aspects.designs.board.XiangqiDesign;
import view.container.styles.BoardStyle;

public class XiangqiStyle extends BoardStyle
{
    public XiangqiStyle(final Container container) {
        super(container);
        this.containerDesign = new XiangqiDesign(this, this.boardPlacement);
    }
}
