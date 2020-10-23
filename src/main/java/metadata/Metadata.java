// 
// Decompiled by Procyon v0.5.36
// 

package metadata;

import annotations.Opt;
import metadata.ai.Ai;
import metadata.graphics.Graphics;
import metadata.info.Info;

import java.io.Serializable;

public class Metadata implements MetadataItem, Serializable
{
    private static final long serialVersionUID = 1L;
    private final Info info;
    private final Graphics graphics;
    private final Ai ai;
    
    public Metadata(@Opt final Info info, @Opt final Graphics graphics, @Opt final Ai ai) {
        if (info != null) {
            this.info = info;
        }
        else {
            this.info = new Info(null, null);
        }
        if (graphics != null) {
            this.graphics = graphics;
        }
        else {
            this.graphics = new Graphics(null, null);
        }
        if (ai != null) {
            this.ai = ai;
        }
        else {
            this.ai = new Ai(null, null, null);
        }
    }
    
    public Info info() {
        return this.info;
    }
    
    public Graphics graphics() {
        return this.graphics;
    }
    
    public Ai ai() {
        return this.ai;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(metadata\n");
        if (this.info != null) {
            sb.append(this.info.toString());
        }
        if (this.graphics != null) {
            sb.append(this.graphics.toString());
        }
        if (this.ai != null) {
            sb.append(this.ai.toString());
        }
        sb.append(")\n");
        return sb.toString();
    }
}
