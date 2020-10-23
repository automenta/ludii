// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.gvt.event;

import java.util.EventListener;

public interface SelectionListener extends EventListener
{
    void selectionChanged(final SelectionEvent p0);
    
    void selectionDone(final SelectionEvent p0);
    
    void selectionCleared(final SelectionEvent p0);
    
    void selectionStarted(final SelectionEvent p0);
}
