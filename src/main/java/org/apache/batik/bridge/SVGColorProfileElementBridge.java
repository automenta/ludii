// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import org.apache.xmlgraphics.java2d.color.RenderingIntent;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.io.IOException;
import java.awt.color.ICC_Profile;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;
import org.w3c.dom.Element;
import org.apache.batik.ext.awt.color.NamedProfileCache;

public class SVGColorProfileElementBridge extends AbstractSVGBridge implements ErrorConstants
{
    public NamedProfileCache cache;
    
    public SVGColorProfileElementBridge() {
        this.cache = new NamedProfileCache();
    }
    
    @Override
    public String getLocalName() {
        return "color-profile";
    }
    
    public ICCColorSpaceWithIntent createICCColorSpaceWithIntent(final BridgeContext ctx, final Element paintedElement, final String iccProfileName) {
        ICCColorSpaceWithIntent cs = this.cache.request(iccProfileName.toLowerCase());
        if (cs != null) {
            return cs;
        }
        final Document doc = paintedElement.getOwnerDocument();
        final NodeList list = doc.getElementsByTagNameNS("http://www.w3.org/2000/svg", "color-profile");
        final int n = list.getLength();
        Element profile = null;
        for (int i = 0; i < n; ++i) {
            final Node node = list.item(i);
            if (node.getNodeType() == 1) {
                final Element profileNode = (Element)node;
                final String nameAttr = profileNode.getAttributeNS(null, "name");
                if (iccProfileName.equalsIgnoreCase(nameAttr)) {
                    profile = profileNode;
                }
            }
        }
        if (profile == null) {
            return null;
        }
        final String href = XLinkSupport.getXLinkHref(profile);
        ICC_Profile p = null;
        if (href != null) {
            final String baseURI = profile.getBaseURI();
            ParsedURL pDocURL = null;
            if (baseURI != null) {
                pDocURL = new ParsedURL(baseURI);
            }
            final ParsedURL purl = new ParsedURL(pDocURL, href);
            if (!purl.complete()) {
                final BridgeException be = new BridgeException(ctx, paintedElement, "uri.malformed", new Object[] { href });
                ctx.getUserAgent().displayError(be);
                return null;
            }
            try {
                ctx.getUserAgent().checkLoadExternalResource(purl, pDocURL);
                p = ICC_Profile.getInstance(purl.openStream());
            }
            catch (IOException ioEx) {
                final BridgeException be2 = new BridgeException(ctx, paintedElement, ioEx, "uri.io", new Object[] { href });
                ctx.getUserAgent().displayError(be2);
                return null;
            }
            catch (SecurityException secEx) {
                final BridgeException be2 = new BridgeException(ctx, paintedElement, secEx, "uri.unsecure", new Object[] { href });
                ctx.getUserAgent().displayError(be2);
                return null;
            }
        }
        if (p == null) {
            return null;
        }
        final RenderingIntent intent = convertIntent(profile, ctx);
        cs = new ICCColorSpaceWithIntent(p, intent, href, iccProfileName);
        this.cache.put(iccProfileName.toLowerCase(), cs);
        return cs;
    }
    
    private static RenderingIntent convertIntent(final Element profile, final BridgeContext ctx) {
        final String intent = profile.getAttributeNS(null, "rendering-intent");
        if (intent.length() == 0) {
            return RenderingIntent.AUTO;
        }
        if ("perceptual".equals(intent)) {
            return RenderingIntent.PERCEPTUAL;
        }
        if ("auto".equals(intent)) {
            return RenderingIntent.AUTO;
        }
        if ("relative-colorimetric".equals(intent)) {
            return RenderingIntent.RELATIVE_COLORIMETRIC;
        }
        if ("absolute-colorimetric".equals(intent)) {
            return RenderingIntent.ABSOLUTE_COLORIMETRIC;
        }
        if ("saturation".equals(intent)) {
            return RenderingIntent.SATURATION;
        }
        throw new BridgeException(ctx, profile, "attribute.malformed", new Object[] { "rendering-intent", intent });
    }
}
