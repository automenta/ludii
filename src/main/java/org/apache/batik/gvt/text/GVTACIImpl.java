// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.text.AttributedString;
import java.text.StringCharacterIterator;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Set;

public class GVTACIImpl implements GVTAttributedCharacterIterator
{
    private String simpleString;
    private Set allAttributes;
    private ArrayList mapList;
    private static int START_RUN;
    private static int END_RUN;
    private static int MID_RUN;
    private static int SINGLETON;
    private int[] charInRun;
    private CharacterIterator iter;
    private int currentIndex;
    
    public GVTACIImpl() {
        this.iter = null;
        this.currentIndex = -1;
        this.simpleString = "";
        this.buildAttributeTables();
    }
    
    public GVTACIImpl(final AttributedCharacterIterator aci) {
        this.iter = null;
        this.currentIndex = -1;
        this.buildAttributeTables(aci);
    }
    
    @Override
    public void setString(final String s) {
        this.simpleString = s;
        this.iter = new StringCharacterIterator(this.simpleString);
        this.buildAttributeTables();
    }
    
    @Override
    public void setString(final AttributedString s) {
        this.iter = s.getIterator();
        this.buildAttributeTables((AttributedCharacterIterator)this.iter);
    }
    
    @Override
    public void setAttributeArray(final TextAttribute attr, final Object[] attValues, int beginIndex, int endIndex) {
        beginIndex = Math.max(beginIndex, 0);
        endIndex = Math.min(endIndex, this.simpleString.length());
        if (this.charInRun[beginIndex] == GVTACIImpl.END_RUN) {
            if (this.charInRun[beginIndex - 1] == GVTACIImpl.MID_RUN) {
                this.charInRun[beginIndex - 1] = GVTACIImpl.END_RUN;
            }
            else {
                this.charInRun[beginIndex - 1] = GVTACIImpl.SINGLETON;
            }
        }
        if (this.charInRun[endIndex + 1] == GVTACIImpl.END_RUN) {
            this.charInRun[endIndex + 1] = GVTACIImpl.SINGLETON;
        }
        else if (this.charInRun[endIndex + 1] == GVTACIImpl.MID_RUN) {
            this.charInRun[endIndex + 1] = GVTACIImpl.START_RUN;
        }
        for (int i = beginIndex; i <= endIndex; ++i) {
            this.charInRun[i] = GVTACIImpl.SINGLETON;
            final int n = Math.min(i, attValues.length - 1);
            this.mapList.get(i).put(attr, attValues[n]);
        }
    }
    
    @Override
    public Set getAllAttributeKeys() {
        return this.allAttributes;
    }
    
    @Override
    public Object getAttribute(final AttributedCharacterIterator.Attribute attribute) {
        return this.getAttributes().get(attribute);
    }
    
    @Override
    public Map getAttributes() {
        return this.mapList.get(this.currentIndex);
    }
    
    @Override
    public int getRunLimit() {
        int ndx = this.currentIndex;
        do {
            ++ndx;
        } while (this.charInRun[ndx] == GVTACIImpl.MID_RUN);
        return ndx;
    }
    
    @Override
    public int getRunLimit(final AttributedCharacterIterator.Attribute attribute) {
        int ndx = this.currentIndex;
        final Object value = this.getAttributes().get(attribute);
        if (value == null) {
            do {
                ++ndx;
            } while (this.mapList.get(ndx).get(attribute) == null);
        }
        else {
            do {
                ++ndx;
            } while (value.equals(this.mapList.get(ndx).get(attribute)));
        }
        return ndx;
    }
    
    @Override
    public int getRunLimit(final Set attributes) {
        int ndx = this.currentIndex;
        do {
            ++ndx;
        } while (attributes.equals(this.mapList.get(ndx)));
        return ndx;
    }
    
    @Override
    public int getRunStart() {
        int ndx;
        for (ndx = this.currentIndex; this.charInRun[ndx] == GVTACIImpl.MID_RUN; --ndx) {}
        return ndx;
    }
    
    @Override
    public int getRunStart(final AttributedCharacterIterator.Attribute attribute) {
        int ndx = this.currentIndex - 1;
        final Object value = this.getAttributes().get(attribute);
        try {
            if (value == null) {
                while (this.mapList.get(ndx - 1).get(attribute) == null) {
                    --ndx;
                }
            }
            else {
                while (value.equals(this.mapList.get(ndx - 1).get(attribute))) {
                    --ndx;
                }
            }
        }
        catch (IndexOutOfBoundsException ex) {}
        return ndx;
    }
    
    @Override
    public int getRunStart(final Set attributes) {
        int ndx = this.currentIndex;
        try {
            while (attributes.equals(this.mapList.get(ndx - 1))) {
                --ndx;
            }
        }
        catch (IndexOutOfBoundsException ex) {}
        return ndx;
    }
    
    @Override
    public Object clone() {
        final GVTAttributedCharacterIterator cloneACI = new GVTACIImpl(this);
        return cloneACI;
    }
    
    @Override
    public char current() {
        return this.iter.current();
    }
    
    @Override
    public char first() {
        return this.iter.first();
    }
    
    @Override
    public int getBeginIndex() {
        return this.iter.getBeginIndex();
    }
    
    @Override
    public int getEndIndex() {
        return this.iter.getEndIndex();
    }
    
    @Override
    public int getIndex() {
        return this.iter.getIndex();
    }
    
    @Override
    public char last() {
        return this.iter.last();
    }
    
    @Override
    public char next() {
        return this.iter.next();
    }
    
    @Override
    public char previous() {
        return this.iter.previous();
    }
    
    @Override
    public char setIndex(final int position) {
        return this.iter.setIndex(position);
    }
    
    private void buildAttributeTables() {
        this.allAttributes = new HashSet();
        this.mapList = new ArrayList(this.simpleString.length());
        this.charInRun = new int[this.simpleString.length()];
        for (int i = 0; i < this.charInRun.length; ++i) {
            this.charInRun[i] = GVTACIImpl.SINGLETON;
            this.mapList.set(i, new HashMap());
        }
    }
    
    private void buildAttributeTables(final AttributedCharacterIterator aci) {
        this.allAttributes = aci.getAllAttributeKeys();
        final int length = aci.getEndIndex() - aci.getBeginIndex();
        this.mapList = new ArrayList(length);
        this.charInRun = new int[length];
        char c = aci.first();
        final char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = c;
            this.charInRun[i] = GVTACIImpl.SINGLETON;
            this.mapList.set(i, new HashMap(aci.getAttributes()));
            c = aci.next();
        }
        this.simpleString = new String(chars);
    }
    
    static {
        GVTACIImpl.START_RUN = 2;
        GVTACIImpl.END_RUN = 3;
        GVTACIImpl.MID_RUN = 1;
        GVTACIImpl.SINGLETON = 0;
    }
    
    public static class TransformAttributeFilter implements AttributeFilter
    {
        @Override
        public AttributedCharacterIterator mutateAttributes(final AttributedCharacterIterator aci) {
            return aci;
        }
    }
}
