// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import gnu.trove.list.array.TIntArrayList;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DominoStyle extends LargePieceStyle
{
    public DominoStyle(final Component component) {
        super(component);
    }
    
    public SVGGraphics2D drawLargePieceVisuals(final SVGGraphics2D g2d, final TIntArrayList cellLocations, final int imageSize, final int imageX, final int imageY, final int state, final Context context, final boolean secondary, final int maskedValue) {
        super.drawLargePieceVisuals(g2d, cellLocations, imageSize, imageX, imageY, state, context, secondary, maskedValue);
        Point2D currentPoint = new Point2D.Double();
        double minCellLocationX = 99999.0;
        double maxCellLocationX = -99999.0;
        double minCellLocationY = 99999.0;
        double maxCellLocationY = -99999.0;
        for (int i = 0; i < cellLocations.size(); ++i) {
            currentPoint = this.boardForLargePiece.topology().cells().get(cellLocations.get(i)).centroid();
            if (currentPoint.getX() < minCellLocationX) {
                minCellLocationX = currentPoint.getX();
            }
            if (currentPoint.getX() > maxCellLocationX) {
                maxCellLocationX = currentPoint.getX();
            }
            if (currentPoint.getY() < minCellLocationY) {
                minCellLocationY = currentPoint.getY();
            }
            if (currentPoint.getY() > maxCellLocationY) {
                maxCellLocationY = currentPoint.getY();
            }
        }
        maxCellLocationX -= minCellLocationX;
        minCellLocationX -= minCellLocationX;
        maxCellLocationY -= minCellLocationY;
        minCellLocationY -= minCellLocationY;
        final int strokeWidth = imageSize / 5;
        maxCellLocationX += imageSize;
        maxCellLocationY += imageSize;
        g2d.setStroke(new BasicStroke(strokeWidth, 0, 0));
        final GeneralPath path = new GeneralPath();
        path.moveTo(minCellLocationX + strokeWidth / 2, minCellLocationY + strokeWidth / 2);
        path.lineTo(minCellLocationX + strokeWidth / 2, maxCellLocationY - strokeWidth / 2);
        path.lineTo(maxCellLocationX - strokeWidth / 2, maxCellLocationY - strokeWidth / 2);
        path.lineTo(maxCellLocationX - strokeWidth / 2, minCellLocationY + strokeWidth / 2);
        path.lineTo(minCellLocationX + strokeWidth / 2, minCellLocationY + strokeWidth / 2);
        path.closePath();
        final double xDistance = maxCellLocationX - minCellLocationX;
        final double yDistance = maxCellLocationY - minCellLocationY;
        if (xDistance > yDistance) {
            path.moveTo(minCellLocationX + xDistance / 2.0, minCellLocationY);
            path.lineTo(minCellLocationX + xDistance / 2.0, maxCellLocationY);
        }
        else {
            path.moveTo(minCellLocationX, minCellLocationY + yDistance / 2.0);
            path.lineTo(maxCellLocationX, minCellLocationY + yDistance / 2.0);
        }
        g2d.setColor(Color.BLACK);
        g2d.draw(path);
        final Point2D.Double[] dominoSides = { new Point2D.Double(minCellLocationX + imageSize, minCellLocationY + imageSize), new Point2D.Double(maxCellLocationX - imageSize, maxCellLocationY - imageSize) };
        final int[] dominoValues = { 0, 0 };
        if (state < 2) {
            dominoValues[0] = this.component.getValue();
            dominoValues[1] = this.component.getValue2();
        }
        else {
            dominoValues[1] = this.component.getValue();
            dominoValues[0] = this.component.getValue2();
        }
        for (int j = 0; j < dominoSides.length; ++j) {
            this.drawPips((int)dominoSides[j].x, (int)dominoSides[j].y, dominoValues[j], imageSize * 2, g2d);
        }
        return g2d;
    }
    
    public void drawPips(final int positionX, final int positionY, final int pipValue, final int imageSize, final Graphics2D g2d) {
        final int maxDominoValueForPips = 9;
        final double pipSpacingMultiplier = 0.8;
        final double pipSizeFraction = 0.15;
        final Point2D pipTranslation = new Point2D.Double(0.0, 0.0);
        if (pipValue <= 9) {
            final double pipSize = (int)(imageSize * 0.15);
            final int dw = (int)(imageSize * 0.8 / 2.0 - pipSize);
            final int dh = (int)(imageSize * 0.8 / 2.0 - pipSize);
            final int dx = (int)(positionX + imageSize * pipTranslation.getX());
            final int dy = (int)(positionY + imageSize * pipTranslation.getY());
            final ArrayList<Point> pipPositions = new ArrayList<>();
            switch (pipValue) {
                case 1 -> {
                    pipPositions.add(new Point(dx, dy));
                    break;
                }
                case 2 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    break;
                }
                case 3 -> {
                    pipPositions.add(new Point(dx, dy));
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    break;
                }
                case 4 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    break;
                }
                case 5 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    pipPositions.add(new Point(dx, dy));
                    break;
                }
                case 6 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    pipPositions.add(new Point(dx, dy + dh));
                    pipPositions.add(new Point(dx, dy - dw));
                    break;
                }
                case 7 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    pipPositions.add(new Point(dx, dy + dh));
                    pipPositions.add(new Point(dx, dy - dw));
                    pipPositions.add(new Point(dx, dy));
                    break;
                }
                case 8 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    pipPositions.add(new Point(dx, dy + dh));
                    pipPositions.add(new Point(dx, dy - dw));
                    pipPositions.add(new Point(dx + dw, dy));
                    pipPositions.add(new Point(dx - dw, dy));
                    break;
                }
                case 9 -> {
                    pipPositions.add(new Point(dx + dw, dy + dh));
                    pipPositions.add(new Point(dx - dw, dy - dw));
                    pipPositions.add(new Point(dx - dw, dy + dh));
                    pipPositions.add(new Point(dx + dw, dy - dw));
                    pipPositions.add(new Point(dx, dy + dh));
                    pipPositions.add(new Point(dx, dy - dw));
                    pipPositions.add(new Point(dx + dw, dy));
                    pipPositions.add(new Point(dx - dw, dy));
                    pipPositions.add(new Point(dx, dy));
                    break;
                }
            }
            for (Point pipPosition : pipPositions) {
                final int pipX = pipPosition.x;
                final int pipY = pipPosition.y;
                g2d.setColor(Color.BLACK);
                g2d.fillOval(pipX - (int) pipSize / 2, pipY - (int) pipSize / 2, (int) pipSize, (int) pipSize);
            }
        }
        else {
            final Font valueFont = new Font("Arial", 1, imageSize / 2);
            g2d.setColor(Color.BLACK);
            g2d.setFont(valueFont);
            final Rectangle2D rect = valueFont.getStringBounds(Integer.toString(pipValue), g2d.getFontRenderContext());
            try {
                g2d.drawString(Integer.toString(pipValue), (int)(positionX - rect.getWidth() / 2.0), (int)(positionY + rect.getHeight() / 2.0));
            }
            catch (Exception ex) {}
        }
    }
}
