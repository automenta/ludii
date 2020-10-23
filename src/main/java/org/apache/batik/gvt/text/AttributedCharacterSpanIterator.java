// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.util.Map;
import java.util.Set;
import java.text.AttributedCharacterIterator;

public class AttributedCharacterSpanIterator implements AttributedCharacterIterator
{
    private AttributedCharacterIterator aci;
    private int begin;
    private int end;
    
    public AttributedCharacterSpanIterator(final AttributedCharacterIterator aci, final int start, final int stop) {
        this.aci = aci;
        this.end = Math.min(aci.getEndIndex(), stop);
        this.begin = Math.max(aci.getBeginIndex(), start);
        this.aci.setIndex(this.begin);
    }
    
    @Override
    public Set getAllAttributeKeys() {
        return this.aci.getAllAttributeKeys();
    }
    
    @Override
    public Object getAttribute(final Attribute attribute) {
        return this.aci.getAttribute(attribute);
    }
    
    @Override
    public Map getAttributes() {
        return this.aci.getAttributes();
    }
    
    @Override
    public int getRunLimit() {
        return Math.min(this.aci.getRunLimit(), this.end);
    }
    
    @Override
    public int getRunLimit(final Attribute attribute) {
        return Math.min(this.aci.getRunLimit(attribute), this.end);
    }
    
    @Override
    public int getRunLimit(final Set attributes) {
        return Math.min(this.aci.getRunLimit(attributes), this.end);
    }
    
    @Override
    public int getRunStart() {
        return Math.max(this.aci.getRunStart(), this.begin);
    }
    
    @Override
    public int getRunStart(final Attribute attribute) {
        return Math.max(this.aci.getRunStart(attribute), this.begin);
    }
    
    @Override
    public int getRunStart(final Set attributes) {
        return Math.max(this.aci.getRunStart(attributes), this.begin);
    }
    
    @Override
    public Object clone() {
        return new AttributedCharacterSpanIterator((AttributedCharacterIterator)this.aci.clone(), this.begin, this.end);
    }
    
    @Override
    public char current() {
        return this.aci.current();
    }
    
    @Override
    public char first() {
        return this.aci.setIndex(this.begin);
    }
    
    @Override
    public int getBeginIndex() {
        return this.begin;
    }
    
    @Override
    public int getEndIndex() {
        return this.end;
    }
    
    @Override
    public int getIndex() {
        return this.aci.getIndex();
    }
    
    @Override
    public char last() {
        return this.setIndex(this.end - 1);
    }
    
    @Override
    public char next() {
        if (this.getIndex() < this.end - 1) {
            return this.aci.next();
        }
        return this.setIndex(this.end);
    }
    
    @Override
    public char previous() {
        if (this.getIndex() > this.begin) {
            return this.aci.previous();
        }
        return '\uffff';
    }
    
    @Override
    public char setIndex(final int position) {
        int ndx = Math.max(position, this.begin);
        ndx = Math.min(ndx, this.end);
        char c = this.aci.setIndex(ndx);
        if (ndx == this.end) {
            c = '\uffff';
        }
        return c;
    }
}
