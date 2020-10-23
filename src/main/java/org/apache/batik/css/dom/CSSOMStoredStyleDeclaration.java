// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.css.CSSRule;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleDeclarationProvider;

public abstract class CSSOMStoredStyleDeclaration extends CSSOMSVGStyleDeclaration implements CSSOMStyleDeclaration.ValueProvider, CSSOMStyleDeclaration.ModificationHandler, StyleDeclarationProvider
{
    protected StyleDeclaration declaration;
    
    public CSSOMStoredStyleDeclaration(final CSSEngine eng) {
        super(null, null, eng);
        ((CSSOMStyleDeclaration)(this.valueProvider = this)).setModificationHandler(this);
    }
    
    @Override
    public StyleDeclaration getStyleDeclaration() {
        return this.declaration;
    }
    
    @Override
    public void setStyleDeclaration(final StyleDeclaration sd) {
        this.declaration = sd;
    }
    
    @Override
    public Value getValue(final String name) {
        final int idx = this.cssEngine.getPropertyIndex(name);
        for (int i = 0; i < this.declaration.size(); ++i) {
            if (idx == this.declaration.getIndex(i)) {
                return this.declaration.getValue(i);
            }
        }
        return null;
    }
    
    @Override
    public boolean isImportant(final String name) {
        final int idx = this.cssEngine.getPropertyIndex(name);
        for (int i = 0; i < this.declaration.size(); ++i) {
            if (idx == this.declaration.getIndex(i)) {
                return this.declaration.getPriority(i);
            }
        }
        return false;
    }
    
    @Override
    public String getText() {
        return this.declaration.toString(this.cssEngine);
    }
    
    @Override
    public int getLength() {
        return this.declaration.size();
    }
    
    @Override
    public String item(final int idx) {
        return this.cssEngine.getPropertyName(this.declaration.getIndex(idx));
    }
}
