// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SelectorList;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.Parser;

public interface ExtendedParser extends Parser
{
    void parseStyleDeclaration(final String p0) throws CSSException, IOException;
    
    void parseRule(final String p0) throws CSSException, IOException;
    
    SelectorList parseSelectors(final String p0) throws CSSException, IOException;
    
    LexicalUnit parsePropertyValue(final String p0) throws CSSException, IOException;
    
    SACMediaList parseMedia(final String p0) throws CSSException, IOException;
    
    boolean parsePriority(final String p0) throws CSSException, IOException;
}
