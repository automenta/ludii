// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface CombinatorCondition extends Condition
{
    Condition getFirstCondition();
    
    Condition getSecondCondition();
}
