// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.features;

import game.types.play.RoleType;
import main.StringRoutines;
import metadata.ai.AIItem;
import metadata.ai.misc.Pair;

public class FeatureSet implements AIItem
{
    protected final RoleType role;
    protected final String[] featureStrings;
    protected final float[] featureWeights;
    
    public FeatureSet(final RoleType role, final Pair[] features) {
        this.role = role;
        this.featureStrings = new String[features.length];
        this.featureWeights = new float[features.length];
        for (int i = 0; i < features.length; ++i) {
            this.featureStrings[i] = features[i].key();
            this.featureWeights[i] = features[i].floatVal();
        }
    }
    
    public RoleType role() {
        return this.role;
    }
    
    public String[] featureStrings() {
        return this.featureStrings;
    }
    
    public float[] featureWeights() {
        return this.featureWeights;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (featureSet ").append(this.role).append(" {\n");
        for (int i = 0; i < this.featureStrings.length; ++i) {
            sb.append("        (pair ");
            sb.append(StringRoutines.quote(this.featureStrings[i].trim())).append(" ");
            sb.append(this.featureWeights[i]);
            sb.append(")\n");
        }
        sb.append("    })\n");
        return sb.toString();
    }
    
    public String toStringThresholded(final float threshold) {
        final StringBuilder sb = new StringBuilder();
        sb.append("    (featureSet ").append(this.role).append(" {\n");
        for (int i = 0; i < this.featureStrings.length; ++i) {
            if (Math.abs(this.featureWeights[i]) >= threshold) {
                sb.append("        (pair ");
                sb.append(StringRoutines.quote(this.featureStrings[i].trim())).append(" ");
                sb.append(this.featureWeights[i]);
                sb.append(")\n");
            }
        }
        sb.append("    })\n");
        return sb.toString();
    }
}
