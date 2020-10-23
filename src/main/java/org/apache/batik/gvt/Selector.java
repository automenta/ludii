// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt;

import org.apache.batik.gvt.event.SelectionListener;
import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;

public interface Selector extends GraphicsNodeMouseListener, GraphicsNodeKeyListener, GraphicsNodeChangeListener
{
    Object getSelection();
    
    boolean isEmpty();
    
    void addSelectionListener(final SelectionListener p0);
    
    void removeSelectionListener(final SelectionListener p0);
}
