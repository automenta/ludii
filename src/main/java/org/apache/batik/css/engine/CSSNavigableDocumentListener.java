// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface CSSNavigableDocumentListener
{
    void nodeInserted(final Node p0);
    
    void nodeToBeRemoved(final Node p0);
    
    void subtreeModified(final Node p0);
    
    void characterDataModified(final Node p0);
    
    void attrModified(final Element p0, final Attr p1, final short p2, final String p3, final String p4);
    
    void overrideStyleTextChanged(final CSSStylableElement p0, final String p1);
    
    void overrideStylePropertyRemoved(final CSSStylableElement p0, final String p1);
    
    void overrideStylePropertyChanged(final CSSStylableElement p0, final String p1, final String p2, final String p3);
}
