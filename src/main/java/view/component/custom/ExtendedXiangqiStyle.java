// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import util.ImageUtil;
import view.component.BaseComponentStyle;
import view.component.custom.pieceTypes.XiangqiType;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ExtendedXiangqiStyle extends BaseComponentStyle
{
    public ExtendedXiangqiStyle(final Component component) {
        super(component);
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.genericMetadataChecks(context, localState);
        final String path = ImageUtil.getImageFullPath("disc");
        final int g2dSize = (int)(imageSize * this.scale());
        SVGGraphics2D g2d = new SVGGraphics2D(g2dSize, g2dSize);
        this.renderImageSVGFromPath(g2d, context, imageSize, path, localState);
        Font valueFont = null;
        for (int i = 0; i < XiangqiType.values().length; ++i) {
            if (XiangqiType.values()[i].englishName().equals(this.svgName) || XiangqiType.values()[i].kanji().equals(this.svgName) || XiangqiType.values()[i].romaji().equalsIgnoreCase(this.svgName) || XiangqiType.values()[i].name().equalsIgnoreCase(this.svgName)) {
                g2d = this.imageSVG.get(localState);
                if (XiangqiType.values()[i].kanji().length() == 1) {
                    valueFont = new Font("Arial", 0, g2dSize / 2);
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(valueFont);
                    final Rectangle2D rect = valueFont.getStringBounds(Character.toString(XiangqiType.values()[i].kanji().charAt(0)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(XiangqiType.values()[i].kanji().charAt(0)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 1.5), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() / 3.0));
                    break;
                }
                if (XiangqiType.values()[i].kanji().length() == 2) {
                    valueFont = new Font("Arial", 0, g2dSize / 3);
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(valueFont);
                    Rectangle2D rect = valueFont.getStringBounds(Character.toString(XiangqiType.values()[i].kanji().charAt(0)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(XiangqiType.values()[i].kanji().charAt(0)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 1.5), this.imageSVG.get(localState).getHeight() / 2);
                    rect = valueFont.getStringBounds(Character.toString(XiangqiType.values()[i].kanji().charAt(1)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(XiangqiType.values()[i].kanji().charAt(1)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 1.5), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() / 1.5));
                    break;
                }
            }
        }
        if (valueFont == null) {
            super.renderImageSVG(context, imageSize, localState, secondary, maskedValue);
        }
    }
}
