// 
// Decompiled by Procyon v0.5.36
// 

package main.grammar;

public class Symbol
{
    private final SymbolType type;
    private String name;
    private String path;
    private String keyword;
    private String grammarLabel;
    private String notionalLocation;
    private boolean isAbstract;
    private Symbol returnType;
    private boolean hidden;
    private int nesting;
    private boolean usedInGrammar;
    private boolean usedInMetadata;
    private boolean visited;
    private int depth;
    private GrammarRule rule;
    private PackageInfo pack;
    private final Class<?> cls;
    
    public Symbol(final SymbolType type, final String path, final String alias, final Class<?> cls) {
        this.name = "";
        this.path = "";
        this.keyword = "";
        this.grammarLabel = null;
        this.notionalLocation = "";
        this.isAbstract = false;
        this.returnType = null;
        this.hidden = false;
        this.nesting = 0;
        this.usedInGrammar = false;
        this.usedInMetadata = false;
        this.visited = false;
        this.depth = -1;
        this.rule = null;
        this.pack = null;
        this.type = type;
        this.path = path;
        this.cls = cls;
        this.extractPackagePath();
        this.extractName();
        this.deriveKeyword(alias);
        this.grammarLabel = this.keyword;
    }
    
    public Symbol(final SymbolType type, final String path, final String alias, final String notionalLocation, final Class<?> cls) {
        this.name = "";
        this.path = "";
        this.keyword = "";
        this.grammarLabel = null;
        this.notionalLocation = "";
        this.isAbstract = false;
        this.returnType = null;
        this.hidden = false;
        this.nesting = 0;
        this.usedInGrammar = false;
        this.usedInMetadata = false;
        this.visited = false;
        this.depth = -1;
        this.rule = null;
        this.pack = null;
        this.type = type;
        this.path = path;
        this.notionalLocation = notionalLocation;
        this.cls = cls;
        this.extractName();
        this.deriveKeyword(alias);
        this.grammarLabel = this.name;
    }
    
    public Symbol(final Symbol other) {
        this.name = "";
        this.path = "";
        this.keyword = "";
        this.grammarLabel = null;
        this.notionalLocation = "";
        this.isAbstract = false;
        this.returnType = null;
        this.hidden = false;
        this.nesting = 0;
        this.usedInGrammar = false;
        this.usedInMetadata = false;
        this.visited = false;
        this.depth = -1;
        this.rule = null;
        this.pack = null;
        this.type = other.type;
        this.name = other.name;
        this.path = other.path;
        this.keyword = other.keyword;
        this.grammarLabel = other.grammarLabel;
        this.notionalLocation = other.notionalLocation;
        this.isAbstract = other.isAbstract;
        this.returnType = other.returnType;
        this.nesting = other.nesting;
        this.usedInGrammar = other.usedInGrammar;
        this.visited = other.visited;
        this.rule = other.rule;
        this.pack = other.pack;
        this.cls = other.cls;
    }
    
    public SymbolType type() {
        return this.type;
    }
    
    public String name() {
        return this.name;
    }
    
    public String path() {
        return this.path;
    }
    
    public String keyword() {
        return this.keyword;
    }
    
    public String grammarLabel() {
        return this.grammarLabel;
    }
    
    public void setGrammarLabel(final String gl) {
        this.grammarLabel = gl;
    }
    
    public String notionalLocation() {
        return this.notionalLocation;
    }
    
    public boolean isAbstract() {
        return this.isAbstract;
    }
    
    public void setIsAbstract(final boolean val) {
        this.isAbstract = val;
    }
    
    public boolean hidden() {
        return this.hidden;
    }
    
    public void setHidden(final boolean val) {
        this.hidden = val;
    }
    
    public Symbol returnType() {
        return this.returnType;
    }
    
    public void setReturnType(final Symbol symbol) {
        this.returnType = symbol;
    }
    
    public int nesting() {
        return this.nesting;
    }
    
    public void setNesting(final int val) {
        this.nesting = val;
    }
    
    public boolean usedInGrammar() {
        return this.usedInGrammar;
    }
    
    public void setUsedInGrammar(final boolean value) {
        this.usedInGrammar = value;
    }
    
    public boolean usedInMetadata() {
        return this.usedInMetadata;
    }
    
    public void setUsedInMetadata(final boolean value) {
        this.usedInMetadata = value;
    }
    
    public boolean visited() {
        return this.visited;
    }
    
    public void setVisited(final boolean value) {
        this.visited = value;
    }
    
    public int depth() {
        return this.depth;
    }
    
    public void setDepth(final int value) {
        this.depth = value;
    }
    
    public GrammarRule rule() {
        return this.rule;
    }
    
    public void setRule(final GrammarRule r) {
        this.rule = r;
    }
    
    public PackageInfo pack() {
        return this.pack;
    }
    
    public void setPack(final PackageInfo pi) {
        this.pack = pi;
    }
    
    public boolean terminal() {
        return this.type == SymbolType.Primitive || this.type == SymbolType.Predefined || this.type == SymbolType.Constant;
    }
    
    public Class<?> cls() {
        return this.cls;
    }
    
    public boolean matches(final Symbol other) {
        return this.path.equalsIgnoreCase(other.path()) && this.nesting == other.nesting;
    }
    
    public boolean compatibleWith(final Symbol other) {
        if (this.cls.isAssignableFrom(other.cls())) {
            return true;
        }
        if (this.cls.isAssignableFrom(other.returnType().cls())) {
            return true;
        }
        if (this.name.equals("Play")) {
            return other.name().equals("Phase");
        }
        else if (this.name.equals("Item")) {
            return other.name().equals("Regions");
        }
        else if (this.name.equals("BooleanFunction")) {
            return other.returnType().name().equalsIgnoreCase("boolean");
        }
        else if (this.name.equals("IntFunction")) {
            return other.returnType().name().equals("int") || other.returnType().name().equals("Integer");
        }
        else if (this.name.equals("FloatFunction")) {
            return other.returnType().name().equals("float") || other.returnType().name().equals("Float");
        }
        else if (this.name.equals("RegionFunction")) {
            return other.returnType().name().equals("Region") || other.returnType().name().equals("Sites");
        }
        else if (this.name.equals("GraphFunction")) {
            return other.returnType().name().equals("Graph") || other.returnType().name().equals("Tiling");
        }
        else if (this.name.equals("RangeFunction")) {
            return other.returnType().name().equals("Range");
        }
        else if (this.name.equals("Directions")) {
            return other.returnType().name().equals("Directions");
        }
        else return this.name.equals("IntArrayFunction") && other.returnType().name().equals("int[]");
    }
    
    public String disambiguation(final Symbol other) {
        final String label = (this.type == SymbolType.Class) ? this.keyword : this.name;
        final String labelOther = (this.type == SymbolType.Class) ? other.keyword : other.name;
        final String[] subs = this.path.split("\\.");
        final String[] subsOther = other.path.split("\\.");
        for (int level = 1; level < subs.length; ++level) {
            String newLabel = label;
            for (int ll = 1; ll < level; ++ll) {
                newLabel = subs[subs.length - ll - 1] + "." + newLabel;
            }
            String newLabelOther = labelOther;
            for (int ll2 = 1; ll2 < level; ++ll2) {
                newLabelOther = subsOther[subsOther.length - ll2 - 1] + "." + newLabelOther;
            }
            if (!newLabel.equals(newLabelOther)) {
                return newLabel;
            }
        }
        return null;
    }
    
    public boolean validReturnType(final ClauseArg arg) {
        if (this.path.equals(arg.symbol().path()) && this.nesting <= arg.nesting()) {
            return true;
        }
        if (arg.symbol().name.contains("Function") || arg.symbol().name.contains("Constant")) {
            if (arg.symbol().name.equals("MoveListFunction") && this.name.equals("Move")) {
                return true;
            }
            return arg.symbol().name.equals("BitSetFunction") && this.name.equals("BitSet");
        }
        return false;
    }
    
    public boolean validReturnType(final Clause clause) {
        return this.path.equalsIgnoreCase(clause.symbol().path()) && this.nesting <= clause.symbol().nesting();
    }
    
    public boolean isCollectionOf(final Symbol other) {
        return this.path.equalsIgnoreCase(other.path()) && this.nesting > other.nesting;
    }
    
    void extractName() {
        while (true) {
            final int c = this.path.indexOf("[]");
            if (c == -1) {
                break;
            }
            ++this.nesting;
            this.path = this.path.substring(0, c) + this.path.substring(c + 2);
        }
        this.name = this.path;
        this.name = this.name.replace('/', '.');
        this.name = this.name.replace('$', '.');
        if (this.name.contains(".java")) {
            this.name = this.name.substring(0, this.name.length() - 5);
        }
        int c;
        for (c = this.name.length() - 1; c >= 0 && this.name.charAt(c) != '.'; --c) {}
        if (c >= 0) {
            this.name = this.name.substring(c);
        }
        if (this.name.length() > 0 && this.name.charAt(0) == '.') {
            this.name = this.name.substring(1);
        }
        if (this.name.contains(">")) {
            this.name = this.name.replace('$', '.');
        }
    }
    
    void extractPackagePath() {
        this.notionalLocation = this.path;
        this.notionalLocation = this.notionalLocation.replace('/', '.');
        if (this.notionalLocation.contains(".java")) {
            this.notionalLocation = this.notionalLocation.substring(0, this.name.length() - 5);
        }
        int c;
        for (c = this.notionalLocation.length() - 1; c >= 0 && this.notionalLocation.charAt(c) != '.'; --c) {}
        if (c >= 0) {
            this.notionalLocation = this.notionalLocation.substring(0, c);
        }
    }
    
    void deriveKeyword(final String alias) {
        if (alias != null) {
            int c;
            for (c = alias.length() - 1; c >= 0 && alias.charAt(c) != '.'; --c) {}
            this.keyword = ((c < 0) ? alias : alias.substring(c + 1));
            return;
        }
        this.keyword = this.name;
        if (this.type == SymbolType.Class) {
            for (int c = 0; c < this.keyword.length(); ++c) {
                if (c == 0 || this.keyword.charAt(c - 1) == '.') {
                    this.keyword = this.keyword.substring(0, c) + this.keyword.substring(c, c + 1).toLowerCase() + this.keyword.substring(c + 1);
                }
            }
        }
    }
    
    public String javaDescription() {
        String str = this.name;
        for (int n = 0; n < this.nesting; ++n) {
            str += "[]";
        }
        return str;
    }
    
    public String toString(final boolean forceLower) {
        String str = (forceLower || !this.terminal()) ? this.grammarLabel : this.name;
        if (this.type != SymbolType.Constant) {
            str = "<" + str + ">";
        }
        for (int n = 0; n < this.nesting; ++n) {
            str = str + "{" + str + "}";
        }
        return str;
    }
    
    @Override
    public String toString() {
        return this.toString(false);
    }
    
    public String info() {
        final StringBuilder sb = new StringBuilder();
        sb.append((this.usedInGrammar() ? "g" : "~") + (this.usedInMetadata() ? "m" : "~") + (this.isAbstract() ? "*" : "~") + " " + this.toString() + " (" + this.path() + ") => " + this.returnType() + "\n    pack=" + this.notionalLocation() + ", label=" + this.grammarLabel() + ", cls=" + ((this.cls() == null) ? "null" : this.cls().getName()) + ", keyword=" + this.keyword);
        return sb.toString();
    }
    
    public enum SymbolType
    {
        Primitive("Primitive"), 
        Predefined("Predefined"), 
        Constant("Constant"), 
        Class("Class");
        
        public String name;
        
        SymbolType(final String name) {
            this.name = name;
        }
    }
}
