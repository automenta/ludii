// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import game.equipment.component.tile.Path;
import game.functions.dim.DimConstant;
import game.functions.graph.generators.shape.Shape;
import game.types.board.SiteType;
import game.util.graph.Face;
import game.util.graph.Graph;
import game.util.graph.Vertex;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import util.SettingsColour;
import view.component.BaseComponentStyle;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

public class TileStyle extends BaseComponentStyle
{
    public TileStyle(final Component component) {
        super(component);
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        while (this.imageSVG.size() <= localState) {
            this.imageSVG.add(null);
        }
        final SVGGraphics2D g2d = this.getTileSVGImage(context, imageSize, localState, secondary, maskedValue);
        this.imageSVG.set(localState, g2d);
    }
    
    protected SVGGraphics2D getTileSVGImage(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.genericMetadataChecks(context, localState);
        SVGGraphics2D g2d = new SVGGraphics2D(imageSize, imageSize);
        final int numEdges = this.component.numSides();
        if (secondary) {
            final Graph tileGraph = new Shape(null, new DimConstant(numEdges)).eval(context, SiteType.Cell);
            tileGraph.normalise();
            final Face tileGraphFace = tileGraph.faces().get(0);
            g2d.setStroke(new BasicStroke(0.0f, 1, 1));
            final GeneralPath path = new GeneralPath();
            for (int i = 0; i < tileGraphFace.vertices().size(); ++i) {
                final Vertex v = tileGraphFace.vertices().get(i);
                if (i == 0) {
                    path.moveTo(v.pt().x() * imageSize, v.pt().y() * imageSize);
                }
                else {
                    path.lineTo(v.pt().x() * imageSize, v.pt().y() * imageSize);
                }
            }
            if (context.game().metadata().graphics().pieceFillColour(this.component.owner(), this.component.name(), context, localState) != null) {
                g2d.setColor(context.game().metadata().graphics().pieceFillColour(this.component.owner(), this.component.name(), context, localState));
            }
            else if (context.game().players().count() >= this.component.owner()) {
                g2d.setColor(SettingsColour.playerColour(this.component.owner(), context));
            }
            else {
                g2d.setColor(Color.BLACK);
            }
            path.closePath();
            g2d.fill(path);
        }
        if (maskedValue == 0) {
            g2d = this.getForeground(g2d, imageSize);
            int[] terminus = this.component.terminus();
            if (this.component.numTerminus() > 0) {
                terminus = new int[numEdges];
                Arrays.fill(terminus, this.component.numTerminus());
                g2d.setColor(Color.RED);
                final double lineThicknessMultiplier = 0.33;
                final int lineThickness = (int)(imageSize * 0.33);
                g2d.setStroke(new BasicStroke(lineThickness, 0, 0));
                this.drawPathLines(g2d, terminus, imageSize, numEdges);
            }
        }
        return g2d;
    }
    
    private void drawPathLines(final SVGGraphics2D g2d, final int[] terminus, final int imageSize, final int numEdges) {
        int terminusSpacing = imageSize / (terminus[0] + 1);
        if (numEdges == 4 && this.component.paths() != null) {
            terminusSpacing = imageSize / (this.component.numTerminus() + 1);
            for (final Path tilePath : this.component.paths()) {
                int ax = 0;
                int ay = 0;
                int bx = 0;
                int by = 0;
                switch (tilePath.side1()) {
                    case 0 -> {
                        ax = terminusSpacing * (tilePath.terminus1() + 1);
                        ay = 0;
                        break;
                    }
                    case 1 -> {
                        ax = imageSize;
                        ay = terminusSpacing * (tilePath.terminus1() + 1);
                        break;
                    }
                    case 2 -> {
                        ax = terminusSpacing * (tilePath.terminus1() + 1);
                        ay = imageSize;
                        break;
                    }
                    case 3 -> {
                        ax = 0;
                        ay = terminusSpacing * (tilePath.terminus1() + 1);
                        break;
                    }
                }
                switch (tilePath.side2()) {
                    case 0 -> {
                        bx = terminusSpacing * (tilePath.terminus2() + 1);
                        by = 0;
                        break;
                    }
                    case 1 -> {
                        bx = imageSize;
                        by = terminusSpacing * (tilePath.terminus2() + 1);
                        break;
                    }
                    case 2 -> {
                        bx = terminusSpacing * (tilePath.terminus2() + 1);
                        by = imageSize;
                        break;
                    }
                    case 3 -> {
                        bx = 0;
                        by = terminusSpacing * (tilePath.terminus2() + 1);
                        break;
                    }
                }
                final double off = 0.666;
                final int aax = ax + (int)(0.666 * (imageSize / 2 - ax));
                final int aay = ay + (int)(0.666 * (imageSize / 2 - ay));
                final int bbx = bx + (int)(0.666 * (imageSize / 2 - bx));
                final int bby = by + (int)(0.666 * (imageSize / 2 - by));
                final GeneralPath path2 = new GeneralPath();
                path2.moveTo(ax, ay);
                path2.curveTo(aax, aay, bbx, bby, bx, by);
                g2d.setColor(SettingsColour.getDefaultPlayerColours()[tilePath.colour()]);
                g2d.draw(path2);
            }
        }
    }
    
    @Override
    public double scale() {
        return 1.0;
    }
}
