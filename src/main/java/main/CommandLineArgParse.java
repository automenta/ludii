// 
// Decompiled by Procyon v0.5.36
// 

package main;

import java.io.PrintStream;
import java.util.*;

public final class CommandLineArgParse
{
    protected final boolean caseSensitive;
    protected final String description;
    protected final List<ArgOption> namelessOptions;
    protected final Map<String, ArgOption> namedOptions;
    protected final List<ArgOption> requiredNamedOptions;
    protected final List<ArgOption> allOptions;
    protected final List<Object> providedNamelessValues;
    protected final Map<String, Object> providedValues;
    
    public CommandLineArgParse() {
        this(true);
    }
    
    public CommandLineArgParse(final boolean caseSensitive) {
        this(caseSensitive, null);
    }
    
    public CommandLineArgParse(final boolean caseSensitive, final String description) {
        this.namelessOptions = new ArrayList<>();
        this.namedOptions = new HashMap<>();
        this.requiredNamedOptions = new ArrayList<>();
        this.allOptions = new ArrayList<>();
        this.providedNamelessValues = new ArrayList<>();
        this.providedValues = new HashMap<>();
        this.caseSensitive = caseSensitive;
        this.description = description;
    }
    
    public void addOption(final ArgOption argOption) {
        if (argOption.names != null) {
            for (String name : argOption.names) {
                if (!this.caseSensitive) {
                    name = name.toLowerCase();
                }
                if (name.equals("-h") || name.equals("--help")) {
                    System.err.println("Not adding option! Cannot use arg name: " + name + ". This is reserved for help message.");
                    return;
                }
            }
        }
        else {
            if (argOption.expectsList()) {
                System.err.println("Multi-valued nameless arguments are not currently supported!");
                return;
            }
            if (!this.namedOptions.isEmpty()) {
                System.err.println("Adding nameless options after named options is not currently supported!");
                return;
            }
        }
        if (argOption.numValsStr != null && !argOption.numValsStr.equals("+") && !argOption.numValsStr.equals("*")) {
            System.err.println("Not adding option! Invalid numVals specified: " + argOption.numValsStr);
            return;
        }
        if (argOption.type == null) {
            if (argOption.defaultVal != null) {
                if (argOption.defaultVal instanceof Boolean) {
                    argOption.type = OptionTypes.Boolean;
                }
                else if (argOption.defaultVal instanceof Integer) {
                    argOption.type = OptionTypes.Int;
                }
                else if (argOption.defaultVal instanceof Float) {
                    argOption.type = OptionTypes.Float;
                }
                else if (argOption.defaultVal instanceof Double) {
                    argOption.type = OptionTypes.Double;
                }
                else {
                    argOption.type = OptionTypes.String;
                }
            }
            else if (argOption.expectsList()) {
                argOption.type = OptionTypes.String;
            }
            else if (argOption.numVals == 1) {
                argOption.type = OptionTypes.String;
            }
            else {
                argOption.type = OptionTypes.Boolean;
            }
        }
        if (argOption.type != OptionTypes.Boolean && argOption.numValsStr == null && argOption.numVals == 0) {
            System.err.println("Not adding option! Cannot accept 0 values for non-boolean option.");
            return;
        }
        if (argOption.type == OptionTypes.Boolean && argOption.defaultVal == null) {
            argOption.defaultVal = Boolean.FALSE;
        }
        this.allOptions.add(argOption);
        if (argOption.defaultVal != null && argOption.legalVals != null) {
            boolean found = false;
            for (final Object legalVal : argOption.legalVals) {
                if (legalVal.equals(argOption.defaultVal)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.err.println("Error: default value " + argOption.defaultVal + " is not legal. Legal values = " + Arrays.toString(argOption.legalVals));
                return;
            }
        }
        if (argOption.names == null) {
            this.namelessOptions.add(argOption);
        }
        else {
            for (String name : argOption.names) {
                if (!this.caseSensitive) {
                    name = name.toLowerCase();
                }
                if (this.namedOptions.containsKey(name)) {
                    System.err.println("Error: Duplicate name:" + name);
                }
                this.namedOptions.put(name, argOption);
            }
            if (argOption.required) {
                this.requiredNamedOptions.add(argOption);
            }
        }
    }
    
    public boolean parseArguments(final String[] args) {
        String currentToken = null;
        int nextNamelessOption = 0;
        ArgOption currentOption = null;
        String currentOptionName = null;
        List<Object> currentValues = null;
        try {
            for (int i = 0; i < args.length; ++i) {
                currentToken = args[i];
                final String token = this.caseSensitive ? currentToken : currentToken.toLowerCase();
                if (token.equals("-h") || token.equals("--help")) {
                    this.printHelp(System.out);
                    return false;
                }
                if (nextNamelessOption < this.namelessOptions.size()) {
                    if (!this.finishArgOption(currentOption, currentOptionName, currentValues)) {
                        return false;
                    }
                    if (this.namedOptions.containsKey(token)) {
                        System.err.println("Error: found name \"" + currentToken + "\" while expecting more nameless options.");
                        return false;
                    }
                    currentOption = this.namelessOptions.get(nextNamelessOption);
                    currentOptionName = "NAMELESS_" + nextNamelessOption;
                    currentValues = new ArrayList<>(1);
                    currentValues.add(tokenToVal(token, currentOption.type));
                    ++nextNamelessOption;
                }
                else if (this.namedOptions.containsKey(token)) {
                    if (!this.finishArgOption(currentOption, currentOptionName, currentValues)) {
                        return false;
                    }
                    currentOption = this.namedOptions.get(token);
                    currentOptionName = currentToken;
                    currentValues = new ArrayList<>();
                }
                else {
                    currentValues.add(tokenToVal(token, currentOption.type));
                }
            }
            if (!this.finishArgOption(currentOption, currentOptionName, currentValues)) {
                return false;
            }
        }
        catch (Exception e) {
            System.err.println("Parsing args failed on token \"" + currentToken + "\" with exception:");
            e.printStackTrace();
            System.err.println();
            this.printHelp(System.err);
            System.err.println();
            return false;
        }
        if (this.providedNamelessValues.size() < this.namelessOptions.size()) {
            System.err.println("Missing value for nameless option " + this.providedNamelessValues.size());
            return false;
        }
        for (final ArgOption option : this.requiredNamedOptions) {
            final String key = this.caseSensitive ? option.names[0] : option.names[0].toLowerCase();
            if (!this.providedValues.containsKey(key)) {
                System.err.println("Missing value for required option: " + option.names[0]);
                return false;
            }
        }
        return true;
    }
    
    public Object getValue(final int i) {
        return this.providedNamelessValues.get(i);
    }
    
    public boolean getValueBool(final int i) {
        return (boolean) this.providedNamelessValues.get(i);
    }
    
    public int getValueInt(final int i) {
        return (int) this.providedNamelessValues.get(i);
    }
    
    public float getValueFloat(final int i) {
        return (float) this.providedNamelessValues.get(i);
    }

    public double getValueDouble(final int i) {
        return (double) this.providedNamelessValues.get(i);
    }

    public String getValueString(final int i) {
        return (String) this.providedNamelessValues.get(i);
    }
    
    public Object getValue(final String name) {
        String key = name;
        if (!this.caseSensitive) {
            key = key.toLowerCase();
        }
        return this.providedValues.getOrDefault(key, this.namedOptions.get(key).defaultVal);
    }
    
    public boolean getValueBool(final String name) {
        return (boolean)this.getValue(name);
    }
    
    public int getValueInt(final String name) {
        return (int)this.getValue(name);
    }
    
    public float getValueFloat(final String name) {
        return (float)this.getValue(name);
    }
    
    public double getValueDouble(final String name) {
        return (double)this.getValue(name);
    }
    
    public String getValueString(final String name) {
        return (String)this.getValue(name);
    }
    
    public void printHelp(final PrintStream out) {
        if (this.description != null) {
            out.print(this.description);
        }
        else {
            out.print("No program description.");
        }
        out.println();
        out.println();
        if (!this.namelessOptions.isEmpty()) {
            out.println("Positional arguments:");
            for (final ArgOption option : this.namelessOptions) {
                printOptionLine(option, out);
            }
            out.println();
        }
        out.println("Required named arguments:");
        for (int i = 0; i < this.allOptions.size(); ++i) {
            final ArgOption option = this.allOptions.get(i);
            if (option.names != null && option.required) {
                printOptionLine(option, out);
            }
        }
        out.println();
        out.println("Optional named arguments:");
        out.println(" -h, --help                                                      Show this help message.");
        for (int i = 0; i < this.allOptions.size(); ++i) {
            final ArgOption option = this.allOptions.get(i);
            if (option.names != null && !option.required) {
                printOptionLine(option, out);
            }
        }
    }
    
    private static void printOptionLine(final ArgOption option, final PrintStream out) {
        final StringBuilder sb = new StringBuilder();
        if (option.names == null) {
            if (option.legalVals != null) {
                sb.append(" {");
                for (int i = 0; i < option.legalVals.length; ++i) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(option.legalVals[i]);
                }
                sb.append("}");
            }
            else {
                sb.append(" " + option.type.toString().toUpperCase());
            }
        }
        else {
            sb.append(" ");
            for (int i = 0; i < option.names.length; ++i) {
                sb.append(option.names[i]);
                if (i + 1 < option.names.length) {
                    sb.append(", ");
                }
            }
            String metaVar;
            for (metaVar = option.names[0].toUpperCase(); metaVar.startsWith("-"); metaVar = metaVar.substring(1)) {}
            metaVar = metaVar.replaceAll("-", "_");
            if (option.numValsStr == null) {
                if (option.numVals > 0) {
                    if (option.numVals == 1) {
                        sb.append(" " + metaVar);
                    }
                    else {
                        for (int j = 1; j <= option.numVals; ++j) {
                            sb.append(" " + metaVar + "_" + j);
                        }
                    }
                }
            }
            else if (option.numValsStr.equals("+")) {
                sb.append(" " + metaVar + "_1");
                sb.append(" [ " + metaVar + "_* ... ]");
            }
            else if (option.numValsStr.equals("*")) {
                sb.append(" [ " + metaVar + "_* ... ]");
            }
        }
        if (sb.length() >= 65) {
            sb.append("\t");
        }
        else {
            while (sb.length() < 65) {
                sb.append(" ");
            }
        }
        sb.append(option.help);
        out.println(sb.toString());
    }
    
    private boolean finishArgOption(final ArgOption currentOption, final String currentOptionName, final List<Object> currentValues) {
        if (currentOption != null) {
            if (currentOption.numValsStr == null) {
                if (currentValues.size() != currentOption.numVals) {
                    System.err.println("Error: " + currentOptionName + " requires " + currentOption.numVals + " values, but received " + currentValues.size() + " values.");
                    return false;
                }
            }
            else if (currentOption.numValsStr.equals("+") && currentValues.size() == 0) {
                System.err.println("Error: " + currentOptionName + " requires more than 0 values, but only received 0 values.");
                return false;
            }
            if (currentOption.legalVals != null) {
                for (final Object val : currentValues) {
                    boolean found = false;
                    for (final Object legalVal : currentOption.legalVals) {
                        if (val.equals(legalVal)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.err.println("Error: " + val + " is an illegal value. Legal values = " + Arrays.toString(currentOption.legalVals));
                        return false;
                    }
                }
            }
            if (currentOption.names == null) {
                if (currentOption.expectsList()) {
                    this.providedNamelessValues.add(currentValues);
                }
                else if (currentValues.size() == 0 && currentOption.type == OptionTypes.Boolean) {
                    this.providedNamelessValues.add(Boolean.TRUE);
                }
                else {
                    this.providedNamelessValues.add(currentValues.get(0));
                }
            }
            else {
                for (String name : currentOption.names) {
                    if (!this.caseSensitive) {
                        name = name.toLowerCase();
                    }
                    if (currentOption.expectsList()) {
                        this.providedValues.put(name, currentValues);
                    }
                    else if (currentValues.size() == 0 && currentOption.type == OptionTypes.Boolean) {
                        this.providedValues.put(name, Boolean.TRUE);
                    }
                    else {
                        this.providedValues.put(name, currentValues.get(0));
                    }
                }
            }
        }
        return true;
    }
    
    private static Object tokenToVal(final String token, final OptionTypes type) {
        if (type == OptionTypes.Boolean) {
            return Boolean.parseBoolean(token);
        }
        if (type == OptionTypes.Double) {
            return Double.parseDouble(token);
        }
        if (type == OptionTypes.Float) {
            return Float.parseFloat(token);
        }
        if (type == OptionTypes.Int) {
            return Integer.parseInt(token);
        }
        return token;
    }
    
    public enum OptionTypes
    {
        Boolean, 
        Int, 
        Float, 
        Double, 
        String
    }
    
    public static final class ArgOption
    {
        protected String[] names;
        protected OptionTypes type;
        protected int numVals;
        protected String numValsStr;
        protected Object defaultVal;
        protected boolean required;
        protected Object[] legalVals;
        protected String help;
        
        public ArgOption() {
            this.names = null;
            this.type = null;
            this.numVals = 0;
            this.numValsStr = null;
            this.defaultVal = null;
            this.required = false;
            this.legalVals = null;
            this.help = "";
        }
        
        public ArgOption withNames(final String... optionNames) {
            this.names = optionNames;
            return this;
        }
        
        public ArgOption withType(final OptionTypes optionType) {
            this.type = optionType;
            return this;
        }
        
        public ArgOption withNumVals(final int optionNumVals) {
            this.numVals = optionNumVals;
            return this;
        }
        
        public ArgOption withNumVals(final String optionNumValsStr) {
            if (StringRoutines.isInteger(optionNumValsStr)) {
                return this.withNumVals(Integer.parseInt(optionNumValsStr));
            }
            this.numValsStr = optionNumValsStr;
            return this;
        }
        
        public ArgOption withDefault(final Object optionDefaultVal) {
            this.defaultVal = optionDefaultVal;
            return this;
        }
        
        public ArgOption setRequired() {
            return this.setRequired(true);
        }
        
        public ArgOption setRequired(final boolean required) {
            this.required = required;
            return this;
        }
        
        public ArgOption withLegalVals(final Object... optionLegalVals) {
            this.legalVals = optionLegalVals;
            return this;
        }
        
        public ArgOption help(final String optionHelp) {
            this.help = optionHelp;
            return this;
        }
        
        protected boolean expectsList() {
            return this.numValsStr != null || this.numVals > 1;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[ArgOption: ");
            if (this.names != null) {
                for (int i = 0; i < this.names.length; ++i) {
                    sb.append(this.names[i]);
                    if (i + 1 < this.names.length) {
                        sb.append(", ");
                    }
                }
            }
            sb.append(" type=" + this.type);
            if (this.numValsStr != null) {
                sb.append(" numVals=" + this.numValsStr);
            }
            else {
                sb.append(" numVals=" + this.numVals);
            }
            if (this.defaultVal != null) {
                sb.append(" default=" + this.defaultVal);
            }
            if (this.required) {
                sb.append(" required");
            }
            if (this.legalVals != null) {
                sb.append(" legalVals=" + Arrays.toString(this.legalVals));
            }
            if (this.help.length() > 0) {
                sb.append("\t\t" + this.help);
            }
            sb.append("]");
            return sb.toString();
        }
    }
}
