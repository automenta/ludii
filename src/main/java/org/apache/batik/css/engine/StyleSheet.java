// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.engine;

import org.w3c.css.sac.SACMediaList;

public class StyleSheet
{
    protected Rule[] rules;
    protected int size;
    protected StyleSheet parent;
    protected boolean alternate;
    protected SACMediaList media;
    protected String title;
    
    public StyleSheet() {
        this.rules = new Rule[16];
    }
    
    public void setMedia(final SACMediaList m) {
        this.media = m;
    }
    
    public SACMediaList getMedia() {
        return this.media;
    }
    
    public StyleSheet getParent() {
        return this.parent;
    }
    
    public void setParent(final StyleSheet ss) {
        this.parent = ss;
    }
    
    public void setAlternate(final boolean b) {
        this.alternate = b;
    }
    
    public boolean isAlternate() {
        return this.alternate;
    }
    
    public void setTitle(final String t) {
        this.title = t;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public Rule getRule(final int i) {
        return this.rules[i];
    }
    
    public void clear() {
        this.size = 0;
        this.rules = new Rule[10];
    }
    
    public void append(final Rule r) {
        if (this.size == this.rules.length) {
            final Rule[] t = new Rule[this.size * 2];
            System.arraycopy(this.rules, 0, t, 0, this.size);
            this.rules = t;
        }
        this.rules[this.size++] = r;
    }
    
    public String toString(final CSSEngine eng) {
        final StringBuffer sb = new StringBuffer(this.size * 8);
        for (int i = 0; i < this.size; ++i) {
            sb.append(this.rules[i].toString(eng));
        }
        return sb.toString();
    }
}
