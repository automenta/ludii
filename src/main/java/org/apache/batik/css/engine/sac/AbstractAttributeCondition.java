// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.AttributeCondition;

public abstract class AbstractAttributeCondition implements AttributeCondition, ExtendedCondition
{
    protected String value;
    
    protected AbstractAttributeCondition(final String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final AbstractAttributeCondition c = (AbstractAttributeCondition)obj;
        return c.value.equals(this.value);
    }
    
    @Override
    public int hashCode() {
        return (this.value == null) ? -1 : this.value.hashCode();
    }
    
    @Override
    public int getSpecificity() {
        return 256;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
}
