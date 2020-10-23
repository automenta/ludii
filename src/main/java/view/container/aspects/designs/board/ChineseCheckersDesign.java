// 
// Decompiled by Procyon v0.5.36
// 

package view.container.aspects.designs.board;

import gnu.trove.list.array.TIntArrayList;
import topology.Cell;
import topology.Vertex;
import util.SettingsColour;
import view.container.aspects.designs.BoardDesign;
import view.container.aspects.placement.BoardPlacement;
import view.container.styles.BoardStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;

public class ChineseCheckersDesign extends BoardDesign
{
    public ChineseCheckersDesign(final BoardStyle boardStyle, final BoardPlacement boardPlacement) {
        super(boardStyle, boardPlacement);
    }
    
    @Override
    protected void fillCells(final Graphics2D g2d) {
        final List<Cell> cells = this.topology().cells();
        g2d.setColor(this.colorFillPhase0);
        g2d.setStroke(this.strokeThin);
        for (final Cell cell : cells) {
            final GeneralPath path = new GeneralPath();
            for (int v = 0; v < cell.vertices().size(); ++v) {
                if (path.getCurrentPoint() == null) {
                    final Vertex prev = cell.vertices().get(cell.vertices().size() - 1);
                    final Point prevPosn = this.screenPosn(prev.centroid());
                    path.moveTo(prevPosn.x, prevPosn.y);
                }
                final Vertex corner = cell.vertices().get(v);
                final Point cornerPosn = this.screenPosn(corner.centroid());
                path.lineTo(cornerPosn.x, cornerPosn.y);
            }
            g2d.setColor(this.colorFillPhase0);
            if (cell.index() < 10) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[4]);
            }
            if (cell.index() > 110) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[1]);
            }
            final int[] p3Safe = { 10, 11, 12, 13, 23, 24, 25, 35, 36, 46 };
            if (TIntArrayList.wrap(p3Safe).contains(cell.index())) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[3]);
            }
            final int[] p5Safe = { 19, 20, 21, 22, 32, 33, 34, 44, 45, 55 };
            if (TIntArrayList.wrap(p5Safe).contains(cell.index())) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[5]);
            }
            final int[] p2Safe = { 98, 99, 100, 101, 86, 87, 88, 75, 76, 65 };
            if (TIntArrayList.wrap(p2Safe).contains(cell.index())) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[2]);
            }
            final int[] p6Safe = { 107, 108, 109, 110, 95, 96, 97, 84, 85, 74 };
            if (TIntArrayList.wrap(p6Safe).contains(cell.index())) {
                g2d.setColor(SettingsColour.getCustomPlayerColours()[6]);
            }
            g2d.fill(path);
        }
    }
}
