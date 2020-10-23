// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.placement.Board;

import topology.Vertex;
import util.ContainerUtil;
import util.Context;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.util.List;

public class BackgammonPlacement extends BoardPlacement
{
    private final int homeSize;
    
    public BackgammonPlacement(final BoardStyle containerStyle) {
        super(containerStyle);
        this.homeSize = this.topology().vertices().size() / 4;
    }
    
    @Override
    public void customiseGraphElementLocations(final Context context) {
        final int pixels = this.placement.width;
        final int unitsX = 2 * this.homeSize() + 1 + 1;
        final int unitsY = 2 * (this.homeSize() - 1) + 1 + 1;
        final int mx = pixels / 2;
        final int my = pixels / 2;
        final int unit = pixels / (unitsX + 1) / 2 * 2;
        final int border = unit / 2;
        final int ax = mx - (int)(unitsX * unit / 2.0 + 0.5);
        final int ay = my - (int)(unitsY * unit / 2.0 + 0.5);
        final int cx = ax + border;
        final int cy = ay + border;
        final List<Vertex> vertices = this.topology().vertices();
        final int halfSize = vertices.size() / 2;
        final int offset = (int)(0.08 * Math.abs(vertices.get(0).centroid().getX() * pixels - vertices.get(1).centroid().getX() * pixels));
        for (int n = 0; n < vertices.size(); ++n) {
            final Vertex vertex = vertices.get(n);
            final int sign = (n < halfSize) ? -1 : 1;
            final int x = cx + n % halfSize * unit + unit / 2;
            final int y = cy + n / halfSize * 10 * unit + unit / 2 + sign * offset;
            vertex.setCentroid(x / (double)pixels, y / (double)pixels, 0.0);
        }
        ContainerUtil.normaliseGraphElements(this.topology());
        ContainerUtil.centerGraphElements(this.topology());
        this.calculateAverageCellRadius();
        this.resetPlacement(context);
    }
    
    public int homeSize() {
        return this.homeSize;
    }
}
