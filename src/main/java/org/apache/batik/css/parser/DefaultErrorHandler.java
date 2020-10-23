// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

public class DefaultErrorHandler implements ErrorHandler
{
    public static final ErrorHandler INSTANCE;
    
    protected DefaultErrorHandler() {
    }
    
    @Override
    public void warning(final CSSParseException e) {
    }
    
    @Override
    public void error(final CSSParseException e) {
    }
    
    @Override
    public void fatalError(final CSSParseException e) {
        throw e;
    }
    
    static {
        INSTANCE = new DefaultErrorHandler();
    }
}
