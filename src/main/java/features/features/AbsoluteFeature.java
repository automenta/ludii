// 
// Decompiled by Procyon v0.5.36
// 

package features.features;

import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.patterns.Pattern;
import gnu.trove.list.array.TFloatArrayList;

public class AbsoluteFeature extends Feature
{
    protected final int toPosition;
    protected final int fromPosition;
    protected final int lastToPosition;
    protected final int lastFromPosition;
    
    public AbsoluteFeature(final Pattern pattern, final int toPosition, final int fromPosition) {
        this.pattern = pattern;
        this.toPosition = toPosition;
        this.fromPosition = fromPosition;
        this.lastToPosition = -1;
        this.lastFromPosition = -1;
    }
    
    public AbsoluteFeature(final AbsoluteFeature other) {
        this.pattern = new Pattern(other.pattern);
        this.toPosition = other.toPosition;
        this.fromPosition = other.fromPosition;
        this.lastToPosition = other.lastToPosition;
        this.lastFromPosition = other.lastFromPosition;
        this.comment = other.comment;
    }
    
    public AbsoluteFeature(final String string) {
        final String[] parts = string.split(":");
        int toPos = -1;
        int fromPos = -1;
        int lastToPos = -1;
        int lastFromPos = -1;
        for (String part : parts) {
            if (part.startsWith("last_to=<")) {
                part = part.substring("last_to=<".length(), part.length() - ">".length());
                lastToPos = Integer.parseInt(part);
            }
            else if (part.startsWith("last_from=<")) {
                part = part.substring("last_from=<".length(), part.length() - ">".length());
                lastFromPos = Integer.parseInt(part);
            }
            else if (part.startsWith("to=<")) {
                part = part.substring("to=<".length(), part.length() - ">".length());
                toPos = Integer.parseInt(part);
            }
            else if (part.startsWith("from=<")) {
                part = part.substring("from=<".length(), part.length() - ">".length());
                fromPos = Integer.parseInt(part);
            }
            else if (part.startsWith("pat=<")) {
                part = part.substring("pat=<".length(), part.length() - ">".length());
                this.pattern = new Pattern(part);
            }
            else if (part.startsWith("comment=\"")) {
                part = part.substring("comment=\"".length(), part.length() - "\"".length());
                this.comment = part;
            }
        }
        this.toPosition = toPos;
        this.fromPosition = fromPos;
        this.lastToPosition = lastToPos;
        this.lastFromPosition = lastFromPos;
    }
    
    public int toPosition() {
        return this.toPosition;
    }
    
    public int fromPosition() {
        return this.fromPosition;
    }
    
    public int lastToPosition() {
        return this.lastToPosition;
    }
    
    public int lastFromPosition() {
        return this.lastFromPosition;
    }
    
    @Override
    public Feature rotatedCopy(final float rotation) {
        final AbsoluteFeature copy = new AbsoluteFeature(this);
        for (final FeatureElement element : copy.pattern().featureElements()) {
            if (element instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                if (rel.walk().steps().size() <= 0) {
                    continue;
                }
                rel.walk().steps().setQuick(0, rel.walk().steps().getQuick(0) + rotation);
            }
        }
        return copy;
    }
    
    @Override
    public Feature reflectedCopy() {
        final AbsoluteFeature copy = new AbsoluteFeature(this);
        for (final FeatureElement element : copy.pattern().featureElements()) {
            if (element instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                final TFloatArrayList steps = rel.walk().steps();
                for (int i = 0; i < steps.size(); ++i) {
                    steps.setQuick(i, steps.getQuick(i) * -1.0f);
                }
            }
        }
        return copy;
    }
    
    @Override
    public boolean generalises(final Feature other) {
        if (!(other instanceof AbsoluteFeature)) {
            return false;
        }
        final AbsoluteFeature otherFeature = (AbsoluteFeature)other;
        return this.toPosition == otherFeature.toPosition && this.fromPosition == otherFeature.fromPosition && this.lastToPosition == otherFeature.lastToPosition && this.lastFromPosition == otherFeature.lastFromPosition && this.pattern.generalises(otherFeature.pattern);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.fromPosition;
        result = 31 * result + this.toPosition;
        result = 31 * result + this.lastFromPosition;
        result = 31 * result + this.lastToPosition;
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!super.equals(other)) {
            return false;
        }
        if (!(other instanceof AbsoluteFeature)) {
            return false;
        }
        final AbsoluteFeature otherFeature = (AbsoluteFeature)other;
        return this.toPosition == otherFeature.toPosition && this.fromPosition == otherFeature.fromPosition && this.lastToPosition == otherFeature.lastToPosition && this.lastFromPosition == otherFeature.lastFromPosition;
    }
    
    @Override
    public boolean equalsIgnoreRotRef(final Feature other) {
        if (!super.equalsIgnoreRotRef(other)) {
            return false;
        }
        if (!(other instanceof AbsoluteFeature)) {
            return false;
        }
        final AbsoluteFeature otherFeature = (AbsoluteFeature)other;
        return this.toPosition == otherFeature.toPosition && this.fromPosition == otherFeature.fromPosition && this.lastToPosition == otherFeature.lastToPosition && this.lastFromPosition == otherFeature.lastFromPosition;
    }
    
    @Override
    public int hashCodeIgnoreRotRef() {
        final int prime = 31;
        int result = super.hashCodeIgnoreRotRef();
        result = 31 * result + this.fromPosition;
        result = 31 * result + this.toPosition;
        result = 31 * result + this.lastFromPosition;
        result = 31 * result + this.lastToPosition;
        return result;
    }
    
    @Override
    public String toString() {
        String str = String.format("pat=<%s>", this.pattern);
        if (this.toPosition != -1) {
            str = String.format("to=<%s>:%s", this.toPosition, str);
        }
        if (this.fromPosition != -1) {
            str = String.format("from=<%s>:%s", this.fromPosition, str);
        }
        if (this.lastToPosition != -1) {
            str = String.format("last_to=<%s>:%s", this.lastToPosition, str);
        }
        if (this.lastFromPosition != -1) {
            str = String.format("last_from=<%s>:%s", this.lastFromPosition, str);
        }
        if (!this.comment.isEmpty()) {
            str = String.format("%s:comment=\"%s\"", str, this.comment);
        }
        return "abs:" + str;
    }
}
