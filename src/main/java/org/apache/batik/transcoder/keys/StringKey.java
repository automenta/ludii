// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.transcoder.keys;

import org.apache.batik.transcoder.TranscodingHints;

public class StringKey extends TranscodingHints.Key
{
    @Override
    public boolean isCompatibleValue(final Object v) {
        return v instanceof String;
    }
}
