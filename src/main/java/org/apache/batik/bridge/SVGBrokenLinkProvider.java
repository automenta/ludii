// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.util.Map;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;
import org.apache.batik.gvt.CompositeGraphicsNode;
import java.util.HashMap;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider;

public class SVGBrokenLinkProvider extends DefaultBrokenLinkProvider implements ErrorConstants
{
    @Override
    public Filter getBrokenLinkImage(final Object base, final String code, final Object[] params) {
        final String message = DefaultBrokenLinkProvider.formatMessage(base, code, params);
        final Map props = new HashMap();
        props.put("org.apache.batik.BrokenLinkImage", message);
        final CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        return new GraphicsNodeRable8Bit((GraphicsNode)cgn, props);
    }
}
