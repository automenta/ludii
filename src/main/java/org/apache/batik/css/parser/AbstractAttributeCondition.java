// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.AttributeCondition;

public abstract class AbstractAttributeCondition implements AttributeCondition
{
    protected String value;
    
    protected AbstractAttributeCondition(final String value) {
        this.value = value;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
}
