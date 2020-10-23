// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import org.w3c.css.sac.ContentCondition;
import org.w3c.css.sac.LangCondition;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.PositionalCondition;
import org.w3c.css.sac.NegativeCondition;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionFactory;

public class CSSConditionFactory implements ConditionFactory
{
    protected String classNamespaceURI;
    protected String classLocalName;
    protected String idNamespaceURI;
    protected String idLocalName;
    
    public CSSConditionFactory(final String cns, final String cln, final String idns, final String idln) {
        this.classNamespaceURI = cns;
        this.classLocalName = cln;
        this.idNamespaceURI = idns;
        this.idLocalName = idln;
    }
    
    @Override
    public CombinatorCondition createAndCondition(final Condition first, final Condition second) throws CSSException {
        return new CSSAndCondition(first, second);
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
        return new CSSAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    @Override
    public AttributeCondition createIdCondition(final String value) throws CSSException {
        return new CSSIdCondition(this.idNamespaceURI, this.idLocalName, value);
    }
    
    @Override
    public LangCondition createLangCondition(final String lang) throws CSSException {
        return new CSSLangCondition(lang);
    }
    
    @Override
    public AttributeCondition createOneOfAttributeCondition(final String localName, final String nsURI, final boolean specified, final String value) throws CSSException {
        return new CSSOneOfAttributeCondition(localName, nsURI, specified, value);
    }
    
    @Override
    public AttributeCondition createBeginHyphenAttributeCondition(final String localName, final String namespaceURI, final boolean specified, final String value) throws CSSException {
        return new CSSBeginHyphenAttributeCondition(localName, namespaceURI, specified, value);
    }
    
    @Override
    public AttributeCondition createClassCondition(final String namespaceURI, final String value) throws CSSException {
        return new CSSClassCondition(this.classLocalName, this.classNamespaceURI, value);
    }
    
    @Override
    public AttributeCondition createPseudoClassCondition(final String namespaceURI, final String value) throws CSSException {
        return new CSSPseudoClassCondition(namespaceURI, value);
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
}
