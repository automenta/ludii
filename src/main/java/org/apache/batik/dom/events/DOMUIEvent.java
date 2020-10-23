// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import java.util.List;
import org.apache.batik.xml.XMLUtilities;
import java.util.ArrayList;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.UIEvent;

public class DOMUIEvent extends AbstractEvent implements UIEvent
{
    private AbstractView view;
    private int detail;
    
    @Override
    public AbstractView getView() {
        return this.view;
    }
    
    @Override
    public int getDetail() {
        return this.detail;
    }
    
    @Override
    public void initUIEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg) {
        this.initEvent(typeArg, canBubbleArg, cancelableArg);
        this.view = viewArg;
        this.detail = detailArg;
    }
    
    public void initUIEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.view = viewArg;
        this.detail = detailArg;
    }
    
    protected String[] split(final String s) {
        final List a = new ArrayList(8);
        int i = 0;
        final int len = s.length();
        while (i < len) {
            char c = s.charAt(i++);
            if (XMLUtilities.isXMLSpace(c)) {
                continue;
            }
            final StringBuffer sb = new StringBuffer();
            sb.append(c);
            while (i < len) {
                c = s.charAt(i++);
                if (XMLUtilities.isXMLSpace(c)) {
                    a.add(sb.toString());
                    break;
                }
                sb.append(c);
            }
            if (i != len) {
                continue;
            }
            a.add(sb.toString());
        }
        return a.toArray(new String[a.size()]);
    }
}
