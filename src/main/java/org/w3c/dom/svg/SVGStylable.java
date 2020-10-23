// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.dom.svg;

import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;

public interface SVGStylable
{
    SVGAnimatedString getClassName();
    
    CSSStyleDeclaration getStyle();
    
    CSSValue getPresentationAttribute(final String p0);
}
