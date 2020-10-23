// 
// Decompiled by Procyon v0.5.36
// 

package features.elements;

import features.Walk;

public class RelativeFeatureElement extends FeatureElement
{
    protected ElementType type;
    protected boolean not;
    protected Walk walk;
    protected final int itemIndex;
    
    public RelativeFeatureElement(final ElementType type, final Walk walk) {
        this(type, false, walk, -1);
    }
    
    public RelativeFeatureElement(final ElementType type, final boolean not, final Walk walk) {
        this(type, not, walk, -1);
    }
    
    public RelativeFeatureElement(final ElementType type, final Walk walk, final int itemIndex) {
        this(type, false, walk, itemIndex);
    }
    
    public RelativeFeatureElement(final ElementType type, final boolean not, final Walk walk, final int itemIndex) {
        this.type = null;
        this.not = false;
        this.type = type;
        this.not = not;
        this.walk = walk;
        this.itemIndex = itemIndex;
    }
    
    public RelativeFeatureElement(final RelativeFeatureElement other) {
        this.type = null;
        this.not = false;
        this.type = other.type;
        this.not = other.not;
        this.walk = new Walk(other.walk());
        this.itemIndex = other.itemIndex;
    }
    
    public RelativeFeatureElement(final String string) {
        this.type = null;
        this.not = false;
        final int startWalkStringIdx = string.indexOf('{');
        String typeString = string.substring(0, startWalkStringIdx);
        final String walkString = string.substring(startWalkStringIdx);
        if (!typeString.isEmpty() && typeString.charAt(0) == '!') {
            this.not = true;
            typeString = typeString.substring("!".length());
        }
        int iIdx = -1;
        final ElementType[] values = ElementType.values();
        final int length = values.length;
        int i = 0;
        while (i < length) {
            final ElementType elType = values[i];
            if (typeString.startsWith(elType.label)) {
                this.type = elType;
                if (typeString.length() > elType.label.length()) {
                    iIdx = Integer.parseInt(typeString.substring(elType.label.length()));
                    break;
                }
                break;
            }
            else {
                ++i;
            }
        }
        this.itemIndex = iIdx;
        this.walk = new Walk(walkString);
    }
    
    @Override
    public ElementType type() {
        return this.type;
    }
    
    @Override
    public void setType(final ElementType type) {
        this.type = type;
    }
    
    @Override
    public void negate() {
        this.not = true;
    }
    
    @Override
    public boolean not() {
        return this.not;
    }
    
    public Walk walk() {
        return this.walk;
    }
    
    @Override
    public int itemIndex() {
        return this.itemIndex;
    }
    
    @Override
    public boolean isAbsolute() {
        return false;
    }
    
    @Override
    public boolean isRelative() {
        return true;
    }
    
    @Override
    public boolean generalises(final FeatureElement other) {
        final TypeGeneralisationResult generalisationResult = FeatureElement.testTypeGeneralisation(this.type, this.not, other.type(), other.not());
        if (!generalisationResult.generalises) {
            return false;
        }
        if (other instanceof AbsoluteFeatureElement) {
            return this.walk.steps().isEmpty();
        }
        final RelativeFeatureElement otherRel = (RelativeFeatureElement)other;
        return generalisationResult.strictlyGeneralises && this.walk.equals(otherRel.walk());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.itemIndex;
        result = 31 * result + (this.not ? 1231 : 1237);
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        result = 31 * result + ((this.walk == null) ? 0 : this.walk.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof RelativeFeatureElement)) {
            return false;
        }
        final RelativeFeatureElement otherElement = (RelativeFeatureElement)other;
        return this.type == otherElement.type && this.not == otherElement.not && this.walk.equals(otherElement.walk()) && this.itemIndex == otherElement.itemIndex;
    }
    
    @Override
    public String toString() {
        String str = this.type().label;
        if (this.type == ElementType.Item || this.type == ElementType.IsPos || this.type == ElementType.Connectivity) {
            str += this.itemIndex;
        }
        if (this.not) {
            str = "!" + str;
        }
        str += this.walk;
        return str;
    }
}
