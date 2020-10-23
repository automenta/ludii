// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.bridge;

import java.io.InputStream;
import org.apache.batik.gvt.font.GVTFontFamily;

public interface FontFamilyResolver
{
    GVTFontFamily resolve(final String p0);
    
    GVTFontFamily resolve(final String p0, final FontFace p1);
    
    GVTFontFamily loadFont(final InputStream p0, final FontFace p1) throws Exception;
    
    GVTFontFamily getDefault();
    
    GVTFontFamily getFamilyThatCanDisplay(final char p0);
}
