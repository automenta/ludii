// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.PositionalCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;

public class DefaultConditionFactory implements ConditionFactory
{
    public static final ConditionFactory INSTANCE;
    
    protected DefaultConditionFactory() {
    }
    
    @Override
    public CombinatorCondition createAndCondition(final Condition first, final Condition second) throws CSSException {
        return new DefaultAndCondition(first, second);
    }
    
    @Override
    public CombinatorCondition createOrCondition(final Condition first, final Condition second) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public NegativeCondition createNegativeCondition(final Condition condition) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public PositionalCondition createPositionalCondition(final int position, final boolean typeNode, final boolean type) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public AttributeCondition createAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return new DefaultAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    @Override
    public AttributeCondition createIdCondition(final String value) throws CSSException {
        return new DefaultIdCondition(value);
    }
    
    @Override
    public LangCondition createLangCondition(final String lang) throws CSSException {
        return new DefaultLangCondition(lang);
    }
    
    @Override
    public AttributeCondition createOneOfAttributeCondition(final String localName, final String nsURI, final boolean specified, final String value) throws CSSException {
        return new DefaultOneOfAttributeCondition(localName, nsURI, specified, value);
    }
    
    @Override
    public AttributeCondition createBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return new DefaultBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    @Override
    public AttributeCondition createClassCondition(final String namespaceURI, final String value) throws CSSException {
        return new DefaultClassCondition(namespaceURI, value);
    }
    
    @Override
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value) throws CSSException {
        return new DefaultPseudoClassCondition(namespaceURI, value);
    }
    
    @Override
    public Condition createOnlyChildCondition() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public Condition createOnlyTypeCondition() throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    @Override
    public ContentCondition createContentCondition(final String data) throws CSSException {
        throw new CSSException("Not implemented in CSS2");
    }
    
    static {
        INSTANCE = new DefaultConditionFactory();
    }
}
