// 
// Decompiled by Procyon v0.5.36
// 

package main.options;

import java.util.ArrayList;
import java.util.List;

public class UserSelections
{
    private List<String> selectedOptionStrings;
    private int ruleset;
    
    public UserSelections(final List<String> selectedOptionStrings) {
        this.selectedOptionStrings = new ArrayList<>();
        this.ruleset = -1;
        this.selectedOptionStrings = selectedOptionStrings;
    }
    
    public List<String> selectedOptionStrings() {
        return this.selectedOptionStrings;
    }
    
    public void setSelectOptionStrings(final List<String> optionSelections) {
        this.selectedOptionStrings = optionSelections;
    }
    
    public int ruleset() {
        return this.ruleset;
    }
    
    public void setRuleset(final int set) {
        this.ruleset = set;
    }
}
