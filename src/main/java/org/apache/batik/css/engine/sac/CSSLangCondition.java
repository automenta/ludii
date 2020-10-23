// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.css.sac.LangCondition;

public class CSSLangCondition implements LangCondition, ExtendedCondition
{
    protected String lang;
    protected String langHyphen;
    
    public CSSLangCondition(final String lang) {
        this.lang = lang.toLowerCase();
        this.langHyphen = lang + '-';
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        final CSSLangCondition c = (CSSLangCondition)obj;
        return c.lang.equals(this.lang);
    }
    
    @Override
    public short getConditionType() {
        return 6;
    }
    
    @Override
    public String getLang() {
        return this.lang;
    }
    
    @Override
    public int getSpecificity() {
        return 256;
    }
    
    @Override
    public boolean match(final Element e, final String pseudoE) {
        String s = e.getAttribute("lang").toLowerCase();
        if (s.equals(this.lang) || s.startsWith(this.langHyphen)) {
            return true;
        }
        s = e.getAttributeNS("http://www.w3.org/XML/1998/namespace", "lang").toLowerCase();
        return s.equals(this.lang) || s.startsWith(this.langHyphen);
    }
    
    @Override
    public void fillAttributeSet(final Set attrSet) {
        attrSet.add("lang");
    }
    
    @Override
    public String toString() {
        return ":lang(" + this.lang + ')';
    }
}
