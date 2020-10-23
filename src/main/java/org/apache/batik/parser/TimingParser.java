// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.util.Calendar;
import java.io.IOException;
import org.apache.batik.xml.XMLUtilities;

public abstract class TimingParser extends AbstractParser
{
    protected static final int TIME_OFFSET = 0;
    protected static final int TIME_SYNCBASE = 1;
    protected static final int TIME_EVENTBASE = 2;
    protected static final int TIME_REPEAT = 3;
    protected static final int TIME_ACCESSKEY = 4;
    protected static final int TIME_ACCESSKEY_SVG12 = 5;
    protected static final int TIME_MEDIA_MARKER = 6;
    protected static final int TIME_WALLCLOCK = 7;
    protected static final int TIME_INDEFINITE = 8;
    protected boolean useSVG11AccessKeys;
    protected boolean useSVG12AccessKeys;
    
    public TimingParser(final boolean useSVG11AccessKeys, final boolean useSVG12AccessKeys) {
        this.useSVG11AccessKeys = useSVG11AccessKeys;
        this.useSVG12AccessKeys = useSVG12AccessKeys;
    }
    
    protected Object[] parseTimingSpecifier() throws ParseException, IOException {
        this.skipSpaces();
        boolean escaped = false;
        if (this.current == 92) {
            escaped = true;
            this.current = this.reader.read();
        }
        Object[] ret = null;
        if (this.current == 43 || (this.current == 45 && !escaped) || (this.current >= 48 && this.current <= 57)) {
            final float offset = this.parseOffset();
            ret = new Object[] { 0, offset };
        }
        else if (XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
            ret = this.parseIDValue(escaped);
        }
        else {
            this.reportUnexpectedCharacterError(this.current);
        }
        return ret;
    }
    
    protected String parseName() throws ParseException, IOException {
        final StringBuffer sb = new StringBuffer();
        boolean midEscaped = false;
        do {
            sb.append((char)this.current);
            this.current = this.reader.read();
            midEscaped = false;
            if (this.current == 92) {
                midEscaped = true;
                this.current = this.reader.read();
            }
        } while (XMLUtilities.isXMLNameCharacter((char)this.current) && (midEscaped || (this.current != 45 && this.current != 46)));
        return sb.toString();
    }
    
    protected Object[] parseIDValue(boolean escaped) throws ParseException, IOException {
        final String id = this.parseName();
        if (((id.equals("accessKey") && this.useSVG11AccessKeys) || id.equals("accesskey")) && !escaped) {
            if (this.current != 40) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            if (this.current == -1) {
                this.reportError("end.of.stream", new Object[0]);
            }
            final char key = (char)this.current;
            this.current = this.reader.read();
            if (this.current != 41) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            this.skipSpaces();
            float offset = 0.0f;
            if (this.current == 43 || this.current == 45) {
                offset = this.parseOffset();
            }
            return new Object[] { 4, offset, key };
        }
        if (id.equals("accessKey") && this.useSVG12AccessKeys && !escaped) {
            if (this.current != 40) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            final StringBuffer keyName = new StringBuffer();
            while ((this.current >= 65 && this.current <= 90) || (this.current >= 97 && this.current <= 122) || (this.current >= 48 && this.current <= 57) || this.current == 43) {
                keyName.append((char)this.current);
                this.current = this.reader.read();
            }
            if (this.current != 41) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            this.skipSpaces();
            float offset = 0.0f;
            if (this.current == 43 || this.current == 45) {
                offset = this.parseOffset();
            }
            return new Object[] { 5, offset, keyName.toString() };
        }
        if (id.equals("wallclock") && !escaped) {
            if (this.current != 40) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            this.skipSpaces();
            final Calendar wallclockValue = this.parseWallclockValue();
            this.skipSpaces();
            if (this.current != 41) {
                this.reportError("character.unexpected", new Object[] { this.current });
            }
            this.current = this.reader.read();
            return new Object[] { 7, wallclockValue };
        }
        if (id.equals("indefinite") && !escaped) {
            return new Object[] { 8 };
        }
        if (this.current != 46) {
            this.skipSpaces();
            float offset2 = 0.0f;
            if (this.current == 43 || this.current == 45) {
                offset2 = this.parseOffset();
            }
            return new Object[] { 2, offset2, null, id };
        }
        this.current = this.reader.read();
        if (this.current == 92) {
            escaped = true;
            this.current = this.reader.read();
        }
        if (!XMLUtilities.isXMLNameFirstCharacter((char)this.current)) {
            this.reportUnexpectedCharacterError(this.current);
        }
        final String id2 = this.parseName();
        if ((id2.equals("begin") || id2.equals("end")) && !escaped) {
            this.skipSpaces();
            float offset = 0.0f;
            if (this.current == 43 || this.current == 45) {
                offset = this.parseOffset();
            }
            return new Object[] { 1, offset, id, id2 };
        }
        if (id2.equals("repeat") && !escaped) {
            Integer repeatIteration = null;
            if (this.current == 40) {
                this.current = this.reader.read();
                repeatIteration = this.parseDigits();
                if (this.current != 41) {
                    this.reportUnexpectedCharacterError(this.current);
                }
                this.current = this.reader.read();
            }
            this.skipSpaces();
            float offset3 = 0.0f;
            if (this.current == 43 || this.current == 45) {
                offset3 = this.parseOffset();
            }
            return new Object[] { 3, offset3, id, repeatIteration };
        }
        if (id2.equals("marker") && !escaped) {
            if (this.current != 40) {
                this.reportUnexpectedCharacterError(this.current);
            }
            final String markerName = this.parseName();
            if (this.current != 41) {
                this.reportUnexpectedCharacterError(this.current);
            }
            this.current = this.reader.read();
            return new Object[] { 6, id, markerName };
        }
        this.skipSpaces();
        float offset = 0.0f;
        if (this.current == 43 || this.current == 45) {
            offset = this.parseOffset();
        }
        return new Object[] { 2, offset, id, id2 };
    }
    
    protected float parseClockValue() throws ParseException, IOException {
        final int d1 = this.parseDigits();
        float offset;
        if (this.current == 58) {
            this.current = this.reader.read();
            final int d2 = this.parseDigits();
            if (this.current == 58) {
                this.current = this.reader.read();
                final int d3 = this.parseDigits();
                offset = (float)(d1 * 3600 + d2 * 60 + d3);
            }
            else {
                offset = (float)(d1 * 60 + d2);
            }
            if (this.current == 46) {
                this.current = this.reader.read();
                offset += this.parseFraction();
            }
        }
        else if (this.current == 46) {
            this.current = this.reader.read();
            offset = (this.parseFraction() + d1) * this.parseUnit();
        }
        else {
            offset = d1 * this.parseUnit();
        }
        return offset;
    }
    
    protected float parseOffset() throws ParseException, IOException {
        boolean offsetNegative = false;
        if (this.current == 45) {
            offsetNegative = true;
            this.current = this.reader.read();
            this.skipSpaces();
        }
        else if (this.current == 43) {
            this.current = this.reader.read();
            this.skipSpaces();
        }
        if (offsetNegative) {
            return -this.parseClockValue();
        }
        return this.parseClockValue();
    }
    
    protected int parseDigits() throws ParseException, IOException {
        int value = 0;
        if (this.current < 48 || this.current > 57) {
            this.reportUnexpectedCharacterError(this.current);
        }
        do {
            value = value * 10 + (this.current - 48);
            this.current = this.reader.read();
        } while (this.current >= 48 && this.current <= 57);
        return value;
    }
    
    protected float parseFraction() throws ParseException, IOException {
        float value = 0.0f;
        if (this.current < 48 || this.current > 57) {
            this.reportUnexpectedCharacterError(this.current);
        }
        float weight = 0.1f;
        do {
            value += weight * (this.current - 48);
            weight *= 0.1f;
            this.current = this.reader.read();
        } while (this.current >= 48 && this.current <= 57);
        return value;
    }
    
    protected float parseUnit() throws ParseException, IOException {
        if (this.current == 104) {
            this.current = this.reader.read();
            return 3600.0f;
        }
        if (this.current == 109) {
            this.current = this.reader.read();
            if (this.current == 105) {
                this.current = this.reader.read();
                if (this.current != 110) {
                    this.reportUnexpectedCharacterError(this.current);
                }
                this.current = this.reader.read();
                return 60.0f;
            }
            if (this.current == 115) {
                this.current = this.reader.read();
                return 0.001f;
            }
            this.reportUnexpectedCharacterError(this.current);
        }
        else if (this.current == 115) {
            this.current = this.reader.read();
        }
        return 1.0f;
    }
    
    protected Calendar parseWallclockValue() throws ParseException, IOException {
        int y = 0;
        int M = 0;
        int d = 0;
        int h = 0;
        int m = 0;
        int s = 0;
        int tzh = 0;
        int tzm = 0;
        float frac = 0.0f;
        boolean dateSpecified = false;
        boolean timeSpecified = false;
        boolean tzSpecified = false;
        boolean tzNegative = false;
        String tzn = null;
        int digits1 = this.parseDigits();
        Label_0443: {
            if (this.current == 45) {
                dateSpecified = true;
                y = digits1;
                this.current = this.reader.read();
                M = this.parseDigits();
                if (this.current != 45) {
                    this.reportUnexpectedCharacterError(this.current);
                }
                this.current = this.reader.read();
                d = this.parseDigits();
                if (this.current != 84) {
                    break Label_0443;
                }
                this.current = this.reader.read();
                digits1 = this.parseDigits();
                if (this.current != 58) {
                    this.reportUnexpectedCharacterError(this.current);
                }
            }
            if (this.current == 58) {
                timeSpecified = true;
                h = digits1;
                this.current = this.reader.read();
                m = this.parseDigits();
                if (this.current == 58) {
                    this.current = this.reader.read();
                    s = this.parseDigits();
                    if (this.current == 46) {
                        this.current = this.reader.read();
                        frac = this.parseFraction();
                    }
                }
                if (this.current == 90) {
                    tzSpecified = true;
                    tzn = "UTC";
                    this.current = this.reader.read();
                }
                else if (this.current == 43 || this.current == 45) {
                    final StringBuffer tznb = new StringBuffer();
                    tzSpecified = true;
                    if (this.current == 45) {
                        tzNegative = true;
                        tznb.append('-');
                    }
                    else {
                        tznb.append('+');
                    }
                    this.current = this.reader.read();
                    tzh = this.parseDigits();
                    if (tzh < 10) {
                        tznb.append('0');
                    }
                    tznb.append(tzh);
                    if (this.current != 58) {
                        this.reportUnexpectedCharacterError(this.current);
                    }
                    tznb.append(':');
                    this.current = this.reader.read();
                    tzm = this.parseDigits();
                    if (tzm < 10) {
                        tznb.append('0');
                    }
                    tznb.append(tzm);
                    tzn = tznb.toString();
                }
            }
        }
        if (!dateSpecified && !timeSpecified) {
            this.reportUnexpectedCharacterError(this.current);
        }
        Calendar wallclockTime;
        if (tzSpecified) {
            final int offset = (tzNegative ? -1 : 1) * (tzh * 3600000 + tzm * 60000);
            wallclockTime = Calendar.getInstance(new SimpleTimeZone(offset, tzn));
        }
        else {
            wallclockTime = Calendar.getInstance();
        }
        if (dateSpecified && timeSpecified) {
            wallclockTime.set(y, M, d, h, m, s);
        }
        else if (dateSpecified) {
            wallclockTime.set(y, M, d, 0, 0, 0);
        }
        else {
            wallclockTime.set(10, h);
            wallclockTime.set(12, m);
            wallclockTime.set(13, s);
        }
        if (frac == 0.0f) {
            wallclockTime.set(14, (int)(frac * 1000.0f));
        }
        else {
            wallclockTime.set(14, 0);
        }
        return wallclockTime;
    }
}
