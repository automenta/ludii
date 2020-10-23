// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.spi;

import java.awt.Graphics2D;
import java.util.Map;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.Image;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.awt.Toolkit;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.util.ParsedURL;

public class JDKRegistryEntry extends AbstractRegistryEntry implements URLRegistryEntry
{
    public static final float PRIORITY = 1000000.0f;
    
    public JDKRegistryEntry() {
        super("JDK", 1000000.0f, new String[0], new String[] { "image/gif" });
    }
    
    @Override
    public boolean isCompatibleURL(final ParsedURL purl) {
        try {
            new URL(purl.toString());
        }
        catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }
    
    @Override
    public Filter handleURL(final ParsedURL purl, final boolean needRawData) {
        URL url;
        try {
            url = new URL(purl.toString());
        }
        catch (MalformedURLException mue) {
            return null;
        }
        final DeferRable dr = new DeferRable();
        String errCode;
        Object[] errParam;
        if (purl != null) {
            errCode = "url.format.unreadable";
            errParam = new Object[] { "JDK", url };
        }
        else {
            errCode = "stream.format.unreadable";
            errParam = new Object[] { "JDK" };
        }
        final Thread t = new Thread() {
            @Override
            public void run() {
                Filter filt = null;
                try {
                    final Toolkit tk = Toolkit.getDefaultToolkit();
                    final Image img = tk.createImage(url);
                    if (img != null) {
                        final RenderedImage ri = JDKRegistryEntry.this.loadImage(img, dr);
                        if (ri != null) {
                            filt = new RedRable(GraphicsUtil.wrap(ri));
                        }
                    }
                }
                catch (ThreadDeath td) {
                    filt = ImageTagRegistry.getBrokenLinkImage(JDKRegistryEntry.this, errCode, errParam);
                    dr.setSource(filt);
                    throw td;
                }
                catch (Throwable t) {}
                if (filt == null) {
                    filt = ImageTagRegistry.getBrokenLinkImage(JDKRegistryEntry.this, errCode, errParam);
                }
                dr.setSource(filt);
            }
        };
        t.start();
        return dr;
    }
    
    public RenderedImage loadImage(final Image img, final DeferRable dr) {
        if (img instanceof RenderedImage) {
            return (RenderedImage)img;
        }
        final MyImgObs observer = new MyImgObs();
        Toolkit.getDefaultToolkit().prepareImage(img, -1, -1, observer);
        observer.waitTilWidthHeightDone();
        if (observer.imageError) {
            return null;
        }
        final int width = observer.width;
        final int height = observer.height;
        dr.setBounds(new Rectangle2D.Double(0.0, 0.0, width, height));
        final BufferedImage bi = new BufferedImage(width, height, 2);
        final Graphics2D g2d = bi.createGraphics();
        observer.waitTilImageDone();
        if (observer.imageError) {
            return null;
        }
        dr.setProperties(new HashMap());
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bi;
    }
    
    public static class MyImgObs implements ImageObserver
    {
        boolean widthDone;
        boolean heightDone;
        boolean imageDone;
        int width;
        int height;
        boolean imageError;
        int IMG_BITS;
        
        public MyImgObs() {
            this.widthDone = false;
            this.heightDone = false;
            this.imageDone = false;
            this.width = -1;
            this.height = -1;
            this.imageError = false;
            this.IMG_BITS = 224;
        }
        
        public void clear() {
            this.width = -1;
            this.height = -1;
            this.widthDone = false;
            this.heightDone = false;
            this.imageDone = false;
        }
        
        @Override
        public boolean imageUpdate(final Image img, final int infoflags, final int x, final int y, final int width, final int height) {
            synchronized (this) {
                boolean notify = false;
                if ((infoflags & 0x1) != 0x0) {
                    this.width = width;
                }
                if ((infoflags & 0x2) != 0x0) {
                    this.height = height;
                }
                if ((infoflags & 0x20) != 0x0) {
                    this.width = width;
                    this.height = height;
                }
                if ((infoflags & this.IMG_BITS) != 0x0) {
                    if (!this.widthDone || !this.heightDone || !this.imageDone) {
                        this.widthDone = true;
                        this.heightDone = true;
                        this.imageDone = true;
                        notify = true;
                    }
                    if ((infoflags & 0x40) != 0x0) {
                        this.imageError = true;
                    }
                }
                if (!this.widthDone && this.width != -1) {
                    notify = true;
                    this.widthDone = true;
                }
                if (!this.heightDone && this.height != -1) {
                    notify = true;
                    this.heightDone = true;
                }
                if (notify) {
                    this.notifyAll();
                }
            }
            return true;
        }
        
        public synchronized void waitTilWidthHeightDone() {
            while (true) {
                if (this.widthDone) {
                    if (this.heightDone) {
                        break;
                    }
                }
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        
        public synchronized void waitTilWidthDone() {
            while (!this.widthDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        
        public synchronized void waitTilHeightDone() {
            while (!this.heightDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
        
        public synchronized void waitTilImageDone() {
            while (!this.imageDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {}
            }
        }
    }
}
