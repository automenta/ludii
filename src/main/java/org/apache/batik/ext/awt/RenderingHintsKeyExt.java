// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt;

import java.awt.RenderingHints;

public final class RenderingHintsKeyExt
{
    public static final int KEY_BASE;
    public static final RenderingHints.Key KEY_TRANSCODING;
    public static final String VALUE_TRANSCODING_PRINTING = "Printing";
    public static final String VALUE_TRANSCODING_VECTOR = "Vector";
    public static final RenderingHints.Key KEY_AREA_OF_INTEREST;
    public static final RenderingHints.Key KEY_BUFFERED_IMAGE;
    public static final RenderingHints.Key KEY_COLORSPACE;
    public static final RenderingHints.Key KEY_AVOID_TILE_PAINTING;
    public static final Object VALUE_AVOID_TILE_PAINTING_ON;
    public static final Object VALUE_AVOID_TILE_PAINTING_OFF;
    public static final Object VALUE_AVOID_TILE_PAINTING_DEFAULT;
    
    private RenderingHintsKeyExt() {
    }
    
    static {
        VALUE_AVOID_TILE_PAINTING_ON = new Object();
        VALUE_AVOID_TILE_PAINTING_OFF = new Object();
        VALUE_AVOID_TILE_PAINTING_DEFAULT = new Object();
        int base = 10100;
        RenderingHints.Key trans = null;
        RenderingHints.Key aoi = null;
        RenderingHints.Key bi = null;
        RenderingHints.Key cs = null;
        RenderingHints.Key atp = null;
        while (true) {
            int val = base;
            try {
                trans = new TranscodingHintKey(val++);
                aoi = new AreaOfInterestHintKey(val++);
                bi = new BufferedImageHintKey(val++);
                cs = new ColorSpaceHintKey(val++);
                atp = new AvoidTilingHintKey(val++);
            }
            catch (Exception e) {
                System.err.println("You have loaded the Batik jar files more than once\nin the same JVM this is likely a problem with the\nway you are loading the Batik jar files.");
                base = (int)(Math.random() * 2000000.0);
                continue;
            }
            break;
        }
        KEY_BASE = base;
        KEY_TRANSCODING = trans;
        KEY_AREA_OF_INTEREST = aoi;
        KEY_BUFFERED_IMAGE = bi;
        KEY_COLORSPACE = cs;
        KEY_AVOID_TILE_PAINTING = atp;
    }
}
