// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.rendered;

import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.Graphics2D;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import java.util.Hashtable;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.IndexColorModel;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class IndexImage
{
    static byte[][] computeRGB(final int nCubes, final Cube[] cubes) {
        final byte[] r = new byte[nCubes];
        final byte[] g = new byte[nCubes];
        final byte[] b = new byte[nCubes];
        byte[] rgb = new byte[3];
        for (int i = 0; i < nCubes; ++i) {
            rgb = cubes[i].averageColorRGB(rgb);
            r[i] = rgb[0];
            g[i] = rgb[1];
            b[i] = rgb[2];
        }
        final byte[][] result = { r, g, b };
        return result;
    }
    
    static void logRGB(final byte[] r, final byte[] g, final byte[] b) {
        final StringBuffer buff = new StringBuffer(100);
        final int nColors = r.length;
        for (int i = 0; i < nColors; ++i) {
            final String rgbStr = "(" + (r[i] + 128) + ',' + (g[i] + 128) + ',' + (b[i] + 128) + "),";
            buff.append(rgbStr);
        }
        System.out.println("RGB:" + nColors + (Object)buff);
    }
    
    static List[] createColorList(final BufferedImage bi) {
        final int w = bi.getWidth();
        final int h = bi.getHeight();
        final List[] colors = new ArrayList[4096];
        for (int i_w = 0; i_w < w; ++i_w) {
        Label_0182:
            for (int i_h = 0; i_h < h; ++i_h) {
                final int rgb = bi.getRGB(i_w, i_h) & 0xFFFFFF;
                final int idx = (rgb & 0xF00000) >>> 12 | (rgb & 0xF000) >>> 8 | (rgb & 0xF0) >>> 4;
                List v = colors[idx];
                if (v == null) {
                    v = new ArrayList();
                    v.add(new Counter(rgb));
                    colors[idx] = v;
                }
                else {
                    final Iterator i = v.iterator();
                    while (i.hasNext()) {
                        if (i.next().add(rgb)) {
                            continue Label_0182;
                        }
                    }
                    v.add(new Counter(rgb));
                }
            }
        }
        return colors;
    }
    
    static Counter[][] convertColorList(final List[] colors) {
        final Counter[] EMPTY_COUNTER = new Counter[0];
        final Counter[][] colorTbl = new Counter[4096][];
        for (int i = 0; i < colors.length; ++i) {
            final List cl = colors[i];
            if (cl == null) {
                colorTbl[i] = EMPTY_COUNTER;
            }
            else {
                final int nSlots = cl.size();
                colorTbl[i] = cl.toArray(new Counter[nSlots]);
                colors[i] = null;
            }
        }
        return colorTbl;
    }
    
    public static BufferedImage getIndexedImage(BufferedImage bi, final int nColors) {
        final int w = bi.getWidth();
        final int h = bi.getHeight();
        List[] colors = createColorList(bi);
        final Counter[][] colorTbl = convertColorList(colors);
        colors = null;
        int nCubes = 1;
        int fCube = 0;
        final Cube[] cubes = new Cube[nColors];
        cubes[0] = new Cube(colorTbl, w * h);
        while (nCubes < nColors) {
            while (cubes[fCube].isDone() && ++fCube != nCubes) {}
            if (fCube == nCubes) {
                break;
            }
            Cube c = cubes[fCube];
            Cube nc = c.split();
            if (nc == null) {
                continue;
            }
            if (nc.count > c.count) {
                final Cube tmp = c;
                c = nc;
                nc = tmp;
            }
            int j = fCube;
            for (int cnt = c.count, i = fCube + 1; i < nCubes && cubes[i].count >= cnt; ++i) {
                cubes[j++] = cubes[i];
            }
            cubes[j++] = c;
            for (int cnt = nc.count; j < nCubes && cubes[j].count >= cnt; ++j) {}
            for (int i = nCubes; i > j; --i) {
                cubes[i] = cubes[i - 1];
            }
            cubes[j++] = nc;
            ++nCubes;
        }
        final byte[][] rgbTbl = computeRGB(nCubes, cubes);
        final IndexColorModel icm = new IndexColorModel(8, nCubes, rgbTbl[0], rgbTbl[1], rgbTbl[2]);
        BufferedImage indexed = new BufferedImage(w, h, 13, icm);
        final Graphics2D g2d = indexed.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.drawImage(bi, 0, 0, null);
        g2d.dispose();
        int bits;
        for (bits = 1; bits <= 8 && 1 << bits < nCubes; ++bits) {}
        if (bits > 4) {
            return indexed;
        }
        if (bits == 3) {
            bits = 4;
        }
        final ColorModel cm = new IndexColorModel(bits, nCubes, rgbTbl[0], rgbTbl[1], rgbTbl[2]);
        final SampleModel sm = new MultiPixelPackedSampleModel(0, w, h, bits);
        final WritableRaster ras = Raster.createWritableRaster(sm, new Point(0, 0));
        bi = indexed;
        indexed = new BufferedImage(cm, ras, bi.isAlphaPremultiplied(), null);
        GraphicsUtil.copyData(bi, indexed);
        return indexed;
    }
    
    private static class Counter
    {
        final int val;
        int count;
        
        Counter(final int val) {
            this.count = 1;
            this.val = val;
        }
        
        boolean add(final int val) {
            if (this.val != val) {
                return false;
            }
            ++this.count;
            return true;
        }
        
        int[] getRgb(final int[] rgb) {
            rgb[0] = (this.val & 0xFF0000) >> 16;
            rgb[1] = (this.val & 0xFF00) >> 8;
            rgb[2] = (this.val & 0xFF);
            return rgb;
        }
    }
    
    private static class Cube
    {
        static final byte[] RGB_BLACK;
        int[] min;
        int[] max;
        boolean done;
        final Counter[][] colors;
        int count;
        static final int RED = 0;
        static final int GRN = 1;
        static final int BLU = 2;
        
        Cube(final Counter[][] colors, final int count) {
            this.min = new int[] { 0, 0, 0 };
            this.max = new int[] { 255, 255, 255 };
            this.done = false;
            this.count = 0;
            this.colors = colors;
            this.count = count;
        }
        
        public boolean isDone() {
            return this.done;
        }
        
        private boolean contains(final int[] val) {
            final int vRed = val[0];
            final int vGrn = val[1];
            final int vBlu = val[2];
            return this.min[0] <= vRed && vRed <= this.max[0] && this.min[1] <= vGrn && vGrn <= this.max[1] && this.min[2] <= vBlu && vBlu <= this.max[2];
        }
        
        Cube split() {
            final int dr = this.max[0] - this.min[0] + 1;
            final int dg = this.max[1] - this.min[1] + 1;
            final int db = this.max[2] - this.min[2] + 1;
            int splitChannel;
            int c0;
            int c2;
            if (dr >= dg) {
                if (dr >= db) {
                    splitChannel = 0;
                    c0 = 1;
                    c2 = 2;
                }
                else {
                    splitChannel = 2;
                    c0 = 0;
                    c2 = 1;
                }
            }
            else if (dg >= db) {
                splitChannel = 1;
                c0 = 0;
                c2 = 2;
            }
            else {
                splitChannel = 2;
                c0 = 1;
                c2 = 0;
            }
            Cube ret = this.splitChannel(splitChannel, c0, c2);
            if (ret != null) {
                return ret;
            }
            ret = this.splitChannel(c0, splitChannel, c2);
            if (ret != null) {
                return ret;
            }
            ret = this.splitChannel(c2, splitChannel, c0);
            if (ret != null) {
                return ret;
            }
            this.done = true;
            return null;
        }
        
        private void normalize(final int splitChannel, final int[] counts) {
            if (this.count == 0) {
                return;
            }
            final int iMin = this.min[splitChannel];
            final int iMax = this.max[splitChannel];
            int loBound = -1;
            int hiBound = -1;
            for (int i = iMin; i <= iMax; ++i) {
                if (counts[i] != 0) {
                    loBound = i;
                    break;
                }
            }
            for (int i = iMax; i >= iMin; --i) {
                if (counts[i] != 0) {
                    hiBound = i;
                    break;
                }
            }
            final boolean flagChangedLo = loBound != -1 && iMin != loBound;
            final boolean flagChangedHi = hiBound != -1 && iMax != hiBound;
            if (flagChangedLo) {
                this.min[splitChannel] = loBound;
            }
            if (flagChangedHi) {
                this.max[splitChannel] = hiBound;
            }
        }
        
        Cube splitChannel(final int splitChannel, final int c0, final int c1) {
            if (this.min[splitChannel] == this.max[splitChannel]) {
                return null;
            }
            if (this.count == 0) {
                return null;
            }
            final int half = this.count / 2;
            final int[] counts = this.computeCounts(splitChannel, c0, c1);
            int tcount = 0;
            int lastAdd = -1;
            int splitLo = this.min[splitChannel];
            int splitHi = this.max[splitChannel];
            for (int i = this.min[splitChannel]; i <= this.max[splitChannel]; ++i) {
                final int c2 = counts[i];
                if (c2 == 0) {
                    if (tcount == 0 && i < this.max[splitChannel]) {
                        this.min[splitChannel] = i + 1;
                    }
                }
                else if (tcount + c2 < half) {
                    lastAdd = i;
                    tcount += c2;
                }
                else if (half - tcount <= tcount + c2 - half) {
                    if (lastAdd != -1) {
                        splitLo = lastAdd;
                        splitHi = i;
                        break;
                    }
                    if (c2 == this.count) {
                        this.max[splitChannel] = i;
                        return null;
                    }
                    splitLo = i;
                    splitHi = i + 1;
                    tcount += c2;
                    break;
                }
                else {
                    if (i != this.max[splitChannel]) {
                        tcount += c2;
                        splitLo = i;
                        splitHi = i + 1;
                        break;
                    }
                    if (c2 == this.count) {
                        return null;
                    }
                    splitLo = lastAdd;
                    splitHi = i;
                    break;
                }
            }
            final Cube ret = new Cube(this.colors, tcount);
            this.count -= tcount;
            ret.min[splitChannel] = this.min[splitChannel];
            ret.max[splitChannel] = splitLo;
            this.min[splitChannel] = splitHi;
            ret.min[c0] = this.min[c0];
            ret.max[c0] = this.max[c0];
            ret.min[c1] = this.min[c1];
            ret.max[c1] = this.max[c1];
            this.normalize(splitChannel, counts);
            ret.normalize(splitChannel, counts);
            return ret;
        }
        
        private int[] computeCounts(final int splitChannel, final int c0, final int c1) {
            final int splitSh4 = (2 - splitChannel) * 4;
            final int c0Sh4 = (2 - c0) * 4;
            final int c1Sh4 = (2 - c1) * 4;
            final int half = this.count / 2;
            final int[] counts = new int[256];
            int tcount = 0;
            final int minR = this.min[0];
            final int minG = this.min[1];
            final int minB = this.min[2];
            final int maxR = this.max[0];
            final int maxG = this.max[1];
            final int maxB = this.max[2];
            final int[] minIdx = { minR >> 4, minG >> 4, minB >> 4 };
            final int[] maxIdx = { maxR >> 4, maxG >> 4, maxB >> 4 };
            int[] vals = { 0, 0, 0 };
            for (int i = minIdx[splitChannel]; i <= maxIdx[splitChannel]; ++i) {
                final int idx1 = i << splitSh4;
                for (int j = minIdx[c0]; j <= maxIdx[c0]; ++j) {
                    final int idx2 = idx1 | j << c0Sh4;
                    for (int k = minIdx[c1]; k <= maxIdx[c1]; ++k) {
                        final int idx3 = idx2 | k << c1Sh4;
                        final Counter[] arr$;
                        final Counter[] v = arr$ = this.colors[idx3];
                        for (final Counter c2 : arr$) {
                            vals = c2.getRgb(vals);
                            if (this.contains(vals)) {
                                final int[] array = counts;
                                final int n = vals[splitChannel];
                                array[n] += c2.count;
                                tcount += c2.count;
                            }
                        }
                    }
                }
            }
            return counts;
        }
        
        @Override
        public String toString() {
            return "Cube: [" + this.min[0] + '-' + this.max[0] + "] [" + this.min[1] + '-' + this.max[1] + "] [" + this.min[2] + '-' + this.max[2] + "] n:" + this.count;
        }
        
        public int averageColor() {
            if (this.count == 0) {
                return 0;
            }
            final byte[] rgb = this.averageColorRGB(null);
            return (rgb[0] << 16 & 0xFF0000) | (rgb[1] << 8 & 0xFF00) | (rgb[2] & 0xFF);
        }
        
        public byte[] averageColorRGB(final byte[] rgb) {
            if (this.count == 0) {
                return Cube.RGB_BLACK;
            }
            float red = 0.0f;
            float grn = 0.0f;
            float blu = 0.0f;
            final int minR = this.min[0];
            final int minG = this.min[1];
            final int minB = this.min[2];
            final int maxR = this.max[0];
            final int maxG = this.max[1];
            final int maxB = this.max[2];
            final int[] minIdx = { minR >> 4, minG >> 4, minB >> 4 };
            final int[] maxIdx = { maxR >> 4, maxG >> 4, maxB >> 4 };
            int[] vals = new int[3];
            for (int i = minIdx[0]; i <= maxIdx[0]; ++i) {
                final int idx1 = i << 8;
                for (int j = minIdx[1]; j <= maxIdx[1]; ++j) {
                    final int idx2 = idx1 | j << 4;
                    for (int k = minIdx[2]; k <= maxIdx[2]; ++k) {
                        final int idx3 = idx2 | k;
                        final Counter[] arr$;
                        final Counter[] v = arr$ = this.colors[idx3];
                        for (final Counter c : arr$) {
                            vals = c.getRgb(vals);
                            if (this.contains(vals)) {
                                final float weight = c.count / (float)this.count;
                                red += vals[0] * weight;
                                grn += vals[1] * weight;
                                blu += vals[2] * weight;
                            }
                        }
                    }
                }
            }
            final byte[] result = (rgb == null) ? new byte[3] : rgb;
            result[0] = (byte)(red + 0.5f);
            result[1] = (byte)(grn + 0.5f);
            result[2] = (byte)(blu + 0.5f);
            return result;
        }
        
        static {
            RGB_BLACK = new byte[] { 0, 0, 0 };
        }
    }
}
