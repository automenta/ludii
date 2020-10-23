// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.css.sac.ElementSelector;

public abstract class AbstractElementSelector implements ElementSelector, ExtendedSelector
{
    protected String namespaceURI;
    protected String localName;
    
    protected AbstractElementSelector(final String uri, final String name) {
        this.namespaceURI = uri;
        this.localName = name;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final AbstractElementSelector s = (AbstractElementSelector)obj;
        return s.namespaceURI.equals(this.namespaceURI) && s.localName.equals(this.localName);
    }
    
    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }
    
    @Override
    public String getLocalName() {
        return this.localName;
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
    }
}
