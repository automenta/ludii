// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.image;

import org.apache.batik.transcoder.keys.IntegerKey;
import org.apache.batik.transcoder.keys.FloatKey;
import java.io.OutputStream;
import java.awt.image.SinglePixelPackedSampleModel;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.transcoder.TranscoderOutput;
import java.lang.reflect.InvocationTargetException;
import java.awt.image.BufferedImage;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.transcoder.TranscodingHints;

public class PNGTranscoder extends ImageTranscoder
{
    public static final TranscodingHints.Key KEY_GAMMA;
    public static final float[] DEFAULT_CHROMA;
    public static final TranscodingHints.Key KEY_INDEXED;
    
    public PNGTranscoder() {
        this.hints.put(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
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
        final OutputStream ostream = output.getOutputStream();
        if (ostream == null) {
            throw new TranscoderException(Messages.formatMessage("png.badoutput", null));
        }
        boolean forceTransparentWhite = false;
        if (this.hints.containsKey(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE)) {
            forceTransparentWhite = (boolean)this.hints.get(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE);
        }
        if (forceTransparentWhite) {
            final SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)img.getSampleModel();
            this.forceTransparentWhite(img, sppsm);
        }
        WriteAdapter adapter = this.getWriteAdapter("org.apache.batik.ext.awt.image.codec.png.PNGTranscoderInternalCodecWriteAdapter");
        if (adapter == null) {
            adapter = this.getWriteAdapter("org.apache.batik.transcoder.image.PNGTranscoderImageIOWriteAdapter");
        }
        if (adapter == null) {
            throw new TranscoderException("Could not write PNG file because no WriteAdapter is availble");
        }
        adapter.writeImage(this, img, output);
    }
    
    static {
        KEY_GAMMA = new FloatKey();
        DEFAULT_CHROMA = new float[] { 0.3127f, 0.329f, 0.64f, 0.33f, 0.3f, 0.6f, 0.15f, 0.06f };
        KEY_INDEXED = new IntegerKey();
    }
    
    public interface WriteAdapter
    {
        void writeImage(final PNGTranscoder p0, final BufferedImage p1, final TranscoderOutput p2) throws TranscoderException;
    }
}
