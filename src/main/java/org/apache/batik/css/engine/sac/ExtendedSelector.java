// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.css.sac.Selector;

public interface ExtendedSelector extends Selector
{
    boolean match(final Element p0, final String p1);
    
    int getSpecificity();
    
    void fillAttributeSet(final Set p0);
}
