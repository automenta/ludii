// 
// Decompiled by Procyon v0.5.36
// 

package features.patterns;

import features.Walk;
import features.elements.AbsoluteFeatureElement;
import features.elements.FeatureElement;
import features.elements.RelativeFeatureElement;
import features.graph_search.Path;
import gnu.trove.list.array.TFloatArrayList;

import java.util.ArrayList;
import java.util.List;

public class Pattern
{
    protected List<FeatureElement> featureElements;
    protected TFloatArrayList allowedRotations;
    protected boolean allowsReflection;
    protected boolean matchMoverDirection;
    
    public Pattern() {
        this.allowedRotations = null;
        this.allowsReflection = true;
        this.matchMoverDirection = false;
        this.featureElements = new ArrayList<>();
    }
    
    public Pattern(final FeatureElement... elements) {
        this.allowedRotations = null;
        this.allowsReflection = true;
        this.matchMoverDirection = false;
        this.featureElements = new ArrayList<>(elements.length);
        for (final FeatureElement element : elements) {
            this.featureElements.add(element);
        }
    }
    
    public Pattern(final List<FeatureElement> elements) {
        this.allowedRotations = null;
        this.allowsReflection = true;
        this.matchMoverDirection = false;
        this.featureElements = new ArrayList<>(elements);
    }
    
    public Pattern(final Pattern other) {
        this.allowedRotations = null;
        this.allowsReflection = true;
        this.matchMoverDirection = false;
        this.featureElements = new ArrayList<>(other.featureElements().size());
        for (final FeatureElement element : other.featureElements()) {
            this.featureElements.add(FeatureElement.copy(element));
        }
        this.allowedRotations = other.allowedRotations;
    }
    
    public Pattern(final String string) {
        this.allowedRotations = null;
        this.allowsReflection = true;
        this.matchMoverDirection = false;
        int currIdx = 0;
        while (currIdx < string.length()) {
            if (string.startsWith("refl=true,", currIdx)) {
                this.allowsReflection = true;
                currIdx += "refl=true,".length();
            }
            else if (string.startsWith("refl=false,", currIdx)) {
                this.allowsReflection = false;
                currIdx += "refl=false,".length();
            }
            else if (string.startsWith("rots=", currIdx)) {
                if (string.startsWith("rots=all,", currIdx)) {
                    this.allowedRotations = null;
                    currIdx += "rots=all,".length();
                }
                else {
                    final int rotsListEnd = string.indexOf("]", currIdx);
                    String rotsListSubstring = string.substring(currIdx, rotsListEnd + 2);
                    currIdx += rotsListSubstring.length();
                    rotsListSubstring = rotsListSubstring.substring("rots=[".length(), rotsListSubstring.length() - "],".length());
                    final String[] rotElements = rotsListSubstring.split(",");
                    this.allowedRotations = new TFloatArrayList(rotElements.length);
                    for (final String rotElement : rotElements) {
                        this.allowedRotations.add(Float.parseFloat(rotElement));
                    }
                }
            }
            else if (string.startsWith("els=", currIdx)) {
                final int elsListEnd = string.indexOf("]", currIdx);
                String elsListSubstring = string.substring(currIdx, elsListEnd + 1);
                currIdx += elsListSubstring.length();
                elsListSubstring = elsListSubstring.substring("els=[".length(), elsListSubstring.length() - "]".length());
                final List<String> elements = new ArrayList<>();
                int elsIdx = 0;
                String elementString = "";
                while (elsIdx < elsListSubstring.length()) {
                    final char nextChar = elsListSubstring.charAt(elsIdx);
                    elementString += Character.toString(nextChar);
                    if (nextChar == '}') {
                        elements.add(elementString.trim());
                        elementString = "";
                        elsIdx += 2;
                    }
                    else {
                        ++elsIdx;
                    }
                }
                this.featureElements = new ArrayList<>(elements.size());
                for (final String element : elements) {
                    this.featureElements.add(FeatureElement.fromString(element));
                }
            }
            else {
                System.err.println("Error in Pattern(String) constructor: don't know how to handle: " + string.substring(currIdx));
            }
        }
    }
    
    public static List<Pattern> deduplicate(final List<Pattern> patterns) {
        final List<Pattern> newPatterns = new ArrayList<>(patterns.size());
        for (final Pattern pattern : patterns) {
            boolean shouldAdd = true;
            int i = 0;
            while (i < newPatterns.size()) {
                final Pattern otherPattern = newPatterns.get(i);
                if (pattern.equals(otherPattern)) {
                    shouldAdd = false;
                    break;
                }
                if (pattern.generalises(otherPattern)) {
                    newPatterns.remove(i);
                }
                else {
                    ++i;
                }
            }
            if (shouldAdd) {
                newPatterns.add(pattern);
            }
        }
        return newPatterns;
    }
    
    public static Pattern merge(final Pattern p1, final Pattern p2) {
        final List<FeatureElement> mergedElements = new ArrayList<>();
        for (final FeatureElement element : p1.featureElements) {
            mergedElements.add(FeatureElement.copy(element));
        }
        for (final FeatureElement element : p2.featureElements) {
            mergedElements.add(FeatureElement.copy(element));
        }
        final Pattern merged = new Pattern(mergedElements);
        return merged.allowRotations(p1.allowedRotations).allowRotations(p2.allowedRotations);
    }
    
    public void addElement(final FeatureElement newElement) {
        this.featureElements.add(newElement);
    }
    
    public List<FeatureElement> featureElements() {
        return this.featureElements;
    }
    
    public void prependStep(final int direction) {
        for (final FeatureElement element : this.featureElements) {
            if (element instanceof RelativeFeatureElement) {
                ((RelativeFeatureElement)element).walk().prependStep((float)direction);
            }
            else {
                System.err.println("Warning: trying to prepend a step to an Absolute Feature Element!");
            }
        }
    }
    
    public void prependWalk(final Walk walk) {
        for (final FeatureElement featureElement : this.featureElements) {
            if (featureElement instanceof RelativeFeatureElement) {
                final RelativeFeatureElement relativeFeatureEl = (RelativeFeatureElement)featureElement;
                relativeFeatureEl.walk().prependWalk(walk);
            }
        }
    }
    
    public void prependWalkWithCorrection(final Walk walk, final Path path, final float rotToRevert, final int refToRevert) {
        for (final FeatureElement featureElement : this.featureElements) {
            if (featureElement instanceof RelativeFeatureElement) {
                final RelativeFeatureElement relativeFeatureEl = (RelativeFeatureElement)featureElement;
                relativeFeatureEl.walk().prependWalkWithCorrection(walk, path, rotToRevert, refToRevert);
            }
        }
    }
    
    public Pattern allowRotations(final TFloatArrayList allowed) {
        if (this.allowedRotations == null) {
            this.allowedRotations = allowed;
        }
        else {
            this.allowedRotations.retainAll(allowed);
        }
        return this;
    }
    
    public Pattern allowReflection(final boolean flag) {
        this.allowsReflection = flag;
        return this;
    }
    
    public Pattern matchMoverDirection() {
        this.matchMoverDirection = true;
        return this;
    }
    
    public TFloatArrayList allowedRotations() {
        return this.allowedRotations;
    }
    
    public boolean allowsReflection() {
        return this.allowsReflection;
    }
    
    public boolean matchesMoverDirection() {
        return this.matchMoverDirection;
    }
    
    public void setAllowedRotations(final TFloatArrayList allowedRotations) {
        this.allowedRotations = allowedRotations;
    }
    
    public void applyReflection(final int reflection) {
        if (reflection == 1) {
            return;
        }
        for (final FeatureElement element : this.featureElements) {
            if (element instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                final TFloatArrayList steps = rel.walk().steps();
                for (int i = 0; i < steps.size(); ++i) {
                    steps.setQuick(i, steps.getQuick(i) * reflection);
                }
            }
        }
    }
    
    public void applyRotation(final float rotation) {
        for (final FeatureElement element : this.featureElements) {
            if (element instanceof RelativeFeatureElement) {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                final TFloatArrayList steps = rel.walk().steps();
                if (steps.size() <= 0) {
                    continue;
                }
                steps.setQuick(0, steps.getQuick(0) + rotation);
            }
        }
    }
    
    public boolean isConsistent() {
        final List<AbsoluteFeatureElement> checkedAbsolutes = new ArrayList<>();
        final List<RelativeFeatureElement> checkedRelatives = new ArrayList<>();
        for (final FeatureElement element : this.featureElements) {
            if (element instanceof AbsoluteFeatureElement) {
                final AbsoluteFeatureElement abs = (AbsoluteFeatureElement)element;
                for (final AbsoluteFeatureElement other : checkedAbsolutes) {
                    if (abs.position() == other.position() && !abs.equals(other) && !abs.isCompatibleWith(other) && !abs.generalises(other) && !other.generalises(abs)) {
                        return false;
                    }
                }
                checkedAbsolutes.add(abs);
            }
            else {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                for (final RelativeFeatureElement other2 : checkedRelatives) {
                    if (rel.walk().equals(other2.walk()) && !rel.equals(other2) && !rel.isCompatibleWith(other2) && !rel.generalises(other2) && !other2.generalises(rel)) {
                        return false;
                    }
                }
                checkedRelatives.add(rel);
            }
        }
        return true;
    }
    
    public boolean generalises(final Pattern other) {
        boolean foundStrictGeneralisation = false;
        for (final FeatureElement featureElement : this.featureElements()) {
            boolean foundGeneralisation = false;
            for (final FeatureElement otherElement : other.featureElements()) {
                if (featureElement.generalises(otherElement)) {
                    foundStrictGeneralisation = true;
                    foundGeneralisation = true;
                    break;
                }
                if (featureElement.equals(otherElement)) {
                    foundGeneralisation = true;
                    break;
                }
            }
            if (!foundGeneralisation) {
                return false;
            }
        }
        if (other.allowedRotations == null) {
            if (this.allowedRotations != null) {
                return false;
            }
        }
        else if (this.allowedRotations == null) {
            foundStrictGeneralisation = true;
        }
        else {
            for (int i = 0; i < other.allowedRotations().size(); ++i) {
                final float allowedRotation = other.allowedRotations().getQuick(i);
                if (!this.allowedRotations.contains(allowedRotation)) {
                    return false;
                }
            }
            foundStrictGeneralisation = (this.allowedRotations.size() > other.allowedRotations().size());
        }
        return foundStrictGeneralisation;
    }
    
    public void removeRedundancies() {
        final List<FeatureElement> newFeatureElements = new ArrayList<>(this.featureElements.size());
        for (final FeatureElement element : this.featureElements) {
            boolean shouldAdd = true;
            if (element instanceof AbsoluteFeatureElement) {
                final AbsoluteFeatureElement abs = (AbsoluteFeatureElement)element;
                for (int i = 0; i < newFeatureElements.size(); ++i) {
                    final FeatureElement alreadyAdded = newFeatureElements.get(i);
                    if (alreadyAdded instanceof AbsoluteFeatureElement) {
                        final AbsoluteFeatureElement other = (AbsoluteFeatureElement)alreadyAdded;
                        if (abs.position() == other.position()) {
                            if (abs.equals(other)) {
                                shouldAdd = false;
                                break;
                            }
                            if (abs.generalises(other)) {
                                shouldAdd = false;
                                break;
                            }
                            if (other.generalises(abs)) {
                                newFeatureElements.set(i, abs);
                                shouldAdd = false;
                                break;
                            }
                        }
                    }
                }
            }
            else {
                final RelativeFeatureElement rel = (RelativeFeatureElement)element;
                for (int i = 0; i < newFeatureElements.size(); ++i) {
                    final FeatureElement alreadyAdded = newFeatureElements.get(i);
                    if (alreadyAdded instanceof RelativeFeatureElement) {
                        final RelativeFeatureElement other2 = (RelativeFeatureElement)alreadyAdded;
                        if (rel.walk().equals(other2.walk())) {
                            if (rel.equals(other2)) {
                                shouldAdd = false;
                                break;
                            }
                            if (rel.generalises(other2)) {
                                shouldAdd = false;
                                break;
                            }
                            if (other2.generalises(rel)) {
                                newFeatureElements.set(i, rel);
                                shouldAdd = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (shouldAdd) {
                newFeatureElements.add(element);
            }
        }
        this.featureElements = newFeatureElements;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        if (this.allowedRotations == null) {
            result *= 31;
        }
        else {
            int allowedRotsHash = 0;
            for (int i = 0; i < this.allowedRotations.size(); ++i) {
                allowedRotsHash ^= 41 * Float.floatToIntBits(this.allowedRotations.getQuick(i));
            }
            result = 31 * result + (31 + allowedRotsHash);
        }
        result = 31 * result + (this.allowsReflection ? 1231 : 1237);
        if (this.featureElements == null) {
            result *= 31;
        }
        else {
            int featureElementsHash = 0;
            for (final FeatureElement element : this.featureElements) {
                featureElementsHash ^= 37 * element.hashCode();
            }
            result = 31 * result + (31 + featureElementsHash);
        }
        result = 31 * result + (this.matchMoverDirection ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Pattern)) {
            return false;
        }
        final Pattern otherPattern = (Pattern)other;
        if (this.featureElements.size() != otherPattern.featureElements.size()) {
            return false;
        }
        for (final FeatureElement element : this.featureElements) {
            if (!otherPattern.featureElements.contains(element)) {
                return false;
            }
        }
        for (final FeatureElement element : otherPattern.featureElements()) {
            if (!this.featureElements.contains(element)) {
                return false;
            }
        }
        if (otherPattern.allowedRotations == null) {
            return this.allowedRotations == null;
        }
        if (this.allowedRotations == null) {
            return false;
        }
        if (this.allowedRotations.size() != otherPattern.allowedRotations.size()) {
            return false;
        }
        for (int i = 0; i < otherPattern.allowedRotations().size(); ++i) {
            if (!this.allowedRotations.contains(otherPattern.allowedRotations().getQuick(i))) {
                return false;
            }
        }
        return this.allowsReflection == otherPattern.allowsReflection;
    }
    
    public boolean equalsIgnoreRotRef(final Pattern other) {
        if (this.featureElements.size() != other.featureElements.size()) {
            return false;
        }
        for (final FeatureElement element : this.featureElements) {
            if (!other.featureElements.contains(element)) {
                return false;
            }
        }
        for (final FeatureElement element : other.featureElements()) {
            if (!this.featureElements.contains(element)) {
                return false;
            }
        }
        return this.allowsReflection == other.allowsReflection;
    }
    
    public int hashCodeIgnoreRotRef() {
        final int prime = 31;
        int result = 1;
        if (this.featureElements == null) {
            result *= 31;
        }
        else {
            int featureElementsHash = 0;
            for (final FeatureElement element : this.featureElements) {
                featureElementsHash ^= element.hashCode();
            }
            result = 31 * result + (31 + featureElementsHash);
        }
        result = 31 * result + (this.matchMoverDirection ? 1231 : 1237);
        return result;
    }
    
    @Override
    public String toString() {
        String str = "";
        if (this.allowsReflection) {
            str += "refl=true,";
        }
        else {
            str += "refl=false,";
        }
        String rotsStr;
        if (this.allowedRotations != null) {
            rotsStr = "[";
            for (int i = 0; i < this.allowedRotations.size(); ++i) {
                rotsStr += this.allowedRotations.getQuick(i);
                if (i < this.allowedRotations.size() - 1) {
                    rotsStr += ",";
                }
            }
            rotsStr += "]";
        }
        else {
            rotsStr = "all";
        }
        str += String.format("rots=%s,", rotsStr);
        str += String.format("els=%s", this.featureElements);
        return str;
    }
}
