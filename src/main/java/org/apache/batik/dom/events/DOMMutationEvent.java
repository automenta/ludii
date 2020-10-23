// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class DOMMutationEvent extends AbstractEvent implements MutationEvent
{
    private Node relatedNode;
    private String prevValue;
    private String newValue;
    private String attrName;
    private short attrChange;
    
    @Override
    public Node getRelatedNode() {
        return this.relatedNode;
    }
    
    @Override
    public String getPrevValue() {
        return this.prevValue;
    }
    
    @Override
    public String getNewValue() {
        return this.newValue;
    }
    
    @Override
    public String getAttrName() {
        return this.attrName;
    }
    
    @Override
    public short getAttrChange() {
        return this.attrChange;
    }
    
    @Override
    public void initMutationEvent(final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final Node relatedNodeArg, final String prevValueArg, final String newValueArg, final String attrNameArg, final short attrChangeArg) {
        this.initEvent(typeArg, canBubbleArg, cancelableArg);
        this.relatedNode = relatedNodeArg;
        this.prevValue = prevValueArg;
        this.newValue = newValueArg;
        this.attrName = attrNameArg;
        this.attrChange = attrChangeArg;
    }
    
    public void initMutationEventNS(final String namespaceURIArg, final String typeArg, final boolean canBubbleArg, final boolean cancelableArg, final Node relatedNodeArg, final String prevValueArg, final String newValueArg, final String attrNameArg, final short attrChangeArg) {
        this.initEventNS(namespaceURIArg, typeArg, canBubbleArg, cancelableArg);
        this.relatedNode = relatedNodeArg;
        this.prevValue = prevValueArg;
        this.newValue = newValueArg;
        this.attrName = attrNameArg;
        this.attrChange = attrChangeArg;
    }
}
