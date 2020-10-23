// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng;

public interface RestorableUniformRandomProvider extends UniformRandomProvider
{
    RandomProviderState saveState();
    
    void restoreState(final RandomProviderState p0);
}
