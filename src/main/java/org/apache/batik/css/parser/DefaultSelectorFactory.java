// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.ProcessingInstructionSelector;
import org.w3c.css.sac.CharacterDataSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.css.sac.SelectorFactory;

public class DefaultSelectorFactory implements SelectorFactory
{
    public static final SelectorFactory INSTANCE;
    
    protected DefaultSelectorFactory() {
    }
    
    @Override
    public ConditionalSelector createConditionalSelector(final SimpleSelector selector, final Condition condition) throws CSSException {
        return new DefaultConditionalSelector(selector, condition);
    }
    
    @Override
    public SimpleSelector createAnyNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public SimpleSelector createRootNodeSelector() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public NegativeSelector createNegativeSelector(final SimpleSelector selector) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public ElementSelector createElementSelector(final String namespaceURI, final String tagName) throws CSSException {
        return new DefaultElementSelector(namespaceURI, tagName);
    }
    
    @Override
    public CharacterDataSelector createTextNodeSelector(final String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public CharacterDataSelector createCDataSectionSelector(final String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public ProcessingInstructionSelector createProcessingInstructionSelector(final String target, final String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public CharacterDataSelector createCommentSelector(final String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public ElementSelector createPseudoElementSelector(final String namespaceURI, final String pseudoName) throws CSSException {
        return new DefaultPseudoElementSelector(namespaceURI, pseudoName);
    }
    
    @Override
    public DescendantSelector createDescendantSelector(final Selector parent, final SimpleSelector descendant) throws CSSException {
        return new DefaultDescendantSelector(parent, descendant);
    }
    
    @Override
    public DescendantSelector createChildSelector(final Selector parent, final SimpleSelector child) throws CSSException {
        return new DefaultChildSelector(parent, child);
    }
    
    @Override
    public SiblingSelector createDirectAdjacentSelector(final short nodeType, final Selector child, final SimpleSelector directAdjacent) throws CSSException {
        return new DefaultDirectAdjacentSelector(nodeType, child, directAdjacent);
    }
    
    static {
        INSTANCE = new DefaultSelectorFactory();
    }
}
