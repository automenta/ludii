// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.value;

import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.CSSStylableElement;
import org.w3c.dom.DOMException;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.css.sac.LexicalUnit;

public abstract class RectManager extends LengthManager
{
    protected int orientation;
    
    @Override
    public Value createValue(LexicalUnit lu, final CSSEngine engine) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 41: {
                if (!lu.getFunctionName().equalsIgnoreCase("rect")) {
                    break;
                }
            }
            case 38: {
                lu = lu.getParameters();
                final Value top = this.createRectComponent(lu);
                lu = lu.getNextLexicalUnit();
                if (lu == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                final Value right = this.createRectComponent(lu);
                lu = lu.getNextLexicalUnit();
                if (lu == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                final Value bottom = this.createRectComponent(lu);
                lu = lu.getNextLexicalUnit();
                if (lu == null || lu.getLexicalUnitType() != 0) {
                    throw this.createMalformedRectDOMException();
                }
                lu = lu.getNextLexicalUnit();
                final Value left = this.createRectComponent(lu);
                return new RectValue(top, right, bottom, left);
            }
        }
        throw this.createMalformedRectDOMException();
    }
    
    private Value createRectComponent(final LexicalUnit lu) throws DOMException {
        switch (lu.getLexicalUnitType()) {
            case 35: {
                if (lu.getStringValue().equalsIgnoreCase("auto")) {
                    return ValueConstants.AUTO_VALUE;
                }
                break;
            }
            case 15: {
                return new FloatValue((short)3, lu.getFloatValue());
            }
            case 16: {
                return new FloatValue((short)4, lu.getFloatValue());
            }
            case 17: {
                return new FloatValue((short)5, lu.getFloatValue());
            }
            case 19: {
                return new FloatValue((short)6, lu.getFloatValue());
            }
            case 20: {
                return new FloatValue((short)7, lu.getFloatValue());
            }
            case 18: {
                return new FloatValue((short)8, lu.getFloatValue());
            }
            case 21: {
                return new FloatValue((short)9, lu.getFloatValue());
            }
            case 22: {
                return new FloatValue((short)10, lu.getFloatValue());
            }
            case 13: {
                return new FloatValue((short)1, (float)lu.getIntegerValue());
            }
            case 14: {
                return new FloatValue((short)1, lu.getFloatValue());
            }
            case 23: {
                return new FloatValue((short)2, lu.getFloatValue());
            }
        }
        throw this.createMalformedRectDOMException();
    }
    
    @Override
    public Value computeValue(final CSSStylableElement elt, final String pseudo, final CSSEngine engine, final int idx, final StyleMap sm, final Value value) {
        if (value.getCssValueType() != 1) {
            return value;
        }
        if (value.getPrimitiveType() != 24) {
            return value;
        }
        final RectValue rect = (RectValue)value;
        this.orientation = 1;
        final Value top = super.computeValue(elt, pseudo, engine, idx, sm, rect.getTop());
        final Value bottom = super.computeValue(elt, pseudo, engine, idx, sm, rect.getBottom());
        this.orientation = 0;
        final Value left = super.computeValue(elt, pseudo, engine, idx, sm, rect.getLeft());
        final Value right = super.computeValue(elt, pseudo, engine, idx, sm, rect.getRight());
        if (top != rect.getTop() || right != rect.getRight() || bottom != rect.getBottom() || left != rect.getLeft()) {
            return new RectValue(top, right, bottom, left);
        }
        return value;
    }
    
    @Override
    protected int getOrientation() {
        return this.orientation;
    }
    
    private DOMException createMalformedRectDOMException() {
        final Object[] p = { this.getPropertyName() };
        final String s = Messages.formatMessage("malformed.rect", p);
        return new DOMException((short)12, s);
    }
}
