// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

public class Properties
{
    public static final long INNER = 1L;
    public static final long OUTER = 2L;
    public static final long PERIMETER = 4L;
    public static final long CENTRE = 8L;
    public static final long MAJOR = 16L;
    public static final long MINOR = 32L;
    public static final long PIVOT = 64L;
    public static final long INTERLAYER = 128L;
    public static final long NULL_NBOR = 256L;
    public static final long CORNER = 1024L;
    public static final long CORNER_CONVEX = 2048L;
    public static final long CORNER_CONCAVE = 4096L;
    public static final long PHASE_0 = 1048576L;
    public static final long PHASE_1 = 2097152L;
    public static final long PHASE_2 = 4194304L;
    public static final long PHASE_3 = 8388608L;
    public static final long LEFT = 33554432L;
    public static final long RIGHT = 67108864L;
    public static final long TOP = 134217728L;
    public static final long BOTTOM = 268435456L;
    public static final long AXIAL = 1073741824L;
    public static final long HORIZONTAL = 2147483648L;
    public static final long VERTICAL = 4294967296L;
    public static final long ANGLED = 8589934592L;
    public static final long SLASH = 17179869184L;
    public static final long SLOSH = 34359738368L;
    public static final long SIDE_N = 1099511627776L;
    public static final long SIDE_E = 2199023255552L;
    public static final long SIDE_S = 4398046511104L;
    public static final long SIDE_W = 8796093022208L;
    public static final long SIDE_NE = 17592186044416L;
    public static final long SIDE_SE = 35184372088832L;
    public static final long SIDE_SW = 70368744177664L;
    public static final long SIDE_NW = 140737488355328L;
    private long properties;
    
    public Properties() {
        this.properties = 0L;
    }
    
    public Properties(final long properties) {
        this.properties = 0L;
        this.properties = properties;
    }
    
    public Properties(final Properties other) {
        this.properties = 0L;
        this.properties = other.properties;
    }
    
    public void clear() {
        this.properties = 0L;
    }
    
    public long get() {
        return this.properties;
    }
    
    public boolean get(final long property) {
        return (this.properties & property) != 0x0L;
    }
    
    public void set(final long property) {
        this.properties |= property;
    }
    
    public void set(final long property, final boolean on) {
        if (on) {
            this.properties |= property;
        }
        else {
            this.properties &= ~property;
        }
    }
    
    public void add(final long other) {
        this.properties |= other;
    }
    
    public int phase() {
        if (this.get(1048576L)) {
            return 0;
        }
        if (this.get(2097152L)) {
            return 1;
        }
        if (this.get(4194304L)) {
            return 2;
        }
        if (this.get(8388608L)) {
            return 3;
        }
        return -1;
    }
    
    public void clearPhase() {
        this.properties &= 0xFFFFFFFFFFEFFFFFL;
        this.properties &= 0xFFFFFFFFFFDFFFFFL;
        this.properties &= 0xFFFFFFFFFFBFFFFFL;
        this.properties &= 0xFFFFFFFFFF7FFFFFL;
    }
    
    public void setPhase(final int phase) {
        this.properties &= 0xFFFFFFFFFFEFFFFFL;
        this.properties &= 0xFFFFFFFFFFDFFFFFL;
        this.properties &= 0xFFFFFFFFFFBFFFFFL;
        this.properties &= 0xFFFFFFFFFF7FFFFFL;
        switch (phase) {
            case 0 -> {
                this.properties |= 0x100000L;
                break;
            }
            case 1 -> {
                this.properties |= 0x200000L;
                break;
            }
            case 2 -> {
                this.properties |= 0x400000L;
                break;
            }
            case 3 -> {
                this.properties |= 0x800000L;
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.get(1L)) {
            sb.append(" I");
        }
        if (this.get(2L)) {
            sb.append(" O");
        }
        if (this.get(64L)) {
            sb.append(" PVT");
        }
        if (this.get(4L)) {
            sb.append(" PRM");
        }
        if (this.get(128L)) {
            sb.append(" IL");
        }
        if (this.get(1024L)) {
            sb.append(" CNR");
        }
        if (this.get(2048L)) {
            sb.append("(X)");
        }
        if (this.get(4096L)) {
            sb.append("(V)");
        }
        if (this.get(8L)) {
            sb.append(" CTR");
        }
        if (this.get(1073741824L)) {
            sb.append(" AXL");
        }
        if (this.get(8589934592L)) {
            sb.append(" AGL");
        }
        if (this.get(1048576L)) {
            sb.append(" PH_0");
        }
        if (this.get(2097152L)) {
            sb.append(" PH_1");
        }
        if (this.get(4194304L)) {
            sb.append(" PH_2");
        }
        if (this.get(8388608L)) {
            sb.append(" PH_3");
        }
        String side = "";
        if (this.get(1099511627776L)) {
            side += "/N";
        }
        if (this.get(2199023255552L)) {
            side += "/E";
        }
        if (this.get(4398046511104L)) {
            side += "/S";
        }
        if (this.get(8796093022208L)) {
            side += "/W";
        }
        if (this.get(17592186044416L)) {
            side += "/NE";
        }
        if (this.get(35184372088832L)) {
            side += "/SE";
        }
        if (this.get(70368744177664L)) {
            side += "/SW";
        }
        if (this.get(140737488355328L)) {
            side += "/NW";
        }
        if (!side.isEmpty()) {
            side = " SD_" + side.substring(1);
        }
        sb.append(side);
        final String str = sb.toString();
        return "<" + (str.isEmpty() ? "" : str.substring(1)) + ">";
    }
}
