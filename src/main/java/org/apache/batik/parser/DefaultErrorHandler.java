// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

public class DefaultErrorHandler implements ErrorHandler
{
    @Override
    public void error(final ParseException e) throws ParseException {
        throw e;
    }
}
