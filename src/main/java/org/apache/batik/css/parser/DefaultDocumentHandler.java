// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.DocumentHandler;

public class DefaultDocumentHandler implements DocumentHandler
{
    public static final DocumentHandler INSTANCE;
    
    protected DefaultDocumentHandler() {
    }
    
    @Override
    public void startDocument(final InputSource source) throws CSSException {
    }
    
    @Override
    public void endDocument(final InputSource source) throws CSSException {
    }
    
    @Override
    public void comment(final String text) throws CSSException {
    }
    
    @Override
    public void ignorableAtRule(final String atRule) throws CSSException {
    }
    
    @Override
    public void namespaceDeclaration(final String prefix, final String uri) throws CSSException {
    }
    
    @Override
    public void importStyle(final String uri, final SACMediaList media, final String defaultNamespaceURI) throws CSSException {
    }
    
    @Override
    public void startMedia(final SACMediaList media) throws CSSException {
    }
    
    @Override
    public void endMedia(final SACMediaList media) throws CSSException {
    }
    
    @Override
    public void startPage(final String name, final String pseudo_page) throws CSSException {
    }
    
    @Override
    public void endPage(final String name, final String pseudo_page) throws CSSException {
    }
    
    @Override
    public void startFontFace() throws CSSException {
    }
    
    @Override
    public void endFontFace() throws CSSException {
    }
    
    @Override
    public void startSelector(final SelectorList selectors) throws CSSException {
    }
    
    @Override
    public void endSelector(final SelectorList selectors) throws CSSException {
    }
    
    @Override
    public void property(final String name, final LexicalUnit value, final boolean important) throws CSSException {
    }
    
    static {
        INSTANCE = new DefaultDocumentHandler();
    }
}
