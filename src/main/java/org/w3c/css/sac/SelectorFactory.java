// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface SelectorFactory
{
    ConditionalSelector createConditionalSelector(final SimpleSelector p0, final Condition p1) throws CSSException;
    
    SimpleSelector createAnyNodeSelector() throws CSSException;
    
    SimpleSelector createRootNodeSelector() throws CSSException;
    
    NegativeSelector createNegativeSelector(final SimpleSelector p0) throws CSSException;
    
    ElementSelector createElementSelector(final String p0, final String p1) throws CSSException;
    
    CharacterDataSelector createTextNodeSelector(final String p0) throws CSSException;
    
    CharacterDataSelector createCDataSectionSelector(final String p0) throws CSSException;
    
    ProcessingInstructionSelector createProcessingInstructionSelector(final String p0, final String p1) throws CSSException;
    
    CharacterDataSelector createCommentSelector(final String p0) throws CSSException;
    
    ElementSelector createPseudoElementSelector(final String p0, final String p1) throws CSSException;
    
    DescendantSelector createDescendantSelector(final Selector p0, final SimpleSelector p1) throws CSSException;
    
    DescendantSelector createChildSelector(final Selector p0, final SimpleSelector p1) throws CSSException;
    
    SiblingSelector createDirectAdjacentSelector(final short p0, final Selector p1, final SimpleSelector p2) throws CSSException;
}
