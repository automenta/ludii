// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show.sites;

import annotations.Hide;
import annotations.Opt;
import metadata.graphics.GraphicsItem;

@Hide
public class ShowSitesAsHoles implements GraphicsItem
{
    private final boolean sitesAsHoles;
    
    public ShowSitesAsHoles(@Opt final Boolean sitesAsHoles) {
        this.sitesAsHoles = (sitesAsHoles == null || sitesAsHoles);
    }
    
    public boolean sitesAsHoles() {
        return this.sitesAsHoles;
    }
}
