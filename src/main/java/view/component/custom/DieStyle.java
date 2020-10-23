// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.component.BaseComponentStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DieStyle extends BaseComponentStyle
{
    public DieStyle(final Component component) {
        super(component);
        this.setDefaultDiceDesign();
    }
    
    private void setDefaultDiceDesign() {
        if (this.component.getNumFaces() == 6 || this.component.getNumFaces() == 10 || this.component.getNumFaces() == 12) {
            this.component.setNameWithoutNumber("square");
        }
        else if (this.component.getNumFaces() == 4) {
            this.component.setNameWithoutNumber("rectangle");
        }
        else if (this.component.getNumFaces() == 2) {
            this.component.setNameWithoutNumber("paddle");
        }
        else {
            this.component.setNameWithoutNumber("triangle");
        }
    }
    
    @Override
    public void renderImageSVGFromPath(final SVGGraphics2D g2dOriginal, final Context context, final int imageSize, final String filePathDice, final int localState) {
        while (this.imageSVG.size() <= localState) {
            this.imageSVG.add(null);
        }
        this.imageSVG.set(localState, this.getSVGImageFromFilePath(g2dOriginal, imageSize, filePathDice, localState, this.colour, this.edgeColour));
        final SVGGraphics2D g2d = this.imageSVG.get(localState);
        final Point diceCenter = new Point(g2d.getWidth() / 2, g2d.getHeight() / 2);
        final int diceValue = this.component.getFaces()[localState];
        if (context.game().metadata().graphics().pieceForeground(this.component.owner(), this.component.name(), context, localState) == null) {
            this.drawPips(context, diceCenter.x, diceCenter.y, diceValue, imageSize, g2d);
        }
    }
    
    public void drawPips(final Context context, final int positionX, final int positionY, final int pipValue, final int imageSize, final Graphics2D g2d) {
        final int maxDieValueForPips = 6;
        double pipSpacingMultiplier = 0.8;
        double pipSizeFraction = 0.15;
        Point2D pipTranslation = new Point2D.Double(0.0, 0.0);
        if (this.svgName.equalsIgnoreCase("triangle")) {
            pipSpacingMultiplier = 0.4;
            pipSizeFraction = 0.1;
            pipTranslation = new Point2D.Double(0.0, 0.15);
        }
        if (this.svgName.equalsIgnoreCase("rectangle")) {
            pipSpacingMultiplier = 0.4;
            pipSizeFraction = 0.1;
        }
        if (pipValue <= 6 && !context.game().metadata().graphics().noDicePips()) {
            final double pipSize = (int)(imageSize * pipSizeFraction);
            final int dw = (int)(imageSize * pipSpacingMultiplier / 2.0 - pipSize);
            final int dh = (int)(imageSize * pipSpacingMultiplier / 2.0 - pipSize);
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
            }
            for (Point pipPosition : pipPositions) {
                final int pipX = pipPosition.x;
                final int pipY = pipPosition.y;
                g2d.setColor(Color.BLACK);
                g2d.fillOval(pipX - (int) pipSize / 2, pipY - (int) pipSize / 2, (int) pipSize, (int) pipSize);
            }
        }
        else {
            final Font valueFont = new Font("Arial", 1, imageSize / 3);
            g2d.setColor(Color.BLACK);
            g2d.setFont(valueFont);
            final Rectangle2D rect = valueFont.getStringBounds(Integer.toString(pipValue), g2d.getFontRenderContext());
            try {
                if (this.svgName.equalsIgnoreCase("triangle")) {
                    g2d.drawString(Integer.toString(pipValue), (int)(positionX - rect.getWidth() / 2.0), (int)(positionY + rect.getHeight() / 1.5));
                }
                else {
                    g2d.drawString(Integer.toString(pipValue), (int)(positionX - rect.getWidth() / 2.0), (int)(positionY + rect.getHeight() / 2.0));
                }
            }
            catch (Exception ex) {}
        }
    }
}
