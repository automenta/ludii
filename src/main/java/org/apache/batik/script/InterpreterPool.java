// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.script;

import java.util.Iterator;
import org.apache.batik.util.Service;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.Map;

public class InterpreterPool
{
    public static final String BIND_NAME_DOCUMENT = "document";
    protected static Map defaultFactories;
    protected Map factories;
    
    public InterpreterPool() {
        (this.factories = new HashMap(7)).putAll(InterpreterPool.defaultFactories);
    }
    
    public Interpreter createInterpreter(final Document document, final String language) {
        return this.createInterpreter(document, language, null);
    }
    
    public Interpreter createInterpreter(final Document document, final String language, ImportInfo imports) {
        final InterpreterFactory factory = this.factories.get(language);
        if (factory == null) {
            return null;
        }
        if (imports == null) {
            imports = ImportInfo.getImports();
        }
        Interpreter interpreter = null;
        final SVGOMDocument svgDoc = (SVGOMDocument)document;
        URL url = null;
        try {
            url = new URL(svgDoc.getDocumentURI());
        }
        catch (MalformedURLException ex) {}
        interpreter = factory.createInterpreter(url, svgDoc.isSVG12(), imports);
        if (interpreter == null) {
            return null;
        }
        if (document != null) {
            interpreter.bindObject("document", document);
        }
        return interpreter;
    }
    
    public void putInterpreterFactory(final String language, final InterpreterFactory factory) {
        this.factories.put(language, factory);
    }
    
    public void removeInterpreterFactory(final String language) {
        this.factories.remove(language);
    }
    
    static {
        InterpreterPool.defaultFactories = new HashMap(7);
        final Iterator iter = Service.providers(InterpreterFactory.class);
        while (iter.hasNext()) {
            InterpreterFactory factory = null;
            factory = iter.next();
            final String[] arr$;
            final String[] mimeTypes = arr$ = factory.getMimeTypes();
            for (final String mimeType : arr$) {
                InterpreterPool.defaultFactories.put(mimeType, factory);
            }
        }
    }
}
