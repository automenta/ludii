// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface DescendantSelector extends Selector
{
    Selector getAncestorSelector();
    
    SimpleSelector getSimpleSelector();
}
