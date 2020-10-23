// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.CompositeRule;
import java.util.List;

public interface CompositeRable extends FilterColorInterpolation
{
    void setSources(final List p0);
    
    void setCompositeRule(final CompositeRule p0);
    
    CompositeRule getCompositeRule();
}
