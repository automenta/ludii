// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value.svg12;

import org.w3c.dom.DOMException;
import org.w3c.css.sac.LexicalUnit;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.AbstractValueFactory;

public class MarginShorthandManager extends AbstractValueFactory implements ShorthandManager
{
    @Override
    public String getPropertyName() {
        return "margin";
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
    public void setValues(final CSSEngine eng, final PropertyHandler ph, LexicalUnit lu, final boolean imp) throws DOMException {
        if (lu.getLexicalUnitType() == 12) {
            return;
        }
        final LexicalUnit[] lus = new LexicalUnit[4];
        int cnt = 0;
        while (lu != null) {
            if (cnt == 4) {
                throw this.createInvalidLexicalUnitDOMException(lu.getLexicalUnitType());
            }
            lus[cnt++] = lu;
            lu = lu.getNextLexicalUnit();
        }
        switch (cnt) {
            case 1: {
                final LexicalUnit[] array = lus;
                final int n = 3;
                final LexicalUnit[] array2 = lus;
                final int n2 = 2;
                final LexicalUnit[] array3 = lus;
                final int n3 = 1;
                final LexicalUnit lexicalUnit = lus[0];
                array3[n3] = lexicalUnit;
                array[n] = (array2[n2] = lexicalUnit);
                break;
            }
            case 2: {
                lus[2] = lus[0];
                lus[3] = lus[1];
                break;
            }
            case 3: {
                lus[3] = lus[1];
                break;
            }
        }
        ph.property("margin-top", lus[0], imp);
        ph.property("margin-right", lus[1], imp);
        ph.property("margin-bottom", lus[2], imp);
        ph.property("margin-left", lus[3], imp);
    }
}
