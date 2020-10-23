// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;

public interface ValueManager
{
    String getPropertyName();
    
    boolean isInheritedProperty();
    
    boolean isAnimatableProperty();
    
    boolean isAdditiveProperty();
    
    int getPropertyType();
    
    Value getDefaultValue();
    
    Value createValue(final LexicalUnit p0, final CSSEngine p1) throws DOMException;
    
    Value createFloatValue(final short p0, final float p1) throws DOMException;
    
    Value createStringValue(final short p0, final String p1, final CSSEngine p2) throws DOMException;
    
    Value computeValue(final CSSStylableElement p0, final String p1, final CSSEngine p2, final int p3, final StyleMap p4, final Value p5);
}
