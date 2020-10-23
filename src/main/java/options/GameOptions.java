/*
 * Decompiled with CFR 0.150.
 */
package options;

import exception.DuplicateOptionUseException;
import exception.UnusedOptionException;
import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOptions {
    public static final int MAX_OPTION_CATEGORIES = 10;
    private final List<OptionCategory> categories = new ArrayList<>();
    private boolean optionsLoaded = false;

    public List<OptionCategory> categories() {
        return Collections.unmodifiableList(this.categories);
    }

    public boolean optionsLoaded() {
        return this.optionsLoaded;
    }

    public void setOptionsLoaded(boolean set) {
        this.optionsLoaded = set;
    }

    public void setOptionCategories(List<Option>[] optionsAvList) {
        this.categories.clear();
        for (List<Option> options : optionsAvList) {
            this.categories.add(new OptionCategory(options));
        }
        this.optionsLoaded = true;
    }

    public void clear() {
        this.categories.clear();
        this.optionsLoaded = false;
    }

    public int numCategories() {
        return this.categories.size();
    }

    public void add(Option option) {
        for (OptionCategory category : this.categories) {
            if (!option.tag().equals(category.tag())) continue;
            category.add(option);
            return;
        }
        OptionCategory category = new OptionCategory(option);
        this.categories.add(category);
    }

    public void add(OptionCategory category) {
        this.categories.add(category);
    }

    public int[] computeOptionSelections(List<String> selectedOptionStrings) {
        int[] optionSelections = new int[this.numCategories()];
        boolean[] usedOptionStrings = new boolean[selectedOptionStrings.size()];
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            OptionCategory category = this.categories.get(cat);
            int maxPriority = Integer.MIN_VALUE;
            int activeOptionIdx = -1;
            for (int i = 0; i < category.options().size(); ++i) {
                Option option = category.options().get(i);
                String optionStr = StringRoutines.join("/", option.menuHeadings());
                int optionStrIndex = selectedOptionStrings.indexOf(optionStr);
                if (optionStrIndex >= 0) {
                    if (usedOptionStrings[optionStrIndex]) {
                        throw new DuplicateOptionUseException(optionStr);
                    }
                    usedOptionStrings[optionStrIndex] = true;
                    activeOptionIdx = i;
                    break;
                }
                if (option.priority() <= maxPriority) continue;
                activeOptionIdx = i;
                maxPriority = option.priority();
            }
            optionSelections[cat] = activeOptionIdx;
        }
        for (int i = 0; i < usedOptionStrings.length; ++i) {
            if (usedOptionStrings[i]) continue;
            throw new UnusedOptionException(selectedOptionStrings.get(i));
        }
        return optionSelections;
    }

    public List<String> allOptionStrings(List<String> selectedOptionStrings) {
        ArrayList<String> strings = new ArrayList<>();
        boolean[] usedOptionStrings = new boolean[selectedOptionStrings.size()];
        for (OptionCategory category : this.categories) {
            int maxPriority = Integer.MIN_VALUE;
            String activeOptionStr = null;
            for (int i = 0; i < category.options().size(); ++i) {
                Option option = category.options().get(i);
                String optionStr = StringRoutines.join("/", option.menuHeadings());
                int optionStrIndex = selectedOptionStrings.indexOf(optionStr);
                if (optionStrIndex >= 0) {
                    if (usedOptionStrings[optionStrIndex]) {
                        throw new DuplicateOptionUseException(optionStr);
                    }
                    usedOptionStrings[optionStrIndex] = true;
                    activeOptionStr = optionStr;
                    break;
                }
                if (option.priority() <= maxPriority) continue;
                activeOptionStr = optionStr;
                maxPriority = option.priority();
            }
            strings.add(activeOptionStr);
        }
        for (int i = 0; i < usedOptionStrings.length; ++i) {
            if (usedOptionStrings[i]) continue;
            throw new UnusedOptionException(selectedOptionStrings.get(i));
        }
        return strings;
    }

    public boolean optionExists(String optionString) {
        for (OptionCategory category : this.categories) {
            for (int i = 0; i < category.options().size(); ++i) {
                Option option = category.options().get(i);
                String optionStr = StringRoutines.join("/", option.menuHeadings());
                if (!optionString.equals(optionStr)) continue;
                return true;
            }
        }
        return false;
    }

    public List<String> toStrings(int[] optionSelections) {
        ArrayList<String> strings = new ArrayList<>();
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            OptionCategory category = this.categories.get(cat);
            int selection = optionSelections[cat];
            Option option = category.options().get(selection);
            List<String> headings = option.menuHeadings();
            strings.add(StringRoutines.join("/", headings));
        }
        return strings;
    }

    public List<Option> activeOptionObjects(List<String> selectedOptionStrings) {
        ArrayList<Option> options = new ArrayList<>(this.numCategories());
        int[] selections = this.computeOptionSelections(selectedOptionStrings);
        for (int i = 0; i < this.categories.size(); ++i) {
            OptionCategory category = this.categories.get(i);
            options.add(category.options().get(selections[i]));
        }
        return options;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (OptionCategory category : this.categories) {
            sb.append(category.toString()).append("\n");
        }
        return sb.toString();
    }
}

