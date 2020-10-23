// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface ConditionFactory
{
    CombinatorCondition createAndCondition(final Condition p0, final Condition p1) throws CSSException;
    
    CombinatorCondition createOrCondition(final Condition p0, final Condition p1) throws CSSException;
    
    NegativeCondition createNegativeCondition(final Condition p0) throws CSSException;
    
    PositionalCondition createPositionalCondition(final int p0, final boolean p1, final boolean p2) throws CSSException;
    
    AttributeCondition createAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
    
    AttributeCondition createIdCondition(final String p0) throws CSSException;
    
    LangCondition createLangCondition(final String p0) throws CSSException;
    
    AttributeCondition createOneOfAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
    
    AttributeCondition createBeginHyphenAttributeCondition(final String p0, final String p1, final boolean p2, final String p3) throws CSSException;
    
    AttributeCondition createClassCondition(final String p0, final String p1) throws CSSException;
    
    AttributeCondition createPseudoClassCondition(final String p0, final String p1) throws CSSException;
    
    Condition createOnlyChildCondition() throws CSSException;
    
    Condition createOnlyTypeCondition() throws CSSException;
    
    ContentCondition createContentCondition(final String p0) throws CSSException;
}
