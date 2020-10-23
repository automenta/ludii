// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.awt.font.TextLayout;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;

public abstract class ConcreteTextPainter extends BasicTextPainter
{
    public void paint(final AttributedCharacterIterator aci, final Point2D location, final TextNode.Anchor anchor, final Graphics2D g2d) {
        final TextLayout layout = new TextLayout(aci, this.fontRenderContext);
        final float advance = layout.getAdvance();
        float tx = 0.0f;
        switch (anchor.getType()) {
            case 1: {
                tx = -advance / 2.0f;
                break;
            }
            case 2: {
                tx = -advance;
                break;
            }
        }
        layout.draw(g2d, (float)(location.getX() + tx), (float)location.getY());
    }
}
