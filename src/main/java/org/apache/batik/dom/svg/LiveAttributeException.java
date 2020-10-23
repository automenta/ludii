// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.Element;

public class LiveAttributeException extends RuntimeException
{
    public static final short ERR_ATTRIBUTE_MISSING = 0;
    public static final short ERR_ATTRIBUTE_MALFORMED = 1;
    public static final short ERR_ATTRIBUTE_NEGATIVE = 2;
    protected Element e;
    protected String attributeName;
    protected short code;
    protected String value;
    
    public LiveAttributeException(final Element e, final String an, final short code, final String val) {
        this.e = e;
        this.attributeName = an;
        this.code = code;
        this.value = val;
    }
    
    public Element getElement() {
        return this.e;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public short getCode() {
        return this.code;
    }
    
    public String getValue() {
        return this.value;
    }
}
