// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.batik.w3c.dom.Location;
import org.w3c.dom.events.EventTarget;
import org.apache.batik.script.ScriptEventWrapper;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.Event;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.events.AbstractEvent;
import org.w3c.dom.events.DocumentEvent;
import java.io.Reader;
import org.apache.batik.script.InterpreterException;
import java.io.IOException;
import java.io.StringReader;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import org.w3c.dom.svg.EventListenerInitializer;
import java.util.jar.Manifest;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.batik.dom.util.XLinkSupport;
import org.w3c.dom.NodeList;
import org.apache.batik.dom.AbstractElement;
import org.w3c.dom.svg.SVGSVGElement;
import org.w3c.dom.svg.SVGDocument;
import java.util.HashMap;
import java.util.HashSet;
import org.w3c.dom.Node;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;
import java.util.WeakHashMap;
import java.util.Map;
import org.apache.batik.script.Interpreter;
import java.util.Set;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Document;

public class BaseScriptingEnvironment
{
    public static final String INLINE_SCRIPT_DESCRIPTION = "BaseScriptingEnvironment.constant.inline.script.description";
    public static final String EVENT_SCRIPT_DESCRIPTION = "BaseScriptingEnvironment.constant.event.script.description";
    protected static final String EVENT_NAME = "event";
    protected static final String ALTERNATE_EVENT_NAME = "evt";
    protected static final String APPLICATION_ECMASCRIPT = "application/ecmascript";
    protected BridgeContext bridgeContext;
    protected UserAgent userAgent;
    protected Document document;
    protected ParsedURL docPURL;
    protected Set languages;
    protected Interpreter interpreter;
    protected Map windowObjects;
    protected WeakHashMap executedScripts;
    
    public static boolean isDynamicDocument(final BridgeContext ctx, final Document doc) {
        final Element elt = doc.getDocumentElement();
        return elt != null && "http://www.w3.org/2000/svg".equals(elt.getNamespaceURI()) && (elt.getAttributeNS(null, "onabort").length() > 0 || elt.getAttributeNS(null, "onerror").length() > 0 || elt.getAttributeNS(null, "onresize").length() > 0 || elt.getAttributeNS(null, "onunload").length() > 0 || elt.getAttributeNS(null, "onscroll").length() > 0 || elt.getAttributeNS(null, "onzoom").length() > 0 || isDynamicElement(ctx, doc.getDocumentElement()));
    }
    
    public static boolean isDynamicElement(final BridgeContext ctx, final Element elt) {
        final List bridgeExtensions = ctx.getBridgeExtensions(elt.getOwnerDocument());
        return isDynamicElement(elt, ctx, bridgeExtensions);
    }
    
    public static boolean isDynamicElement(final Element elt, final BridgeContext ctx, final List bridgeExtensions) {
        for (final Object bridgeExtension1 : bridgeExtensions) {
            final BridgeExtension bridgeExtension2 = (BridgeExtension)bridgeExtension1;
            if (bridgeExtension2.isDynamicElement(elt)) {
                return true;
            }
        }
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if (elt.getAttributeNS(null, "onkeyup").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onkeydown").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onkeypress").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onload").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onerror").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onactivate").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onclick").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onfocusin").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onfocusout").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmousedown").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmousemove").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseout").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseover").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseup").length() > 0) {
                return true;
            }
        }
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1 && isDynamicElement(ctx, (Element)n)) {
                return true;
            }
        }
        return false;
    }
    
    public BaseScriptingEnvironment(final BridgeContext ctx) {
        this.languages = new HashSet();
        this.windowObjects = new HashMap();
        this.executedScripts = new WeakHashMap();
        this.bridgeContext = ctx;
        this.document = ctx.getDocument();
        this.docPURL = new ParsedURL(((SVGDocument)this.document).getURL());
        this.userAgent = this.bridgeContext.getUserAgent();
    }
    
    public org.apache.batik.bridge.Window getWindow(final Interpreter interp, final String lang) {
        org.apache.batik.bridge.Window w = this.windowObjects.get(interp);
        if (w == null) {
            w = ((interp == null) ? new Window(null, null) : this.createWindow(interp, lang));
            this.windowObjects.put(interp, w);
        }
        return w;
    }
    
    public org.apache.batik.bridge.Window getWindow() {
        return this.getWindow(null, null);
    }
    
    protected org.apache.batik.bridge.Window createWindow(final Interpreter interp, final String lang) {
        return new Window(interp, lang);
    }
    
    public Interpreter getInterpreter() {
        if (this.interpreter != null) {
            return this.interpreter;
        }
        final SVGSVGElement root = (SVGSVGElement)this.document.getDocumentElement();
        final String lang = root.getContentScriptType();
        return this.getInterpreter(lang);
    }
    
    public Interpreter getInterpreter(final String lang) {
        this.interpreter = this.bridgeContext.getInterpreter(lang);
        if (this.interpreter != null) {
            if (!this.languages.contains(lang)) {
                this.languages.add(lang);
                this.initializeEnvironment(this.interpreter, lang);
            }
            return this.interpreter;
        }
        if (this.languages.contains(lang)) {
            return null;
        }
        this.languages.add(lang);
        return null;
    }
    
    public void initializeEnvironment(final Interpreter interp, final String lang) {
        interp.bindObject("window", this.getWindow(interp, lang));
    }
    
    public void loadScripts() {
        final NodeList scripts = this.document.getElementsByTagNameNS("http://www.w3.org/2000/svg", "script");
        for (int len = scripts.getLength(), i = 0; i < len; ++i) {
            final AbstractElement script = (AbstractElement)scripts.item(i);
            this.loadScript(script);
        }
    }
    
    protected void loadScript(final AbstractElement script) {
        if (this.executedScripts.containsKey(script)) {
            return;
        }
        Node n = script;
        do {
            n = n.getParentNode();
            if (n == null) {
                return;
            }
        } while (n.getNodeType() != 9);
        String type = script.getAttributeNS(null, "type");
        if (type.length() == 0) {
            type = "text/ecmascript";
        }
        if (type.equals("application/java-archive")) {
            try {
                final String href = XLinkSupport.getXLinkHref(script);
                final ParsedURL purl = new ParsedURL(script.getBaseURI(), href);
                this.checkCompatibleScriptURL(type, purl);
                URL docURL = null;
                try {
                    docURL = new URL(this.docPURL.toString());
                }
                catch (MalformedURLException ex) {}
                final DocumentJarClassLoader cll = new DocumentJarClassLoader(new URL(purl.toString()), docURL);
                final URL url = cll.findResource("META-INF/MANIFEST.MF");
                if (url == null) {
                    return;
                }
                final Manifest man = new Manifest(url.openStream());
                this.executedScripts.put(script, null);
                String sh = man.getMainAttributes().getValue("Script-Handler");
                if (sh != null) {
                    final ScriptHandler h = (ScriptHandler)cll.loadClass(sh).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                    h.run(this.document, this.getWindow());
                }
                sh = man.getMainAttributes().getValue("SVG-Handler-Class");
                if (sh != null) {
                    final EventListenerInitializer initializer = (EventListenerInitializer)cll.loadClass(sh).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                    this.getWindow();
                    initializer.initializeEventListeners((SVGDocument)this.document);
                }
            }
            catch (Exception e) {
                if (this.userAgent != null) {
                    this.userAgent.displayError(e);
                }
            }
            return;
        }
        final Interpreter interpreter = this.getInterpreter(type);
        if (interpreter == null) {
            return;
        }
        try {
            final String href2 = XLinkSupport.getXLinkHref(script);
            String desc = null;
            Reader reader = null;
            if (href2.length() > 0) {
                desc = href2;
                final ParsedURL purl2 = new ParsedURL(script.getBaseURI(), href2);
                this.checkCompatibleScriptURL(type, purl2);
                final InputStream is = purl2.openStream();
                final String mediaType = purl2.getContentTypeMediaType();
                String enc = purl2.getContentTypeCharset();
                if (enc != null) {
                    try {
                        reader = new InputStreamReader(is, enc);
                    }
                    catch (UnsupportedEncodingException uee) {
                        enc = null;
                    }
                }
                if (reader == null) {
                    if ("application/ecmascript".equals(mediaType)) {
                        if (purl2.hasContentTypeParameter("version")) {
                            return;
                        }
                        final PushbackInputStream pbis = new PushbackInputStream(is, 8);
                        final byte[] buf = new byte[4];
                        final int read = pbis.read(buf);
                        if (read > 0) {
                            pbis.unread(buf, 0, read);
                            if (read >= 2) {
                                if (buf[0] == -1 && buf[1] == -2) {
                                    if (read >= 4 && buf[2] == 0 && buf[3] == 0) {
                                        enc = "UTF32-LE";
                                        pbis.skip(4L);
                                    }
                                    else {
                                        enc = "UTF-16LE";
                                        pbis.skip(2L);
                                    }
                                }
                                else if (buf[0] == -2 && buf[1] == -1) {
                                    enc = "UTF-16BE";
                                    pbis.skip(2L);
                                }
                                else if (read >= 3 && buf[0] == -17 && buf[1] == -69 && buf[2] == -65) {
                                    enc = "UTF-8";
                                    pbis.skip(3L);
                                }
                                else if (read >= 4 && buf[0] == 0 && buf[1] == 0 && buf[2] == -2 && buf[3] == -1) {
                                    enc = "UTF-32BE";
                                    pbis.skip(4L);
                                }
                            }
                            if (enc == null) {
                                enc = "UTF-8";
                            }
                        }
                        reader = new InputStreamReader(pbis, enc);
                    }
                    else {
                        reader = new InputStreamReader(is);
                    }
                }
            }
            else {
                this.checkCompatibleScriptURL(type, this.docPURL);
                final DocumentLoader dl = this.bridgeContext.getDocumentLoader();
                final Element e2 = script;
                final SVGDocument d = (SVGDocument)e2.getOwnerDocument();
                final int line = dl.getLineNumber(script);
                desc = Messages.formatMessage("BaseScriptingEnvironment.constant.inline.script.description", new Object[] { d.getURL(), "<" + script.getNodeName() + ">", line });
                Node n2 = script.getFirstChild();
                if (n2 == null) {
                    return;
                }
                final StringBuffer sb = new StringBuffer();
                while (n2 != null) {
                    if (n2.getNodeType() == 4 || n2.getNodeType() == 3) {
                        sb.append(n2.getNodeValue());
                    }
                    n2 = n2.getNextSibling();
                }
                reader = new StringReader(sb.toString());
            }
            this.executedScripts.put(script, null);
            interpreter.evaluate(reader, desc);
        }
        catch (IOException e3) {
            if (this.userAgent != null) {
                this.userAgent.displayError(e3);
            }
        }
        catch (InterpreterException e4) {
            System.err.println("InterpExcept: " + e4);
            this.handleInterpreterException(e4);
        }
        catch (SecurityException e5) {
            if (this.userAgent != null) {
                this.userAgent.displayError(e5);
            }
        }
    }
    
    protected void checkCompatibleScriptURL(final String scriptType, final ParsedURL scriptPURL) {
        this.userAgent.checkLoadScript(scriptType, scriptPURL, this.docPURL);
    }
    
    public void dispatchSVGLoadEvent() {
        final SVGSVGElement root = (SVGSVGElement)this.document.getDocumentElement();
        final String lang = root.getContentScriptType();
        final long documentStartTime = System.currentTimeMillis();
        this.bridgeContext.getAnimationEngine().start(documentStartTime);
        this.dispatchSVGLoad(root, true, lang);
    }
    
    protected void dispatchSVGLoad(final Element elt, boolean checkCanRun, final String lang) {
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                this.dispatchSVGLoad((Element)n, checkCanRun, lang);
            }
        }
        final DocumentEvent de = (DocumentEvent)elt.getOwnerDocument();
        final AbstractEvent ev = (AbstractEvent)de.createEvent("SVGEvents");
        String type;
        if (this.bridgeContext.isSVG12()) {
            type = "load";
        }
        else {
            type = "SVGLoad";
        }
        ev.initEventNS("http://www.w3.org/2001/xml-events", type, false, false);
        final NodeEventTarget t = (NodeEventTarget)elt;
        final String s = elt.getAttributeNS(null, "onload");
        if (s.length() == 0) {
            t.dispatchEvent(ev);
            return;
        }
        final Interpreter interp = this.getInterpreter();
        if (interp == null) {
            t.dispatchEvent(ev);
            return;
        }
        if (checkCanRun) {
            this.checkCompatibleScriptURL(lang, this.docPURL);
            checkCanRun = false;
        }
        final DocumentLoader dl = this.bridgeContext.getDocumentLoader();
        final SVGDocument d = (SVGDocument)elt.getOwnerDocument();
        final int line = dl.getLineNumber(elt);
        final String desc = Messages.formatMessage("BaseScriptingEnvironment.constant.event.script.description", new Object[] { d.getURL(), "onload", line });
        final EventListener l = new EventListener() {
            @Override
            public void handleEvent(final Event evt) {
                try {
                    Object event;
                    if (evt instanceof ScriptEventWrapper) {
                        event = ((ScriptEventWrapper)evt).getEventObject();
                    }
                    else {
                        event = evt;
                    }
                    interp.bindObject("event", event);
                    interp.bindObject("evt", event);
                    interp.evaluate(new StringReader(s), desc);
                }
                catch (IOException io) {}
                catch (InterpreterException e) {
                    BaseScriptingEnvironment.this.handleInterpreterException(e);
                }
            }
        };
        t.addEventListenerNS("http://www.w3.org/2001/xml-events", type, l, false, null);
        t.dispatchEvent(ev);
        t.removeEventListenerNS("http://www.w3.org/2001/xml-events", type, l, false);
    }
    
    protected void dispatchSVGZoomEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("zoom");
        }
        else {
            this.dispatchSVGDocEvent("SVGZoom");
        }
    }
    
    protected void dispatchSVGScrollEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("scroll");
        }
        else {
            this.dispatchSVGDocEvent("SVGScroll");
        }
    }
    
    protected void dispatchSVGResizeEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("resize");
        }
        else {
            this.dispatchSVGDocEvent("SVGResize");
        }
    }
    
    protected void dispatchSVGDocEvent(final String eventType) {
        final EventTarget t;
        final SVGSVGElement root = (SVGSVGElement)(t = (SVGSVGElement)this.document.getDocumentElement());
        final DocumentEvent de = (DocumentEvent)this.document;
        final AbstractEvent ev = (AbstractEvent)de.createEvent("SVGEvents");
        ev.initEventNS("http://www.w3.org/2001/xml-events", eventType, false, false);
        t.dispatchEvent(ev);
    }
    
    protected void handleInterpreterException(final InterpreterException ie) {
        if (this.userAgent != null) {
            final Exception ex = ie.getException();
            this.userAgent.displayError((ex == null) ? ie : ex);
        }
    }
    
    protected void handleSecurityException(final SecurityException se) {
        if (this.userAgent != null) {
            this.userAgent.displayError(se);
        }
    }
    
    protected class Window implements org.apache.batik.bridge.Window
    {
        protected Interpreter interpreter;
        protected String language;
        
        public Window(final Interpreter interp, final String lang) {
            this.interpreter = interp;
            this.language = lang;
        }
        
        @Override
        public Object setInterval(final String script, final long interval) {
            return null;
        }
        
        @Override
        public Object setInterval(final Runnable r, final long interval) {
            return null;
        }
        
        @Override
        public void clearInterval(final Object interval) {
        }
        
        @Override
        public Object setTimeout(final String script, final long timeout) {
            return null;
        }
        
        @Override
        public Object setTimeout(final Runnable r, final long timeout) {
            return null;
        }
        
        @Override
        public void clearTimeout(final Object timeout) {
        }
        
        @Override
        public Node parseXML(final String text, final Document doc) {
            return null;
        }
        
        @Override
        public String printNode(final Node n) {
            return null;
        }
        
        @Override
        public void getURL(final String uri, final URLResponseHandler h) {
            this.getURL(uri, h, "UTF8");
        }
        
        @Override
        public void getURL(final String uri, final URLResponseHandler h, final String enc) {
        }
        
        @Override
        public void postURL(final String uri, final String content, final URLResponseHandler h) {
            this.postURL(uri, content, h, "text/plain", null);
        }
        
        @Override
        public void postURL(final String uri, final String content, final URLResponseHandler h, final String mimeType) {
            this.postURL(uri, content, h, mimeType, null);
        }
        
        @Override
        public void postURL(final String uri, final String content, final URLResponseHandler h, final String mimeType, final String fEnc) {
        }
        
        @Override
        public void alert(final String message) {
        }
        
        @Override
        public boolean confirm(final String message) {
            return false;
        }
        
        @Override
        public String prompt(final String message) {
            return null;
        }
        
        @Override
        public String prompt(final String message, final String defVal) {
            return null;
        }
        
        @Override
        public BridgeContext getBridgeContext() {
            return BaseScriptingEnvironment.this.bridgeContext;
        }
        
        @Override
        public Interpreter getInterpreter() {
            return this.interpreter;
        }
        
        @Override
        public Location getLocation() {
            return null;
        }
        
        @Override
        public org.apache.batik.w3c.dom.Window getParent() {
            return null;
        }
    }
}
