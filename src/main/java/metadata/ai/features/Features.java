// 
// Decompiled by Procyon v0.5.36
// 

package metadata.ai.features;

import annotations.Opt;
import metadata.ai.AIItem;

public class Features implements AIItem
{
    protected final FeatureSet[] featureSets;
    
    public Features(@Opt final FeatureSet featureSet) {
        if (featureSet == null) {
            this.featureSets = new FeatureSet[0];
        }
        else {
            this.featureSets = new FeatureSet[] { featureSet };
        }
    }
    
    public Features(@Opt final FeatureSet[] featureSets) {
        if (featureSets == null) {
            this.featureSets = new FeatureSet[0];
        }
        else {
            this.featureSets = featureSets;
        }
    }
    
    public FeatureSet[] featureSets() {
        return this.featureSets;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(features {\n");
        for (final FeatureSet featureSet : this.featureSets) {
            sb.append(featureSet.toString());
        }
        sb.append("})\n");
        return sb.toString();
    }
    
    public String toStringThresholded(final float threshold) {
        final StringBuilder sb = new StringBuilder();
        sb.append("(features {\n");
        for (final FeatureSet featureSet : this.featureSets) {
            sb.append(featureSet.toStringThresholded(threshold));
        }
        sb.append("})\n");
        return sb.toString();
    }
}
