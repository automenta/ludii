// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.events.MutationEvent;
import org.apache.batik.anim.dom.SVGOMScriptElement;
import org.apache.batik.dom.AbstractElement;
import java.util.LinkedList;
import java.io.OutputStream;
import java.net.URLConnection;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import org.apache.batik.util.EncodingUtilities;
import org.apache.batik.util.ParsedURL;
import java.io.Writer;
import java.io.StringWriter;
import java.net.URL;
import org.w3c.dom.DOMImplementation;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import java.util.TimerTask;
import org.w3c.dom.Element;
import org.apache.batik.script.InterpreterException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.batik.script.ScriptEventWrapper;
import org.w3c.dom.events.Event;
import org.apache.batik.script.Interpreter;
import org.apache.batik.dom.events.NodeEventTarget;
import org.w3c.dom.Node;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.events.EventListener;
import org.apache.batik.util.RunnableQueue;
import java.util.Timer;

public class ScriptingEnvironment extends BaseScriptingEnvironment
{
    public static final String[] SVG_EVENT_ATTRS;
    public static final String[] SVG_DOM_EVENT;
    protected Timer timer;
    protected UpdateManager updateManager;
    protected RunnableQueue updateRunnableQueue;
    protected EventListener domNodeInsertedListener;
    protected EventListener domNodeRemovedListener;
    protected EventListener domAttrModifiedListener;
    protected EventListener svgAbortListener;
    protected EventListener svgErrorListener;
    protected EventListener svgResizeListener;
    protected EventListener svgScrollListener;
    protected EventListener svgUnloadListener;
    protected EventListener svgZoomListener;
    protected EventListener beginListener;
    protected EventListener endListener;
    protected EventListener repeatListener;
    protected EventListener focusinListener;
    protected EventListener focusoutListener;
    protected EventListener activateListener;
    protected EventListener clickListener;
    protected EventListener mousedownListener;
    protected EventListener mouseupListener;
    protected EventListener mouseoverListener;
    protected EventListener mouseoutListener;
    protected EventListener mousemoveListener;
    protected EventListener keypressListener;
    protected EventListener keydownListener;
    protected EventListener keyupListener;
    protected EventListener[] listeners;
    Map attrToDOMEvent;
    Map attrToListener;
    
    public ScriptingEnvironment(final BridgeContext ctx) {
        super(ctx);
        this.timer = new Timer(true);
        this.svgAbortListener = new ScriptingEventListener("onabort");
        this.svgErrorListener = new ScriptingEventListener("onerror");
        this.svgResizeListener = new ScriptingEventListener("onresize");
        this.svgScrollListener = new ScriptingEventListener("onscroll");
        this.svgUnloadListener = new ScriptingEventListener("onunload");
        this.svgZoomListener = new ScriptingEventListener("onzoom");
        this.beginListener = new ScriptingEventListener("onbegin");
        this.endListener = new ScriptingEventListener("onend");
        this.repeatListener = new ScriptingEventListener("onrepeat");
        this.focusinListener = new ScriptingEventListener("onfocusin");
        this.focusoutListener = new ScriptingEventListener("onfocusout");
        this.activateListener = new ScriptingEventListener("onactivate");
        this.clickListener = new ScriptingEventListener("onclick");
        this.mousedownListener = new ScriptingEventListener("onmousedown");
        this.mouseupListener = new ScriptingEventListener("onmouseup");
        this.mouseoverListener = new ScriptingEventListener("onmouseover");
        this.mouseoutListener = new ScriptingEventListener("onmouseout");
        this.mousemoveListener = new ScriptingEventListener("onmousemove");
        this.keypressListener = new ScriptingEventListener("onkeypress");
        this.keydownListener = new ScriptingEventListener("onkeydown");
        this.keyupListener = new ScriptingEventListener("onkeyup");
        this.listeners = new EventListener[] { this.svgAbortListener, this.svgErrorListener, this.svgResizeListener, this.svgScrollListener, this.svgUnloadListener, this.svgZoomListener, this.beginListener, this.endListener, this.repeatListener, this.focusinListener, this.focusoutListener, this.activateListener, this.clickListener, this.mousedownListener, this.mouseupListener, this.mouseoverListener, this.mouseoutListener, this.mousemoveListener, this.keypressListener, this.keydownListener, this.keyupListener };
        this.attrToDOMEvent = new HashMap(ScriptingEnvironment.SVG_EVENT_ATTRS.length);
        this.attrToListener = new HashMap(ScriptingEnvironment.SVG_EVENT_ATTRS.length);
        for (int i = 0; i < ScriptingEnvironment.SVG_EVENT_ATTRS.length; ++i) {
            this.attrToDOMEvent.put(ScriptingEnvironment.SVG_EVENT_ATTRS[i], ScriptingEnvironment.SVG_DOM_EVENT[i]);
            this.attrToListener.put(ScriptingEnvironment.SVG_EVENT_ATTRS[i], this.listeners[i]);
        }
        this.updateManager = ctx.getUpdateManager();
        this.updateRunnableQueue = this.updateManager.getUpdateRunnableQueue();
        this.addScriptingListeners(this.document.getDocumentElement());
        this.addDocumentListeners();
    }
    
    protected void addDocumentListeners() {
        this.domNodeInsertedListener = new DOMNodeInsertedListener();
        this.domNodeRemovedListener = new DOMNodeRemovedListener();
        this.domAttrModifiedListener = new DOMAttrModifiedListener();
        final NodeEventTarget et = (NodeEventTarget)this.document;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false, null);
    }
    
    protected void removeDocumentListeners() {
        final NodeEventTarget et = (NodeEventTarget)this.document;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false);
    }
    
    @Override
    protected org.apache.batik.bridge.Window createWindow(final Interpreter interp, final String lang) {
        return new Window(interp, lang);
    }
    
    public void runEventHandler(final String script, final Event evt, final String lang, final String desc) {
        final Interpreter interpreter = this.getInterpreter(lang);
        if (interpreter == null) {
            return;
        }
        try {
            this.checkCompatibleScriptURL(lang, this.docPURL);
            Object event;
            if (evt instanceof ScriptEventWrapper) {
                event = ((ScriptEventWrapper)evt).getEventObject();
            }
            else {
                event = evt;
            }
            interpreter.bindObject("event", event);
            interpreter.bindObject("evt", event);
            interpreter.evaluate(new StringReader(script), desc);
        }
        catch (IOException ioe) {}
        catch (InterpreterException ie) {
            this.handleInterpreterException(ie);
        }
        catch (SecurityException se) {
            this.handleSecurityException(se);
        }
    }
    
    public void interrupt() {
        this.timer.cancel();
        this.removeScriptingListeners(this.document.getDocumentElement());
        this.removeDocumentListeners();
    }
    
    public void addScriptingListeners(final Node node) {
        if (node.getNodeType() == 1) {
            this.addScriptingListenersOn((Element)node);
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            this.addScriptingListeners(n);
        }
    }
    
    protected void addScriptingListenersOn(final Element elt) {
        final NodeEventTarget target = (NodeEventTarget)elt;
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if ("svg".equals(elt.getLocalName())) {
                if (elt.hasAttributeNS(null, "onabort")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGAbort", this.svgAbortListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onerror")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGError", this.svgErrorListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onresize")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGResize", this.svgResizeListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onscroll")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGScroll", this.svgScrollListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onunload")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", this.svgUnloadListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onzoom")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGZoom", this.svgZoomListener, false, null);
                }
            }
            else {
                final String name = elt.getLocalName();
                if (name.equals("set") || name.startsWith("animate")) {
                    if (elt.hasAttributeNS(null, "onbegin")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "beginEvent", this.beginListener, false, null);
                    }
                    if (elt.hasAttributeNS(null, "onend")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "endEvent", this.endListener, false, null);
                    }
                    if (elt.hasAttributeNS(null, "onrepeat")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "repeatEvent", this.repeatListener, false, null);
                    }
                    return;
                }
            }
        }
        if (elt.hasAttributeNS(null, "onfocusin")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.focusinListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onfocusout")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.focusoutListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onactivate")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMActivate", this.activateListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onclick")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.clickListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmousedown")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.mousedownListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseup")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.mouseupListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseover")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseout")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmousemove")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.mousemoveListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeypress")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.keypressListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeydown")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.keydownListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeyup")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.keyupListener, false, null);
        }
    }
    
    protected void removeScriptingListeners(final Node node) {
        if (node.getNodeType() == 1) {
            this.removeScriptingListenersOn((Element)node);
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            this.removeScriptingListeners(n);
        }
    }
    
    protected void removeScriptingListenersOn(final Element elt) {
        final NodeEventTarget target = (NodeEventTarget)elt;
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if ("svg".equals(elt.getLocalName())) {
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGAbort", this.svgAbortListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGError", this.svgErrorListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGResize", this.svgResizeListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGScroll", this.svgScrollListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", this.svgUnloadListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGZoom", this.svgZoomListener, false);
            }
            else {
                final String name = elt.getLocalName();
                if (name.equals("set") || name.startsWith("animate")) {
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "beginEvent", this.beginListener, false);
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "endEvent", this.endListener, false);
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "repeatEvent", this.repeatListener, false);
                    return;
                }
            }
        }
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.focusinListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.focusoutListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMActivate", this.activateListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.clickListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.mousedownListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.mouseupListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.mousemoveListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.keypressListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.keydownListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.keyupListener, false);
    }
    
    protected void updateScriptingListeners(final Element elt, final String attr) {
        final String domEvt = this.attrToDOMEvent.get(attr);
        if (domEvt == null) {
            return;
        }
        final EventListener listener = this.attrToListener.get(attr);
        final NodeEventTarget target = (NodeEventTarget)elt;
        if (elt.hasAttributeNS(null, attr)) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", domEvt, listener, false, null);
        }
        else {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", domEvt, listener, false);
        }
    }
    
    static {
        SVG_EVENT_ATTRS = new String[] { "onabort", "onerror", "onresize", "onscroll", "onunload", "onzoom", "onbegin", "onend", "onrepeat", "onfocusin", "onfocusout", "onactivate", "onclick", "onmousedown", "onmouseup", "onmouseover", "onmouseout", "onmousemove", "onkeypress", "onkeydown", "onkeyup" };
        SVG_DOM_EVENT = new String[] { "SVGAbort", "SVGError", "SVGResize", "SVGScroll", "SVGUnload", "SVGZoom", "beginEvent", "endEvent", "repeatEvent", "DOMFocusIn", "DOMFocusOut", "DOMActivate", "click", "mousedown", "mouseup", "mouseover", "mouseout", "mousemove", "keypress", "keydown", "keyup" };
    }
    
    protected class EvaluateRunnable implements Runnable
    {
        protected Interpreter interpreter;
        protected String script;
        
        public EvaluateRunnable(final String s, final Interpreter interp) {
            this.interpreter = interp;
            this.script = s;
        }
        
        @Override
        public void run() {
            try {
                this.interpreter.evaluate(this.script);
            }
            catch (InterpreterException ie) {
                ScriptingEnvironment.this.handleInterpreterException(ie);
            }
        }
    }
    
    protected class EvaluateIntervalRunnable implements Runnable
    {
        public int count;
        public boolean error;
        protected Interpreter interpreter;
        protected String script;
        
        public EvaluateIntervalRunnable(final String s, final Interpreter interp) {
            this.interpreter = interp;
            this.script = s;
        }
        
        @Override
        public void run() {
            synchronized (this) {
                if (this.error) {
                    return;
                }
                --this.count;
            }
            try {
                this.interpreter.evaluate(this.script);
            }
            catch (InterpreterException ie) {
                ScriptingEnvironment.this.handleInterpreterException(ie);
                synchronized (this) {
                    this.error = true;
                }
            }
            catch (Exception e) {
                if (ScriptingEnvironment.this.userAgent != null) {
                    ScriptingEnvironment.this.userAgent.displayError(e);
                }
                else {
                    e.printStackTrace();
                }
                synchronized (this) {
                    this.error = true;
                }
            }
        }
    }
    
    protected class EvaluateRunnableRunnable implements Runnable
    {
        public int count;
        public boolean error;
        protected Runnable runnable;
        
        public EvaluateRunnableRunnable(final Runnable r) {
            this.runnable = r;
        }
        
        @Override
        public void run() {
            synchronized (this) {
                if (this.error) {
                    return;
                }
                --this.count;
            }
            try {
                this.runnable.run();
            }
            catch (Exception e) {
                if (ScriptingEnvironment.this.userAgent != null) {
                    ScriptingEnvironment.this.userAgent.displayError(e);
                }
                else {
                    e.printStackTrace();
                }
                synchronized (this) {
                    this.error = true;
                }
            }
        }
    }
    
    protected class Window implements org.apache.batik.bridge.Window
    {
        protected Interpreter interpreter;
        protected String language;
        protected Location location;
        static final String DEFLATE = "deflate";
        static final String GZIP = "gzip";
        static final String UTF_8 = "UTF-8";
        final /* synthetic */ ScriptingEnvironment this$0;
        
        public Window(final Interpreter interp, final String lang) {
            this.interpreter = interp;
            this.language = lang;
        }
        
        @Override
        public Object setInterval(final String script, final long interval) {
            final IntervalScriptTimerTask tt = new IntervalScriptTimerTask(script);
            ScriptingEnvironment.this.timer.schedule(tt, interval, interval);
            return tt;
        }
        
        @Override
        public Object setInterval(final Runnable r, final long interval) {
            final IntervalRunnableTimerTask tt = new IntervalRunnableTimerTask(r);
            ScriptingEnvironment.this.timer.schedule(tt, interval, interval);
            return tt;
        }
        
        @Override
        public void clearInterval(final Object interval) {
            if (interval == null) {
                return;
            }
            ((TimerTask)interval).cancel();
        }
        
        @Override
        public Object setTimeout(final String script, final long timeout) {
            final TimeoutScriptTimerTask tt = new TimeoutScriptTimerTask(script);
            ScriptingEnvironment.this.timer.schedule(tt, timeout);
            return tt;
        }
        
        @Override
        public Object setTimeout(final Runnable r, final long timeout) {
            final TimeoutRunnableTimerTask tt = new TimeoutRunnableTimerTask(r);
            ScriptingEnvironment.this.timer.schedule(tt, timeout);
            return tt;
        }
        
        @Override
        public void clearTimeout(final Object timeout) {
            if (timeout == null) {
                return;
            }
            ((TimerTask)timeout).cancel();
        }
        
        @Override
        public Node parseXML(final String text, final Document doc) {
            final SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
            URL urlObj = null;
            if (doc instanceof SVGOMDocument) {
                urlObj = ((SVGOMDocument)doc).getURLObject();
            }
            if (urlObj == null) {
                urlObj = ((SVGOMDocument)ScriptingEnvironment.this.bridgeContext.getDocument()).getURLObject();
            }
            final String uri = (urlObj == null) ? "" : urlObj.toString();
            Node res = DOMUtilities.parseXML(text, doc, uri, null, null, df);
            if (res != null) {
                return res;
            }
            if (doc instanceof SVGOMDocument) {
                final Map prefixes = new HashMap();
                prefixes.put("xmlns", "http://www.w3.org/2000/xmlns/");
                prefixes.put("xmlns:xlink", "http://www.w3.org/1999/xlink");
                res = DOMUtilities.parseXML(text, doc, uri, prefixes, "svg", df);
                if (res != null) {
                    return res;
                }
            }
            SAXDocumentFactory sdf;
            if (doc != null) {
                sdf = new SAXDocumentFactory(doc.getImplementation(), XMLResourceDescriptor.getXMLParserClassName());
            }
            else {
                sdf = new SAXDocumentFactory(new GenericDOMImplementation(), XMLResourceDescriptor.getXMLParserClassName());
            }
            return DOMUtilities.parseXML(text, doc, uri, null, null, sdf);
        }
        
        @Override
        public String printNode(final Node n) {
            try {
                final Writer writer = new StringWriter();
                DOMUtilities.writeNode(n, writer);
                writer.close();
                return writer.toString();
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        @Override
        public void getURL(final String uri, final URLResponseHandler h) {
            this.getURL(uri, h, null);
        }
        
        @Override
        public void getURL(final String uri, final URLResponseHandler h, final String enc) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        final ParsedURL burl = ((SVGOMDocument)ScriptingEnvironment.this.document).getParsedURL();
                        final ParsedURL purl = new ParsedURL(burl, uri);
                        String e = null;
                        if (enc != null) {
                            e = EncodingUtilities.javaEncoding(enc);
                            e = ((e == null) ? enc : e);
                        }
                        final InputStream is = purl.openStream();
                        Reader r;
                        if (e == null) {
                            r = new InputStreamReader(is);
                        }
                        else {
                            try {
                                r = new InputStreamReader(is, e);
                            }
                            catch (UnsupportedEncodingException uee) {
                                r = new InputStreamReader(is);
                            }
                        }
                        r = new BufferedReader(r);
                        final StringBuffer sb = new StringBuffer();
                        final char[] buf = new char[4096];
                        int read;
                        while ((read = r.read(buf, 0, buf.length)) != -1) {
                            sb.append(buf, 0, read);
                        }
                        r.close();
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    h.getURLDone(true, purl.getContentType(), sb.toString());
                                }
                                catch (Exception e) {
                                    if (ScriptingEnvironment.this.userAgent != null) {
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e2) {
                        if (e2 instanceof SecurityException) {
                            ScriptingEnvironment.this.userAgent.displayError(e2);
                        }
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    h.getURLDone(false, null, null);
                                }
                                catch (Exception e) {
                                    if (ScriptingEnvironment.this.userAgent != null) {
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                }
            };
            t.setPriority(1);
            t.start();
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
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        final String base = ScriptingEnvironment.this.document.getDocumentURI();
                        URL url;
                        if (base == null) {
                            url = new URL(uri);
                        }
                        else {
                            url = new URL(new URL(base), uri);
                        }
                        final URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", mimeType);
                        OutputStream os = conn.getOutputStream();
                        String e = null;
                        String enc = fEnc;
                        if (enc != null) {
                            if (enc.startsWith("deflate")) {
                                os = new DeflaterOutputStream(os);
                                if (enc.length() > "deflate".length()) {
                                    enc = enc.substring("deflate".length() + 1);
                                }
                                else {
                                    enc = "";
                                }
                                conn.setRequestProperty("Content-Encoding", "deflate");
                            }
                            if (enc.startsWith("gzip")) {
                                os = new GZIPOutputStream(os);
                                if (enc.length() > "gzip".length()) {
                                    enc = enc.substring("gzip".length() + 1);
                                }
                                else {
                                    enc = "";
                                }
                                conn.setRequestProperty("Content-Encoding", "deflate");
                            }
                            if (enc.length() != 0) {
                                e = EncodingUtilities.javaEncoding(enc);
                                if (e == null) {
                                    e = "UTF-8";
                                }
                            }
                            else {
                                e = "UTF-8";
                            }
                        }
                        Writer w;
                        if (e == null) {
                            w = new OutputStreamWriter(os);
                        }
                        else {
                            w = new OutputStreamWriter(os, e);
                        }
                        w.write(content);
                        w.flush();
                        w.close();
                        os.close();
                        final InputStream is = conn.getInputStream();
                        e = "UTF-8";
                        Reader r;
                        if (e == null) {
                            r = new InputStreamReader(is);
                        }
                        else {
                            r = new InputStreamReader(is, e);
                        }
                        r = new BufferedReader(r);
                        final StringBuffer sb = new StringBuffer();
                        final char[] buf = new char[4096];
                        int read;
                        while ((read = r.read(buf, 0, buf.length)) != -1) {
                            sb.append(buf, 0, read);
                        }
                        r.close();
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    h.getURLDone(true, conn.getContentType(), sb.toString());
                                }
                                catch (Exception e) {
                                    if (ScriptingEnvironment.this.userAgent != null) {
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e2) {
                        if (e2 instanceof SecurityException) {
                            ScriptingEnvironment.this.userAgent.displayError(e2);
                        }
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    h.getURLDone(false, null, null);
                                }
                                catch (Exception e) {
                                    if (ScriptingEnvironment.this.userAgent != null) {
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                }
            };
            t.setPriority(1);
            t.start();
        }
        
        @Override
        public void alert(final String message) {
            if (ScriptingEnvironment.this.userAgent != null) {
                ScriptingEnvironment.this.userAgent.showAlert(message);
            }
        }
        
        @Override
        public boolean confirm(final String message) {
            return ScriptingEnvironment.this.userAgent != null && ScriptingEnvironment.this.userAgent.showConfirm(message);
        }
        
        @Override
        public String prompt(final String message) {
            if (ScriptingEnvironment.this.userAgent != null) {
                return ScriptingEnvironment.this.userAgent.showPrompt(message);
            }
            return null;
        }
        
        @Override
        public String prompt(final String message, final String defVal) {
            if (ScriptingEnvironment.this.userAgent != null) {
                return ScriptingEnvironment.this.userAgent.showPrompt(message, defVal);
            }
            return null;
        }
        
        @Override
        public BridgeContext getBridgeContext() {
            return ScriptingEnvironment.this.bridgeContext;
        }
        
        @Override
        public Interpreter getInterpreter() {
            return this.interpreter;
        }
        
        @Override
        public org.apache.batik.w3c.dom.Window getParent() {
            return null;
        }
        
        @Override
        public org.apache.batik.w3c.dom.Location getLocation() {
            if (this.location == null) {
                this.location = new Location(ScriptingEnvironment.this.bridgeContext);
            }
            return this.location;
        }
        
        protected class IntervalScriptTimerTask extends TimerTask
        {
            protected EvaluateIntervalRunnable eir;
            
            public IntervalScriptTimerTask(final String script) {
                this.eir = Window.this.this$0.new EvaluateIntervalRunnable(script, Window.this.interpreter);
            }
            
            @Override
            public void run() {
                synchronized (this.eir) {
                    if (this.eir.count > 1) {
                        return;
                    }
                    final EvaluateIntervalRunnable eir = this.eir;
                    ++eir.count;
                }
                synchronized (ScriptingEnvironment.this.updateRunnableQueue.getIteratorLock()) {
                    if (ScriptingEnvironment.this.updateRunnableQueue.getThread() == null) {
                        this.cancel();
                        return;
                    }
                    ScriptingEnvironment.this.updateRunnableQueue.invokeLater(this.eir);
                }
                synchronized (this.eir) {
                    if (this.eir.error) {
                        this.cancel();
                    }
                }
            }
        }
        
        protected class IntervalRunnableTimerTask extends TimerTask
        {
            protected EvaluateRunnableRunnable eihr;
            
            public IntervalRunnableTimerTask(final Runnable r) {
                this.eihr = Window.this.this$0.new EvaluateRunnableRunnable(r);
            }
            
            @Override
            public void run() {
                synchronized (this.eihr) {
                    if (this.eihr.count > 1) {
                        return;
                    }
                    final EvaluateRunnableRunnable eihr = this.eihr;
                    ++eihr.count;
                }
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater(this.eihr);
                synchronized (this.eihr) {
                    if (this.eihr.error) {
                        this.cancel();
                    }
                }
            }
        }
        
        protected class TimeoutScriptTimerTask extends TimerTask
        {
            private String script;
            
            public TimeoutScriptTimerTask(final String script) {
                this.script = script;
            }
            
            @Override
            public void run() {
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new EvaluateRunnable(this.script, Window.this.interpreter));
            }
        }
        
        protected class TimeoutRunnableTimerTask extends TimerTask
        {
            private Runnable r;
            
            public TimeoutRunnableTimerTask(final Runnable r) {
                this.r = r;
            }
            
            @Override
            public void run() {
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeoutRunnableTimerTask.this.r.run();
                        }
                        catch (Exception e) {
                            if (ScriptingEnvironment.this.userAgent != null) {
                                ScriptingEnvironment.this.userAgent.displayError(e);
                            }
                        }
                    }
                });
            }
        }
    }
    
    protected class DOMNodeInsertedListener implements EventListener
    {
        protected LinkedList toExecute;
        
        protected DOMNodeInsertedListener() {
            this.toExecute = new LinkedList();
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Node n = (Node)evt.getTarget();
            ScriptingEnvironment.this.addScriptingListeners(n);
            this.gatherScriptElements(n);
            while (!this.toExecute.isEmpty()) {
                ScriptingEnvironment.this.loadScript(this.toExecute.removeFirst());
            }
        }
        
        protected void gatherScriptElements(Node n) {
            if (n.getNodeType() == 1) {
                if (n instanceof SVGOMScriptElement) {
                    this.toExecute.add(n);
                }
                else {
                    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                        this.gatherScriptElements(n);
                    }
                }
            }
        }
    }
    
    protected class DOMNodeRemovedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            ScriptingEnvironment.this.removeScriptingListeners((Node)evt.getTarget());
        }
    }
    
    protected class DOMAttrModifiedListener implements EventListener
    {
        @Override
        public void handleEvent(final Event evt) {
            final MutationEvent me = (MutationEvent)evt;
            if (me.getAttrChange() != 1) {
                ScriptingEnvironment.this.updateScriptingListeners((Element)me.getTarget(), me.getAttrName());
            }
        }
    }
    
    protected class ScriptingEventListener implements EventListener
    {
        protected String attribute;
        
        public ScriptingEventListener(final String attr) {
            this.attribute = attr;
        }
        
        @Override
        public void handleEvent(final Event evt) {
            final Element elt = (Element)evt.getCurrentTarget();
            final String script = elt.getAttributeNS(null, this.attribute);
            if (script.length() == 0) {
                return;
            }
            final DocumentLoader dl = ScriptingEnvironment.this.bridgeContext.getDocumentLoader();
            final SVGDocument d = (SVGDocument)elt.getOwnerDocument();
            final int line = dl.getLineNumber(elt);
            final String desc = Messages.formatMessage("BaseScriptingEnvironment.constant.event.script.description", new Object[] { d.getURL(), this.attribute, line });
            Element e;
            for (e = elt; e != null && (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI()) || !"svg".equals(e.getLocalName())); e = SVGUtilities.getParentElement(e)) {}
            if (e == null) {
                return;
            }
            final String lang = e.getAttributeNS(null, "contentScriptType");
            ScriptingEnvironment.this.runEventHandler(script, evt, lang, desc);
        }
    }
}
