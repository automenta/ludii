// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.axes;

import topology.AxisLabel;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BoardAxis extends ContainerAxis
{
    protected BoardStyle boardStyle;
    protected BoardPlacement boardPlacement;
    
    public BoardAxis(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        this.boardStyle = boardStyle;
        this.boardPlacement = boardPlacement;
    }
    
    @Override
    public void drawAxes(final Graphics2D g2d) {
        final List<AxisLabel> axisLabels = this.getAxisLabels();
        final Font oldFont = g2d.getFont();
        g2d.setFont(new Font("Arial", 1, 16));
        g2d.setColor(new Color(180, 180, 180));
        for (final AxisLabel al : axisLabels) {
            final String label = al.label();
            final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(label, g2d);
            final Point drawPosn = this.boardStyle.screenPosn(al.posn());
            g2d.drawString(label, (int)(drawPosn.x - bounds.getWidth() / 2.0), (int)(this.boardPlacement.unscaledPlacement().height - (drawPosn.y - bounds.getHeight() / 2.0)));
        }
        g2d.setFont(oldFont);
    }
    
    protected List<AxisLabel> getAxisLabels() {
        final List<AxisLabel> axisLabels = new ArrayList<>();
        return axisLabels;
    }
}
