// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;

public class StringValue extends AbstractValue
{
    protected String value;
    protected short unitType;
    
    public static String getCssText(final short type, final String value) {
        switch (type) {
            case 20: {
                return "url(" + value + ')';
            }
            case 19: {
                final char q = (value.indexOf(34) != -1) ? '\'' : '\"';
                return q + value + q;
            }
            default: {
                return value;
            }
        }
    }
    
    public StringValue(final short type, final String s) {
        this.unitType = type;
        this.value = s;
    }
    
    @Override
    public short getPrimitiveType() {
        return this.unitType;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof StringValue)) {
            return false;
        }
        final StringValue v = (StringValue)obj;
        return this.unitType == v.unitType && this.value.equals(v.value);
    }
    
    @Override
    public String getCssText() {
        return getCssText(this.unitType, this.value);
    }
    
    @Override
    public String getStringValue() throws DOMException {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.getCssText();
    }
}
