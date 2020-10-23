// 
// Decompiled by Procyon v0.5.36
// 

package supplementary.bgg;

public class Rating
{
    private final BggGame game;
    private final User user;
    private final byte score;
    
    public Rating(final BggGame game, final User user, final String details) {
        this.game = game;
        this.user = user;
        this.score = this.extractScore(details);
    }
    
    public BggGame game() {
        return this.game;
    }
    
    public User user() {
        return this.user;
    }
    
    public int score() {
        return this.score;
    }
    
    byte extractScore(final String details) {
        final int c = details.indexOf("'score':");
        if (c < 0) {
            System.out.println("** Failed to find score in: " + details);
            return -1;
        }
        int cc;
        for (cc = c + 1; cc < details.length() && details.charAt(cc) != ','; ++cc) {}
        if (cc >= details.length()) {
            System.out.println("** Failed to find closing ',' for score in: " + details);
            return -1;
        }
        final String str = details.substring(c + 9, cc).trim();
        double value = -1.0;
        try {
            value = Double.parseDouble(str);
        }
        catch (NumberFormatException e) {
            try {
                value = Integer.parseInt(str);
            }
            catch (NumberFormatException ex) {}
        }
        return (byte)(value + 0.5);
    }
}
