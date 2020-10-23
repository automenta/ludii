// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.image;

import org.apache.batik.transcoder.keys.StringKey;
import org.apache.batik.transcoder.TranscoderException;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.transcoder.TranscoderOutput;
import java.lang.reflect.InvocationTargetException;
import java.awt.image.BufferedImage;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.transcoder.TranscodingHints;

public class TIFFTranscoder extends ImageTranscoder
{
    public static final TranscodingHints.Key KEY_FORCE_TRANSPARENT_WHITE;
    public static final TranscodingHints.Key KEY_COMPRESSION_METHOD;
    
    public TIFFTranscoder() {
        this.hints.put(TIFFTranscoder.KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
    }
    
    public UserAgent getUserAgent() {
        return this.userAgent;
    }
    
    @Override
    public BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, 2);
    }
    
    private WriteAdapter getWriteAdapter(final String className) {
        try {
            final Class clazz = Class.forName(className);
            final WriteAdapter adapter = clazz.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            return adapter;
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        catch (InstantiationException e2) {
            return null;
        }
        catch (IllegalAccessException e3) {
            return null;
        }
        catch (NoSuchMethodException e4) {
            return null;
        }
        catch (InvocationTargetException e5) {
            return null;
        }
    }
    
    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) throws TranscoderException {
        boolean forceTransparentWhite = false;
        if (this.hints.containsKey(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE)) {
            forceTransparentWhite = (boolean)this.hints.get(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE);
        }
        if (forceTransparentWhite) {
            final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)img.getSampleModel();
            this.forceTransparentWhite(img, sppsm);
        }
        WriteAdapter adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.tiff.TIFFTranscoderInternalCodecWriteAdapter");
        if (adapter == null) {
            adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.imageio.TIFFTranscoderImageIOWriteAdapter");
        }
        if (adapter == null) {
            throw new TranscoderException("Could not write TIFF file because no WriteAdapter is availble");
        }
        adapter.writeImage(this, img, output);
    }
    
    static {
        KEY_FORCE_TRANSPARENT_WHITE = ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE;
        KEY_COMPRESSION_METHOD = new StringKey();
    }
    
    public interface WriteAdapter
    {
        void writeImage(final TIFFTranscoder p0, final BufferedImage p1, final TranscoderOutput p2) throws TranscoderException;
    }
}
