// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.views.AbstractView;
import java.util.Iterator;
import java.util.HashSet;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;

public class DOMMouseEvent extends DOMUIEvent implements MouseEvent
{
    private int screenX;
    private int screenY;
    private int clientX;
    private int clientY;
    private short button;
    private EventTarget relatedTarget;
    protected HashSet modifierKeys;
    
    public DOMMouseEvent() {
        this.modifierKeys = new HashSet();
    }
    
    @Override
    public int getScreenX() {
        return this.screenX;
    }
    
    @Override
    public int getScreenY() {
        return this.screenY;
    }
    
    @Override
    public int getClientX() {
        return this.clientX;
    }
    
    @Override
    public int getClientY() {
        return this.clientY;
    }
    
    @Override
    public boolean getCtrlKey() {
        return this.modifierKeys.contains("Control");
    }
    
    @Override
    public boolean getShiftKey() {
        return this.modifierKeys.contains("Shift");
    }
    
    @Override
    public boolean getAltKey() {
        return this.modifierKeys.contains("Alt");
    }
    
    @Override
    public boolean getMetaKey() {
        return this.modifierKeys.contains("Meta");
    }
    
    @Override
    public short getButton() {
        return this.button;
    }
    
    @Override
    public EventTarget getRelatedTarget() {
        return this.relatedTarget;
    }
    
    public boolean getModifierState(final String keyIdentifierArg) {
        return this.modifierKeys.contains(keyIdentifierArg);
    }
    
    public String getModifiersString() {
        if (this.modifierKeys.isEmpty()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer(this.modifierKeys.size() * 8);
        final Iterator i = this.modifierKeys.iterator();
        sb.append(i.next());
        while (i.hasNext()) {
            sb.append(' ');
            sb.append(i.next());
        }
        return sb.toString();
    }
    
    @Override
    public void initMouseEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg, final int screenXArg, final int screenYArg, final int clientXArg, final int clientYArg, final boolean ctrlKeyArg, final boolean altKeyArg, final boolean shiftKeyArg, final boolean metaKeyArg, final short buttonArg, final EventTarget relatedTargetArg) {
        this.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
        this.screenX = screenXArg;
        this.screenY = screenYArg;
        this.clientX = clientXArg;
        this.clientY = clientYArg;
        if (ctrlKeyArg) {
            this.modifierKeys.add("Control");
        }
        if (altKeyArg) {
            this.modifierKeys.add("Alt");
        }
        if (shiftKeyArg) {
            this.modifierKeys.add("Shift");
        }
        if (metaKeyArg) {
            this.modifierKeys.add("Meta");
        }
        this.button = buttonArg;
        this.relatedTarget = relatedTargetArg;
    }
    
    public void initMouseEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final AbstractView viewArg, final int detailArg, final int screenXArg, final int screenYArg, final int clientXArg, final int clientYArg, final short buttonArg, final EventTarget relatedTargetArg, final String modifiersList) {
        this.initUIEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
        this.screenX = screenXArg;
        this.screenY = screenYArg;
        this.clientX = clientXArg;
        this.clientY = clientYArg;
        this.button = buttonArg;
        this.relatedTarget = relatedTargetArg;
        this.modifierKeys.clear();
        final String[] arr$;
        final String[] modifiers = arr$ = this.split(modifiersList);
        for (final String modifier : arr$) {
            this.modifierKeys.add(modifier);
        }
    }
}
