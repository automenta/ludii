// 
// Decompiled by Procyon v0.5.36
// 

package game.functions.booleans;

import game.Game;
import util.Context;

public final class BooleanConstant extends BaseBooleanFunction
{
    private static final long serialVersionUID = 1L;
    private static final TrueConstant TRUE_INSTANCE;
    private static final FalseConstant FALSE_INSTANCE;
    private final boolean a;
    
    public static BaseBooleanFunction construct(final boolean a) {
        if (a) {
            return BooleanConstant.TRUE_INSTANCE;
        }
        return BooleanConstant.FALSE_INSTANCE;
    }
    
    private BooleanConstant(final boolean a) {
        this.a = a;
    }
    
    @Override
    public boolean eval(final Context context) {
        return this.a;
    }
    
    @Override
    public String toString() {
        final String str = String.valueOf(this.a);
        return str;
    }
    
    @Override
    public boolean isStatic() {
        return true;
    }
    
    @Override
    public long gameFlags(final Game game) {
        return 0L;
    }
    
    @Override
    public void preprocess(final Game game) {
    }
    
    static {
        TRUE_INSTANCE = new TrueConstant();
        FALSE_INSTANCE = new FalseConstant();
    }
    
    public static final class TrueConstant extends BaseBooleanFunction
    {
        private static final long serialVersionUID = 1L;
        
        TrueConstant() {
        }
        
        @Override
        public boolean eval(final Context context) {
            return true;
        }
        
        @Override
        public String toString() {
            final String str = "True";
            return "True";
        }
        
        @Override
        public boolean isStatic() {
            return true;
        }
        
        @Override
        public long gameFlags(final Game game) {
            return 0L;
        }
        
        @Override
        public void preprocess(final Game game) {
        }
    }
    
    public static final class FalseConstant extends BaseBooleanFunction
    {
        private static final long serialVersionUID = 1L;
        
        FalseConstant() {
        }
        
        @Override
        public boolean eval(final Context context) {
            return false;
        }
        
        @Override
        public String toString() {
            final String str = "False";
            return "False";
        }
        
        @Override
        public boolean isStatic() {
            return true;
        }
        
        @Override
        public long gameFlags(final Game game) {
            return 0L;
        }
        
        @Override
        public void preprocess(final Game game) {
        }
    }
}
