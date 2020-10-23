////
//// Decompiled by Procyon v0.5.36
////
//
//package com.sun.activation.registries;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.Hashtable;
//import java.util.StringTokenizer;
//
//public class MimeTypeFile
//{
//    private String fname;
//    private final Hashtable type_hash;
//
//    public MimeTypeFile(final String new_fname) throws IOException {
//        this.fname = null;
//        this.type_hash = new Hashtable();
//        File mime_file = null;
//        FileReader fr = null;
//        this.fname = new_fname;
//        mime_file = new File(this.fname);
//        fr = new FileReader(mime_file);
//        try {
//            this.parse(new BufferedReader(fr));
//        }
//        finally {
//            try {
//                fr.close();
//            }
//            catch (IOException ex) {}
//        }
//    }
//
//    public MimeTypeFile(final InputStream is) throws IOException {
//        this.fname = null;
//        this.type_hash = new Hashtable();
//        this.parse(new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1)));
//    }
//
//    public MimeTypeFile() {
//        this.fname = null;
//        this.type_hash = new Hashtable();
//    }
//
//    public MimeTypeEntry getMimeTypeEntry(final String file_ext) {
//        return this.type_hash.get(file_ext);
//    }
//
//    public String getMIMETypeString(final String file_ext) {
//        final MimeTypeEntry entry = this.getMimeTypeEntry(file_ext);
//        if (entry != null) {
//            return entry.getMIMEType();
//        }
//        return null;
//    }
//
//    public void appendToRegistry(final String mime_types) {
//        try {
//            this.parse(new BufferedReader(new StringReader(mime_types)));
//        }
//        catch (IOException ex) {}
//    }
//
//    private void parse(final BufferedReader buf_reader) throws IOException {
//        String line = null;
//        String prev = null;
//        while ((line = buf_reader.readLine()) != null) {
//            if (prev == null) {
//                prev = line;
//            }
//            else {
//                prev += line;
//            }
//            final int end = prev.length();
//            if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
//                prev = prev.substring(0, end - 1);
//            }
//            else {
//                this.parseEntry(prev);
//                prev = null;
//            }
//        }
//        if (prev != null) {
//            this.parseEntry(prev);
//        }
//    }
//
//    private void parseEntry(String line) {
//        String mime_type = null;
//        String file_ext = null;
//        line = line.trim();
//        if (line.length() == 0) {
//            return;
//        }
//        if (line.charAt(0) == '#') {
//            return;
//        }
//        if (line.indexOf(61) > 0) {
//            final LineTokenizer lt = new LineTokenizer(line);
//            while (lt.hasMoreTokens()) {
//                final String name = lt.nextToken();
//                String value = null;
//                if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens()) {
//                    value = lt.nextToken();
//                }
//                if (value == null) {
//                    if (LogSupport.isLoggable()) {
//                        LogSupport.log("Bad .mime.types entry: " + line);
//                    }
//                    return;
//                }
//                if (name.equals("type")) {
//                    mime_type = value;
//                }
//                else {
//                    if (!name.equals("exts")) {
//                        continue;
//                    }
//                    final StringTokenizer st = new StringTokenizer(value, ",");
//                    while (st.hasMoreTokens()) {
//                        file_ext = st.nextToken();
//                        final MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
//                        this.type_hash.put(file_ext, entry);
//                        if (LogSupport.isLoggable()) {
//                            LogSupport.log("Added: " + entry.toString());
//                        }
//                    }
//                }
//            }
//        }
//        else {
//            final StringTokenizer strtok = new StringTokenizer(line);
//            final int num_tok = strtok.countTokens();
//            if (num_tok == 0) {
//                return;
//            }
//            mime_type = strtok.nextToken();
//            while (strtok.hasMoreTokens()) {
//                MimeTypeEntry entry2 = null;
//                file_ext = strtok.nextToken();
//                entry2 = new MimeTypeEntry(mime_type, file_ext);
//                this.type_hash.put(file_ext, entry2);
//                if (LogSupport.isLoggable()) {
//                    LogSupport.log("Added: " + entry2.toString());
//                }
//            }
//        }
//    }
//}
