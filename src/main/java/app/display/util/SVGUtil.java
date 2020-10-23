// 
// Decompiled by Procyon v0.5.36
// 

package app.display.util;

import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SVGUtil
{
    public static BufferedImage createSVGImage(final String imageEntry, final double width, final double height) {
        final BufferedImage[] imagePointer = { null };
        try {
            final InputStream inputStream = new ByteArrayInputStream(imageEntry.getBytes(StandardCharsets.UTF_8));
            final TranscoderInput input = new TranscoderInput(inputStream);
            final ImageTranscoder t = new ImageTranscoder() {
                @Override
                protected ImageRenderer createRenderer() {
                    final ImageRenderer r = super.createRenderer();
                    final RenderingHints rh = r.getRenderingHints();
                    rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
                    rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
                    rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                    rh.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
                    rh.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
                    rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
                    rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
                    rh.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
                    rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
                    r.setRenderingHints(rh);
                    return r;
                }
                
                @Override
                public BufferedImage createImage(final int w, final int h) {
                    return new BufferedImage(w, h, 2);
                }
                
                @Override
                public void writeImage(final BufferedImage img, final TranscoderOutput output) {
                    imagePointer[0] = img;
                }
            };
            t.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, Boolean.FALSE);
            if (width > 0.0 && height > 0.0) {
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float)width);
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float)height);
            }
            t.transcode(input, null);
        }
        catch (TranscoderException ex) {}
        return imagePointer[0];
    }
}
