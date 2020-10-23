// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.svggen.font.table;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.util.List;

public class GlyfCompositeDescript extends GlyfDescript
{
    private List components;
    protected boolean beingResolved;
    protected boolean resolved;
    
    public GlyfCompositeDescript(final GlyfTable parentTable, final ByteArrayInputStream bais) {
        super(parentTable, (short)(-1), bais);
        this.components = new ArrayList();
        this.beingResolved = false;
        this.resolved = false;
        GlyfCompositeComp comp;
        do {
            comp = new GlyfCompositeComp(bais);
            this.components.add(comp);
        } while ((comp.getFlags() & 0x20) != 0x0);
        if ((comp.getFlags() & 0x100) != 0x0) {
            this.readInstructions(bais, bais.read() << 8 | bais.read());
        }
    }
    
    @Override
    public void resolve() {
        if (this.resolved) {
            return;
        }
        if (this.beingResolved) {
            System.err.println("Circular reference in GlyfCompositeDesc");
            return;
        }
        this.beingResolved = true;
        int firstIndex = 0;
        int firstContour = 0;
        for (final Object component : this.components) {
            final GlyfCompositeComp comp = (GlyfCompositeComp)component;
            comp.setFirstIndex(firstIndex);
            comp.setFirstContour(firstContour);
            final GlyfDescript desc = this.parentTable.getDescription(comp.getGlyphIndex());
            if (desc != null) {
                desc.resolve();
                firstIndex += desc.getPointCount();
                firstContour += desc.getContourCount();
            }
        }
        this.resolved = true;
        this.beingResolved = false;
    }
    
    @Override
    public int getEndPtOfContours(final int i) {
        final GlyfCompositeComp c = this.getCompositeCompEndPt(i);
        if (c != null) {
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            return gd.getEndPtOfContours(i - c.getFirstContour()) + c.getFirstIndex();
        }
        return 0;
    }
    
    @Override
    public byte getFlags(final int i) {
        final GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            return gd.getFlags(i - c.getFirstIndex());
        }
        return 0;
    }
    
    @Override
    public short getXCoordinate(final int i) {
        final GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            final int n = i - c.getFirstIndex();
            final int x = gd.getXCoordinate(n);
            final int y = gd.getYCoordinate(n);
            short x2 = (short)c.scaleX(x, y);
            x2 += (short)c.getXTranslate();
            return x2;
        }
        return 0;
    }
    
    @Override
    public short getYCoordinate(final int i) {
        final GlyfCompositeComp c = this.getCompositeComp(i);
        if (c != null) {
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            final int n = i - c.getFirstIndex();
            final int x = gd.getXCoordinate(n);
            final int y = gd.getYCoordinate(n);
            short y2 = (short)c.scaleY(x, y);
            y2 += (short)c.getYTranslate();
            return y2;
        }
        return 0;
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    @Override
    public int getPointCount() {
        if (!this.resolved) {
            System.err.println("getPointCount called on unresolved GlyfCompositeDescript");
        }
        final GlyfCompositeComp c = this.components.get(this.components.size() - 1);
        return c.getFirstIndex() + this.parentTable.getDescription(c.getGlyphIndex()).getPointCount();
    }
    
    @Override
    public int getContourCount() {
        if (!this.resolved) {
            System.err.println("getContourCount called on unresolved GlyfCompositeDescript");
        }
        final GlyfCompositeComp c = this.components.get(this.components.size() - 1);
        return c.getFirstContour() + this.parentTable.getDescription(c.getGlyphIndex()).getContourCount();
    }
    
    public int getComponentIndex(final int i) {
        return this.components.get(i).getFirstIndex();
    }
    
    public int getComponentCount() {
        return this.components.size();
    }
    
    protected GlyfCompositeComp getCompositeComp(final int i) {
        for (final Object component : this.components) {
            final GlyfCompositeComp c = (GlyfCompositeComp)component;
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstIndex() <= i && i < c.getFirstIndex() + gd.getPointCount()) {
                return c;
            }
        }
        return null;
    }
    
    protected GlyfCompositeComp getCompositeCompEndPt(final int i) {
        for (final Object component : this.components) {
            final GlyfCompositeComp c = (GlyfCompositeComp)component;
            final GlyphDescription gd = this.parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstContour() <= i && i < c.getFirstContour() + gd.getContourCount()) {
                return c;
            }
        }
        return null;
    }
}
