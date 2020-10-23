// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.batik.parser;

import java.util.Calendar;
import java.io.IOException;

public class TimingSpecifierParser extends TimingParser
{
    protected TimingSpecifierHandler timingSpecifierHandler;
    
    public TimingSpecifierParser(final boolean useSVG11AccessKeys, final boolean useSVG12AccessKeys) {
        super(useSVG11AccessKeys, useSVG12AccessKeys);
        this.timingSpecifierHandler = DefaultTimingSpecifierHandler.INSTANCE;
    }
    
    public void setTimingSpecifierHandler(final TimingSpecifierHandler handler) {
        this.timingSpecifierHandler = handler;
    }
    
    public TimingSpecifierHandler getTimingSpecifierHandler() {
        return this.timingSpecifierHandler;
    }
    
    @Override
    protected void doParse() throws ParseException, IOException {
        this.current = this.reader.read();
        final Object[] spec = this.parseTimingSpecifier();
        this.skipSpaces();
        if (this.current != -1) {
            this.reportError("end.of.stream.expected", new Object[] { this.current });
        }
        this.handleTimingSpecifier(spec);
    }
    
    protected void handleTimingSpecifier(final Object[] spec) {
        final int type = (int)spec[0];
        switch (type) {
            case 0: {
                this.timingSpecifierHandler.offset((float)spec[1]);
                break;
            }
            case 1: {
                this.timingSpecifierHandler.syncbase((float)spec[1], (String)spec[2], (String)spec[3]);
                break;
            }
            case 2: {
                this.timingSpecifierHandler.eventbase((float)spec[1], (String)spec[2], (String)spec[3]);
                break;
            }
            case 3: {
                final float offset = (float)spec[1];
                final String syncbaseID = (String)spec[2];
                if (spec[3] == null) {
                    this.timingSpecifierHandler.repeat(offset, syncbaseID);
                    break;
                }
                this.timingSpecifierHandler.repeat(offset, syncbaseID, (int)spec[3]);
                break;
            }
            case 4: {
                this.timingSpecifierHandler.accesskey((float)spec[1], (char)spec[2]);
                break;
            }
            case 5: {
                this.timingSpecifierHandler.accessKeySVG12((float)spec[1], (String)spec[2]);
                break;
            }
            case 6: {
                this.timingSpecifierHandler.mediaMarker((String)spec[1], (String)spec[2]);
                break;
            }
            case 7: {
                this.timingSpecifierHandler.wallclock((Calendar)spec[1]);
                break;
            }
            case 8: {
                this.timingSpecifierHandler.indefinite();
                break;
            }
        }
    }
}
