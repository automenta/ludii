// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.apache.batik.parser.DefaultPreserveAspectRatioHandler;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.w3c.dom.DOMException;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGPreserveAspectRatio;

public abstract class AbstractSVGPreserveAspectRatio implements SVGPreserveAspectRatio, SVGConstants
{
    protected static final String[] ALIGN_VALUES;
    protected static final String[] MEET_OR_SLICE_VALUES;
    protected short align;
    protected short meetOrSlice;
    
    public static String getValueAsString(final short align, final short meetOrSlice) {
        if (align < 1 || align > 10) {
            return null;
        }
        final String value = AbstractSVGPreserveAspectRatio.ALIGN_VALUES[align];
        if (align == 1) {
            return value;
        }
        if (meetOrSlice < 1 || meetOrSlice > 2) {
            return null;
        }
        return value + ' ' + AbstractSVGPreserveAspectRatio.MEET_OR_SLICE_VALUES[meetOrSlice];
    }
    
    public AbstractSVGPreserveAspectRatio() {
        this.align = 6;
        this.meetOrSlice = 1;
    }
    
    @Override
    public short getAlign() {
        return this.align;
    }
    
    @Override
    public short getMeetOrSlice() {
        return this.meetOrSlice;
    }
    
    @Override
    public void setAlign(final short align) {
        this.align = align;
        this.setAttributeValue(this.getValueAsString());
    }
    
    @Override
    public void setMeetOrSlice(final short meetOrSlice) {
        this.meetOrSlice = meetOrSlice;
        this.setAttributeValue(this.getValueAsString());
    }
    
    public void reset() {
        this.align = 6;
        this.meetOrSlice = 1;
    }
    
    protected abstract void setAttributeValue(final String p0) throws DOMException;
    
    protected abstract DOMException createDOMException(final short p0, final String p1, final Object[] p2);
    
    protected void setValueAsString(final String value) throws DOMException {
        final PreserveAspectRatioParserHandler ph = new PreserveAspectRatioParserHandler();
        try {
            final PreserveAspectRatioParser p = new PreserveAspectRatioParser();
            p.setPreserveAspectRatioHandler(ph);
            p.parse(value);
            this.align = ph.getAlign();
            this.meetOrSlice = ph.getMeetOrSlice();
        }
        catch (ParseException ex) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio", new Object[] { value });
        }
    }
    
    public String getValueAsString() {
        if (this.align < 1 || this.align > 10) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio.align", new Object[] { this.align });
        }
        final String value = AbstractSVGPreserveAspectRatio.ALIGN_VALUES[this.align];
        if (this.align == 1) {
            return value;
        }
        if (this.meetOrSlice < 1 || this.meetOrSlice > 2) {
            throw this.createDOMException((short)13, "preserve.aspect.ratio.meet.or.slice", new Object[] { this.meetOrSlice });
        }
        return value + ' ' + AbstractSVGPreserveAspectRatio.MEET_OR_SLICE_VALUES[this.meetOrSlice];
    }
    
    static {
        ALIGN_VALUES = new String[] { null, "none", "xMinYMin", "xMidYMin", "xMaxYMin", "xMinYMid", "xMidYMid", "xMaxYMid", "xMinYMax", "xMidYMax", "xMaxYMax" };
        MEET_OR_SLICE_VALUES = new String[] { null, "meet", "slice" };
    }
    
    protected static class PreserveAspectRatioParserHandler extends DefaultPreserveAspectRatioHandler
    {
        public short align;
        public short meetOrSlice;
        
        protected PreserveAspectRatioParserHandler() {
            this.align = 6;
            this.meetOrSlice = 1;
        }
        
        public short getAlign() {
            return this.align;
        }
        
        public short getMeetOrSlice() {
            return this.meetOrSlice;
        }
        
        @Override
        public void none() throws ParseException {
            this.align = 1;
        }
        
        @Override
        public void xMaxYMax() throws ParseException {
            this.align = 10;
        }
        
        @Override
        public void xMaxYMid() throws ParseException {
            this.align = 7;
        }
        
        @Override
        public void xMaxYMin() throws ParseException {
            this.align = 4;
        }
        
        @Override
        public void xMidYMax() throws ParseException {
            this.align = 9;
        }
        
        @Override
        public void xMidYMid() throws ParseException {
            this.align = 6;
        }
        
        @Override
        public void xMidYMin() throws ParseException {
            this.align = 3;
        }
        
        @Override
        public void xMinYMax() throws ParseException {
            this.align = 8;
        }
        
        @Override
        public void xMinYMid() throws ParseException {
            this.align = 5;
        }
        
        @Override
        public void xMinYMin() throws ParseException {
            this.align = 2;
        }
        
        @Override
        public void meet() throws ParseException {
            this.meetOrSlice = 1;
        }
        
        @Override
        public void slice() throws ParseException {
            this.meetOrSlice = 2;
        }
    }
}
