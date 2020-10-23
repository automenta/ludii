// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg;

import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.AbstractValueFactory;

public class MarkerShorthandManager extends AbstractValueFactory implements ShorthandManager
{
    @Override
    public String getPropertyName() {
        return "marker";
    }
    
    @Override
    public boolean isAnimatableProperty() {
        return true;
    }
    
    @Override
    public boolean isAdditiveProperty() {
        return false;
    }
    
    @Override
    public void setValues(final CSSEngine eng, final PropertyHandler ph, final LexicalUnit lu, final boolean imp) throws DOMException {
        ph.property("marker-end", lu, imp);
        ph.property("marker-mid", lu, imp);
        ph.property("marker-start", lu, imp);
    }
}
