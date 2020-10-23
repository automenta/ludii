/*
 * Decompiled with CFR 0.150.
 */
package grammar;

public class Symbol {
    private final SymbolType type;
    private String name = "";
    private String path = "";
    private String keyword = "";
    private String grammarLabel = null;
    private String notionalLocation = "";
    private boolean isAbstract = false;
    private Symbol returnType = null;
    private boolean hidden = false;
    private int nesting = 0;
    private boolean usedInGrammar = false;
    private boolean usedInMetadata = false;
    private boolean visited = false;
    private int depth = -1;
    private GrammarRule rule = null;
    private PackageInfo pack = null;
    private final Class<?> cls;

    public Symbol(SymbolType type, String path, String alias, Class<?> cls) {
        this.type = type;
        this.path = path;
        this.cls = cls;
        this.extractPackagePath();
        this.extractName();
        this.deriveKeyword(alias);
        this.grammarLabel = this.keyword;
    }

    public Symbol(SymbolType type, String path, String alias, String notionalLocation, Class<?> cls) {
        this.type = type;
        this.path = path;
        this.notionalLocation = notionalLocation;
        this.cls = cls;
        this.extractName();
        this.deriveKeyword(alias);
        this.grammarLabel = this.name;
    }

    public Symbol(Symbol other) {
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

    public void setGrammarLabel(String gl) {
        this.grammarLabel = gl;
    }

    public String notionalLocation() {
        return this.notionalLocation;
    }

    public boolean isAbstract() {
        return this.isAbstract;
    }

    public void setIsAbstract(boolean val) {
        this.isAbstract = val;
    }

    public boolean hidden() {
        return this.hidden;
    }

    public void setHidden(boolean val) {
        this.hidden = val;
    }

    public Symbol returnType() {
        return this.returnType;
    }

    public void setReturnType(Symbol symbol) {
        this.returnType = symbol;
    }

    public int nesting() {
        return this.nesting;
    }

    public void setNesting(int val) {
        this.nesting = val;
    }

    public boolean usedInGrammar() {
        return this.usedInGrammar;
    }

    public void setUsedInGrammar(boolean value) {
        this.usedInGrammar = value;
    }

    public boolean usedInMetadata() {
        return this.usedInMetadata;
    }

    public void setUsedInMetadata(boolean value) {
        this.usedInMetadata = value;
    }

    public boolean visited() {
        return this.visited;
    }

    public void setVisited(boolean value) {
        this.visited = value;
    }

    public int depth() {
        return this.depth;
    }

    public void setDepth(int value) {
        this.depth = value;
    }

    public GrammarRule rule() {
        return this.rule;
    }

    public void setRule(GrammarRule r) {
        this.rule = r;
    }

    public PackageInfo pack() {
        return this.pack;
    }

    public void setPack(PackageInfo pi) {
        this.pack = pi;
    }

    public boolean terminal() {
        return this.type == SymbolType.Primitive || this.type == SymbolType.Predefined || this.type == SymbolType.Constant;
    }

    public Class<?> cls() {
        return this.cls;
    }

    public boolean matches(Symbol other) {
        return this.path.equalsIgnoreCase(other.path()) && this.nesting == other.nesting;
    }

    public boolean compatibleWith(Symbol other) {
        if (this.cls.isAssignableFrom(other.cls())) {
            return true;
        }
        if (this.cls.isAssignableFrom(other.returnType().cls())) {
            return true;
        }
        return this.name.equals("Play") ? other.name().equals("Phase") : (this.name.equals("Item") ? other.name().equals("Regions") : (this.name.equals("BooleanFunction") ? other.returnType().name().equalsIgnoreCase("boolean") : (this.name.equals("IntFunction") ? other.returnType().name().equals("int") || other.returnType().name().equals("Integer") : (this.name.equals("FloatFunction") ? other.returnType().name().equals("float") || other.returnType().name().equals("Float") : (this.name.equals("RegionFunction") ? other.returnType().name().equals("Region") || other.returnType().name().equals("Sites") : (this.name.equals("GraphFunction") ? other.returnType().name().equals("Graph") || other.returnType().name().equals("Tiling") : (this.name.equals("RangeFunction") ? other.returnType().name().equals("Range") : (this.name.equals("Directions") ? other.returnType().name().equals("Directions") : this.name.equals("IntArrayFunction") && other.returnType().name().equals("int[]")))))))));
    }

    public String disambiguation(Symbol other) {
        String label = this.type == SymbolType.Class ? this.keyword : this.name;
        String labelOther = this.type == SymbolType.Class ? other.keyword : other.name;
        String[] subs = this.path.split("\\.");
        String[] subsOther = other.path.split("\\.");
        for (int level = 1; level < subs.length; ++level) {
            String newLabel = label;
            for (int ll = 1; ll < level; ++ll) {
                newLabel = subs[subs.length - ll - 1] + "." + newLabel;
            }
            String newLabelOther = labelOther;
            for (int ll = 1; ll < level; ++ll) {
                newLabelOther = subsOther[subsOther.length - ll - 1] + "." + newLabelOther;
            }
            if (newLabel.equals(newLabelOther)) continue;
            return newLabel;
        }
        return null;
    }

    public boolean validReturnType(ClauseArg arg) {
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

    public boolean validReturnType(Clause clause) {
        return this.path.equalsIgnoreCase(clause.symbol().path()) && this.nesting <= clause.symbol().nesting();
    }

    public boolean isCollectionOf(Symbol other) {
        return this.path.equalsIgnoreCase(other.path()) && this.nesting > other.nesting;
    }

    void extractName() {
        int c;
        while ((c = this.path.indexOf("[]")) != -1) {
            ++this.nesting;
            this.path = this.path.substring(0, c) + this.path.substring(c + 2);
        }
        this.name = this.path;
        this.name = this.name.replace('/', '.');
        this.name = this.name.replace('$', '.');
        if (this.name.contains(".java")) {
            this.name = this.name.substring(0, this.name.length() - 5);
        }
        for (c = this.name.length() - 1; c >= 0 && this.name.charAt(c) != '.'; --c) {
        }
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
        int c;
        this.notionalLocation = this.path;
        this.notionalLocation = this.notionalLocation.replace('/', '.');
        if (this.notionalLocation.contains(".java")) {
            this.notionalLocation = this.notionalLocation.substring(0, this.name.length() - 5);
        }
        for (c = this.notionalLocation.length() - 1; c >= 0 && this.notionalLocation.charAt(c) != '.'; --c) {
        }
        if (c >= 0) {
            this.notionalLocation = this.notionalLocation.substring(0, c);
        }
    }

    void deriveKeyword(String alias) {
        if (alias != null) {
            int c;
            for (c = alias.length() - 1; c >= 0 && alias.charAt(c) != '.'; --c) {
            }
            this.keyword = c < 0 ? alias : alias.substring(c + 1);
            return;
        }
        this.keyword = this.name;
        if (this.type == SymbolType.Class) {
            for (int c = 0; c < this.keyword.length(); ++c) {
                if (c != 0 && this.keyword.charAt(c - 1) != '.') continue;
                this.keyword = this.keyword.substring(0, c) + this.keyword.substring(c, c + 1).toLowerCase() + this.keyword.substring(c + 1);
            }
        }
    }

    public String javaDescription() {
        String str = this.name;
        for (int n = 0; n < this.nesting; ++n) {
            str = str + "[]";
        }
        return str;
    }

    public String toString(boolean forceLower) {
        String str;
        String string = str = forceLower || !this.terminal() ? this.grammarLabel : this.name;
        if (this.type != SymbolType.Constant) {
            str = "<" + str + ">";
        }
        for (int n = 0; n < this.nesting; ++n) {
            str = str + "{" + str + "}";
        }
        return str;
    }

    public String toString() {
        return this.toString(false);
    }

    public String info() {
        StringBuilder sb = new StringBuilder();
        sb.append((this.usedInGrammar() ? "g" : "~") + (this.usedInMetadata() ? "m" : "~") + (this.isAbstract() ? "*" : "~") + " " + this.toString() + " (" + this.path() + ") => " + this.returnType() + "\n    pack=" + this.notionalLocation() + ", label=" + this.grammarLabel() + ", cls=" + (this.cls() == null ? "null" : this.cls().getName()) + ", keyword=" + this.keyword);
        return sb.toString();
    }

    public enum SymbolType {
        Primitive("Primitive"),
        Predefined("Predefined"),
        Constant("Constant"),
        Class("Class");

        public String name;

        SymbolType(String name) {
            this.name = name;
        }
    }
}

