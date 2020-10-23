package root;/*
 * Decompiled with CFR 0.150.
 */

import java.io.PrintStream;
import java.util.*;

public final class CommandLineArgParse {
    protected final boolean caseSensitive;
    protected final String description;
    protected final List<ArgOption> namelessOptions = new ArrayList<>();
    protected final Map<String, ArgOption> namedOptions = new HashMap<>();
    protected final List<ArgOption> requiredNamedOptions = new ArrayList<>();
    protected final List<ArgOption> allOptions = new ArrayList<>();
    protected final List<Object> providedNamelessValues = new ArrayList<>();
    protected final Map<String, Object> providedValues = new HashMap<>();

    public CommandLineArgParse() {
        this(true);
    }

    public CommandLineArgParse(boolean caseSensitive) {
        this(caseSensitive, null);
    }

    public CommandLineArgParse(boolean caseSensitive, String description) {
        this.caseSensitive = caseSensitive;
        this.description = description;
    }

    public void addOption(ArgOption argOption) {
        if (argOption.names != null) {
            for (String name : argOption.names) {
                if (!this.caseSensitive) {
                    name = name.toLowerCase();
                }
                if (!name.equals("-h") && !name.equals("--help")) continue;
                System.err.println("Not adding option! Cannot use arg name: " + name + ". This is reserved for help message.");
                return;
            }
        } else {
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
            argOption.type = argOption.defaultVal != null ? (argOption.defaultVal instanceof Boolean ? OptionTypes.Boolean : (argOption.defaultVal instanceof Integer ? OptionTypes.Int : (argOption.defaultVal instanceof Float ? OptionTypes.Float : (argOption.defaultVal instanceof Double ? OptionTypes.Double : OptionTypes.String)))) : (argOption.expectsList() ? OptionTypes.String : (argOption.numVals == 1 ? OptionTypes.String : OptionTypes.Boolean));
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
            Object[] arrobject = argOption.legalVals;
            int n = arrobject.length;
            for (int name = 0; name < n; ++name) {
                Object legalVal = arrobject[name];
                if (!legalVal.equals(argOption.defaultVal)) continue;
                found = true;
                break;
            }
            if (!found) {
                System.err.println("Error: default value " + argOption.defaultVal + " is not legal. Legal values = " + Arrays.toString(argOption.legalVals));
                return;
            }
        }
        if (argOption.names == null) {
            this.namelessOptions.add(argOption);
        } else {
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

    public boolean parseArguments(String[] args) {
        String currentToken = null;
        int nextNamelessOption = 0;
        ArgOption currentOption = null;
        String currentOptionName = null;
        ArrayList<Object> currentValues = null;
        try {
            for (int i = 0; i < args.length; ++i) {
                String token;
                currentToken = args[i];
                String string = token = this.caseSensitive ? currentToken : currentToken.toLowerCase();
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
                    currentValues = new ArrayList(1);
                    currentValues.add(CommandLineArgParse.tokenToVal(token, currentOption.type));
                    ++nextNamelessOption;
                    continue;
                }
                if (this.namedOptions.containsKey(token)) {
                    if (!this.finishArgOption(currentOption, currentOptionName, currentValues)) {
                        return false;
                    }
                    currentOption = this.namedOptions.get(token);
                    currentOptionName = currentToken;
                    currentValues = new ArrayList<>();
                    continue;
                }
                currentValues.add(CommandLineArgParse.tokenToVal(token, currentOption.type));
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
        for (ArgOption option : this.requiredNamedOptions) {
            String key = this.caseSensitive ? option.names[0] : option.names[0].toLowerCase();
            if (this.providedValues.containsKey(key)) continue;
            System.err.println("Missing value for required option: " + option.names[0]);
            return false;
        }
        return true;
    }

    public Object getValue(int i) {
        return this.providedNamelessValues.get(i);
    }

    public boolean getValueBool(int i) {
        return (Boolean)this.providedNamelessValues.get(i);
    }

    public int getValueInt(int i) {
        return (Integer)this.providedNamelessValues.get(i);
    }

    public float getValueFloat(int i) {
        return ((Float)this.providedNamelessValues.get(i)).floatValue();
    }

    public double getValueDouble(int i) {
        return (Double)this.providedNamelessValues.get(i);
    }

    public String getValueString(int i) {
        return (String)this.providedNamelessValues.get(i);
    }

    public Object getValue(String name) {
        String key = name;
        if (!this.caseSensitive) {
            key = key.toLowerCase();
        }
        return this.providedValues.getOrDefault(key, this.namedOptions.get(key).defaultVal);
    }

    public boolean getValueBool(String name) {
        return (Boolean)this.getValue(name);
    }

    public int getValueInt(String name) {
        return (Integer)this.getValue(name);
    }

    public float getValueFloat(String name) {
        return ((Float)this.getValue(name)).floatValue();
    }

    public double getValueDouble(String name) {
        return (Double)this.getValue(name);
    }

    public String getValueString(String name) {
        return (String)this.getValue(name);
    }

    public void printHelp(PrintStream out) {
        int i;
        if (this.description != null) {
            out.print(this.description);
        } else {
            out.print("No program description.");
        }
        out.println();
        out.println();
        if (!this.namelessOptions.isEmpty()) {
            out.println("Positional arguments:");
            for (ArgOption option2 : this.namelessOptions) {
                CommandLineArgParse.printOptionLine(option2, out);
            }
            out.println();
        }
        out.println("Required named arguments:");
        ArgOption option2;
        for (i = 0; i < this.allOptions.size(); ++i) {
            option2 = this.allOptions.get(i);
            if (option2.names == null || !option2.required) continue;
            CommandLineArgParse.printOptionLine(option2, out);
        }
        out.println();
        out.println("Optional named arguments:");
        out.println(" -h, --help                                                      Show this help message.");
        for (i = 0; i < this.allOptions.size(); ++i) {
            option2 = this.allOptions.get(i);
            if (option2.names == null || option2.required) continue;
            CommandLineArgParse.printOptionLine(option2, out);
        }
    }

    private static void printOptionLine(ArgOption option, PrintStream out) {
        StringBuilder sb = new StringBuilder();
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
            } else {
                sb.append(" " + option.type.toString().toUpperCase());
            }
        } else {
            sb.append(" ");
            for (int i = 0; i < option.names.length; ++i) {
                sb.append(option.names[i]);
                if (i + 1 >= option.names.length) continue;
                sb.append(", ");
            }
            String metaVar = option.names[0].toUpperCase();
            while (metaVar.startsWith("-")) {
                metaVar = metaVar.substring(1);
            }
            metaVar = metaVar.replaceAll("-", "_");
            if (option.numValsStr == null) {
                if (option.numVals > 0) {
                    if (option.numVals == 1) {
                        sb.append(" " + metaVar);
                    } else {
                        for (int i = 1; i <= option.numVals; ++i) {
                            sb.append(" " + metaVar + "_" + i);
                        }
                    }
                }
            } else if (option.numValsStr.equals("+")) {
                sb.append(" " + metaVar + "_1");
                sb.append(" [ " + metaVar + "_* ... ]");
            } else if (option.numValsStr.equals("*")) {
                sb.append(" [ " + metaVar + "_* ... ]");
            }
        }
        if (sb.length() >= 65) {
            sb.append("\t");
        } else {
            while (sb.length() < 65) {
                sb.append(" ");
            }
        }
        sb.append(option.help);
        out.println(sb.toString());
    }

    private boolean finishArgOption(ArgOption currentOption, String currentOptionName, List<Object> currentValues) {
        if (currentOption != null) {
            if (currentOption.numValsStr == null) {
                if (currentValues.size() != currentOption.numVals) {
                    System.err.println("Error: " + currentOptionName + " requires " + currentOption.numVals + " values, but received " + currentValues.size() + " values.");
                    return false;
                }
            } else if (currentOption.numValsStr.equals("+") && currentValues.size() == 0) {
                System.err.println("Error: " + currentOptionName + " requires more than 0 values, but only received 0 values.");
                return false;
            }
            if (currentOption.legalVals != null) {
                for (Object e : currentValues) {
                    boolean found = false;
                    for (Object legalVal : currentOption.legalVals) {
                        if (!e.equals(legalVal)) continue;
                        found = true;
                        break;
                    }
                    if (found) continue;
                    System.err.println("Error: " + e + " is an illegal value. Legal values = " + Arrays.toString(currentOption.legalVals));
                    return false;
                }
            }
            if (currentOption.names == null) {
                if (currentOption.expectsList()) {
                    this.providedNamelessValues.add(currentValues);
                } else if (currentValues.size() == 0 && currentOption.type == OptionTypes.Boolean) {
                    this.providedNamelessValues.add(Boolean.TRUE);
                } else {
                    this.providedNamelessValues.add(currentValues.get(0));
                }
            } else {
                for (String name : currentOption.names) {
                    if (!this.caseSensitive) {
                        name = name.toLowerCase();
                    }
                    if (currentOption.expectsList()) {
                        this.providedValues.put(name, currentValues);
                        continue;
                    }
                    if (currentValues.size() == 0 && currentOption.type == OptionTypes.Boolean) {
                        this.providedValues.put(name, Boolean.TRUE);
                        continue;
                    }
                    this.providedValues.put(name, currentValues.get(0));
                }
            }
        }
        return true;
    }

    private static Object tokenToVal(String token, OptionTypes type) {
        if (type == OptionTypes.Boolean) {
            return Boolean.parseBoolean(token);
        }
        if (type == OptionTypes.Double) {
            return Double.parseDouble(token);
        }
        if (type == OptionTypes.Float) {
            return Float.valueOf(Float.parseFloat(token));
        }
        if (type == OptionTypes.Int) {
            return Integer.parseInt(token);
        }
        return token;
    }

    public static final class ArgOption {
        protected String[] names = null;
        protected OptionTypes type = null;
        protected int numVals = 0;
        protected String numValsStr = null;
        protected Object defaultVal = null;
        protected boolean required = false;
        protected Object[] legalVals = null;
        protected String help = "";

        public ArgOption withNames(String ... optionNames) {
            this.names = optionNames;
            return this;
        }

        public ArgOption withType(OptionTypes optionType) {
            this.type = optionType;
            return this;
        }

        public ArgOption withNumVals(int optionNumVals) {
            this.numVals = optionNumVals;
            return this;
        }

        public ArgOption withNumVals(String optionNumValsStr) {
            if (StringRoutines.isInteger(optionNumValsStr)) {
                return this.withNumVals(Integer.parseInt(optionNumValsStr));
            }
            this.numValsStr = optionNumValsStr;
            return this;
        }

        public ArgOption withDefault(Object optionDefaultVal) {
            this.defaultVal = optionDefaultVal;
            return this;
        }

        public ArgOption setRequired() {
            return this.setRequired(true);
        }

        public ArgOption setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public ArgOption withLegalVals(Object ... optionLegalVals) {
            this.legalVals = optionLegalVals;
            return this;
        }

        public ArgOption help(String optionHelp) {
            this.help = optionHelp;
            return this;
        }

        protected boolean expectsList() {
            if (this.numValsStr != null) {
                return true;
            }
            return this.numVals > 1;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[ArgOption: ");
            if (this.names != null) {
                for (int i = 0; i < this.names.length; ++i) {
                    sb.append(this.names[i]);
                    if (i + 1 >= this.names.length) continue;
                    sb.append(", ");
                }
            }
            sb.append(" type=" + this.type);
            if (this.numValsStr != null) {
                sb.append(" numVals=" + this.numValsStr);
            } else {
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

    public enum OptionTypes {
        Boolean,
        Int,
        Float,
        Double,
        String

    }
}

