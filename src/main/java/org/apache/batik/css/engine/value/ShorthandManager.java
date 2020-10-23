// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.CSSEngine;

public interface ShorthandManager
{
    String getPropertyName();
    
    boolean isAnimatableProperty();
    
    boolean isAdditiveProperty();
    
    void setValues(final CSSEngine p0, final PropertyHandler p1, final LexicalUnit p2, final boolean p3) throws DOMException;
    
    public interface PropertyHandler
    {
        void property(final String p0, final LexicalUnit p1, final boolean p2);
    }
}
