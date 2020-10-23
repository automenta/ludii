// 
// Decompiled by Procyon v0.5.36
// 

package game.util.math;

import game.functions.ints.IntConstant;
import game.functions.ints.IntFunction;
import game.functions.ints.board.Id;
import game.types.board.LandmarkType;
import game.types.play.RoleType;
import util.BaseLudeme;

public class Pair extends BaseLudeme
{
    final IntFunction intKey;
    final String stringKey;
    final IntFunction intValue;
    final String stringValue;
    final LandmarkType landmark;
    
    public Pair(final IntFunction key, final IntFunction value) {
        this.intKey = key;
        this.intValue = value;
        this.stringKey = null;
        this.stringValue = null;
        this.landmark = null;
    }
    
    public Pair(final RoleType key, final IntFunction value) {
        this.intKey = new Id(null, key);
        this.intValue = value;
        this.stringKey = null;
        this.stringValue = null;
        this.landmark = null;
    }
    
    public Pair(final RoleType key, final RoleType value) {
        this.intKey = new Id(null, key);
        this.intValue = new Id(null, value);
        this.stringKey = null;
        this.stringValue = null;
        this.landmark = null;
    }
    
    public Pair(final String key, final String value) {
        this.intKey = null;
        this.intValue = null;
        this.stringKey = key;
        this.stringValue = value;
        this.landmark = null;
    }
    
    public Pair(final IntFunction key, final String value) {
        this.intKey = key;
        this.intValue = null;
        this.stringKey = null;
        this.stringValue = value;
        this.landmark = null;
    }
    
    public Pair(final RoleType key, final String value) {
        this.intKey = new Id(null, key);
        this.intValue = null;
        this.stringKey = null;
        this.stringValue = value;
        this.landmark = null;
    }
    
    public Pair(final RoleType key, final LandmarkType landmark) {
        this.intKey = new Id(null, key);
        this.intValue = null;
        this.stringKey = null;
        this.stringValue = null;
        this.landmark = landmark;
    }
    
    public Pair(final String key, final RoleType value) {
        this.intKey = null;
        this.intValue = new Id(null, value);
        this.stringKey = key;
        this.stringValue = null;
        this.landmark = null;
    }
    
    public IntFunction intValue() {
        if (this.intValue != null) {
            return this.intValue;
        }
        return new IntConstant(-1);
    }
    
    public IntFunction intKey() {
        if (this.intKey != null) {
            return this.intKey;
        }
        return new IntConstant(-1);
    }
    
    public String stringValue() {
        return this.stringValue;
    }
    
    public LandmarkType landmarkType() {
        return this.landmark;
    }
    
    public String stringKey() {
        return this.stringKey;
    }
}
