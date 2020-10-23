// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.dom.svg;

public interface ExtendedTraitAccess extends TraitAccess
{
    boolean hasProperty(final String p0);
    
    boolean hasTrait(final String p0, final String p1);
    
    boolean isPropertyAnimatable(final String p0);
    
    boolean isAttributeAnimatable(final String p0, final String p1);
    
    boolean isPropertyAdditive(final String p0);
    
    boolean isAttributeAdditive(final String p0, final String p1);
    
    boolean isTraitAnimatable(final String p0, final String p1);
    
    boolean isTraitAdditive(final String p0, final String p1);
    
    int getPropertyType(final String p0);
    
    int getAttributeType(final String p0, final String p1);
}
