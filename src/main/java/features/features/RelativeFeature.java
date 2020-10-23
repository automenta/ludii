// 
// Decompiled by Procyon v0.5.36
// 

package features.features;

import features.Walk;
import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.patterns.Pattern;
import gnu.trove.list.array.TFloatArrayList;

import java.util.Objects;

public class RelativeFeature extends Feature
{
    protected final Walk toPosition;
    protected final Walk fromPosition;
    protected final Walk lastToPosition;
    protected final Walk lastFromPosition;
    
    public RelativeFeature(final Pattern pattern, final Walk toPosition, final Walk fromPosition) {
        this.pattern = pattern;
        this.toPosition = toPosition;
        this.fromPosition = fromPosition;
        this.lastToPosition = null;
        this.lastFromPosition = null;
    }
    
    public RelativeFeature(final Pattern pattern, final Walk toPosition, final Walk fromPosition, final Walk lastToPosition, final Walk lastFromPosition) {
        this.pattern = pattern;
        this.toPosition = toPosition;
        this.fromPosition = fromPosition;
        this.lastToPosition = lastToPosition;
        this.lastFromPosition = lastFromPosition;
    }
    
    public RelativeFeature(final RelativeFeature other) {
        this.pattern = new Pattern(other.pattern);
        this.toPosition = ((other.toPosition == null) ? null : new Walk(other.toPosition));
        this.fromPosition = ((other.fromPosition == null) ? null : new Walk(other.fromPosition));
        this.lastToPosition = ((other.lastToPosition == null) ? null : new Walk(other.lastToPosition));
        this.lastFromPosition = ((other.lastFromPosition == null) ? null : new Walk(other.lastFromPosition));
        this.comment = other.comment;
    }
    
    public RelativeFeature(final String string) {
        final String[] parts = string.split(":");
        Walk toPos = null;
        Walk fromPos = null;
        Walk lastToPos = null;
        Walk lastFromPos = null;
        for (String part : parts) {
            if (part.startsWith("last_to=<")) {
                part = part.substring("last_to=<".length(), part.length() - ">".length());
                lastToPos = new Walk(part);
            }
            else if (part.startsWith("last_from=<")) {
                part = part.substring("last_from=<".length(), part.length() - ">".length());
                lastFromPos = new Walk(part);
            }
            else if (part.startsWith("to=<")) {
                part = part.substring("to=<".length(), part.length() - ">".length());
                toPos = new Walk(part);
            }
            else if (part.startsWith("from=<")) {
                part = part.substring("from=<".length(), part.length() - ">".length());
                fromPos = new Walk(part);
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
    
    public Walk toPosition() {
        return this.toPosition;
    }
    
    public Walk fromPosition() {
        return this.fromPosition;
    }
    
    public Walk lastToPosition() {
        return this.lastToPosition;
    }
    
    public Walk lastFromPosition() {
        return this.lastFromPosition;
    }
    
    @Override
    public Feature rotatedCopy(final float rotation) {
        final RelativeFeature copy = new RelativeFeature(this);
        if (copy.toPosition != null && !copy.toPosition().steps().isEmpty()) {
            copy.toPosition().steps().setQuick(0, copy.toPosition().steps().getQuick(0) + rotation);
        }
        if (copy.fromPosition != null && !copy.fromPosition().steps().isEmpty()) {
            copy.fromPosition().steps().setQuick(0, copy.fromPosition().steps().getQuick(0) + rotation);
        }
        if (copy.lastToPosition != null && !copy.lastToPosition().steps().isEmpty()) {
            copy.lastToPosition().steps().setQuick(0, copy.lastToPosition().steps().getQuick(0) + rotation);
        }
        if (copy.lastFromPosition != null && !copy.lastFromPosition().steps().isEmpty()) {
            copy.lastFromPosition().steps().setQuick(0, copy.lastFromPosition().steps().getQuick(0) + rotation);
        }
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
        final RelativeFeature copy = new RelativeFeature(this);
        if (copy.toPosition != null) {
            final TFloatArrayList steps = copy.toPosition.steps();
            for (int i = 0; i < steps.size(); ++i) {
                steps.setQuick(i, steps.getQuick(i) * -1.0f);
            }
        }
        if (copy.fromPosition != null) {
            final TFloatArrayList steps = copy.fromPosition.steps();
            for (int i = 1; i < steps.size(); ++i) {
                steps.setQuick(i, steps.getQuick(i) * -1.0f);
            }
        }
        if (copy.lastToPosition != null) {
            final TFloatArrayList steps = copy.lastToPosition.steps();
            for (int i = 1; i < steps.size(); ++i) {
                steps.setQuick(i, steps.getQuick(i) * -1.0f);
            }
        }
        if (copy.lastFromPosition != null) {
            final TFloatArrayList steps = copy.lastFromPosition.steps();
            for (int i = 1; i < steps.size(); ++i) {
                steps.setQuick(i, steps.getQuick(i) * -1.0f);
            }
        }
        for (final FeatureElement element : copy.pattern().featureElements()) {
            if (element instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                final TFloatArrayList steps2 = rel.walk().steps();
                for (int j = 1; j < steps2.size(); ++j) {
                    steps2.setQuick(j, steps2.getQuick(j) * -1.0f);
                }
            }
        }
        return copy;
    }
    
    @Override
    public boolean generalises(final Feature other) {
        if (!(other instanceof RelativeFeature)) {
            return false;
        }
        final RelativeFeature otherFeature = (RelativeFeature)other;
        boolean foundStrictGeneralization = false;
        if (this.toPosition != null) {
            if (!this.toPosition.equals(otherFeature.toPosition)) {
                return false;
            }
        }
        else if (otherFeature.toPosition == null) {
            return false;
        }
        if (this.fromPosition != null) {
            if (!this.fromPosition.equals(otherFeature.fromPosition)) {
                return false;
            }
        }
        else if (otherFeature.fromPosition == null) {
            return false;
        }
        if (this.lastToPosition != null) {
            if (!this.lastToPosition.equals(otherFeature.lastToPosition)) {
                return false;
            }
        }
        else if (otherFeature.lastToPosition == null) {
            return false;
        }
        if (this.lastFromPosition != null) {
            if (!this.lastFromPosition.equals(otherFeature.lastFromPosition)) {
                return false;
            }
        }
        else if (otherFeature.lastFromPosition == null) {
            return false;
        }
        if (this.pattern.generalises(otherFeature.pattern)) {
            foundStrictGeneralization = true;
        }
        else if (!this.pattern.equals(otherFeature.pattern)) {
            return false;
        }
        return foundStrictGeneralization;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.fromPosition == null) ? 0 : this.fromPosition.hashCode());
        result = 31 * result + ((this.toPosition == null) ? 0 : this.toPosition.hashCode());
        result = 31 * result + ((this.lastFromPosition == null) ? 0 : this.lastFromPosition.hashCode());
        result = 31 * result + ((this.lastToPosition == null) ? 0 : this.lastToPosition.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!super.equals(other)) {
            return false;
        }
        if (!(other instanceof RelativeFeature)) {
            return false;
        }
        final RelativeFeature otherFeature = (RelativeFeature)other;
        return (Objects.equals(this.toPosition, otherFeature.toPosition)) && (Objects.equals(this.fromPosition, otherFeature.fromPosition)) && (Objects.equals(this.lastToPosition, otherFeature.lastToPosition)) && (Objects.equals(this.lastFromPosition, otherFeature.lastFromPosition));
    }
    
    @Override
    public boolean equalsIgnoreRotRef(final Feature other) {
        if (!super.equalsIgnoreRotRef(other)) {
            return false;
        }
        if (!(other instanceof RelativeFeature)) {
            return false;
        }
        final RelativeFeature otherFeature = (RelativeFeature)other;
        return (Objects.equals(this.toPosition, otherFeature.toPosition)) && (Objects.equals(this.fromPosition, otherFeature.fromPosition)) && (Objects.equals(this.lastToPosition, otherFeature.lastToPosition)) && (Objects.equals(this.lastFromPosition, otherFeature.lastFromPosition));
    }
    
    @Override
    public int hashCodeIgnoreRotRef() {
        final int prime = 31;
        int result = super.hashCodeIgnoreRotRef();
        result = 31 * result + ((this.fromPosition == null) ? 0 : this.fromPosition.hashCode());
        result = 31 * result + ((this.toPosition == null) ? 0 : this.toPosition.hashCode());
        result = 31 * result + ((this.lastFromPosition == null) ? 0 : this.lastFromPosition.hashCode());
        result = 31 * result + ((this.lastToPosition == null) ? 0 : this.lastToPosition.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        String str = String.format("pat=<%s>", this.pattern);
        if (this.toPosition != null) {
            str = String.format("to=<%s>:%s", this.toPosition, str);
        }
        if (this.fromPosition != null) {
            str = String.format("from=<%s>:%s", this.fromPosition, str);
        }
        if (this.lastToPosition != null) {
            str = String.format("last_to=<%s>:%s", this.lastToPosition, str);
        }
        if (this.lastFromPosition != null) {
            str = String.format("last_from=<%s>:%s", this.lastFromPosition, str);
        }
        if (!this.comment.isEmpty()) {
            str = String.format("%s:comment=\"%s\"", str, this.comment);
        }
        return "rel:" + str;
    }
}
