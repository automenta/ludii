// 
// Decompiled by Procyon v0.5.36
// 

package main.options;

import exception.DuplicateOptionUseException;
import exception.UnusedOptionException;
import main.StringRoutines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOptions
{
    public static final int MAX_OPTION_CATEGORIES = 10;
    private final List<OptionCategory> categories;
    private boolean optionsLoaded;
    
    public GameOptions() {
        this.categories = new ArrayList<>();
        this.optionsLoaded = false;
    }
    
    public List<OptionCategory> categories() {
        return Collections.unmodifiableList(this.categories);
    }
    
    public boolean optionsLoaded() {
        return this.optionsLoaded;
    }
    
    public void setOptionsLoaded(final boolean set) {
        this.optionsLoaded = set;
    }
    
    public void setOptionCategories(final List<Option>[] optionsAvList) {
        this.categories.clear();
        for (int n = 0; n < optionsAvList.length; ++n) {
            this.categories.add(new OptionCategory(optionsAvList[n]));
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
    
    public void add(final Option option) {
        for (final OptionCategory category : this.categories) {
            if (option.tag().equals(category.tag())) {
                category.add(option);
                return;
            }
        }
        final OptionCategory category2 = new OptionCategory(option);
        this.categories.add(category2);
    }
    
    public void add(final OptionCategory category) {
        this.categories.add(category);
    }
    
    public int[] computeOptionSelections(final List<String> selectedOptionStrings) {
        final int[] optionSelections = new int[this.numCategories()];
        final boolean[] usedOptionStrings = new boolean[selectedOptionStrings.size()];
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            final OptionCategory category = this.categories.get(cat);
            int maxPriority = Integer.MIN_VALUE;
            int activeOptionIdx = -1;
            int i = 0;
            while (i < category.options().size()) {
                final Option option = category.options().get(i);
                final String optionStr = StringRoutines.join("/", option.menuHeadings());
                final int optionStrIndex = selectedOptionStrings.indexOf(optionStr);
                if (optionStrIndex >= 0) {
                    if (usedOptionStrings[optionStrIndex]) {
                        throw new DuplicateOptionUseException(optionStr);
                    }
                    usedOptionStrings[optionStrIndex] = true;
                    activeOptionIdx = i;
                    break;
                }
                else {
                    if (option.priority() > maxPriority) {
                        activeOptionIdx = i;
                        maxPriority = option.priority();
                    }
                    ++i;
                }
            }
            optionSelections[cat] = activeOptionIdx;
        }
        for (int j = 0; j < usedOptionStrings.length; ++j) {
            if (!usedOptionStrings[j]) {
                throw new UnusedOptionException(selectedOptionStrings.get(j));
            }
        }
        return optionSelections;
    }
    
    public List<String> allOptionStrings(final List<String> selectedOptionStrings) {
        final List<String> strings = new ArrayList<>();
        final boolean[] usedOptionStrings = new boolean[selectedOptionStrings.size()];
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            final OptionCategory category = this.categories.get(cat);
            int maxPriority = Integer.MIN_VALUE;
            String activeOptionStr = null;
            int i = 0;
            while (i < category.options().size()) {
                final Option option = category.options().get(i);
                final String optionStr = StringRoutines.join("/", option.menuHeadings());
                final int optionStrIndex = selectedOptionStrings.indexOf(optionStr);
                if (optionStrIndex >= 0) {
                    if (usedOptionStrings[optionStrIndex]) {
                        throw new DuplicateOptionUseException(optionStr);
                    }
                    usedOptionStrings[optionStrIndex] = true;
                    activeOptionStr = optionStr;
                    break;
                }
                else {
                    if (option.priority() > maxPriority) {
                        activeOptionStr = optionStr;
                        maxPriority = option.priority();
                    }
                    ++i;
                }
            }
            strings.add(activeOptionStr);
        }
        for (int j = 0; j < usedOptionStrings.length; ++j) {
            if (!usedOptionStrings[j]) {
                throw new UnusedOptionException(selectedOptionStrings.get(j));
            }
        }
        return strings;
    }
    
    public boolean optionExists(final String optionString) {
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            final OptionCategory category = this.categories.get(cat);
            for (int i = 0; i < category.options().size(); ++i) {
                final Option option = category.options().get(i);
                final String optionStr = StringRoutines.join("/", option.menuHeadings());
                if (optionString.equals(optionStr)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<String> toStrings(final int[] optionSelections) {
        final List<String> strings = new ArrayList<>();
        for (int cat = 0; cat < this.categories.size(); ++cat) {
            final OptionCategory category = this.categories.get(cat);
            final int selection = optionSelections[cat];
            final Option option = category.options().get(selection);
            final List<String> headings = option.menuHeadings();
            strings.add(StringRoutines.join("/", headings));
        }
        return strings;
    }
    
    public List<Option> activeOptionObjects(final List<String> selectedOptionStrings) {
        final List<Option> options = new ArrayList<>(this.numCategories());
        final int[] selections = this.computeOptionSelections(selectedOptionStrings);
        for (int i = 0; i < this.categories.size(); ++i) {
            final OptionCategory category = this.categories.get(i);
            options.add(category.options().get(selections[i]));
        }
        return options;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final OptionCategory category : this.categories) {
            sb.append(category.toString() + "\n");
        }
        return sb.toString();
    }
}
