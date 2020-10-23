// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.activation.registries;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogSupport
{
    private static boolean debug;
    private static Logger logger;
    private static final Level level;
    
    private LogSupport() {
    }
    
    public static void log(final String msg) {
        if (LogSupport.debug) {
            System.out.println(msg);
        }
        LogSupport.logger.log(LogSupport.level, msg);
    }
    
    public static void log(final String msg, final Throwable t) {
        if (LogSupport.debug) {
            System.out.println(msg + "; Exception: " + t);
        }
        LogSupport.logger.log(LogSupport.level, msg, t);
    }
    
    public static boolean isLoggable() {
        return LogSupport.debug || LogSupport.logger.isLoggable(LogSupport.level);
    }
    
    static {
        LogSupport.debug = false;
        level = Level.FINE;
        try {
            LogSupport.debug = Boolean.getBoolean("javax.activation.debug");
        }
        catch (Throwable t) {}
        LogSupport.logger = Logger.getLogger("javax.activation");
    }
}
