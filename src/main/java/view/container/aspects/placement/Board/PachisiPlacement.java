// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Point2D;

public class PachisiPlacement extends BoardPlacement
{
    public PachisiPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
    }
    
    @Override
    public void setPlacement(final Context context, final Rectangle placement) {
        this.setCustomPlacement(context, placement, new Point2D.Double(0.5, 0.6), 0.8);
    }
}
