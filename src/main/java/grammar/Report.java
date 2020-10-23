/*
 * Decompiled with CFR 0.150.
 */
package grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Report {
    private final List<String> errors = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final List<String> notes = new ArrayList<>();

    public List<String> errors() {
        return Collections.unmodifiableList(this.errors);
    }

    public List<String> warnings() {
        return Collections.unmodifiableList(this.warnings);
    }

    public List<String> notes() {
        return Collections.unmodifiableList(this.notes);
    }

    public void addError(String error) {
        if (!this.errors.contains(error)) {
            this.errors.add(error);
        }
    }

    public void addWarning(String warning) {
        if (!this.warnings.contains(warning)) {
            this.warnings.add(warning);
        }
    }

    public void addNote(String note) {
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

    public static String clippedString(String str, int maxChars) {
        if (str.length() < maxChars) {
            return str;
        }
        return str.substring(0, maxChars - 3) + "...";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String error : this.errors) {
            sb.append(error);
        }
        for (String warning : this.warnings) {
            sb.append("Warning: " + warning);
        }
        for (String note : this.notes) {
            sb.append("Note: " + note);
        }
        return sb.toString();
    }
}

