// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.apache.batik.css.engine.value.Value;

public class StyleMap
{
    public static final short IMPORTANT_MASK = 1;
    public static final short COMPUTED_MASK = 2;
    public static final short NULL_CASCADED_MASK = 4;
    public static final short INHERITED_MASK = 8;
    public static final short LINE_HEIGHT_RELATIVE_MASK = 16;
    public static final short FONT_SIZE_RELATIVE_MASK = 32;
    public static final short COLOR_RELATIVE_MASK = 64;
    public static final short PARENT_RELATIVE_MASK = 128;
    public static final short BLOCK_WIDTH_RELATIVE_MASK = 256;
    public static final short BLOCK_HEIGHT_RELATIVE_MASK = 512;
    public static final short BOX_RELATIVE_MASK = 1024;
    public static final short ORIGIN_MASK = -8192;
    public static final short USER_AGENT_ORIGIN = 0;
    public static final short USER_ORIGIN = 8192;
    public static final short NON_CSS_ORIGIN = 16384;
    public static final short AUTHOR_ORIGIN = 24576;
    public static final short INLINE_AUTHOR_ORIGIN = Short.MIN_VALUE;
    public static final short OVERRIDE_ORIGIN = -24576;
    protected Value[] values;
    protected short[] masks;
    protected boolean fixedCascadedValues;
    
    public StyleMap(final int size) {
        this.values = new Value[size];
        this.masks = new short[size];
    }
    
    public boolean hasFixedCascadedValues() {
        return this.fixedCascadedValues;
    }
    
    public void setFixedCascadedStyle(final boolean b) {
        this.fixedCascadedValues = b;
    }
    
    public Value getValue(final int i) {
        return this.values[i];
    }
    
    public short getMask(final int i) {
        return this.masks[i];
    }
    
    public boolean isImportant(final int i) {
        return (this.masks[i] & 0x1) != 0x0;
    }
    
    public boolean isComputed(final int i) {
        return (this.masks[i] & 0x2) != 0x0;
    }
    
    public boolean isNullCascaded(final int i) {
        return (this.masks[i] & 0x4) != 0x0;
    }
    
    public boolean isInherited(final int i) {
        return (this.masks[i] & 0x8) != 0x0;
    }
    
    public short getOrigin(final int i) {
        return (short)(this.masks[i] & 0xFFFFE000);
    }
    
    public boolean isColorRelative(final int i) {
        return (this.masks[i] & 0x40) != 0x0;
    }
    
    public boolean isParentRelative(final int i) {
        return (this.masks[i] & 0x80) != 0x0;
    }
    
    public boolean isLineHeightRelative(final int i) {
        return (this.masks[i] & 0x10) != 0x0;
    }
    
    public boolean isFontSizeRelative(final int i) {
        return (this.masks[i] & 0x20) != 0x0;
    }
    
    public boolean isBlockWidthRelative(final int i) {
        return (this.masks[i] & 0x100) != 0x0;
    }
    
    public boolean isBlockHeightRelative(final int i) {
        return (this.masks[i] & 0x200) != 0x0;
    }
    
    public void putValue(final int i, final Value v) {
        this.values[i] = v;
    }
    
    public void putMask(final int i, final short m) {
        this.masks[i] = m;
    }
    
    public void putImportant(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x1;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFFE;
        }
    }
    
    public void putOrigin(final int i, final short val) {
        final short[] masks = this.masks;
        masks[i] &= 0x1FFF;
        final short[] masks2 = this.masks;
        masks2[i] |= (short)(val & 0xFFFFE000);
    }
    
    public void putComputed(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x2;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFFD;
        }
    }
    
    public void putNullCascaded(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x4;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFFB;
        }
    }
    
    public void putInherited(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x8;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFF7;
        }
    }
    
    public void putColorRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x40;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFBF;
        }
    }
    
    public void putParentRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x80;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFF7F;
        }
    }
    
    public void putLineHeightRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x10;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFEF;
        }
    }
    
    public void putFontSizeRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x20;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFFDF;
        }
    }
    
    public void putBlockWidthRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x100;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFEFF;
        }
    }
    
    public void putBlockHeightRelative(final int i, final boolean b) {
        if (b) {
            final short[] masks = this.masks;
            masks[i] |= 0x200;
        }
        else {
            final short[] masks2 = this.masks;
            masks2[i] &= 0xFFFFFDFF;
        }
    }
    
    public String toString(final CSSEngine eng) {
        final int nSlots = this.values.length;
        final StringBuffer sb = new StringBuffer(nSlots * 8);
        for (int i = 0; i < nSlots; ++i) {
            final Value v = this.values[i];
            if (v != null) {
                sb.append(eng.getPropertyName(i));
                sb.append(": ");
                sb.append(v);
                if (this.isImportant(i)) {
                    sb.append(" !important");
                }
                sb.append(";\n");
            }
        }
        return sb.toString();
    }
}
