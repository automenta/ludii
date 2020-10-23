// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.smil.TimeEvent;

public class DOMTimeEvent extends AbstractEvent implements TimeEvent
{
    protected AbstractView view;
    protected int detail;
    
    @Override
    public AbstractView getView() {
        return this.view;
    }
    
    @Override
    public int getDetail() {
        return this.detail;
    }
    
    @Override
    public void initTimeEvent(final String typeArg, final AbstractView viewArg, final int detailArg) {
        this.initEvent(typeArg, false, false);
        this.view = viewArg;
        this.detail = detailArg;
    }
    
    public void initTimeEventNS(final String namespaceURIArg, final String typeArg, final AbstractView viewArg, final int detailArg) {
        this.initEventNS(namespaceURIArg, typeArg, false, false);
        this.view = viewArg;
        this.detail = detailArg;
    }
    
    public void setTimestamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
