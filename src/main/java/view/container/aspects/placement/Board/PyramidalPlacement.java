// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

public class PyramidalPlacement extends BoardPlacement
{
    public PyramidalPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
    }
    
    @Override
    public void calculateAverageCellRadius() {
        super.calculateAverageCellRadius();
        this.setCellRadius(this.cellRadius * 1.4);
    }
}
