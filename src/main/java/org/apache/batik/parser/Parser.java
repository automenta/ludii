// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.io.Reader;
import org.apache.batik.i18n.Localizable;

public interface Parser extends Localizable
{
    void parse(final Reader p0) throws ParseException;
    
    void parse(final String p0) throws ParseException;
    
    void setErrorHandler(final ErrorHandler p0);
}
