// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.awt.Graphics2D;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.util.Hashtable;
import java.awt.image.BufferedImage;
import org.apache.batik.i18n.LocalizableSupport;
import java.awt.Color;
import org.apache.batik.ext.awt.image.renderable.Filter;

public class DefaultBrokenLinkProvider extends BrokenLinkProvider
{
    static Filter brokenLinkImg;
    static final String MESSAGE_RSRC = "resources.Messages";
    static final Color BROKEN_LINK_COLOR;
    
    public static String formatMessage(final Object base, final String code, final Object[] params) {
        ClassLoader cl = null;
        try {
            cl = DefaultBrokenLinkProvider.class.getClassLoader();
            cl = base.getClass().getClassLoader();
        }
        catch (SecurityException ex) {}
        final LocalizableSupport ls = new LocalizableSupport("resources.Messages", base.getClass(), cl);
        return ls.formatMessage(code, params);
    }
    
    @Override
    public Filter getBrokenLinkImage(final Object base, final String code, final Object[] params) {
        synchronized (DefaultBrokenLinkProvider.class) {
            if (DefaultBrokenLinkProvider.brokenLinkImg != null) {
                return DefaultBrokenLinkProvider.brokenLinkImg;
            }
            BufferedImage bi = new BufferedImage(100, 100, 2);
            final Hashtable ht = new Hashtable();
            ht.put("org.apache.batik.BrokenLinkImage", formatMessage(base, code, params));
            bi = new BufferedImage(bi.getColorModel(), bi.getRaster(), bi.isAlphaPremultiplied(), ht);
            final Graphics2D g2d = bi.createGraphics();
            g2d.setColor(DefaultBrokenLinkProvider.BROKEN_LINK_COLOR);
            g2d.fillRect(0, 0, 100, 100);
            g2d.setColor(Color.black);
            g2d.drawRect(2, 2, 96, 96);
            g2d.drawString("Broken Image", 6, 50);
            g2d.dispose();
            return DefaultBrokenLinkProvider.brokenLinkImg = new RedRable(GraphicsUtil.wrap(bi));
        }
    }
    
    static {
        DefaultBrokenLinkProvider.brokenLinkImg = null;
        BROKEN_LINK_COLOR = new Color(255, 255, 255, 190);
    }
}
