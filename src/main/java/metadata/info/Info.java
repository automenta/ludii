// 
// Decompiled by Procyon v0.5.36
// 

package metadata.info;

import annotations.Or;
import metadata.MetadataItem;
import metadata.info.database.*;
import metadata.info.database.Date;

import java.io.Serializable;
import java.util.*;

public class Info implements MetadataItem, Serializable
{
    private static final long serialVersionUID = 1L;
    final List<InfoItem> items;
    
    public Info(@Or final InfoItem item, @Or final InfoItem[] items) {
        this.items = new ArrayList<>();
        int numNonNull = 0;
        if (item != null) {
            ++numNonNull;
        }
        if (items != null) {
            ++numNonNull;
        }
        if (numNonNull > 1) {
            throw new IllegalArgumentException("Only one of @Or should be different to null");
        }
        if (items != null) {
            this.items.addAll(Arrays.asList(items));
        }
        else {
            this.items.add(item);
        }
    }
    
    public void addToMap(final Map<String, MetadataItem> map) {
        for (final InfoItem item : this.items) {
            map.put(item.getClass().getSimpleName(), item);
        }
    }
    
    public List<InfoItem> getItem() {
        return Collections.unmodifiableList(this.items);
    }
    
    public ArrayList<String> getSource() {
        final ArrayList<String> sources = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Source) {
                sources.add(((Source)infoItem).source());
            }
        }
        return sources;
    }
    
    public ArrayList<String> getRules() {
        final ArrayList<String> rules = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Rules) {
                rules.add(((Rules)infoItem).rules());
            }
        }
        return rules;
    }
    
    public ArrayList<String> getAuthor() {
        final ArrayList<String> authors = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Author) {
                authors.add(((Author)infoItem).author());
            }
        }
        return authors;
    }
    
    public ArrayList<String> getDate() {
        final ArrayList<String> dates = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Date) {
                dates.add(((Date)infoItem).date());
            }
        }
        return dates;
    }
    
    public ArrayList<String> getPublisher() {
        final ArrayList<String> publishers = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Publisher) {
                publishers.add(((Publisher)infoItem).publisher());
            }
        }
        return publishers;
    }
    
    public ArrayList<String> getCredit() {
        final ArrayList<String> credits = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Credit) {
                credits.add(((Credit)infoItem).credit());
            }
        }
        return credits;
    }
    
    public ArrayList<String> getDescription() {
        final ArrayList<String> descriptions = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Description) {
                descriptions.add(((Description)infoItem).description());
            }
        }
        return descriptions;
    }
    
    public ArrayList<String> getOrigin() {
        final ArrayList<String> origins = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Origin) {
                origins.add(((Origin)infoItem).origin());
            }
        }
        return origins;
    }
    
    public ArrayList<String> getClassification() {
        final ArrayList<String> classifications = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Classification) {
                classifications.add(((Classification)infoItem).classification());
            }
        }
        return classifications;
    }
    
    public ArrayList<String> getVersion() {
        final ArrayList<String> versions = new ArrayList<>();
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Version) {
                versions.add(((Version)infoItem).version());
            }
        }
        return versions;
    }
    
    public String[] getAliases() {
        for (final InfoItem infoItem : this.items) {
            if (infoItem instanceof Aliases) {
                return ((Aliases)infoItem).aliases();
            }
        }
        return new String[0];
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String open = (this.items.size() <= 1) ? "" : "{";
        final String close = (this.items.size() <= 1) ? "" : "}";
        sb.append("    (info " + open + "\n");
        for (final InfoItem item : this.items) {
            if (item != null) {
                sb.append("        " + item.toString());
            }
        }
        sb.append("    " + close + ")\n");
        return sb.toString();
    }
}
