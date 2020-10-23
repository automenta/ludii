// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge.svg12;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.apache.batik.anim.dom.XBLOMContentElement;

public abstract class AbstractContentSelector
{
    protected ContentManager contentManager;
    protected XBLOMContentElement contentElement;
    protected Element boundElement;
    protected static HashMap selectorFactories;
    
    public AbstractContentSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound) {
        this.contentManager = cm;
        this.contentElement = content;
        this.boundElement = bound;
    }
    
    public abstract NodeList getSelectedContent();
    
    abstract boolean update();
    
    protected boolean isSelected(final Node n) {
        return this.contentManager.getContentElement(n) != null;
    }
    
    public static AbstractContentSelector createSelector(final String selectorLanguage, final ContentManager cm, final XBLOMContentElement content, final Element bound, final String selector) {
        final ContentSelectorFactory f = AbstractContentSelector.selectorFactories.get(selectorLanguage);
        if (f == null) {
            throw new RuntimeException("Invalid XBL content selector language '" + selectorLanguage + "'");
        }
        return f.createSelector(cm, content, bound, selector);
    }
    
    static {
        AbstractContentSelector.selectorFactories = new HashMap();
        final ContentSelectorFactory f1 = new XPathPatternContentSelectorFactory();
        final ContentSelectorFactory f2 = new XPathSubsetContentSelectorFactory();
        AbstractContentSelector.selectorFactories.put(null, f1);
        AbstractContentSelector.selectorFactories.put("XPathPattern", f1);
        AbstractContentSelector.selectorFactories.put("XPathSubset", f2);
    }
    
    protected static class XPathSubsetContentSelectorFactory implements ContentSelectorFactory
    {
        @Override
        public AbstractContentSelector createSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound, final String selector) {
            return new XPathSubsetContentSelector(cm, content, bound, selector);
        }
    }
    
    protected static class XPathPatternContentSelectorFactory implements ContentSelectorFactory
    {
        @Override
        public AbstractContentSelector createSelector(final ContentManager cm, final XBLOMContentElement content, final Element bound, final String selector) {
            return new XPathPatternContentSelector(cm, content, bound, selector);
        }
    }
    
    protected interface ContentSelectorFactory
    {
        AbstractContentSelector createSelector(final ContentManager p0, final XBLOMContentElement p1, final Element p2, final String p3);
    }
}
