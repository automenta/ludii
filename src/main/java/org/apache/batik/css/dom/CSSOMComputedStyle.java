// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.CSSEngine;
import org.w3c.dom.css.CSSStyleDeclaration;

public class CSSOMComputedStyle implements CSSStyleDeclaration
{
    protected CSSEngine cssEngine;
    protected CSSStylableElement element;
    protected String pseudoElement;
    protected Map<String,CSSValue> values;
    
    public CSSOMComputedStyle(final CSSEngine e, final CSSStylableElement elt, final String pseudoElt) {
        this.values = new HashMap();
        this.cssEngine = e;
        this.element = elt;
        this.pseudoElement = pseudoElt;
    }
    
    @Override
    public String getCssText() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.cssEngine.getNumberOfProperties(); ++i) {
            sb.append(this.cssEngine.getPropertyName(i));
            sb.append(": ");
            sb.append(this.cssEngine.getComputedStyle(this.element, this.pseudoElement, i).getCssText());
            sb.append(";\n");
        }
        return sb.toString();
    }
    
    @Override
    public void setCssText(final String cssText) throws DOMException {
        throw new DOMException((short)7, "");
    }
    
    @Override
    public String getPropertyValue(final String propertyName) {
        final int idx = this.cssEngine.getPropertyIndex(propertyName);
        if (idx == -1) {
            return "";
        }
        final Value v = this.cssEngine.getComputedStyle(this.element, this.pseudoElement, idx);
        return v.getCssText();
    }
    
    @Override
    public CSSValue getPropertyCSSValue(final String propertyName) {
        CSSValue result = this.values.get(propertyName);
        if (result == null) {
            final int idx = this.cssEngine.getPropertyIndex(propertyName);
            if (idx != -1) {
                result = this.createCSSValue(idx);
                this.values.put(propertyName, result);
            }
        }
        return result;
    }
    
    @Override
    public String removeProperty(final String propertyName) throws DOMException {
        throw new DOMException((short)7, "");
    }
    
    @Override
    public String getPropertyPriority(final String propertyName) {
        return "";
    }
    
    @Override
    public void setProperty(final String propertyName, final String value, final String prio) throws DOMException {
        throw new DOMException((short)7, "");
    }
    
    @Override
    public int getLength() {
        return this.cssEngine.getNumberOfProperties();
    }
    
    @Override
    public String item(final int index) {
        if (index < 0 || index >= this.cssEngine.getNumberOfProperties()) {
            return "";
        }
        return this.cssEngine.getPropertyName(index);
    }
    
    @Override
    public CSSRule getParentRule() {
        return null;
    }
    
    protected CSSValue createCSSValue(final int idx) {
        return new ComputedCSSValue(idx);
    }
    
    public class ComputedCSSValue extends CSSOMValue implements ValueProvider
    {
        protected int index;
        
        public ComputedCSSValue(final int idx) {
            super(null);
            this.valueProvider = this;
            this.index = idx;
        }
        
        @Override
        public Value getValue() {
            return CSSOMComputedStyle.this.cssEngine.getComputedStyle(CSSOMComputedStyle.this.element, CSSOMComputedStyle.this.pseudoElement, this.index);
        }
    }
}
