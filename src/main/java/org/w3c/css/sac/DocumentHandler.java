// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac;

public interface DocumentHandler
{
    void startDocument(final InputSource p0) throws CSSException;
    
    void endDocument(final InputSource p0) throws CSSException;
    
    void comment(final String p0) throws CSSException;
    
    void ignorableAtRule(final String p0) throws CSSException;
    
    void namespaceDeclaration(final String p0, final String p1) throws CSSException;
    
    void importStyle(final String p0, final SACMediaList p1, final String p2) throws CSSException;
    
    void startMedia(final SACMediaList p0) throws CSSException;
    
    void endMedia(final SACMediaList p0) throws CSSException;
    
    void startPage(final String p0, final String p1) throws CSSException;
    
    void endPage(final String p0, final String p1) throws CSSException;
    
    void startFontFace() throws CSSException;
    
    void endFontFace() throws CSSException;
    
    void startSelector(final SelectorList p0) throws CSSException;
    
    void endSelector(final SelectorList p0) throws CSSException;
    
    void property(final String p0, final LexicalUnit p1, final boolean p2) throws CSSException;
}
