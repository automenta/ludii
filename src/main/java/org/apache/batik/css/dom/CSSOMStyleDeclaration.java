// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import java.util.HashMap;
import org.w3c.dom.css.CSSValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import java.util.Map;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;

public class CSSOMStyleDeclaration implements CSSStyleDeclaration
{
    protected ValueProvider valueProvider;
    protected ModificationHandler handler;
    protected CSSRule parentRule;
    protected Map values;
    
    public CSSOMStyleDeclaration(final ValueProvider vp, final CSSRule parent) {
        this.valueProvider = vp;
        this.parentRule = parent;
    }
    
    public void setModificationHandler(final ModificationHandler h) {
        this.handler = h;
    }
    
    @Override
    public String getCssText() {
        return this.valueProvider.getText();
    }
    
    @Override
    public void setCssText(final String cssText) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.values = null;
        this.handler.textChanged(cssText);
    }
    
    @Override
    public String getPropertyValue(final String propertyName) {
        final Value value = this.valueProvider.getValue(propertyName);
        if (value == null) {
            return "";
        }
        return value.getCssText();
    }
    
    @Override
    public CSSValue getPropertyCSSValue(final String propertyName) {
        final Value value = this.valueProvider.getValue(propertyName);
        if (value == null) {
            return null;
        }
        return this.getCSSValue(propertyName);
    }
    
    @Override
    public String removeProperty(final String propertyName) throws DOMException {
        final String result = this.getPropertyValue(propertyName);
        if (result.length() > 0) {
            if (this.handler == null) {
                throw new DOMException((short)7, "");
            }
            if (this.values != null) {
                this.values.remove(propertyName);
            }
            this.handler.propertyRemoved(propertyName);
        }
        return result;
    }
    
    @Override
    public String getPropertyPriority(final String propertyName) {
        return this.valueProvider.isImportant(propertyName) ? "important" : "";
    }
    
    @Override
    public void setProperty(final String propertyName, final String value, final String prio) throws DOMException {
        if (this.handler == null) {
            throw new DOMException((short)7, "");
        }
        this.handler.propertyChanged(propertyName, value, prio);
    }
    
    @Override
    public int getLength() {
        return this.valueProvider.getLength();
    }
    
    @Override
    public String item(final int index) {
        return this.valueProvider.item(index);
    }
    
    @Override
    public CSSRule getParentRule() {
        return this.parentRule;
    }
    
    protected CSSValue getCSSValue(final String name) {
        CSSValue result = null;
        if (this.values != null) {
            result = (CSSValue)this.values.get(name);
        }
        if (result == null) {
            result = this.createCSSValue(name);
            if (this.values == null) {
                this.values = new HashMap(11);
            }
            this.values.put(name, result);
        }
        return result;
    }
    
    protected CSSValue createCSSValue(final String name) {
        return new StyleDeclarationValue(name);
    }
    
    public class StyleDeclarationValue extends CSSOMValue implements CSSOMValue.ValueProvider
    {
        protected String property;
        
        public StyleDeclarationValue(final String prop) {
            super(null);
            ((CSSOMValue)(this.valueProvider = this)).setModificationHandler(new AbstractModificationHandler() {
                @Override
                protected Value getValue() {
                    return StyleDeclarationValue.this.getValue();
                }
                
                @Override
                public void textChanged(final String text) throws DOMException {
                    if (CSSOMStyleDeclaration.this.values == null || CSSOMStyleDeclaration.this.values.get(this) == null || StyleDeclarationValue.this.handler == null) {
                        throw new DOMException((short)7, "");
                    }
                    final String prio = CSSOMStyleDeclaration.this.getPropertyPriority(StyleDeclarationValue.this.property);
                    CSSOMStyleDeclaration.this.handler.propertyChanged(StyleDeclarationValue.this.property, text, prio);
                }
            });
            this.property = prop;
        }
        
        @Override
        public Value getValue() {
            return CSSOMStyleDeclaration.this.valueProvider.getValue(this.property);
        }
    }
    
    public interface ValueProvider
    {
        Value getValue(final String p0);
        
        boolean isImportant(final String p0);
        
        String getText();
        
        int getLength();
        
        String item(final int p0);
    }
    
    public interface ModificationHandler
    {
        void textChanged(final String p0) throws DOMException;
        
        void propertyRemoved(final String p0) throws DOMException;
        
        void propertyChanged(final String p0, final String p1, final String p2) throws DOMException;
    }
}
