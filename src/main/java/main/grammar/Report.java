// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Report
{
    private final List<String> errors;
    private final List<String> warnings;
    private final List<String> notes;
    
    public Report() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.notes = new ArrayList<>();
    }
    
    public List<String> errors() {
        return Collections.unmodifiableList(this.errors);
    }
    
    public List<String> warnings() {
        return Collections.unmodifiableList(this.warnings);
    }
    
    public List<String> notes() {
        return Collections.unmodifiableList(this.notes);
    }
    
    public void addError(final String error) {
        if (!this.errors.contains(error)) {
            this.errors.add(error);
        }
    }
    
    public void addWarning(final String warning) {
        if (!this.warnings.contains(warning)) {
            this.warnings.add(warning);
        }
    }
    
    public void addNote(final String note) {
        if (!this.notes.contains(note)) {
            this.notes.add(note);
        }
    }
    
    public boolean isError() {
        return !this.errors.isEmpty();
    }
    
    public boolean isWarning() {
        return !this.warnings.isEmpty();
    }
    
    public boolean isNote() {
        return !this.notes.isEmpty();
    }
    
    public void clear() {
        this.errors.clear();
        this.warnings.clear();
        this.notes.clear();
    }
    
    public static String clippedString(final String str, final int maxChars) {
        if (str.length() < maxChars) {
            return str;
        }
        return str.substring(0, maxChars - 3) + "...";
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final String error : this.errors) {
            sb.append(error);
        }
        for (final String warning : this.warnings) {
            sb.append("Warning: " + warning);
        }
        for (final String note : this.notes) {
            sb.append("Note: " + note);
        }
        return sb.toString();
    }
}
