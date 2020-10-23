// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.apache.batik.util.ParsedURL;

public abstract class AbstractValueFactory
{
    public abstract String getPropertyName();
    
    protected static String resolveURI(final ParsedURL base, final String value) {
        return new ParsedURL(base, value).toString();
    }
    
    protected DOMException createInvalidIdentifierDOMException(final String ident) {
        final Object[] p = { this.getPropertyName(), ident };
        final String s = Messages.formatMessage("invalid.identifier", p);
        return new DOMException((short)12, s);
    }
    
    protected DOMException createInvalidLexicalUnitDOMException(final short type) {
        final Object[] p = { this.getPropertyName(), type };
        final String s = Messages.formatMessage("invalid.lexical.unit", p);
        return new DOMException((short)9, s);
    }
    
    protected DOMException createInvalidFloatTypeDOMException(final short t) {
        final Object[] p = { this.getPropertyName(), t };
        final String s = Messages.formatMessage("invalid.float.type", p);
        return new DOMException((short)15, s);
    }
    
    protected DOMException createInvalidFloatValueDOMException(final float f) {
        final Object[] p = { this.getPropertyName(), f };
        final String s = Messages.formatMessage("invalid.float.value", p);
        return new DOMException((short)15, s);
    }
    
    protected DOMException createInvalidStringTypeDOMException(final short t) {
        final Object[] p = { this.getPropertyName(), t };
        final String s = Messages.formatMessage("invalid.string.type", p);
        return new DOMException((short)15, s);
    }
    
    protected DOMException createMalformedLexicalUnitDOMException() {
        final Object[] p = { this.getPropertyName() };
        final String s = Messages.formatMessage("malformed.lexical.unit", p);
        return new DOMException((short)15, s);
    }
    
    protected DOMException createDOMException() {
        final Object[] p = { this.getPropertyName() };
        final String s = Messages.formatMessage("invalid.access", p);
        return new DOMException((short)9, s);
    }
}
