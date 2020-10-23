// 
// Decompiled by Procyon v0.5.36
// 

package view.component.custom;

import game.equipment.component.Card;
import game.equipment.component.Component;
import game.types.component.SuitType;
import graphics.svg.SVGtoImage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import util.BaseCardImages;
import util.Context;
import view.component.BaseComponentStyle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CardStyle extends BaseComponentStyle
{
    private final BaseCardImages baseCardImages;
    
    public CardStyle(final Component component) {
        super(component);
        this.baseCardImages = new BaseCardImages();
    }
    
    @Override
    public void renderImageSVG(final Context context, final int imageSize, final int localState, final boolean secondary, final int maskedValue) {
        while (this.imageSVG.size() <= localState) {
            this.imageSVG.add(null);
        }
        final Point2D.Double[][] pts = { new Point2D.Double[0], { new Point2D.Double(0.5, 0.5) }, { new Point2D.Double(0.5, 0.225), new Point2D.Double(0.5, 0.775) }, { new Point2D.Double(0.5, 0.225), new Point2D.Double(0.5, 0.5), new Point2D.Double(0.5, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.5, 0.5), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.31, 0.5), new Point2D.Double(0.69, 0.5), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.5, 0.35), new Point2D.Double(0.31, 0.5), new Point2D.Double(0.69, 0.5), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.5, 0.35), new Point2D.Double(0.31, 0.5), new Point2D.Double(0.69, 0.5), new Point2D.Double(0.5, 0.65), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.31, 0.41), new Point2D.Double(0.69, 0.41), new Point2D.Double(0.5, 0.5), new Point2D.Double(0.31, 0.59), new Point2D.Double(0.69, 0.59), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.31, 0.225), new Point2D.Double(0.69, 0.225), new Point2D.Double(0.5, 0.31), new Point2D.Double(0.31, 0.41), new Point2D.Double(0.69, 0.41), new Point2D.Double(0.31, 0.59), new Point2D.Double(0.69, 0.59), new Point2D.Double(0.5, 0.69), new Point2D.Double(0.31, 0.775), new Point2D.Double(0.69, 0.775) }, { new Point2D.Double(0.81, 0.135) }, { new Point2D.Double(0.81, 0.135) }, { new Point2D.Double(0.81, 0.135) } };
        final int cardSize = imageSize;
        if (!this.baseCardImages.areLoaded()) {
            this.baseCardImages.loadImages(cardSize);
        }
        final int ht = cardSize;
        final int wd = (int)(0.6470588235294118 * ht + 0.5) / 2 * 2;
        final String suitSVGLarge = this.baseCardImages.getPath(0, this.getCardSuitValue(context));
        final Rectangle2D suitRectLarge = SVGtoImage.getBounds(suitSVGLarge, this.baseCardImages.getSuitSizeBig());
        final int lx = (int)suitRectLarge.getWidth();
        final int ly = (int)suitRectLarge.getHeight();
        final String suitSVGSmall = this.baseCardImages.getPath(1, this.getCardSuitValue(context));
        final Rectangle2D suitRectSmall = SVGtoImage.getBounds(suitSVGSmall, this.baseCardImages.getSuitSizeSmall());
        final int sy = (int)suitRectSmall.getHeight();
        final int sx = (int)(sy * lx / (double)ly + 0.5);
        final SVGGraphics2D g2d = new SVGGraphics2D(cardSize, cardSize);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Color cardFillColour = Color.white;
        if (maskedValue == 1) {
            cardFillColour = new Color(180, 0, 0);
        }
        final int round = (int)(0.1 * cardSize + 0.5);
        g2d.setColor(cardFillColour);
        g2d.fillRoundRect(cardSize / 2 - wd / 2, 1, wd, ht - 2, round, round);
        g2d.setStroke(new BasicStroke());
        g2d.setColor(Color.black);
        g2d.drawRoundRect(cardSize / 2 - wd / 2, 1, wd, ht - 2, round, round);
        if (maskedValue == 0) {
            final int off = (int)(0.03 * cardSize + 0.5);
            final int fontSize = (int)(0.11 * cardSize + 0.5);
            final Font cardFont = new Font("Arial", 1, fontSize);
            g2d.setFont(cardFont);
            final String label = this.component.cardType().label();
            final Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(label, g2d);
            final int tx = cardSize / 2 - wd / 2 + 2 * off - (int)(bounds.getWidth() / 2.0 + 0.5);
            final int ty = 5 * off;
            final Color color = this.isBlack(context) ? Color.BLACK : Color.RED;
            g2d.setColor(color);
            g2d.drawString(label, tx, ty);
            SVGtoImage.loadFromString(g2d, suitSVGSmall, this.baseCardImages.getSuitSizeSmall(cardSize), cardSize / 2 - wd / 2 + 2 * off - sx / 2, 6 * off, color, null, false);
            final int number = this.component.cardType().number();
            if (this.component.cardType().isRoyal()) {
                final int ry = (int)(0.6 * cardSize + 0.5);
                final String royalSVG = this.isBlack(context) ? this.baseCardImages.getPath(2, number) : this.baseCardImages.getPath(3, number);
                if (royalSVG != null) {
                    SVGtoImage.loadFromString(g2d, royalSVG, ry, cardSize / 2 - ry / 2, cardSize / 2 - ry / 2, color, null, true);
                }
            }
            if (number < pts.length) {
                for (int n = 0; n < pts[number].length; ++n) {
                    final Point2D.Double pt = pts[number][n];
                    final int x = cardSize / 2 - wd / 2 + (int)(pt.x * wd + 0.5);
                    final int y = (int)(pt.y * ht + 0.5);
                    SVGtoImage.loadFromString(g2d, suitSVGLarge, this.baseCardImages.getSuitSizeBig(cardSize), x - lx / 2, y - ly / 2, color, null, false);
                }
            }
        }
        this.imageSVG.set(localState, g2d);
    }
    
    private boolean isBlack(final Context context) {
        return SuitType.values()[this.getCardSuitValue(context) - 1] != SuitType.Diamonds && SuitType.values()[this.getCardSuitValue(context) - 1] != SuitType.Hearts;
    }
    
    private int getCardSuitValue(final Context context) {
        if (!this.component.isCard()) {
            return -1;
        }
        final Card card = (Card)this.component;
        if (context.game().metadata().graphics().suitRanking() == null) {
            return card.suit();
        }
        final int initValue = card.suit();
        int cardValue = 0;
        for (final SuitType suit : context.game().metadata().graphics().suitRanking()) {
            ++cardValue;
            if (suit == SuitType.Clubs && initValue == SuitType.Clubs.value) {
                return cardValue;
            }
            if (suit == SuitType.Spades && initValue == SuitType.Spades.value) {
                return cardValue;
            }
            if (suit == SuitType.Diamonds && initValue == SuitType.Diamonds.value) {
                return cardValue;
            }
            if (suit == SuitType.Hearts && initValue == SuitType.Hearts.value) {
                return cardValue;
            }
        }
        return card.suit();
    }
}
