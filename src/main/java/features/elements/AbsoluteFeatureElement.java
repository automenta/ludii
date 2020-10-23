// 
// Decompiled by Procyon v0.5.36
// 

package features.elements;

public class AbsoluteFeatureElement extends FeatureElement
{
    protected ElementType type;
    protected boolean not;
    protected int position;
    protected final int itemIndex;
    
    public AbsoluteFeatureElement(final ElementType type, final int position) {
        this.type = null;
        this.not = false;
        this.type = type;
        this.position = position;
        this.itemIndex = -1;
    }
    
    public AbsoluteFeatureElement(final ElementType type, final boolean not, final int position) {
        this.type = null;
        this.not = false;
        this.type = type;
        this.not = not;
        this.position = position;
        this.itemIndex = -1;
    }
    
    public AbsoluteFeatureElement(final ElementType type, final int position, final int itemIndex) {
        this.type = null;
        this.not = false;
        this.type = type;
        this.position = position;
        this.itemIndex = itemIndex;
    }
    
    public AbsoluteFeatureElement(final ElementType type, final boolean not, final int position, final int itemIndex) {
        this.type = null;
        this.not = false;
        this.type = type;
        this.not = not;
        this.position = position;
        this.itemIndex = itemIndex;
    }
    
    public AbsoluteFeatureElement(final AbsoluteFeatureElement other) {
        this.type = null;
        this.not = false;
        this.type = other.type;
        this.not = other.not;
        this.position = other.position;
        this.itemIndex = other.itemIndex;
    }
    
    public AbsoluteFeatureElement(final String string) {
        this.type = null;
        this.not = false;
        final int startPosStringIdx = string.indexOf("{");
        String typeString = string.substring(0, startPosStringIdx);
        String posString = string.substring(startPosStringIdx);
        if (typeString.startsWith("!")) {
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
        posString = posString.substring("{abs-".length(), posString.length() - "}".length());
        this.position = Integer.parseInt(posString);
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
    
    public int position() {
        return this.position;
    }
    
    @Override
    public int itemIndex() {
        return this.itemIndex;
    }
    
    @Override
    public boolean isAbsolute() {
        return true;
    }
    
    @Override
    public boolean isRelative() {
        return false;
    }
    
    @Override
    public boolean generalises(final FeatureElement other) {
        if (other instanceof RelativeFeatureElement) {
            return false;
        }
        final AbsoluteFeatureElement otherElement = (AbsoluteFeatureElement)other;
        return this.position == otherElement.position && FeatureElement.testTypeGeneralisation(this.type, this.not, otherElement.type, otherElement.not).strictlyGeneralises;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.itemIndex;
        result = 31 * result + (this.not ? 1231 : 1237);
        result = 31 * result + this.position;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AbsoluteFeatureElement)) {
            return false;
        }
        final AbsoluteFeatureElement otherElement = (AbsoluteFeatureElement)other;
        return this.type == otherElement.type && this.not == otherElement.not && this.position == otherElement.position && this.itemIndex == otherElement.itemIndex;
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
        str += String.format("{abs-%s}", this.position);
        return str;
    }
}
