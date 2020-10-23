// 
// Decompiled by Procyon v0.5.36
// 

package org.w3c.css.sac.helpers;

import org.w3c.css.sac.Parser;

import java.lang.reflect.InvocationTargetException;

public class ParserFactory
{
    public Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException, NoSuchMethodException, InvocationTargetException {
        final String property = System.getProperty("org.w3c.css.sac.parser");
        if (property == null) {
            throw new NullPointerException("No value for sac.parser property");
        }
        return (Parser) Class.forName(property).getConstructor().newInstance();
    }
}
