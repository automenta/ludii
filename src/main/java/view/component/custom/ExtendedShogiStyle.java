// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Component;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.Context;
import util.ImageUtil;
import view.component.BaseComponentStyle;
import view.component.custom.pieceTypes.ShogiType;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ExtendedShogiStyle extends BaseComponentStyle
{
    public ExtendedShogiStyle(final Component component) {
        super(component);
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        this.genericMetadataChecks(context, localState);
        final String path = ImageUtil.getImageFullPath("shogi_blank");
        final int g2dSize = (int)(imageSize * this.scale());
        SVGGraphics2D g2d = new SVGGraphics2D(g2dSize, g2dSize);
        this.renderImageSVGFromPath(g2d, context, imageSize, path, localState);
        for (int i = 0; i < ShogiType.values().length; ++i) {
            if (ShogiType.values()[i].englishName().equals(this.svgName) || ShogiType.values()[i].kanji().equals(this.svgName) || ShogiType.values()[i].romaji().equalsIgnoreCase(this.svgName) || ShogiType.values()[i].name().equalsIgnoreCase(this.svgName)) {
                g2d = this.imageSVG.get(localState);
                final Font valueFont = new Font("Arial", 0, g2dSize / 3);
                g2d.setColor(Color.BLACK);
                g2d.setFont(valueFont);
                if (ShogiType.values()[i].kanji().length() == 1) {
                    final Rectangle2D rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(0)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(0)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() / 2.0));
                    break;
                }
                if (ShogiType.values()[i].kanji().length() == 2) {
                    Rectangle2D rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(0)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(0)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), this.imageSVG.get(localState).getHeight() / 2);
                    rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(1)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(1)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight()));
                    break;
                }
                if (ShogiType.values()[i].kanji().length() == 3) {
                    Rectangle2D rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(0)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(0)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 - rect.getHeight() / 4.0));
                    rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(1)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(1)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() / 2.0));
                    rect = valueFont.getStringBounds(Character.toString(ShogiType.values()[i].kanji().charAt(2)), g2d.getFontRenderContext());
                    g2d.drawString(Character.toString(ShogiType.values()[i].kanji().charAt(2)), (int)(this.imageSVG.get(localState).getWidth() / 2 - rect.getWidth() / 2.0), (int)(this.imageSVG.get(localState).getHeight() / 2 + rect.getHeight() * 1.3));
                    break;
                }
            }
        }
    }
}
