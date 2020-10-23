// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen;

import java.awt.image.LookupTable;
import java.util.Arrays;
import java.awt.image.ByteLookupTable;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.awt.image.LookupOp;
import java.awt.Rectangle;
import java.awt.image.BufferedImageOp;

public class SVGLookupOp extends AbstractSVGFilterConverter
{
    private static final double GAMMA = 0.4166666666666667;
    private static final int[] linearToSRGBLut;
    private static final int[] sRGBToLinear;
    
    public SVGLookupOp(final SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }
    
    @Override
    public SVGFilterDescriptor toSVG(final BufferedImageOp filter, final Rectangle filterRect) {
        if (filter instanceof LookupOp) {
            return this.toSVG((LookupOp)filter);
        }
        return null;
    }
    
    public SVGFilterDescriptor toSVG(final LookupOp lookupOp) {
        SVGFilterDescriptor filterDesc = this.descMap.get(lookupOp);
        final Document domFactory = this.generatorContext.domFactory;
        if (filterDesc == null) {
            final Element filterDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "filter");
            final Element feComponentTransferDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "feComponentTransfer");
            final String[] lookupTables = this.convertLookupTables(lookupOp);
            final Element feFuncR = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncR");
            final Element feFuncG = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncG");
            final Element feFuncB = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncB");
            Element feFuncA = null;
            final String type = "table";
            if (lookupTables.length == 1) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncG.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncB.setAttributeNS(null, "tableValues", lookupTables[0]);
            }
            else if (lookupTables.length >= 3) {
                feFuncR.setAttributeNS(null, "type", type);
                feFuncG.setAttributeNS(null, "type", type);
                feFuncB.setAttributeNS(null, "type", type);
                feFuncR.setAttributeNS(null, "tableValues", lookupTables[0]);
                feFuncG.setAttributeNS(null, "tableValues", lookupTables[1]);
                feFuncB.setAttributeNS(null, "tableValues", lookupTables[2]);
                if (lookupTables.length == 4) {
                    feFuncA = domFactory.createElementNS("http://www.w3.org/2000/svg", "feFuncA");
                    feFuncA.setAttributeNS(null, "type", type);
                    feFuncA.setAttributeNS(null, "tableValues", lookupTables[3]);
                }
            }
            feComponentTransferDef.appendChild(feFuncR);
            feComponentTransferDef.appendChild(feFuncG);
            feComponentTransferDef.appendChild(feFuncB);
            if (feFuncA != null) {
                feComponentTransferDef.appendChild(feFuncA);
            }
            filterDef.appendChild(feComponentTransferDef);
            filterDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("componentTransfer"));
            final String filterAttrBuf = "url(#" + filterDef.getAttributeNS(null, "id") + ")";
            filterDesc = new SVGFilterDescriptor(filterAttrBuf, filterDef);
            this.defSet.add(filterDef);
            this.descMap.put(lookupOp, filterDesc);
        }
        return filterDesc;
    }
    
    private String[] convertLookupTables(final LookupOp lookupOp) {
        final LookupTable lookupTable = lookupOp.getTable();
        final int nComponents = lookupTable.getNumComponents();
        if (nComponents != 1 && nComponents != 3 && nComponents != 4) {
            throw new SVGGraphics2DRuntimeException("BufferedImage LookupOp should have 1, 3 or 4 lookup arrays");
        }
        final StringBuffer[] lookupTableBuf = new StringBuffer[nComponents];
        for (int i = 0; i < nComponents; ++i) {
            lookupTableBuf[i] = new StringBuffer();
        }
        if (!(lookupTable instanceof ByteLookupTable)) {
            final int[] src = new int[nComponents];
            final int[] dest = new int[nComponents];
            final int offset = lookupTable.getOffset();
            for (int j = 0; j < offset; ++j) {
                for (int k = 0; k < nComponents; ++k) {
                    lookupTableBuf[k].append(this.doubleString(j / 255.0)).append(" ");
                }
            }
            for (int j = offset; j <= 255; ++j) {
                Arrays.fill(src, j);
                lookupTable.lookupPixel(src, dest);
                for (int k = 0; k < nComponents; ++k) {
                    lookupTableBuf[k].append(this.doubleString(dest[k] / 255.0)).append(" ");
                }
            }
        }
        else {
            final byte[] src2 = new byte[nComponents];
            final byte[] dest2 = new byte[nComponents];
            for (int offset = lookupTable.getOffset(), j = 0; j < offset; ++j) {
                for (int k = 0; k < nComponents; ++k) {
                    lookupTableBuf[k].append(this.doubleString(j / 255.0)).append(" ");
                }
            }
            for (int j = 0; j <= 255; ++j) {
                Arrays.fill(src2, (byte)(0xFF & j));
                ((ByteLookupTable)lookupTable).lookupPixel(src2, dest2);
                for (int k = 0; k < nComponents; ++k) {
                    lookupTableBuf[k].append(this.doubleString((0xFF & dest2[k]) / 255.0)).append(" ");
                }
            }
        }
        final String[] lookupTables = new String[nComponents];
        for (int l = 0; l < nComponents; ++l) {
            lookupTables[l] = lookupTableBuf[l].toString().trim();
        }
        return lookupTables;
    }
    
    static {
        linearToSRGBLut = new int[256];
        sRGBToLinear = new int[256];
        for (int i = 0; i < 256; ++i) {
            float value = i / 255.0f;
            if (value <= 0.0031308) {
                value *= 12.92f;
            }
            else {
                value = 1.055f * (float)Math.pow(value, 0.4166666666666667) - 0.055f;
            }
            SVGLookupOp.linearToSRGBLut[i] = Math.round(value * 255.0f);
            value = i / 255.0f;
            if (value <= 0.04045) {
                value /= 12.92f;
            }
            else {
                value = (float)Math.pow((value + 0.055f) / 1.055f, 2.4);
            }
            SVGLookupOp.sRGBToLinear[i] = Math.round(value * 255.0f);
        }
    }
}
