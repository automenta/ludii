// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.apache.batik.anim.dom.XBLOMContentElement;
import java.util.EventObject;

public class ContentSelectionChangedEvent extends EventObject
{
    public ContentSelectionChangedEvent(final XBLOMContentElement c) {
        super(c);
    }
    
    public XBLOMContentElement getContentElement() {
        return (XBLOMContentElement)this.source;
    }
}
