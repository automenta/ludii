// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.rng.core;

import org.apache.commons.rng.RandomProviderState;

import java.util.Arrays;

public class RandomProviderDefaultState implements RandomProviderState
{
    private final byte[] state;
    
    public RandomProviderDefaultState(final byte[] state) {
        this.state = Arrays.copyOf(state, state.length);
    }
    
    public byte[] getState() {
        return Arrays.copyOf(this.state, this.state.length);
    }
}
