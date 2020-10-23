// 
// Decompiled by Procyon v0.5.36
// 

package language.grammar;

import annotations.Alias;
import main.Constants;
import main.FileHandling;
import main.StringRoutines;
import main.grammar.*;
import main.grammar.ebnf.EBNF;
import metadata.MetadataItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class Grammar
{
    private final List<Symbol> symbols;
    private final Map<String, List<Symbol>> symbolMap;
    private final Map<String, List<Symbol>> symbolsByPartialKeyword;
    private final List<GrammarRule> rules;
    private final List<PackageInfo> packages;
    private final List<PackageInfo> packageOrder;
    private Symbol rootGameSymbol;
    private Symbol rootMetadataSymbol;
    private EBNF ebnf;
    public static final String[][] Primitives;
    public static final String[][] Predefined;
    final String[][] Functions;
    public static final String[][] ApplicationConstants;
    private static volatile Grammar singleton;
    
    private Grammar() {
        this.symbols = new ArrayList<>();
        this.symbolMap = new HashMap<>();
        this.symbolsByPartialKeyword = new HashMap<>();
        this.rules = new ArrayList<>();
        this.packages = new ArrayList<>();
        this.packageOrder = new ArrayList<>();
        this.rootGameSymbol = null;
        this.rootMetadataSymbol = null;
        this.ebnf = null;
        this.Functions = new String[][] { { "IntFunction", "int" }, { "IntConstant", "int" }, { "BooleanFunction", "boolean" }, { "BooleanConstant", "boolean" }, { "FloatFunction", "float" }, { "FloatConstant", "float" }, { "IntArrayFunction", "ints" }, { "IntArrayConstant", "ints" }, { "RegionFunction", "sites" }, { "RegionConstant", "sites" }, { "RangeFunction", "range" }, { "RangeConstant", "range" }, { "DirectionsFunction", "directions" }, { "DirectionsConstant", "directions" }, { "GraphFunction", "graph" }, { "GraphConstant", "graph" }, { "GraphFunction", "tiling" }, { "GraphConstant", "tiling" }, { "DimFunction", "dim" }, { "DimConstant", "dim" } };
        if (Constants.combos == null) {
            Constants.createCombos();
        }
        this.generate();
    }
    
    public static Grammar grammar() {
        if (Grammar.singleton == null) {
            synchronized (Grammar.class) {
                if (Grammar.singleton == null) {
                    Grammar.singleton = new Grammar();
                }
            }
        }
        return Grammar.singleton;
    }
    
    public Map<String, List<Symbol>> symbolMap() {
        return this.symbolMap;
    }
    
    public List<Symbol> symbols() {
        return Collections.unmodifiableList(this.symbols);
    }
    
    public EBNF ebnf() {
        if (this.ebnf == null) {
            this.ebnf = new EBNF(grammar().toString());
        }
        return this.ebnf;
    }
    
    void execute() {
        System.out.println("Ludii library 1.0.8.");
        this.generate();
        final String outFileName = "grammar-1.0.8.txt";
        System.out.println("Saving to file grammar-1.0.8.txt.");
        try {
            this.export("grammar-1.0.8.txt");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void generate() {
        this.symbols.clear();
        this.rules.clear();
        this.packages.clear();
        this.createSymbols();
        this.disambiguateSymbols();
        this.createRules();
        this.addReturnTypeClauses();
        this.addApplicationConstantsToRule();
        this.crossReferenceSubclasses();
        this.linkDirectionsRules();
        this.handleDimFunctions();
        this.linkToPackages();
        this.instantiateSingleEnums();
        this.visitSymbols(this.rootGameSymbol);
        this.visitSymbols(this.rootMetadataSymbol);
        for (final Symbol symbol : this.symbols) {
            if (symbol.cls() != null && MetadataItem.class.isAssignableFrom(symbol.cls())) {
                symbol.setUsedInMetadata(true);
            }
        }
        this.setDisplayOrder(this.rootGameSymbol);
        this.removeRedundantFunctionNames();
        this.createSymbolMap();
        this.alphabetiseRuleClauses();
        this.removeDuplicateClauses();
        this.filterOutPrimitiveWrappers();
        if (FileHandling.isCambolbro()) {}
        final boolean debug = false;
        if (debug) {
            System.out.println("\n+++++++++++++++++++ SYMBOLS ++++++++++++++++++++++");
            for (final Symbol symbol2 : this.symbols) {
                System.out.println(symbol2.info());
            }
            System.out.println("\n++++++++++++++++++++ RULES +++++++++++++++++++++++");
            System.out.println("Rules:\n");
            for (final GrammarRule rule : this.rules) {
                System.out.println((rule.lhs().usedInGrammar() ? "g" : "~") + (rule.lhs().usedInMetadata() ? "m" : "~") + " " + rule);
            }
        }
        int numGrammar = 0;
        int numMetadata = 0;
        int numClasses = 0;
        int numConstants = 0;
        int numPredefined = 0;
        int numPrimitive = 0;
        for (final Symbol symbol3 : this.symbols) {
            if (symbol3.usedInGrammar()) {
                ++numGrammar;
            }
            if (symbol3.usedInMetadata()) {
                ++numMetadata;
            }
            if (symbol3.type() == Symbol.SymbolType.Class) {
                ++numClasses;
            }
            if (symbol3.type() == Symbol.SymbolType.Constant) {
                ++numConstants;
            }
            if (symbol3.type() == Symbol.SymbolType.Predefined) {
                ++numPredefined;
            }
            if (symbol3.type() == Symbol.SymbolType.Primitive) {
                ++numPrimitive;
            }
        }
        if (debug) {
            System.out.println(this.symbols.size() + " symbols: " + numClasses + " classes, " + numConstants + " constants, " + numPredefined + " predefined, " + numPrimitive + " primitives.");
            System.out.println(this.rules.size() + " rules, " + numGrammar + " used in grammar, " + numMetadata + " used in metadata.");
        }
    }
    
    void createSymbols() {
        for (int ps = 0; ps < Grammar.Primitives.length; ++ps) {
            Class<?> cls = null;
            if (Grammar.Primitives[ps][0].equals("int")) {
                cls = Integer.TYPE;
            }
            else if (Grammar.Primitives[ps][0].equals("float")) {
                cls = Float.TYPE;
            }
            else if (Grammar.Primitives[ps][0].equals("boolean")) {
                cls = Boolean.TYPE;
            }
            final Symbol symbol = new Symbol(Symbol.SymbolType.Primitive, Grammar.Primitives[ps][0], null, Grammar.Primitives[ps][1], cls);
            symbol.setReturnType(symbol);
            this.symbols.add(symbol);
        }
        for (int ps = 0; ps < Grammar.Predefined.length; ++ps) {
            Class<?> cls = null;
            try {
                cls = Class.forName(Grammar.Predefined[ps][0]);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            final Symbol symbol = new Symbol(Symbol.SymbolType.Predefined, Grammar.Predefined[ps][0], null, Grammar.Predefined[ps][1], cls);
            symbol.setReturnType(symbol);
            this.symbols.add(symbol);
        }
        Symbol symbolInt = null;
        final Iterator<Symbol> iterator = this.symbols.iterator();
        while (iterator.hasNext()) {
            final Symbol symbol = iterator.next();
            if (symbol.grammarLabel().equals("int")) {
                symbolInt = symbol;
                break;
            }
        }
        for (int cs = 0; cs < Grammar.ApplicationConstants.length; ++cs) {
            final Class<?> cls2 = Integer.TYPE;
            final Symbol symbolC = new Symbol(Symbol.SymbolType.Constant, Grammar.ApplicationConstants[cs][0], null, Grammar.ApplicationConstants[cs][2], cls2);
            symbolC.setReturnType(symbolInt);
            this.symbols.add(symbolC);
        }
        this.findSymbolsFromClasses("game.Game");
        this.findSymbolsFromClasses("metadata.Metadata");
        this.checkHiddenClasses();
        this.checkAbstractClasses();
        this.handleEnums();
        this.overrideReturnTypes();
        this.rootGameSymbol = null;
        this.rootMetadataSymbol = null;
        final Iterator<Symbol> iterator2 = this.symbols.iterator();
        while (iterator2.hasNext()) {
            final Symbol symbol = iterator2.next();
            if (symbol.path().equals("game.Game")) {
                this.rootGameSymbol = symbol;
            }
            if (symbol.path().equals("metadata.Metadata")) {
                this.rootMetadataSymbol = symbol;
            }
        }
        if (this.rootGameSymbol == null || this.rootMetadataSymbol == null) {
            throw new RuntimeException("Cannot find game.Game or metadata.Metadata.");
        }
    }
    
    public void findSymbolsFromClasses(final String rootPackageName) {
        Class<?> clsRoot = null;
        try {
            clsRoot = Class.forName(rootPackageName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        final ArrayList<Class<?>> classes = (ArrayList<Class<?>>) ClassEnumerator.getClassesForPackage(clsRoot.getPackage());
        for (final Class<?> cls : classes) {
            if (cls.getName().contains("$")) {
                continue;
            }
            if (cls.getName().contains("package-info")) {
                continue;
            }
            String alias = null;
            final Annotation[] annotations2;
            final Annotation[] annotations = annotations2 = cls.getAnnotations();
            for (final Annotation annotation : annotations2) {
                if (annotation instanceof Alias) {
                    final Alias anno = (Alias)annotation;
                    alias = anno.alias();
                }
            }
            final String classPath = cls.getName();
            final Symbol symbol = new Symbol(Symbol.SymbolType.Class, classPath, alias, cls);
            symbol.setReturnType(symbol);
            this.symbols.add(symbol);
            final Package pack = cls.getPackage();
            String packageName;
            int p;
            for (packageName = pack.getName(), p = 0; p < this.packages.size() && !this.packages.get(p).path().equals(packageName); ++p) {}
            if (p < this.packages.size()) {
                continue;
            }
            this.packages.add(new PackageInfo(packageName));
        }
    }
    
    void disambiguateSymbols() {
        for (int sa = 0; sa < this.symbols.size(); ++sa) {
            final Symbol symbolA = this.symbols.get(sa);
            if (symbolA.type() == Symbol.SymbolType.Class) {
                String grammarLabel = "";
                for (int sb = 0; sb < this.symbols.size(); ++sb) {
                    if (sa != sb) {
                        final Symbol symbolB = this.symbols.get(sb);
                        if (symbolB.type() == Symbol.SymbolType.Class) {
                            if (symbolA.name().equals(symbolB.name())) {
                                final String label = symbolA.disambiguation(symbolB);
                                if (label != null) {
                                    if (label.length() > grammarLabel.length()) {
                                        grammarLabel = label;
                                    }
                                }
                            }
                        }
                    }
                }
                if (grammarLabel != "") {
                    symbolA.setGrammarLabel(grammarLabel);
                }
            }
        }
    }
    
    void createSymbolMap() {
        this.symbolMap.clear();
        for (final Symbol symbol : this.symbols) {
            final String key = symbol.name();
            List<Symbol> list = this.symbolMap.get(key);
            if (list != null) {
                list.add(symbol);
            }
            else {
                list = new ArrayList<>();
                list.add(symbol);
                this.symbolMap.put(key, list);
            }
        }
        this.symbolsByPartialKeyword.clear();
        for (final Symbol symbol : this.symbols) {
            final String fullKey = symbol.keyword();
            for (int i = 1; i < fullKey.length() + 1; ++i) {
                final String key2 = fullKey.substring(0, i);
                List<Symbol> list2 = this.symbolsByPartialKeyword.get(key2);
                if (list2 != null) {
                    list2.add(symbol);
                }
                else {
                    list2 = new ArrayList<>();
                    list2.add(symbol);
                    this.symbolsByPartialKeyword.put(key2, list2);
                }
            }
        }
    }
    
    public List<Symbol> symbolListFromClassName(final String className) {
        final List<Symbol> list = this.symbolMap.get(className);
        if (list != null) {
            return list;
        }
        for (final Symbol symbol : this.symbols) {
            if (symbol.keyword().equals(className)) {
                return this.symbolMap.get(symbol.name());
            }
        }
        return null;
    }
    
    public List<Symbol> symbolsWithPartialKeyword(final String partialKeyword) {
        return this.symbolsByPartialKeyword.get(partialKeyword);
    }
    
    public List<String> classPaths(final String description, final int cursorAt, final boolean usePartial) {
        final List<String> list = new ArrayList<>();
        if (cursorAt <= 0 || cursorAt >= description.length()) {
            System.out.println("** Grammar.classPaths(): Invalid cursor position " + cursorAt + " specified.");
            return list;
        }
        int c = cursorAt - 1;
        char ch = description.charAt(c);
        if (!StringRoutines.isTokenChar(ch)) {
            return list;
        }
        while (c > 0 && StringRoutines.isTokenChar(ch)) {
            --c;
            ch = description.charAt(c);
        }
        int cc;
        char ch2;
        for (cc = cursorAt, ch2 = description.charAt(cc); cc < description.length() && StringRoutines.isTokenChar(ch2); ch2 = description.charAt(cc)) {
            if (++cc < description.length()) {}
        }
        if (cc >= description.length()) {
            System.out.println("** Grammar.classPaths(): Couldn't find end of token from position " + cursorAt + ".");
            return list;
        }
        if (ch2 == ':') {
            return list;
        }
        String partialKeyword = description.substring(c + 1, cursorAt);
        String fullKeyword = description.substring(c + 1, cc);
        boolean isRule = false;
        if (partialKeyword.charAt(0) == '<') {
            isRule = true;
            partialKeyword = partialKeyword.substring(1);
        }
        if (fullKeyword.charAt(0) == '<' && fullKeyword.charAt(fullKeyword.length() - 1) == '>') {
            isRule = true;
            fullKeyword = fullKeyword.substring(1, fullKeyword.length() - 1);
        }
        if (description.charAt(c) == '<' || (description.charAt(c) == '[' && description.charAt(c + 1) == '<') || (description.charAt(c) == '(' && description.charAt(c + 1) == '<')) {
            isRule = true;
        }
        if (fullKeyword.charAt(0) == '\"') {
            list.add("java.lang.String");
            return list;
        }
        if (fullKeyword.equals("true")) {
            list.add("true");
        }
        else if (fullKeyword.equals("false")) {
            list.add("false");
        }
        else if (StringRoutines.isInteger(fullKeyword)) {
            list.add("int");
        }
        else if (StringRoutines.isFloat(fullKeyword) || StringRoutines.isDouble(fullKeyword)) {
            list.add("float");
        }
        final String keyword = usePartial ? partialKeyword : fullKeyword;
        final List<Symbol> matches = this.symbolsWithPartialKeyword(keyword);
        if (matches == null) {
            return list;
        }
        if (ch == '(') {
            for (final Symbol symbol : matches) {
                if (symbol.cls() != null && !symbol.cls().isEnum()) {
                    list.add(symbol.path());
                }
            }
        }
        else if (ch == '<' || isRule) {
            for (final Symbol symbol : matches) {
                list.add(symbol.path());
            }
        }
        else if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == ':' || ch == '{') {
            for (final Symbol symbol : matches) {
                if (symbol.cls() != null && symbol.cls().isEnum()) {
                    int lastDot;
                    for (lastDot = symbol.path().length() - 1; lastDot >= 0 && symbol.path().charAt(lastDot) != '.'; --lastDot) {}
                    final String enumString = symbol.path().substring(0, lastDot) + '$' + symbol.path().substring(lastDot + 1);
                    list.add(enumString);
                }
            }
        }
        final int metadataAt = description.indexOf("(metadata");
        final boolean inMetadata = metadataAt != -1 && metadataAt < cursorAt;
        for (int n = list.size() - 1; n >= 0; --n) {
            final boolean isMetadata = list.get(n).contains("metadata.");
            if ((isMetadata && !inMetadata) || (!isMetadata && inMetadata)) {
                list.remove(n);
            }
        }
        return list;
    }
    
    public static int applicationConstantIndex(final String name) {
        for (int ac = 0; ac < Grammar.ApplicationConstants.length; ++ac) {
            if (Grammar.ApplicationConstants[ac][0].equals(name)) {
                return ac;
            }
        }
        return -1;
    }
    
    void checkHiddenClasses() {
        for (final Symbol symbol : this.symbols) {
            final Class<?> cls = symbol.cls();
            if (cls == null) {
                continue;
            }
            final Annotation[] annotations;
            final Annotation[] annos = annotations = cls.getAnnotations();
            for (final Annotation anno : annotations) {
                if (anno.annotationType().getName().equals("annotations.Hide")) {
                    symbol.setHidden(true);
                    break;
                }
            }
        }
    }
    
    void checkAbstractClasses() {
        for (final Symbol symbol : this.symbols) {
            final Class<?> cls = symbol.cls();
            if (cls == null) {
                continue;
            }
            if (!Modifier.isAbstract(cls.getModifiers())) {
                continue;
            }
            symbol.setIsAbstract(true);
        }
    }
    
    void handleEnums() {
        final List<Symbol> newSymbols = new ArrayList<>();
        for (final Symbol symbol : this.symbols) {
            final Class<?> cls = symbol.cls();
            if (cls == null) {
                continue;
            }
            if (cls.isEnum()) {
                extractEnums(cls, symbol, newSymbols);
            }
            for (final Class<?> inner : cls.getClasses()) {
                if (inner.isEnum()) {
                    extractEnums(inner, symbol, newSymbols);
                }
            }
        }
        for (final Symbol newSymbol : newSymbols) {
            if (this.findSymbolMatch(newSymbol) == null) {
                this.symbols.add(newSymbol);
            }
        }
    }
    
    static void extractEnums(final Class<?> cls, final Symbol symbol, final List<Symbol> newSymbols) {
        final Symbol symbolEnum = new Symbol(Symbol.SymbolType.Class, cls.getName(), null, cls);
        symbolEnum.setReturnType(symbolEnum);
        newSymbols.add(symbolEnum);
        for (final Object enumObj : cls.getEnumConstants()) {
            final String symbolName = enumObj.toString();
            final String path = symbolEnum.path() + "." + symbolName;
            String alias = null;
            Field declaredField = null;
            try {
                declaredField = cls.getDeclaredField(((Enum)enumObj).name());
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (declaredField != null) {
                final Annotation[] annotations2;
                final Annotation[] annotations = annotations2 = declaredField.getAnnotations();
                for (final Annotation annotation : annotations2) {
                    if (annotation instanceof Alias) {
                        final Alias anno = (Alias)annotation;
                        alias = anno.alias();
                    }
                }
            }
            final Symbol symbolValue = new Symbol(Symbol.SymbolType.Constant, path, alias, symbolEnum.notionalLocation(), cls);
            symbolValue.setReturnType(symbolEnum);
            newSymbols.add(symbolValue);
        }
    }
    
    void overrideReturnTypes() {
        final List<Symbol> newSymbols = new ArrayList<>();
        for (final Symbol symbol : this.symbols) {
            if (symbol.type() != Symbol.SymbolType.Class) {
                continue;
            }
            final Class<?> cls = symbol.cls();
            if (cls == null) {
                continue;
            }
            String alias = null;
            final Annotation[] annotations;
            final Annotation[] classAnnotations = annotations = cls.getAnnotations();
            for (final Annotation annotation : annotations) {
                if (annotation instanceof Alias) {
                    final Alias anno = (Alias)annotation;
                    alias = anno.alias();
                }
            }
            final Method[] declaredMethods;
            final Method[] methods = declaredMethods = cls.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                if (method.getName().equals("eval")) {
                    final Type returnType = method.getReturnType();
                    if (returnType == null) {
                        System.out.println("** Bad return type.");
                    }
                    else {
                        final String returnTypeName = returnType.getTypeName();
                        if (!returnTypeName.equals("void")) {
                            final Symbol temp = new Symbol(null, returnTypeName, alias, cls);
                            Symbol returnSymbol = this.findSymbolByPath(temp.path());
                            if (returnSymbol != null) {
                                if (symbol.nesting() != temp.nesting()) {
                                    returnSymbol = new Symbol(returnSymbol);
                                    returnSymbol.setNesting(temp.nesting());
                                    returnSymbol.setReturnType(returnSymbol);
                                    newSymbols.add(returnSymbol);
                                }
                                symbol.setReturnType(returnSymbol);
                            }
                        }
                    }
                }
            }
        }
        for (final Symbol newSymbol : newSymbols) {
            if (this.findSymbolMatch(newSymbol) == null) {
                this.symbols.add(newSymbol);
            }
        }
    }
    
    Symbol findSymbolByPath(final String path) {
        for (final Symbol symbol : this.symbols) {
            if (symbol.path().equalsIgnoreCase(path)) {
                return symbol;
            }
        }
        return null;
    }
    
    void createRules() {
        for (final Symbol symbol : this.symbols) {
            if (symbol.hidden()) {
                continue;
            }
            if (symbol.type() == Symbol.SymbolType.Constant) {
                final Symbol lhs = symbol.returnType();
                final GrammarRule rule = this.getRule(lhs);
                final Clause clause = new Clause(symbol);
                rule.add(clause);
            }
            else {
                final Symbol lhs = symbol;
                final GrammarRule rule = this.getRule(lhs);
                if (symbol.type() == Symbol.SymbolType.Class && !symbol.isAbstract()) {
                    this.expandConstructors(rule, symbol);
                }
                else {
                    final Clause clause = new Clause(symbol);
                    if (rule.containsClause(clause) || (symbol.type() != Symbol.SymbolType.Primitive && symbol.type() != Symbol.SymbolType.Predefined && lhs.path().equals(symbol.path())) || (lhs.matches(symbol) && lhs.nesting() > 0)) {
                        continue;
                    }
                    rule.add(clause);
                }
            }
        }
    }
    
    GrammarRule getRule(final Symbol lhs) {
        for (final GrammarRule rule : this.rules) {
            if (rule.lhs().matches(lhs)) {
                return rule;
            }
        }
        final GrammarRule rule2 = new GrammarRule(lhs);
        this.rules.add(rule2);
        return rule2;
    }
    
    GrammarRule findRule(final Symbol lhs) {
        for (final GrammarRule rule : this.rules) {
            if (rule.lhs().matches(lhs)) {
                return rule;
            }
        }
        return null;
    }
    
    public PackageInfo findPackage(final String path) {
        for (final PackageInfo pack : this.packages) {
            if (path.equalsIgnoreCase(pack.path())) {
                return pack;
            }
        }
        return null;
    }
    
    void expandConstructors(final GrammarRule rule, final Symbol symbol) {
        if (symbol.hidden()) {
            return;
        }
        final Class<?> cls = symbol.cls();
        if (cls == null) {
            return;
        }
        String alias = null;
        final Annotation[] annotations2;
        final Annotation[] classAnnotations = annotations2 = cls.getAnnotations();
        for (final Annotation annotation : annotations2) {
            if (annotation instanceof Alias) {
                final Alias anno = (Alias)annotation;
                alias = anno.alias();
            }
        }
        List<Executable> ctors;
        if (cls.getConstructors().length > 0) {
            ctors = Arrays.asList(cls.getConstructors());
        }
        else {
            ctors = new ArrayList<>();
            final Method[] declaredMethods;
            final Method[] methods = declaredMethods = cls.getDeclaredMethods();
            for (final Method method : declaredMethods) {
                if (method.getName().equals("construct") && Modifier.isStatic(method.getModifiers())) {
                    ctors.add(method);
                }
            }
        }
        for (final Executable ctor : ctors) {
            final List<ClauseArg> constructorArgs = new ArrayList<>();
            final Annotation[] ctorAnnotations = ctor.getAnnotations();
            final Annotation[][] annotations = ctor.getParameterAnnotations();
            final Type[] types = ctor.getGenericParameterTypes();
            final Parameter[] parameters = ctor.getParameters();
            boolean isHidden = false;
            for (int ca = 0; ca < ctorAnnotations.length; ++ca) {
                final Annotation ann = ctorAnnotations[ca];
                if (ann.annotationType().getName().equals("annotations.Hide")) {
                    isHidden = true;
                }
            }
            int prevOrType = 0;
            int prevAndType = 0;
            int orGroup = 0;
            int andGroup = 0;
            for (int n = 0; n < types.length; ++n) {
                final Type type = types[n];
                final String typeName = type.getTypeName();
                final Symbol temp = new Symbol(Symbol.SymbolType.Class, typeName, alias, cls);
                temp.setNesting(0);
                final Symbol symbolP = this.findSymbolMatch(temp);
                if (symbolP != null) {
                    String label = (n < parameters.length && parameters[n].isNamePresent()) ? parameters[n].getName() : null;
                    boolean optional = false;
                    boolean isNamed = false;
                    int orType = 0;
                    int andType = 0;
                    for (int a = 0; a < annotations[n].length; ++a) {
                        final Annotation ann2 = annotations[n][a];
                        if (ann2.annotationType().getName().equals("annotations.Opt")) {
                            optional = true;
                        }
                        if (ann2.annotationType().getName().equals("annotations.Name")) {
                            isNamed = true;
                        }
                        if (ann2.annotationType().getName().equals("annotations.Or")) {
                            orType = 1;
                        }
                        if (ann2.annotationType().getName().equals("annotations.Or2")) {
                            if (orType != 0) {
                                System.out.println("** Both @Or and @Or2 specified for label.");
                            }
                            orType = 2;
                        }
                        if (ann2.annotationType().getName().equals("annotations.And")) {
                            andType = 1;
                        }
                        if (ann2.annotationType().getName().equals("annotations.And2")) {
                            if (andType != 0) {
                                System.out.println("** Both @And and @And2 specified for label.");
                            }
                            andType = 2;
                        }
                    }
                    if (!isNamed) {
                        label = null;
                    }
                    if (orType != 0 && orType != prevOrType) {
                        ++orGroup;
                    }
                    if (andType != 0 && andType != prevAndType) {
                        ++andGroup;
                    }
                    final ClauseArg arg = new ClauseArg(symbolP, label, optional, (orType == 0) ? 0 : orGroup, (andType == 0) ? 0 : andGroup);
                    int nesting = 0;
                    for (int c = 0; c < typeName.length() - 1; ++c) {
                        if (typeName.charAt(c) == '[' && typeName.charAt(c + 1) == ']') {
                            ++nesting;
                        }
                    }
                    if (nesting > 0) {
                        arg.setNesting(nesting);
                    }
                    constructorArgs.add(arg);
                    prevOrType = orType;
                    prevAndType = andType;
                }
            }
            final Clause clause = new Clause(symbol, constructorArgs, isHidden);
            rule.add(clause);
        }
    }
    
    Symbol findSymbolMatch(final Symbol symbol) {
        for (final Symbol sym : this.symbols) {
            if (sym.matches(symbol)) {
                return sym;
            }
        }
        return null;
    }
    
    void crossReferenceSubclasses() {
        for (final Symbol symbol : this.symbols) {
            if (symbol.type() != Symbol.SymbolType.Class) {
                continue;
            }
            if (symbol.hidden()) {
                continue;
            }
            final Class<?> cls = symbol.cls();
            if (cls == null) {
                continue;
            }
            final Class<?> clsSuper = cls.getSuperclass();
            if (clsSuper == null) {
                continue;
            }
            final Symbol symbolSuper = this.findSymbolByPath(clsSuper.getName());
            if (symbolSuper == null) {
                continue;
            }
            final GrammarRule ruleSuper = this.findRule(symbolSuper);
            if (ruleSuper == null) {
                continue;
            }
            final Clause clause = new Clause(symbol);
            if (ruleSuper.containsClause(clause)) {
                continue;
            }
            ruleSuper.add(clause);
        }
    }
    
    void addReturnTypeClauses() {
        for (final Symbol symbol : this.symbols) {
            if (symbol.type() == Symbol.SymbolType.Class) {
                if (symbol.matches(symbol.returnType())) {
                    continue;
                }
                if (symbol.hidden()) {
                    continue;
                }
                final GrammarRule rule = this.findRule(symbol.returnType());
                if (rule == null) {
                    continue;
                }
                final Clause clause = new Clause(symbol);
                if (rule.containsClause(clause)) {
                    continue;
                }
                rule.add(clause);
            }
        }
    }
    
    void addApplicationConstantsToRule() {
        Symbol symbolInt = null;
        for (final Symbol symbol : this.symbols) {
            if (symbol.grammarLabel().equals("int")) {
                symbolInt = symbol;
                break;
            }
        }
        if (symbolInt == null) {
            throw new RuntimeException("Failed to find symbol for <int>.");
        }
        final GrammarRule ruleInt = this.findRule(symbolInt);
        if (ruleInt == null) {
            throw new RuntimeException("Failed to find <int> rule.");
        }
        for (int cs = 0; cs < Grammar.ApplicationConstants.length; ++cs) {
            for (final Symbol symbol2 : this.symbols) {
                if (symbol2.grammarLabel().equals(Grammar.ApplicationConstants[cs][0])) {
                    ruleInt.add(new Clause(symbol2));
                    break;
                }
            }
        }
    }
    
    void linkToPackages() {
        for (final Symbol symbol : this.symbols) {
            symbol.setPack(this.findPackage(symbol.notionalLocation()));
        }
        for (final GrammarRule rule : this.rules) {
            final PackageInfo pack = rule.lhs().pack();
            if (pack != null) {
                pack.add(rule);
            }
        }
    }
    
    void setDisplayOrder(final Symbol rootSymbol) {
        for (final PackageInfo pack : this.packages) {
            pack.listAlphabetically();
            prioritiseSuperClasses(pack);
            prioritisePackageClass(pack);
        }
        this.setPackageOrder(rootSymbol);
    }
    
    static void prioritisePackageClass(final PackageInfo pack) {
        final List<GrammarRule> promote = new ArrayList<>();
        for (int r = pack.rules().size() - 1; r >= 0; --r) {
            final GrammarRule rule = pack.rules().get(r);
            if (pack.path().contains(rule.lhs().grammarLabel().toLowerCase()) && rule.lhs().grammarLabel().length() >= 3) {
                pack.remove(r);
                promote.add(rule);
            }
        }
        final Iterator<GrammarRule> iterator = promote.iterator();
        while (iterator.hasNext()) {
            final GrammarRule rule = iterator.next();
            pack.add(0, rule);
        }
        promote.clear();
        for (int r = pack.rules().size() - 1; r >= 0; --r) {
            final GrammarRule rule = pack.rules().get(r);
            if (pack.path().equals(rule.lhs().name().toLowerCase())) {
                pack.remove(r);
                promote.add(rule);
            }
        }
        final Iterator<GrammarRule> iterator2 = promote.iterator();
        while (iterator2.hasNext()) {
            final GrammarRule rule = iterator2.next();
            pack.add(0, rule);
        }
    }
    
    static void prioritiseSuperClasses(final PackageInfo pack) {
        for (int n = 0; n < pack.rules().size(); ++n) {
            final GrammarRule rule = pack.rules().get(n);
            if (rule.lhs().name().equalsIgnoreCase(pack.shortName())) {
                pack.remove(n);
                pack.add(0, rule);
            }
        }
        for (int a = 0; a < pack.rules().size(); ++a) {
            final GrammarRule ruleA = pack.rules().get(a);
            for (int b = a + 1; b < pack.rules().size(); ++b) {
                final GrammarRule ruleB = pack.rules().get(b);
                if (ruleB.lhs().isCollectionOf(ruleA.lhs())) {
                    pack.remove(b);
                    pack.remove(a);
                    pack.add(a, ruleB);
                    pack.add(b, ruleA);
                }
            }
        }
    }
    
    void linkDirectionsRules() {
        final Symbol symbolDirectionFacing = this.findSymbolByPath("game.util.directions.DirectionFacing");
        symbolDirectionFacing.setUsedInGrammar(true);
        final GrammarRule ruleDirectionFacing = this.findRule(symbolDirectionFacing);
        final Symbol symbolAbsolute = this.findSymbolByPath("game.util.directions.AbsoluteDirection");
        symbolAbsolute.setUsedInGrammar(true);
        final Symbol symbolRelative = this.findSymbolByPath("game.util.directions.RelativeDirection");
        symbolRelative.setUsedInGrammar(true);
        final Symbol symbolFacing = this.findSymbolByPath("game.functions.directions.Directions");
        symbolFacing.setUsedInGrammar(true);
        final Symbol symbolIf = this.findSymbolByPath("game.functions.directions.If");
        symbolIf.setUsedInGrammar(true);
        ruleDirectionFacing.add(new Clause(symbolAbsolute));
        ruleDirectionFacing.add(new Clause(symbolRelative));
        ruleDirectionFacing.add(new Clause(symbolFacing));
        ruleDirectionFacing.add(new Clause(symbolIf));
        final Symbol symbolDirection = this.findSymbolByPath("game.util.directions.Direction");
        symbolDirection.setUsedInGrammar(true);
        final GrammarRule ruleDirection = this.findRule(symbolDirection);
        ruleDirection.add(new Clause(symbolAbsolute));
        ruleDirection.add(new Clause(symbolRelative));
    }
    
    void handleDimFunctions() {
        final Symbol symbolDim = this.findSymbolByPath("game.functions.dim.BaseDimFunction");
        symbolDim.setUsedInGrammar(true);
        final GrammarRule ruleDim = this.findRule(symbolDim);
        Symbol symbolInt = null;
        for (final Symbol symbol : this.symbols) {
            if (symbol.grammarLabel().equals("int")) {
                symbolInt = symbol;
                break;
            }
        }
        if (symbolInt == null) {
            throw new RuntimeException("Failed to find symbol for <int>.");
        }
        ruleDim.add(new Clause(symbolInt));
    }
    
    void visitSymbols(final Symbol rootSymbol) {
        if (rootSymbol == null) {
            System.out.println("** GrammarWriter.visitSymbols() error: Null root symbol.");
            return;
        }
        final boolean isGame = rootSymbol.name().contains("Game");
        this.visitSymbol(rootSymbol, 0, isGame);
    }
    
    void visitSymbol(final Symbol symbol, final int depth, final boolean isGame) {
        if (symbol == null) {
            return;
        }
        if (symbol.depth() == -1) {
            symbol.setDepth(depth);
        }
        else if (depth < symbol.depth()) {
            symbol.setDepth(depth);
        }
        if (symbol.visited()) {
            return;
        }
        if (isGame) {
            symbol.setUsedInGrammar(true);
        }
        else {
            symbol.setUsedInMetadata(true);
        }
        symbol.setVisited(true);
        if (symbol.type() == Symbol.SymbolType.Constant) {
            return;
        }
        if (symbol.rule() == null || symbol.rule().rhs() == null) {
            return;
        }
        if (symbol.rule().rhs() == null) {
            System.out.println("* Symbol with null expression: " + symbol.grammarLabel());
        }
        final boolean isVisible = !symbol.isAbstract() && !symbol.hidden();
        final int nextDepth = depth + (isVisible ? 1 : 0);
        for (final Clause clause : symbol.rule().rhs()) {
            this.visitSymbol(clause.symbol(), nextDepth, isGame);
            for (final GrammarRule ruleC : this.rules) {
                if (ruleC.lhs().validReturnType(clause)) {
                    this.visitSymbol(ruleC.lhs().returnType(), nextDepth, isGame);
                }
            }
            if (clause.args() != null) {
                for (final ClauseArg arg : clause.args()) {
                    this.visitSymbol(arg.symbol(), nextDepth, isGame);
                    for (final GrammarRule ruleA : this.rules) {
                        if (ruleA.lhs().validReturnType(arg)) {
                            this.visitSymbol(ruleA.lhs().returnType(), nextDepth, isGame);
                        }
                    }
                }
            }
        }
    }
    
    void setPackageOrder(final Symbol rootSymbol) {
        this.packageOrder.clear();
        if (rootSymbol == null) {
            System.out.println("** GrammarWriter.setPackageOrder() error: Null root symbol.");
            return;
        }
        for (final Symbol symbol : this.symbols) {
            symbol.setVisited(false);
        }
        this.setPackageOrderRecurse(rootSymbol);
        final String[] packsToMove = { "game.functions", "game.util", "game.types" };
        for (int pm = 0; pm < packsToMove.length; ++pm) {
            for (int p = this.packageOrder.size() - 1; p >= 0; --p) {
                if (this.packageOrder.get(p).path().contains(packsToMove[pm])) {
                    final PackageInfo packInfo = this.packageOrder.get(p);
                    this.packageOrder.remove(p);
                    this.packageOrder.add(packInfo);
                }
            }
        }
    }
    
    void setPackageOrderRecurse(final Symbol symbol) {
        if (symbol == null || symbol.visited() || symbol.type() == Symbol.SymbolType.Constant) {
            return;
        }
        symbol.setVisited(true);
        if (symbol.rule() == null || symbol.rule().rhs() == null) {
            return;
        }
        final PackageInfo pack = symbol.pack();
        if (pack == null) {
            return;
        }
        int p;
        for (p = 0; p < this.packageOrder.size() && !this.packageOrder.get(p).path().equals(pack.path()); ++p) {}
        if (p >= this.packageOrder.size()) {
            this.packageOrder.add(pack);
        }
        if (symbol.rule().rhs() == null) {
            System.out.println("* Symbol with null expression: " + symbol.grammarLabel());
        }
        for (final Clause clause : symbol.rule().rhs()) {
            this.setPackageOrderRecurse(clause.symbol());
            for (final GrammarRule ruleC : this.rules) {
                if (ruleC.lhs().validReturnType(clause)) {
                    this.setPackageOrderRecurse(ruleC.lhs().returnType());
                }
            }
            if (clause.args() != null) {
                for (final ClauseArg arg : clause.args()) {
                    this.setPackageOrderRecurse(arg.symbol());
                    for (final GrammarRule ruleA : this.rules) {
                        if (ruleA.lhs().validReturnType(arg)) {
                            this.setPackageOrderRecurse(ruleA.lhs().returnType());
                        }
                    }
                }
            }
        }
    }
    
    void removeRedundantFunctionNames() {
        for (final GrammarRule rule : this.rules) {
            for (int f = 0; f < this.Functions.length; ++f) {
                if (rule.lhs().grammarLabel().equalsIgnoreCase(this.Functions[f][0])) {
                    rule.lhs().setUsedInGrammar(false);
                    rule.lhs().setUsedInMetadata(false);
                }
            }
            for (int c = rule.rhs().size() - 1; c >= 0; --c) {
                final Clause clause = rule.rhs().get(c);
                for (int f2 = 0; f2 < this.Functions.length; ++f2) {
                    if (clause.symbol().grammarLabel().equalsIgnoreCase(this.Functions[f2][0])) {
                        rule.remove(c);
                    }
                }
            }
        }
    }
    
    void alphabetiseRuleClauses() {
        for (final GrammarRule rule : this.rules) {
            rule.alphabetiseClauses();
        }
    }
    
    void removeDuplicateClauses() {
        for (final GrammarRule rule : this.rules) {
            for (int ca = rule.rhs().size() - 1; ca >= 0; --ca) {
                final Clause clauseA = rule.rhs().get(ca);
                if (!clauseA.isConstructor()) {
                    boolean doRemove = false;
                    for (int cb = ca - 1; cb >= 0; --cb) {
                        final Clause clauseB = rule.rhs().get(cb);
                        if (clauseA.matches(clauseB)) {
                            doRemove = true;
                            break;
                        }
                    }
                    if (doRemove) {
                        rule.remove(ca);
                    }
                }
            }
        }
    }
    
    void filterOutPrimitiveWrappers() {
        for (final GrammarRule rule : this.rules) {
            if ((rule.lhs().grammarLabel().equals("Integer") && rule.rhs().size() == 1 && rule.rhs().get(0).symbol().grammarLabel().equals("Integer")) || (rule.lhs().grammarLabel().equals("Float") && rule.rhs().size() == 1 && rule.rhs().get(0).symbol().grammarLabel().equals("Float")) || (rule.lhs().grammarLabel().equals("Boolean") && rule.rhs().size() == 1 && rule.rhs().get(0).symbol().grammarLabel().equals("Boolean")) || (rule.lhs().grammarLabel().equals("String") && rule.rhs().size() == 1 && rule.rhs().get(0).symbol().grammarLabel().equals("String"))) {
                rule.lhs().setUsedInGrammar(false);
                rule.lhs().setUsedInMetadata(false);
            }
        }
        Symbol symbolInt = null;
        Symbol symbolFloat = null;
        Symbol symbolBoolean = null;
        Symbol symbolString = null;
        for (final Symbol symbol : this.symbols) {
            if (symbol.grammarLabel().equals("int")) {
                symbolInt = symbol;
            }
            if (symbol.grammarLabel().equals("float")) {
                symbolFloat = symbol;
            }
            if (symbol.grammarLabel().equals("boolean")) {
                symbolBoolean = symbol;
            }
            if (symbol.grammarLabel().equals("String")) {
                symbolString = symbol;
            }
        }
        for (final GrammarRule rule2 : this.rules) {
            for (final Clause clause : rule2.rhs()) {
                if (clause != null && clause.args() != null) {
                    for (final ClauseArg arg : clause.args()) {
                        if (arg.symbol().grammarLabel().equals("Integer")) {
                            arg.setSymbol(symbolInt);
                        }
                        if (arg.symbol().grammarLabel().equals("Float")) {
                            arg.setSymbol(symbolFloat);
                        }
                        if (arg.symbol().grammarLabel().equals("Boolean")) {
                            arg.setSymbol(symbolBoolean);
                        }
                        if (arg.symbol().grammarLabel().equals("String")) {
                            arg.setSymbol(symbolString);
                        }
                    }
                }
            }
        }
    }
    
    void instantiateSingleEnums() {
        for (final GrammarRule rule : this.rules) {
            for (final Clause clause : rule.rhs()) {
                if (clause != null && clause.args() != null) {
                    for (int a = clause.args().size() - 1; a >= 0; --a) {
                        final ClauseArg arg = clause.args().get(a);
                        final Symbol symbol = arg.symbol();
                        if (symbol.cls() != null && symbol.cls().isEnum()) {
                            final GrammarRule ruleS = symbol.rule();
                            if (ruleS != null && ruleS.rhs().size() == 1) {
                                final Clause enumValue = ruleS.rhs().get(0);
                                arg.setSymbol(enumValue.symbol());
                            }
                        }
                    }
                }
            }
        }
        for (int r = this.rules.size() - 1; r >= 0; --r) {
            final GrammarRule rule = this.rules.get(r);
            if (rule.lhs().cls() != null && rule.lhs().cls().isEnum() && rule.rhs().size() == 1) {
                this.rules.remove(r);
            }
        }
    }
    
    public String getLudemes() {
        String str = "";
        for (final Symbol s : this.symbols) {
            final String[] pathList = s.path().split("\\.");
            String strAbrev = "";
            for (int i = 0; i < pathList.length; ++i) {
                strAbrev += pathList[i].charAt(0);
            }
            str = str + "\n" + strAbrev + " : " + s.toString().replace("<", "").replace(">", "");
        }
        return str;
    }
    
    public void export(final String fileName) throws IOException {
        final File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fw = new FileWriter(file.getName(), false);
        final BufferedWriter writer = new BufferedWriter(fw);
        final String str = this.toString();
        writer.write(str);
        writer.close();
    }
    
    @Override
    public String toString() {
        String str = "";
        for (final PackageInfo pack : this.packageOrder) {
            final String strP = pack.toString();
            str += strP;
        }
        for (int f = 0; f < this.Functions.length; ++f) {
            String name = this.Functions[f][0];
            name = name.substring(0, 1).toLowerCase() + name.substring(1);
            str = str.replace(name, this.Functions[f][1]);
        }
        str = str.replace(" )", ")");
        str = str.replace("<String>", "string");
        return str;
    }
    
    static {
        Primitives = new String[][] { { "int", "game.functions.ints" }, { "boolean", "game.functions.booleans" }, { "float", "game.functions.floats" } };
        Predefined = new String[][] { { "java.lang.Integer", "game.functions.ints", "java.lang", "integer" }, { "java.lang.Boolean", "game.functions.booleans", "java.lang", "boolean" }, { "java.lang.Float", "game.functions.floats", "java.lang", "float" }, { "java.lang.String", "game.types", "java.lang", "string" } };
        ApplicationConstants = new String[][] { { "Off", "int", "global", "-1" }, { "End", "int", "global", "-2" }, { "Undefined", "int", "global", "-1" } };
        Grammar.singleton = null;
    }
}
