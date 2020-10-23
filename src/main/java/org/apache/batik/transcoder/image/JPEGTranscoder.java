// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.image;

import org.apache.batik.ext.awt.image.spi.ImageWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.RenderedImage;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.apache.batik.ext.awt.image.spi.ImageWriterRegistry;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.image.resources.Messages;
import org.apache.batik.transcoder.TranscoderOutput;
import java.awt.image.BufferedImage;
import java.awt.Color;
import org.apache.batik.transcoder.TranscodingHints;

public class JPEGTranscoder extends ImageTranscoder
{
    public static final TranscodingHints.Key KEY_QUALITY;
    
    public JPEGTranscoder() {
        this.hints.put(ImageTranscoder.KEY_BACKGROUND_COLOR, Color.white);
    }
    
    @Override
    public BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, 1);
    }
    
    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) throws TranscoderException {
        OutputStream ostream = output.getOutputStream();
        ostream = new OutputStreamWrapper(ostream);
        try {
            float quality;
            if (this.hints.containsKey(JPEGTranscoder.KEY_QUALITY)) {
                quality = (float)this.hints.get(JPEGTranscoder.KEY_QUALITY);
            }
            else {
                final TranscoderException te = new TranscoderException(Messages.formatMessage("jpeg.unspecifiedQuality", null));
                this.handler.error(te);
                quality = 0.75f;
            }
            final ImageWriter writer = ImageWriterRegistry.getInstance().getWriterFor("image/jpeg");
            final ImageWriterParams params = new ImageWriterParams();
            params.setJPEGQuality(quality, true);
            final float PixSzMM = this.userAgent.getPixelUnitToMillimeter();
            final int PixSzInch = (int)(25.4 / PixSzMM + 0.5);
            params.setResolution(PixSzInch);
            writer.writeImage(img, ostream, params);
            ostream.flush();
        }
        catch (IOException ex) {
            throw new TranscoderException(ex);
        }
    }
    
    static {
        KEY_QUALITY = new QualityKey();
    }
    
    private static class QualityKey extends TranscodingHints.Key
    {
        @Override
        public boolean isCompatibleValue(final Object v) {
            if (v instanceof Float) {
                final float q = (float)v;
                return q > 0.0f && q <= 1.0f;
            }
            return false;
        }
    }
    
    private static class OutputStreamWrapper extends OutputStream
    {
        OutputStream os;
        
        OutputStreamWrapper(final OutputStream os) {
            this.os = os;
        }
        
        @Override
        public void close() throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.close();
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
        
        @Override
        public void flush() throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.flush();
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b, off, len);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (this.os == null) {
                return;
            }
            try {
                this.os.write(b);
            }
            catch (IOException ioe) {
                this.os = null;
            }
        }
    }
}
