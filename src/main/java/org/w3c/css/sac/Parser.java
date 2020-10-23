// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

import java.util.Locale;

public interface Parser
{
    void setLocale(final Locale p0) throws CSSException;
    
    void setDocumentHandler(final DocumentHandler p0);
    
    void setSelectorFactory(final SelectorFactory p0);
    
    void setConditionFactory(final ConditionFactory p0);
    
    void setErrorHandler(final ErrorHandler p0);
    
    void parseStyleSheet(final InputSource p0) throws CSSException;
    
    void parseStyleSheet(final String p0) throws CSSException;
    
    void parseStyleDeclaration(final InputSource p0) throws CSSException;
    
    void parseRule(final InputSource p0) throws CSSException;
    
    String getParserVersion();
    
    SelectorList parseSelectors(final InputSource p0) throws CSSException;
    
    LexicalUnit parsePropertyValue(final InputSource p0) throws CSSException;
    
    boolean parsePriority(final InputSource p0) throws CSSException;
}
