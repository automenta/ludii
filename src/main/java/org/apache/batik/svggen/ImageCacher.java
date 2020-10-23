// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.ListIterator;
import java.util.LinkedList;
import java.io.ByteArrayOutputStream;
import java.util.zip.Adler32;
import java.util.HashMap;
import java.util.zip.Checksum;
import java.util.Map;

public abstract class ImageCacher implements SVGSyntax, ErrorConstants
{
    DOMTreeManager domTreeManager;
    Map imageCache;
    Checksum checkSum;
    
    public ImageCacher() {
        this.domTreeManager = null;
        this.imageCache = new HashMap();
        this.checkSum = new Adler32();
    }
    
    public ImageCacher(final DOMTreeManager domTreeManager) {
        this();
        this.setDOMTreeManager(domTreeManager);
    }
    
    public void setDOMTreeManager(final DOMTreeManager domTreeManager) {
        if (domTreeManager == null) {
            throw new IllegalArgumentException();
        }
        this.domTreeManager = domTreeManager;
    }
    
    public DOMTreeManager getDOMTreeManager() {
        return this.domTreeManager;
    }
    
    public String lookup(final ByteArrayOutputStream os, final int width, final int height, final SVGGeneratorContext ctx) throws SVGGraphics2DIOException {
        final int checksum = this.getChecksum(os.toByteArray());
        final Integer key = checksum;
        String href = null;
        final Object data = this.getCacheableData(os);
        LinkedList list = this.imageCache.get(key);
        if (list == null) {
            list = new LinkedList();
            this.imageCache.put(key, list);
        }
        else {
            final ListIterator i = list.listIterator(0);
            while (i.hasNext()) {
                final ImageCacheEntry entry = i.next();
                if (entry.checksum == checksum && this.imagesMatch(entry.src, data)) {
                    href = entry.href;
                    break;
                }
            }
        }
        if (href == null) {
            final ImageCacheEntry newEntry = this.createEntry(checksum, data, width, height, ctx);
            list.add(newEntry);
            href = newEntry.href;
        }
        return href;
    }
    
    abstract Object getCacheableData(final ByteArrayOutputStream p0);
    
    abstract boolean imagesMatch(final Object p0, final Object p1) throws SVGGraphics2DIOException;
    
    abstract ImageCacheEntry createEntry(final int p0, final Object p1, final int p2, final int p3, final SVGGeneratorContext p4) throws SVGGraphics2DIOException;
    
    int getChecksum(final byte[] data) {
        this.checkSum.reset();
        this.checkSum.update(data, 0, data.length);
        return (int)this.checkSum.getValue();
    }
    
    private static class ImageCacheEntry
    {
        public int checksum;
        public Object src;
        public String href;
        
        ImageCacheEntry(final int checksum, final Object src, final String href) {
            this.checksum = checksum;
            this.src = src;
            this.href = href;
        }
    }
    
    public static class Embedded extends ImageCacher
    {
        @Override
        public void setDOMTreeManager(final DOMTreeManager domTreeManager) {
            if (this.domTreeManager != domTreeManager) {
                this.domTreeManager = domTreeManager;
                this.imageCache = new HashMap();
            }
        }
        
        @Override
        Object getCacheableData(final ByteArrayOutputStream os) {
            return "data:image/png;base64," + os.toString();
        }
        
        @Override
        boolean imagesMatch(final Object o1, final Object o2) {
            return o1.equals(o2);
        }
        
        @Override
        ImageCacheEntry createEntry(final int checksum, final Object data, final int width, final int height, final SVGGeneratorContext ctx) {
            final String id = ctx.idGenerator.generateID("image");
            this.addToTree(id, (String)data, width, height, ctx);
            return new ImageCacheEntry(checksum, data, "#" + id);
        }
        
        private void addToTree(final String id, final String href, final int width, final int height, final SVGGeneratorContext ctx) {
            final Document domFactory = this.domTreeManager.getDOMFactory();
            final Element imageElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "image");
            imageElement.setAttributeNS(null, "id", id);
            imageElement.setAttributeNS(null, "width", Integer.toString(width));
            imageElement.setAttributeNS(null, "height", Integer.toString(height));
            imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", href);
            this.domTreeManager.addOtherDef(imageElement);
        }
    }
    
    public static class External extends ImageCacher
    {
        private String imageDir;
        private String prefix;
        private String suffix;
        
        public External(final String imageDir, final String prefix, final String suffix) {
            this.imageDir = imageDir;
            this.prefix = prefix;
            this.suffix = suffix;
        }
        
        @Override
        Object getCacheableData(final ByteArrayOutputStream os) {
            return os;
        }
        
        @Override
        boolean imagesMatch(final Object o1, final Object o2) throws SVGGraphics2DIOException {
            boolean match = false;
            FileInputStream imageStream = null;
            try {
                imageStream = new FileInputStream((File)o1);
                final int imageLen = imageStream.available();
                final byte[] imageBytes = new byte[imageLen];
                final byte[] candidateBytes = ((ByteArrayOutputStream)o2).toByteArray();
                for (int bytesRead = 0; bytesRead != imageLen; bytesRead += imageStream.read(imageBytes, bytesRead, imageLen - bytesRead)) {}
                match = Arrays.equals(imageBytes, candidateBytes);
            }
            catch (IOException e) {
                throw new SVGGraphics2DIOException("could not read image File " + ((File)o1).getName());
            }
            finally {
                try {
                    if (imageStream != null) {
                        imageStream.close();
                    }
                }
                catch (IOException ex) {}
            }
            return match;
        }
        
        @Override
        ImageCacheEntry createEntry(final int checksum, final Object data, final int width, final int height, final SVGGeneratorContext ctx) throws SVGGraphics2DIOException {
            File imageFile = null;
            try {
                while (imageFile == null) {
                    final String fileId = ctx.idGenerator.generateID(this.prefix);
                    imageFile = new File(this.imageDir, fileId + this.suffix);
                    if (imageFile.exists()) {
                        imageFile = null;
                    }
                }
                final OutputStream outputStream = new FileOutputStream(imageFile);
                ((ByteArrayOutputStream)data).writeTo(outputStream);
                ((ByteArrayOutputStream)data).close();
            }
            catch (IOException e) {
                throw new SVGGraphics2DIOException("could not write image File " + imageFile.getName());
            }
            return new ImageCacheEntry(checksum, imageFile, imageFile.getName());
        }
    }
}
