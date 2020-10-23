// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;

public interface LiveAttributeValue
{
    void attrAdded(final Attr p0, final String p1);
    
    void attrModified(final Attr p0, final String p1, final String p2);
    
    void attrRemoved(final Attr p0, final String p1);
}
