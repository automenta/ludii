// 
// Decompiled by Procyon v0.5.36
// 

package features.elements;

public abstract class FeatureElement
{
    public static FeatureElement copy(final FeatureElement other) {
        if (other instanceof AbsoluteFeatureElement) {
            return new AbsoluteFeatureElement((AbsoluteFeatureElement)other);
        }
        if (other instanceof RelativeFeatureElement) {
            return new RelativeFeatureElement((RelativeFeatureElement)other);
        }
        return null;
    }
    
    public static FeatureElement fromString(final String string) {
        if (string.contains("abs-")) {
            return new AbsoluteFeatureElement(string);
        }
        return new RelativeFeatureElement(string);
    }
    
    public abstract boolean generalises(final FeatureElement p0);
    
    public abstract ElementType type();
    
    public abstract void setType(final ElementType p0);
    
    public abstract void negate();
    
    public abstract boolean not();
    
    public abstract boolean isAbsolute();
    
    public abstract boolean isRelative();
    
    public abstract int itemIndex();
    
    public final boolean isCompatibleWith(final FeatureElement other) {
        final ElementType myType = this.type();
        final ElementType otherType = other.type();
        if (myType == otherType && this.itemIndex() == other.itemIndex()) {
            return this.not() == other.not();
        }
        if (!this.not() && !other.not()) {
            if (myType == ElementType.Empty) {
                return otherType == ElementType.Any || otherType == ElementType.IsPos || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.Friend) {
                return otherType == ElementType.Any || otherType == ElementType.P1 || otherType == ElementType.P2 || otherType == ElementType.Item || otherType == ElementType.IsPos || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.Enemy) {
                return otherType == ElementType.Any || otherType == ElementType.P1 || otherType == ElementType.P2 || otherType == ElementType.Item || otherType == ElementType.IsPos || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.Off) {
                return false;
            }
            if (myType == ElementType.Any) {
                return otherType != ElementType.Off;
            }
            if (myType == ElementType.P1) {
                return otherType == ElementType.Any || otherType == ElementType.Friend || otherType == ElementType.Enemy || otherType == ElementType.P2 || otherType == ElementType.Item || otherType == ElementType.IsPos || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.P2) {
                return otherType == ElementType.Any || otherType == ElementType.Friend || otherType == ElementType.Enemy || otherType == ElementType.P1 || otherType == ElementType.Item || otherType == ElementType.IsPos;
            }
            if (myType == ElementType.Item) {
                return otherType == ElementType.Any || otherType == ElementType.Friend || otherType == ElementType.Enemy || otherType == ElementType.P1 || otherType == ElementType.P2 || otherType == ElementType.IsPos || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.IsPos) {
                return otherType == ElementType.Any || otherType == ElementType.Friend || otherType == ElementType.Enemy || otherType == ElementType.P1 || otherType == ElementType.P2 || otherType == ElementType.Item || otherType == ElementType.Connectivity;
            }
            if (myType == ElementType.Connectivity) {
                return otherType != ElementType.Off;
            }
        }
        else if (this.not() && other.not()) {
            if (myType == ElementType.Empty) {
                return otherType != ElementType.Any;
            }
            if (myType == ElementType.Friend) {
                return true;
            }
            if (myType == ElementType.Enemy) {
                return true;
            }
            if (myType == ElementType.Off) {
                return otherType != ElementType.Any;
            }
            if (myType == ElementType.Any) {
                return otherType != ElementType.Off;
            }
            if (myType == ElementType.P1) {
                return true;
            }
            if (myType == ElementType.P2) {
                return true;
            }
            if (myType == ElementType.Item) {
                return true;
            }
            if (myType == ElementType.IsPos) {
                return true;
            }
            if (myType == ElementType.Connectivity) {
                return otherType != ElementType.Any;
            }
        }
        else {
            if (!this.not() || other.not()) {
                return other.isCompatibleWith(this);
            }
            if (myType == ElementType.Empty) {
                return true;
            }
            if (myType == ElementType.Friend) {
                return true;
            }
            if (myType == ElementType.Enemy) {
                return true;
            }
            if (myType == ElementType.Off) {
                return true;
            }
            if (myType == ElementType.Any) {
                return otherType == ElementType.Off;
            }
            if (myType == ElementType.P1) {
                return true;
            }
            if (myType == ElementType.P2) {
                return true;
            }
            if (myType == ElementType.Item) {
                return true;
            }
            if (myType == ElementType.IsPos) {
                return true;
            }
            if (myType == ElementType.Connectivity) {
                return otherType != ElementType.Off;
            }
        }
        System.err.println("Warning: FeatureElement.isCompatibleWith() returning default value!");
        return true;
    }
    
    public static TypeGeneralisationResult testTypeGeneralisation(final ElementType firstType, final boolean firstNot, final ElementType secondType, final boolean secondNot) {
        if (firstNot && !secondNot) {
            if (firstType == ElementType.Empty) {
                if (secondType == ElementType.Empty || secondType == ElementType.Off || secondType == ElementType.Item || secondType == ElementType.IsPos || secondType == ElementType.Connectivity) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Friend) {
                if (secondType != ElementType.Empty && secondType != ElementType.Enemy && secondType != ElementType.Off) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Enemy) {
                if (secondType != ElementType.Empty && secondType != ElementType.Friend && secondType != ElementType.Off) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Off) {
                if (secondType == ElementType.Off || secondType == ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Any) {
                if (secondType != ElementType.Off) {
                    return new TypeGeneralisationResult(false, false);
                }
            }
            else if (firstType == ElementType.P1) {
                if (secondType != ElementType.Empty && secondType != ElementType.Off && secondType != ElementType.P2) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.P2) {
                if (secondType != ElementType.Empty && secondType != ElementType.Off && secondType != ElementType.P1) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Item) {
                if (secondType != ElementType.Off) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else {
                if (firstType == ElementType.IsPos) {
                    return new TypeGeneralisationResult(false, false);
                }
                if (firstType == ElementType.Connectivity) {
                    return new TypeGeneralisationResult(false, false);
                }
            }
        }
        else if (!firstNot && secondNot) {
            if (firstType != ElementType.Off) {
                return new TypeGeneralisationResult(false, false);
            }
            if (secondType != ElementType.Any) {
                return new TypeGeneralisationResult(false, false);
            }
        }
        else if (!firstNot && !secondNot) {
            if (firstType == ElementType.Empty) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Friend) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Enemy) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Off) {
                if (secondType != ElementType.Empty) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Any) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
            }
            else if (firstType == ElementType.P1) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.P2) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Item) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.IsPos) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
            else if (firstType == ElementType.Connectivity) {
                if (secondType != ElementType.Any) {
                    return new TypeGeneralisationResult(false, false);
                }
                return new TypeGeneralisationResult(true, true);
            }
        }
        else if (firstType != secondType) {
            if (firstType != ElementType.Any) {
                return new TypeGeneralisationResult(false, false);
            }
            if (secondType != ElementType.Any) {
                return new TypeGeneralisationResult(true, true);
            }
        }
        return new TypeGeneralisationResult(true, false);
    }
    
    public enum ElementType
    {
        Empty("Empty", "-"), 
        Friend("Friend", "f"), 
        Enemy("Enemy", "e"), 
        Off("Off", "#"), 
        Any("Any", "*"), 
        P1("P1", "1"), 
        P2("P2", "2"), 
        Item("Item", "I"), 
        IsPos("IsPos", "pos"), 
        Connectivity("Connectivity", "N"), 
        LastFrom("LastFrom", "last_from"), 
        LastTo("LastTo", "last_to");
        
        public String name;
        public String label;
        
        ElementType(final String name, final String label) {
            this.name = name;
            this.label = label;
        }
    }
    
    public static class TypeGeneralisationResult
    {
        public boolean generalises;
        public boolean strictlyGeneralises;
        
        public TypeGeneralisationResult(final boolean generalises, final boolean strictlyGeneralises) {
            this.generalises = generalises;
            this.strictlyGeneralises = strictlyGeneralises;
        }
    }
}
