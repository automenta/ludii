// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.css.parser;

import org.apache.batik.util.io.StringNormalizingReader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.batik.util.io.StreamNormalizingReader;
import java.io.Reader;
import org.apache.batik.util.io.NormalizingReader;

public class Scanner
{
    protected NormalizingReader reader;
    protected int current;
    protected char[] buffer;
    protected int position;
    protected int type;
    protected int start;
    protected int end;
    protected int blankCharacters;
    
    public Scanner(final Reader r) throws ParseException {
        this.buffer = new char[128];
        try {
            this.reader = new StreamNormalizingReader(r);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    public Scanner(final InputStream is, final String enc) throws ParseException {
        this.buffer = new char[128];
        try {
            this.reader = new StreamNormalizingReader(is, enc);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    public Scanner(final String s) throws ParseException {
        this.buffer = new char[128];
        try {
            this.reader = new StringNormalizingReader(s);
            this.current = this.nextChar();
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    public int getLine() {
        return this.reader.getLine();
    }
    
    public int getColumn() {
        return this.reader.getColumn();
    }
    
    public char[] getBuffer() {
        return this.buffer;
    }
    
    public int getStart() {
        return this.start;
    }
    
    public int getEnd() {
        return this.end;
    }
    
    public void clearBuffer() {
        if (this.position <= 0) {
            this.position = 0;
        }
        else {
            this.buffer[0] = this.buffer[this.position - 1];
            this.position = 1;
        }
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getStringValue() {
        return new String(this.buffer, this.start, this.end - this.start);
    }
    
    public void scanAtRule() throws ParseException {
        try {
        Label_0114:
            while (true) {
                switch (this.current) {
                    case 123: {
                        int brackets = 1;
                    Label_0094:
                        while (true) {
                            this.nextChar();
                            switch (this.current) {
                                case 125: {
                                    if (--brackets > 0) {
                                        continue;
                                    }
                                    break Label_0094;
                                }
                                case -1: {
                                    break Label_0094;
                                }
                                case 123: {
                                    ++brackets;
                                    continue;
                                }
                            }
                        }
                        break Label_0114;
                    }
                    case -1:
                    case 59: {
                        break Label_0114;
                    }
                    default: {
                        this.nextChar();
                        continue;
                    }
                }
            }
            this.end = this.position;
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    public int next() throws ParseException {
        this.blankCharacters = 0;
        this.start = this.position - 1;
        this.nextToken();
        this.end = this.position - this.endGap();
        return this.type;
    }
    
    public void close() {
        try {
            this.reader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected int endGap() {
        int result = (this.current != -1) ? 1 : 0;
        switch (this.type) {
            case 19:
            case 42:
            case 43:
            case 52: {
                ++result;
                break;
            }
            case 18:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 44:
            case 45:
            case 46: {
                result += 2;
                break;
            }
            case 47:
            case 48:
            case 50: {
                result += 3;
                break;
            }
            case 49: {
                result += 4;
                break;
            }
        }
        return result + this.blankCharacters;
    }
    
    protected void nextToken() throws ParseException {
        try {
            switch (this.current) {
                case -1: {
                    this.type = 0;
                }
                case 123: {
                    this.nextChar();
                    this.type = 1;
                }
                case 125: {
                    this.nextChar();
                    this.type = 2;
                }
                case 61: {
                    this.nextChar();
                    this.type = 3;
                }
                case 43: {
                    this.nextChar();
                    this.type = 4;
                }
                case 44: {
                    this.nextChar();
                    this.type = 6;
                }
                case 59: {
                    this.nextChar();
                    this.type = 8;
                }
                case 62: {
                    this.nextChar();
                    this.type = 9;
                }
                case 91: {
                    this.nextChar();
                    this.type = 11;
                }
                case 93: {
                    this.nextChar();
                    this.type = 12;
                }
                case 42: {
                    this.nextChar();
                    this.type = 13;
                }
                case 40: {
                    this.nextChar();
                    this.type = 14;
                }
                case 41: {
                    this.nextChar();
                    this.type = 15;
                }
                case 58: {
                    this.nextChar();
                    this.type = 16;
                }
                case 9:
                case 10:
                case 12:
                case 13:
                case 32: {
                    do {
                        this.nextChar();
                    } while (ScannerUtilities.isCSSSpace((char)this.current));
                    this.type = 17;
                }
                case 47: {
                    this.nextChar();
                    if (this.current != 42) {
                        this.type = 10;
                        return;
                    }
                    this.nextChar();
                    this.start = this.position - 1;
                    while (true) {
                        if (this.current != -1 && this.current != 42) {
                            this.nextChar();
                        }
                        else {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && this.current == 42);
                            if (this.current == -1 || this.current == 47) {
                                break;
                            }
                            continue;
                        }
                    }
                    if (this.current == -1) {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    this.nextChar();
                    this.type = 18;
                }
                case 39: {
                    this.type = this.string1();
                }
                case 34: {
                    this.type = this.string2();
                }
                case 60: {
                    this.nextChar();
                    if (this.current != 33) {
                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    }
                    this.nextChar();
                    if (this.current == 45) {
                        this.nextChar();
                        if (this.current == 45) {
                            this.nextChar();
                            this.type = 21;
                            return;
                        }
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 45: {
                    this.nextChar();
                    if (this.current != 45) {
                        this.type = 5;
                        return;
                    }
                    this.nextChar();
                    if (this.current == 62) {
                        this.nextChar();
                        this.type = 22;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 124: {
                    this.nextChar();
                    if (this.current == 61) {
                        this.nextChar();
                        this.type = 25;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 126: {
                    this.nextChar();
                    if (this.current == 61) {
                        this.nextChar();
                        this.type = 26;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 35: {
                    this.nextChar();
                    if (ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                        this.start = this.position - 1;
                        do {
                            this.nextChar();
                            while (this.current == 92) {
                                this.nextChar();
                                this.escape();
                            }
                        } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                        this.type = 27;
                        return;
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 64: {
                    this.nextChar();
                    switch (this.current) {
                        case 67:
                        case 99: {
                            this.start = this.position - 1;
                            if (isEqualIgnoreCase(this.nextChar(), 'h') && isEqualIgnoreCase(this.nextChar(), 'a') && isEqualIgnoreCase(this.nextChar(), 'r') && isEqualIgnoreCase(this.nextChar(), 's') && isEqualIgnoreCase(this.nextChar(), 'e') && isEqualIgnoreCase(this.nextChar(), 't')) {
                                this.nextChar();
                                this.type = 30;
                                return;
                            }
                            break;
                        }
                        case 70:
                        case 102: {
                            this.start = this.position - 1;
                            if (isEqualIgnoreCase(this.nextChar(), 'o') && isEqualIgnoreCase(this.nextChar(), 'n') && isEqualIgnoreCase(this.nextChar(), 't') && isEqualIgnoreCase(this.nextChar(), '-') && isEqualIgnoreCase(this.nextChar(), 'f') && isEqualIgnoreCase(this.nextChar(), 'a') && isEqualIgnoreCase(this.nextChar(), 'c') && isEqualIgnoreCase(this.nextChar(), 'e')) {
                                this.nextChar();
                                this.type = 31;
                                return;
                            }
                            break;
                        }
                        case 73:
                        case 105: {
                            this.start = this.position - 1;
                            if (isEqualIgnoreCase(this.nextChar(), 'm') && isEqualIgnoreCase(this.nextChar(), 'p') && isEqualIgnoreCase(this.nextChar(), 'o') && isEqualIgnoreCase(this.nextChar(), 'r') && isEqualIgnoreCase(this.nextChar(), 't')) {
                                this.nextChar();
                                this.type = 28;
                                return;
                            }
                            break;
                        }
                        case 77:
                        case 109: {
                            this.start = this.position - 1;
                            if (isEqualIgnoreCase(this.nextChar(), 'e') && isEqualIgnoreCase(this.nextChar(), 'd') && isEqualIgnoreCase(this.nextChar(), 'i') && isEqualIgnoreCase(this.nextChar(), 'a')) {
                                this.nextChar();
                                this.type = 32;
                                return;
                            }
                            break;
                        }
                        case 80:
                        case 112: {
                            this.start = this.position - 1;
                            if (isEqualIgnoreCase(this.nextChar(), 'a') && isEqualIgnoreCase(this.nextChar(), 'g') && isEqualIgnoreCase(this.nextChar(), 'e')) {
                                this.nextChar();
                                this.type = 33;
                                return;
                            }
                            break;
                        }
                        default: {
                            if (!ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
                                throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
                            }
                            this.start = this.position - 1;
                            break;
                        }
                    }
                    do {
                        this.nextChar();
                        while (this.current == 92) {
                            this.nextChar();
                            this.escape();
                        }
                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                    this.type = 29;
                }
                case 33: {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current));
                    if (isEqualIgnoreCase(this.current, 'i') && isEqualIgnoreCase(this.nextChar(), 'm') && isEqualIgnoreCase(this.nextChar(), 'p') && isEqualIgnoreCase(this.nextChar(), 'o') && isEqualIgnoreCase(this.nextChar(), 'r') && isEqualIgnoreCase(this.nextChar(), 't') && isEqualIgnoreCase(this.nextChar(), 'a') && isEqualIgnoreCase(this.nextChar(), 'n') && isEqualIgnoreCase(this.nextChar(), 't')) {
                        this.nextChar();
                        this.type = 23;
                        return;
                    }
                    if (this.current == -1) {
                        throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                    }
                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    this.type = this.number();
                }
                case 46: {
                    switch (this.nextChar()) {
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57: {
                            this.type = this.dotNumber();
                            return;
                        }
                        default: {
                            this.type = 7;
                            return;
                        }
                    }
                    break;
                }
                case 85:
                case 117: {
                    this.nextChar();
                    Label_2474: {
                        switch (this.current) {
                            case 43: {
                                boolean range = false;
                                for (int i = 0; i < 6; ++i) {
                                    this.nextChar();
                                    switch (this.current) {
                                        case 63: {
                                            range = true;
                                            break;
                                        }
                                        default: {
                                            if (range && !ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                                throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                            }
                                            break;
                                        }
                                    }
                                }
                                this.nextChar();
                                if (range) {
                                    this.type = 53;
                                    return;
                                }
                                if (this.current != 45) {
                                    break Label_2474;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                                    this.type = 53;
                                    return;
                                }
                                this.nextChar();
                                this.type = 53;
                                return;
                            }
                            case 82:
                            case 114: {
                                this.nextChar();
                                switch (this.current) {
                                    case 76:
                                    case 108: {
                                        this.nextChar();
                                        switch (this.current) {
                                            case 40: {
                                                do {
                                                    this.nextChar();
                                                } while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current));
                                                switch (this.current) {
                                                    case 39: {
                                                        this.string1();
                                                        this.blankCharacters += 2;
                                                        while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                            ++this.blankCharacters;
                                                            this.nextChar();
                                                        }
                                                        if (this.current == -1) {
                                                            throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        if (this.current != 41) {
                                                            throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        this.nextChar();
                                                        this.type = 51;
                                                        return;
                                                    }
                                                    case 34: {
                                                        this.string2();
                                                        this.blankCharacters += 2;
                                                        while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                            ++this.blankCharacters;
                                                            this.nextChar();
                                                        }
                                                        if (this.current == -1) {
                                                            throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        if (this.current != 41) {
                                                            throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        this.nextChar();
                                                        this.type = 51;
                                                        return;
                                                    }
                                                    case 41: {
                                                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                    }
                                                    default: {
                                                        if (!ScannerUtilities.isCSSURICharacter((char)this.current)) {
                                                            throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        this.start = this.position - 1;
                                                        do {
                                                            this.nextChar();
                                                        } while (this.current != -1 && ScannerUtilities.isCSSURICharacter((char)this.current));
                                                        ++this.blankCharacters;
                                                        while (this.current != -1 && ScannerUtilities.isCSSSpace((char)this.current)) {
                                                            ++this.blankCharacters;
                                                            this.nextChar();
                                                        }
                                                        if (this.current == -1) {
                                                            throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        if (this.current != 41) {
                                                            throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                                                        }
                                                        this.nextChar();
                                                        this.type = 51;
                                                        return;
                                                    }
                                                }
                                                break;
                                            }
                                            default: {
                                                break Label_2474;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                        this.nextChar();
                    }
                    if (this.current == 40) {
                        this.nextChar();
                        this.type = 52;
                        return;
                    }
                    this.type = 20;
                }
                default: {
                    if (this.current == 92) {
                        do {
                            this.nextChar();
                            this.escape();
                        } while (this.current == 92);
                    }
                    else if (!ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
                        this.nextChar();
                        throw new ParseException("identifier.character", this.reader.getLine(), this.reader.getColumn());
                    }
                    while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                        this.nextChar();
                        while (this.current == 92) {
                            this.nextChar();
                            this.escape();
                        }
                    }
                    if (this.current == 40) {
                        this.nextChar();
                        this.type = 52;
                        return;
                    }
                    this.type = 20;
                }
            }
        }
        catch (IOException e) {
            throw new ParseException(e);
        }
    }
    
    protected int string1() throws IOException {
        this.start = this.position;
        while (true) {
            switch (this.nextChar()) {
                case -1: {
                    throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                }
                case 39: {
                    this.nextChar();
                    return 19;
                }
                case 34: {
                    continue;
                }
                case 92: {
                    switch (this.nextChar()) {
                        case 10:
                        case 12: {
                            continue;
                        }
                        default: {
                            this.escape();
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    if (!ScannerUtilities.isCSSStringCharacter((char)this.current)) {
                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    }
                    continue;
                }
            }
        }
    }
    
    protected int string2() throws IOException {
        this.start = this.position;
        while (true) {
            switch (this.nextChar()) {
                case -1: {
                    throw new ParseException("eof", this.reader.getLine(), this.reader.getColumn());
                }
                case 39: {
                    continue;
                }
                case 34: {
                    this.nextChar();
                    return 19;
                }
                case 92: {
                    switch (this.nextChar()) {
                        case 10:
                        case 12: {
                            continue;
                        }
                        default: {
                            this.escape();
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    if (!ScannerUtilities.isCSSStringCharacter((char)this.current)) {
                        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                    }
                    continue;
                }
            }
        }
    }
    
    protected int number() throws IOException {
        while (true) {
            switch (this.nextChar()) {
                case 46: {
                    switch (this.nextChar()) {
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57: {
                            return this.dotNumber();
                        }
                        default: {
                            throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
                        }
                    }
                    break;
                }
                default: {
                    return this.numberUnit(true);
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    continue;
                }
            }
        }
    }
    
    protected int dotNumber() throws IOException {
        while (true) {
            switch (this.nextChar()) {
                default: {
                    return this.numberUnit(false);
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    continue;
                }
            }
        }
    }
    
    protected int numberUnit(final boolean integer) throws IOException {
        switch (this.current) {
            case 37: {
                this.nextChar();
                return 42;
            }
            case 67:
            case 99: {
                switch (this.nextChar()) {
                    case 77:
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 37;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 68:
            case 100: {
                Label_0578: {
                    switch (this.nextChar()) {
                        case 69:
                        case 101: {
                            switch (this.nextChar()) {
                                case 71:
                                case 103: {
                                    this.nextChar();
                                    if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                        do {
                                            this.nextChar();
                                        } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                        return 34;
                                    }
                                    return 47;
                                }
                                default: {
                                    break Label_0578;
                                }
                            }
                            break;
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 69:
            case 101: {
                switch (this.nextChar()) {
                    case 77:
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 36;
                    }
                    case 88:
                    case 120: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 35;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 71:
            case 103: {
                Label_0942: {
                    switch (this.nextChar()) {
                        case 82:
                        case 114: {
                            switch (this.nextChar()) {
                                case 65:
                                case 97: {
                                    switch (this.nextChar()) {
                                        case 68:
                                        case 100: {
                                            this.nextChar();
                                            if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                                do {
                                                    this.nextChar();
                                                } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                                return 34;
                                            }
                                            return 49;
                                        }
                                        default: {
                                            break Label_0942;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 72:
            case 104: {
                this.nextChar();
                switch (this.current) {
                    case 90:
                    case 122: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 41;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 73:
            case 105: {
                switch (this.nextChar()) {
                    case 78:
                    case 110: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 39;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 75:
            case 107: {
                Label_1326: {
                    switch (this.nextChar()) {
                        case 72:
                        case 104: {
                            switch (this.nextChar()) {
                                case 90:
                                case 122: {
                                    this.nextChar();
                                    if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                        do {
                                            this.nextChar();
                                        } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                        return 34;
                                    }
                                    return 50;
                                }
                                default: {
                                    break Label_1326;
                                }
                            }
                            break;
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 77:
            case 109: {
                switch (this.nextChar()) {
                    case 77:
                    case 109: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 38;
                    }
                    case 83:
                    case 115: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 40;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 80:
            case 112: {
                switch (this.nextChar()) {
                    case 67:
                    case 99: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 44;
                    }
                    case 84:
                    case 116: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 45;
                    }
                    case 88:
                    case 120: {
                        this.nextChar();
                        if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            do {
                                this.nextChar();
                            } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                            return 34;
                        }
                        return 46;
                    }
                    default: {
                        while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                            this.nextChar();
                        }
                        return 34;
                    }
                }
                break;
            }
            case 82:
            case 114: {
                Label_1914: {
                    switch (this.nextChar()) {
                        case 65:
                        case 97: {
                            switch (this.nextChar()) {
                                case 68:
                                case 100: {
                                    this.nextChar();
                                    if (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                                        do {
                                            this.nextChar();
                                        } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                                        return 34;
                                    }
                                    return 48;
                                }
                                default: {
                                    break Label_1914;
                                }
                            }
                            break;
                        }
                    }
                }
                while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current)) {
                    this.nextChar();
                }
                return 34;
            }
            case 83:
            case 115: {
                this.nextChar();
                return 43;
            }
            default: {
                if (this.current != -1 && ScannerUtilities.isCSSIdentifierStartCharacter((char)this.current)) {
                    do {
                        this.nextChar();
                    } while (this.current != -1 && ScannerUtilities.isCSSNameCharacter((char)this.current));
                    return 34;
                }
                return integer ? 24 : 54;
            }
        }
    }
    
    protected void escape() throws IOException {
        if (ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
            this.nextChar();
            if (!ScannerUtilities.isCSSHexadecimalCharacter((char)this.current)) {
                if (ScannerUtilities.isCSSSpace((char)this.current)) {
                    this.nextChar();
                }
                return;
            }
        }
        if ((this.current >= 32 && this.current <= 126) || this.current >= 128) {
            this.nextChar();
            return;
        }
        throw new ParseException("character", this.reader.getLine(), this.reader.getColumn());
    }
    
    protected static boolean isEqualIgnoreCase(final int i, final char c) {
        return i != -1 && Character.toLowerCase((char)i) == c;
    }
    
    protected int nextChar() throws IOException {
        this.current = this.reader.read();
        if (this.current == -1) {
            return this.current;
        }
        if (this.position == this.buffer.length) {
            final char[] t = new char[1 + this.position + this.position / 2];
            System.arraycopy(this.buffer, 0, t, 0, this.position);
            this.buffer = t;
        }
        return this.buffer[this.position++] = (char)this.current;
    }
}
