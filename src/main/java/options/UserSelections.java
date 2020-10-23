/*
 * Decompiled with CFR 0.150.
 */
package options;

import java.util.ArrayList;
import java.util.List;

public class UserSelections {
    private List<String> selectedOptionStrings = new ArrayList<>();
    private int ruleset = -1;

    public UserSelections(List<String> selectedOptionStrings) {
        this.selectedOptionStrings = selectedOptionStrings;
    }

    public List<String> selectedOptionStrings() {
        return this.selectedOptionStrings;
    }

    public void setSelectOptionStrings(List<String> optionSelections) {
        this.selectedOptionStrings = optionSelections;
    }

    public int ruleset() {
        return this.ruleset;
    }

    public void setRuleset(int set) {
        this.ruleset = set;
    }
}

