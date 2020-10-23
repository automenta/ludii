// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import view.component.BaseComponentStyle;
import view.component.custom.pieceTypes.NativeAmericanDiceType;

import java.awt.*;

public class NativeAmericanDiceStyle extends BaseComponentStyle
{
    public NativeAmericanDiceStyle(final Component component) {
        super(component);
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.genericMetadataChecks(context, localState);
        final int g2dSize = (int)(imageSize * this.scale());
        final SVGGraphics2D g2d = new SVGGraphics2D(g2dSize, g2dSize);
        this.renderImageSVGFromPath(g2d, context, imageSize, "", localState);
        g2d.setStroke(new BasicStroke((imageSize / 10), 1, 0));
        final Rectangle rect = new Rectangle(0, imageSize / 5, imageSize - imageSize / 7, imageSize - imageSize / 3 - imageSize / 5);
        g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
        NativeAmericanDiceType nativeAmericanDiceType = null;
        for (int i = 0; i < NativeAmericanDiceType.values().length; ++i) {
            if (NativeAmericanDiceType.values()[i].englishName().equals(this.svgName) || NativeAmericanDiceType.values()[i].name().equalsIgnoreCase(this.svgName)) {
                nativeAmericanDiceType = NativeAmericanDiceType.values()[i];
            }
        }
        if (nativeAmericanDiceType == null) {
            return;
        }
        switch (nativeAmericanDiceType) {
            case Patol1 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 2, rect.y, rect.x, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                }
            }
            case Patol2 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x, rect.y, rect.width, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y, rect.x, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                }
            }
            case Notched -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 5, rect.y, rect.x + rect.width / 5, rect.y + rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 5 * 2, rect.y, rect.x + rect.width / 5 * 2, rect.y + rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 5 * 3, rect.y, rect.x + rect.width / 5 * 3, rect.y + rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 5 * 3, rect.y + rect.height, rect.x + rect.width / 5 * 3, rect.y + rect.height - rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 5 * 4, rect.y + rect.height, rect.x + rect.width / 5 * 4, rect.y + rect.height - rect.height / 5);
                }
            }
            case SetDilth -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 2, rect.y, rect.x + rect.width / 3, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2 + rect.width / 6, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                }
            }
            case Nebakuthana1 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawPolygon(new int[]{rect.x + rect.width / 10, rect.x + rect.width / 5 + rect.width / 10, rect.x + rect.width / 5}, new int[]{rect.y, rect.y, rect.y + rect.height / 5}, 3);
                    g2d.drawPolygon(new int[]{rect.x + rect.width - rect.width / 10, rect.x + rect.width - rect.width / 5 - rect.width / 10, rect.x + rect.width - rect.width / 5}, new int[]{rect.y, rect.y, rect.y + rect.height / 5}, 3);
                    g2d.drawPolygon(new int[]{rect.x + rect.width / 10, rect.x + rect.width / 5 + rect.width / 10, rect.x + rect.width / 5}, new int[]{rect.y + rect.height, rect.y + rect.height, rect.y + rect.height - rect.height / 5}, 3);
                    g2d.drawPolygon(new int[]{rect.x + rect.width - rect.width / 10, rect.x + rect.width - rect.width / 5 - rect.width / 10, rect.x + rect.width - rect.width / 5}, new int[]{rect.y + rect.height, rect.y + rect.height, rect.y + rect.height - rect.height / 5}, 3);
                }
            }
            case Nebakuthana2 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y, rect.x + rect.width / 3 * 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y + rect.height, rect.x + rect.width / 3 * 2, rect.y);
                }
            }
            case Nebakuthana3 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2, rect.y + rect.height / 2);
                    g2d.drawPolygon(new int[]{rect.x + rect.width / 2, rect.x + rect.width / 3, rect.x + rect.width / 2, rect.x + rect.width / 3 * 2}, new int[]{rect.y + rect.height / 10, rect.y + rect.height / 2, rect.y + rect.height - rect.height / 10, rect.y + rect.height / 2}, 4);
                }
            }
            case Nebakuthana4 -> {
                if (localState == 0) {
                    final Color oldColour = g2d.getColor();
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
                    g2d.setColor(oldColour);
                    g2d.drawLine(rect.x + rect.width / 8, rect.y, rect.x + rect.width / 8, rect.y + rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 8 * 2, rect.y, rect.x + rect.width / 8 * 2, rect.y + rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 8 * 6, rect.y + rect.height, rect.x + rect.width / 8 * 6, rect.y + rect.height - rect.height / 5);
                    g2d.drawLine(rect.x + rect.width / 8 * 7, rect.y + rect.height, rect.x + rect.width / 8 * 7, rect.y + rect.height - rect.height / 5);
                    break;
                }
                if (localState == 1) {
                    final Color oldColour = g2d.getColor();
                    g2d.setColor(Color.RED);
                    g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
                    g2d.setColor(oldColour);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 4, rect.y + rect.height / 2);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2 + rect.width / 4, rect.y + rect.height / 2);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2, rect.y);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 4, rect.y);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2 + rect.width / 4, rect.y);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 2 + rect.width / 4, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2, rect.y + rect.height / 2, rect.x + rect.width / 4, rect.y + rect.height);
                }
            }
            case Kints1 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x, rect.y, rect.x + rect.width / 6, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y, rect.x + rect.width / 6, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3 * 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3 * 2, rect.y, rect.x + rect.width / 6 * 5, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width / 6 * 5, rect.y + rect.height);
                }
            }
            case Kints2 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 2 - rect.width / 6, rect.y, rect.x + rect.width / 2 - rect.width / 6, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2 - rect.width / 3, rect.y, rect.x + rect.width / 2 - rect.width / 3, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2 + rect.width / 6, rect.y, rect.x + rect.width / 2 + rect.width / 6, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 2 + rect.width / 3, rect.y, rect.x + rect.width / 2 + rect.width / 3, rect.y + rect.height);
                }
            }
            case Kints3 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width / 6, rect.y);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y + rect.height, rect.x + rect.width / 6, rect.y);
                    g2d.drawLine(rect.x + rect.width, rect.y, rect.x + rect.width - rect.width / 6, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width - rect.width / 3, rect.y, rect.x + rect.width - rect.width / 6, rect.y + rect.height);
                }
            }
            case Kints4 -> {
                if (localState == 0) {
                    break;
                }
                if (localState == 1) {
                    g2d.drawLine(rect.x + rect.width / 3, rect.y, rect.x + rect.width / 3 * 2, rect.y + rect.height);
                    g2d.drawLine(rect.x + rect.width / 3, rect.y + rect.height, rect.x + rect.width / 3 * 2, rect.y);
                }
            }
        }
    }
}
