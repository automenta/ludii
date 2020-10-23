// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface ConditionalSelector extends SimpleSelector
{
    SimpleSelector getSimpleSelector();
    
    Condition getCondition();
}
