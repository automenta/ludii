////
//// Decompiled by Procyon v0.5.36
////
//
//package com.sun.activation.registries;
//
//import java.util.NoSuchElementException;
//import java.util.Vector;
//
//class LineTokenizer
//{
//    private int currentPosition;
//    private final int maxPosition;
//    private final String str;
//    private final Vector stack;
//    private static final String singles = "=";
//
//    public LineTokenizer(final String str) {
//        this.stack = new Vector();
//        this.currentPosition = 0;
//        this.str = str;
//        this.maxPosition = str.length();
//    }
//
//    private void skipWhiteSpace() {
//        while (this.currentPosition < this.maxPosition && Character.isWhitespace(this.str.charAt(this.currentPosition))) {
//            ++this.currentPosition;
//        }
//    }
//
//    public boolean hasMoreTokens() {
//        if (this.stack.size() > 0) {
//            return true;
//        }
//        this.skipWhiteSpace();
//        return this.currentPosition < this.maxPosition;
//    }
//
//    public String nextToken() {
//        final int size = this.stack.size();
//        if (size > 0) {
//            final String t = this.stack.elementAt(size - 1);
//            this.stack.removeElementAt(size - 1);
//            return t;
//        }
//        this.skipWhiteSpace();
//        if (this.currentPosition >= this.maxPosition) {
//            throw new NoSuchElementException();
//        }
//        final int start = this.currentPosition;
//        char c = this.str.charAt(start);
//        if (c == '\"') {
//            ++this.currentPosition;
//            boolean filter = false;
//            while (this.currentPosition < this.maxPosition) {
//                c = this.str.charAt(this.currentPosition++);
//                if (c == '\\') {
//                    ++this.currentPosition;
//                    filter = true;
//                }
//                else {
//                    if (c == '\"') {
//                        String s;
//                        if (filter) {
//                            final StringBuffer sb = new StringBuffer();
//                            for (int i = start + 1; i < this.currentPosition - 1; ++i) {
//                                c = this.str.charAt(i);
//                                if (c != '\\') {
//                                    sb.append(c);
//                                }
//                            }
//                            s = sb.toString();
//                        }
//                        else {
//                            s = this.str.substring(start + 1, this.currentPosition - 1);
//                        }
//                        return s;
//                    }
//                    continue;
//                }
//            }
//        }
//        else if ("=".indexOf(c) >= 0) {
//            ++this.currentPosition;
//        }
//        else {
//            while (this.currentPosition < this.maxPosition && "=".indexOf(this.str.charAt(this.currentPosition)) < 0 && !Character.isWhitespace(this.str.charAt(this.currentPosition))) {
//                ++this.currentPosition;
//            }
//        }
//        return this.str.substring(start, this.currentPosition);
//    }
//
//    public void pushToken(final String token) {
//        this.stack.addElement(token);
//    }
//}
