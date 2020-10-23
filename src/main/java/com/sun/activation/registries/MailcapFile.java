////
//// Decompiled by Procyon v0.5.36
////
//
//package com.sun.activation.registries;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class MailcapFile
//{
//    private final Map type_hash;
//    private final Map fallback_hash;
//    private final Map native_commands;
//    private static boolean addReverse;
//
//    public MailcapFile(final String new_fname) throws IOException {
//        this.type_hash = new HashMap();
//        this.fallback_hash = new HashMap();
//        this.native_commands = new HashMap();
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("new MailcapFile: file " + new_fname);
//        }
//        FileReader reader = null;
//        try {
//            reader = new FileReader(new_fname);
//            this.parse(new BufferedReader(reader));
//        }
//        finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                }
//                catch (IOException ex) {}
//            }
//        }
//    }
//
//    public MailcapFile(final InputStream is) throws IOException {
//        this.type_hash = new HashMap();
//        this.fallback_hash = new HashMap();
//        this.native_commands = new HashMap();
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("new MailcapFile: InputStream");
//        }
//        this.parse(new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1)));
//    }
//
//    public MailcapFile() {
//        this.type_hash = new HashMap();
//        this.fallback_hash = new HashMap();
//        this.native_commands = new HashMap();
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("new MailcapFile: default");
//        }
//    }
//
//    public Map getMailcapList(final String mime_type) {
//        Map search_result = null;
//        Map wildcard_result = null;
//        search_result = this.type_hash.get(mime_type);
//        final int separator = mime_type.indexOf(47);
//        final String subtype = mime_type.substring(separator + 1);
//        if (!subtype.equals("*")) {
//            final String type = mime_type.substring(0, separator + 1) + "*";
//            wildcard_result = this.type_hash.get(type);
//            if (wildcard_result != null) {
//                if (search_result != null) {
//                    search_result = this.mergeResults(search_result, wildcard_result);
//                }
//                else {
//                    search_result = wildcard_result;
//                }
//            }
//        }
//        return search_result;
//    }
//
//    public Map getMailcapFallbackList(final String mime_type) {
//        Map search_result = null;
//        Map wildcard_result = null;
//        search_result = this.fallback_hash.get(mime_type);
//        final int separator = mime_type.indexOf(47);
//        final String subtype = mime_type.substring(separator + 1);
//        if (!subtype.equals("*")) {
//            final String type = mime_type.substring(0, separator + 1) + "*";
//            wildcard_result = this.fallback_hash.get(type);
//            if (wildcard_result != null) {
//                if (search_result != null) {
//                    search_result = this.mergeResults(search_result, wildcard_result);
//                }
//                else {
//                    search_result = wildcard_result;
//                }
//            }
//        }
//        return search_result;
//    }
//
//    public String[] getMimeTypes() {
//        final Set types = new HashSet(this.type_hash.keySet());
//        types.addAll(this.fallback_hash.keySet());
//        types.addAll(this.native_commands.keySet());
//        String[] mts = new String[types.size()];
//        mts = types.toArray(mts);
//        return mts;
//    }
//
//    public String[] getNativeCommands(final String mime_type) {
//        String[] cmds = null;
//        final List v = this.native_commands.get(mime_type.toLowerCase(Locale.ENGLISH));
//        if (v != null) {
//            cmds = new String[v.size()];
//            cmds = v.toArray(cmds);
//        }
//        return cmds;
//    }
//
//    private Map mergeResults(final Map first, final Map second) {
//        final Iterator verb_enum = second.keySet().iterator();
//        final Map clonedHash = new HashMap(first);
//        while (verb_enum.hasNext()) {
//            final String verb = verb_enum.next();
//            List cmdVector = clonedHash.get(verb);
//            if (cmdVector == null) {
//                clonedHash.put(verb, second.get(verb));
//            }
//            else {
//                final List oldV = second.get(verb);
//                cmdVector = new ArrayList(cmdVector);
//                cmdVector.addAll(oldV);
//                clonedHash.put(verb, cmdVector);
//            }
//        }
//        return clonedHash;
//    }
//
//    public void appendToMailcap(final String mail_cap) {
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("appendToMailcap: " + mail_cap);
//        }
//        try {
//            this.parse(new StringReader(mail_cap));
//        }
//        catch (IOException ex) {}
//    }
//
//    private void parse(final Reader reader) throws IOException {
//        final BufferedReader buf_reader = new BufferedReader(reader);
//        String line = null;
//        String continued = null;
//        while ((line = buf_reader.readLine()) != null) {
//            line = line.trim();
//            try {
//                if (line.charAt(0) == '#') {
//                    continue;
//                }
//                if (line.charAt(line.length() - 1) == '\\') {
//                    if (continued != null) {
//                        continued += line.substring(0, line.length() - 1);
//                    }
//                    else {
//                        continued = line.substring(0, line.length() - 1);
//                    }
//                }
//                else if (continued != null) {
//                    continued += line;
//                    try {
//                        this.parseLine(continued);
//                    }
//                    catch (MailcapParseException ex) {}
//                    continued = null;
//                }
//                else {
//                    try {
//                        this.parseLine(line);
//                    }
//                    catch (MailcapParseException ex2) {}
//                }
//            }
//            catch (StringIndexOutOfBoundsException e) {}
//        }
//    }
//
//    protected void parseLine(final String mailcapEntry) throws MailcapParseException, IOException {
//        final MailcapTokenizer tokenizer = new MailcapTokenizer(mailcapEntry);
//        tokenizer.setIsAutoquoting(false);
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("parse: " + mailcapEntry);
//        }
//        int currentToken = tokenizer.nextToken();
//        if (currentToken != 2) {
//            reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
//        }
//        final String primaryType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
//        String subType = "*";
//        currentToken = tokenizer.nextToken();
//        if (currentToken != 47 && currentToken != 59) {
//            reportParseError(47, 59, currentToken, tokenizer.getCurrentTokenValue());
//        }
//        if (currentToken == 47) {
//            currentToken = tokenizer.nextToken();
//            if (currentToken != 2) {
//                reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
//            }
//            subType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
//            currentToken = tokenizer.nextToken();
//        }
//        final String mimeType = primaryType + "/" + subType;
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("  Type: " + mimeType);
//        }
//        final Map commands = new LinkedHashMap();
//        if (currentToken != 59) {
//            reportParseError(59, currentToken, tokenizer.getCurrentTokenValue());
//        }
//        tokenizer.setIsAutoquoting(true);
//        currentToken = tokenizer.nextToken();
//        tokenizer.setIsAutoquoting(false);
//        if (currentToken != 2 && currentToken != 59) {
//            reportParseError(2, 59, currentToken, tokenizer.getCurrentTokenValue());
//        }
//        if (currentToken == 2) {
//            List v = this.native_commands.get(mimeType);
//            if (v == null) {
//                v = new ArrayList();
//                v.add(mailcapEntry);
//                this.native_commands.put(mimeType, v);
//            }
//            else {
//                v.add(mailcapEntry);
//            }
//        }
//        if (currentToken != 59) {
//            currentToken = tokenizer.nextToken();
//        }
//        if (currentToken == 59) {
//            boolean isFallback = false;
//            do {
//                currentToken = tokenizer.nextToken();
//                if (currentToken != 2) {
//                    reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
//                }
//                final String paramName = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
//                currentToken = tokenizer.nextToken();
//                if (currentToken != 61 && currentToken != 59 && currentToken != 5) {
//                    reportParseError(61, 59, 5, currentToken, tokenizer.getCurrentTokenValue());
//                }
//                if (currentToken == 61) {
//                    tokenizer.setIsAutoquoting(true);
//                    currentToken = tokenizer.nextToken();
//                    tokenizer.setIsAutoquoting(false);
//                    if (currentToken != 2) {
//                        reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
//                    }
//                    final String paramValue = tokenizer.getCurrentTokenValue();
//                    if (paramName.startsWith("x-java-")) {
//                        final String commandName = paramName.substring(7);
//                        if (commandName.equals("fallback-entry") && paramValue.equalsIgnoreCase("true")) {
//                            isFallback = true;
//                        }
//                        else {
//                            if (LogSupport.isLoggable()) {
//                                LogSupport.log("    Command: " + commandName + ", Class: " + paramValue);
//                            }
//                            List classes = commands.get(commandName);
//                            if (classes == null) {
//                                classes = new ArrayList();
//                                commands.put(commandName, classes);
//                            }
//                            if (MailcapFile.addReverse) {
//                                classes.add(0, paramValue);
//                            }
//                            else {
//                                classes.add(paramValue);
//                            }
//                        }
//                    }
//                    currentToken = tokenizer.nextToken();
//                }
//            } while (currentToken == 59);
//            final Map masterHash = isFallback ? this.fallback_hash : this.type_hash;
//            final Map curcommands = masterHash.get(mimeType);
//            if (curcommands == null) {
//                masterHash.put(mimeType, commands);
//            }
//            else {
//                if (LogSupport.isLoggable()) {
//                    LogSupport.log("Merging commands for type " + mimeType);
//                }
//                for (final String cmdName : curcommands.keySet()) {
//                    final List ccv = curcommands.get(cmdName);
//                    final List cv = commands.get(cmdName);
//                    if (cv == null) {
//                        continue;
//                    }
//                    for (final String clazz : cv) {
//                        if (!ccv.contains(clazz)) {
//                            if (MailcapFile.addReverse) {
//                                ccv.add(0, clazz);
//                            }
//                            else {
//                                ccv.add(clazz);
//                            }
//                        }
//                    }
//                }
//                for (final String cmdName : commands.keySet()) {
//                    if (curcommands.containsKey(cmdName)) {
//                        continue;
//                    }
//                    final List cv2 = commands.get(cmdName);
//                    curcommands.put(cmdName, cv2);
//                }
//            }
//        }
//        else if (currentToken != 5) {
//            reportParseError(5, 59, currentToken, tokenizer.getCurrentTokenValue());
//        }
//    }
//
//    protected static void reportParseError(final int expectedToken, final int actualToken, final String actualTokenValue) throws MailcapParseException {
//        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " token.");
//    }
//
//    protected static void reportParseError(final int expectedToken, final int otherExpectedToken, final int actualToken, final String actualTokenValue) throws MailcapParseException {
//        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " or a " + MailcapTokenizer.nameForToken(otherExpectedToken) + " token.");
//    }
//
//    protected static void reportParseError(final int expectedToken, final int otherExpectedToken, final int anotherExpectedToken, final int actualToken, final String actualTokenValue) throws MailcapParseException {
//        if (LogSupport.isLoggable()) {
//            LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
//        }
//        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
//    }
//
//    static {
//        MailcapFile.addReverse = false;
//        try {
//            MailcapFile.addReverse = Boolean.getBoolean("javax.activation.addreverse");
//        }
//        catch (Throwable t) {}
//    }
//}
