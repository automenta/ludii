// 
// Decompiled by Procyon v0.5.36
// 

package util;

public class BaseCardImages
{
    public static final int SUIT_LARGE = 0;
    public static final int SUIT_SMALL = 1;
    public static final int BLACK_ROYAL = 2;
    public static final int RED_ROYAL = 3;
    public static final int CLUBS = 1;
    public static final int SPADES = 2;
    public static final int DIAMONDS = 3;
    public static final int HEARTS = 4;
    public static final int JOKER = 0;
    public static final int ACE = 1;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;
    private String[][] baseCardImagePaths;
    private int cardSize;
    
    public BaseCardImages() {
        this.baseCardImagePaths = null;
    }
    
    public int getSuitSizeSmall() {
        return this.getSuitSizeSmall(this.cardSize);
    }
    
    public int getSuitSizeSmall(final int cardSizeInput) {
        return (int)(0.1 * cardSizeInput);
    }
    
    public int getSuitSizeBig() {
        return this.getSuitSizeBig(this.cardSize);
    }
    
    public int getSuitSizeBig(final int cardSizeInput) {
        return (int)(0.16 * cardSizeInput);
    }
    
    public String getPath(final int type, final int which) {
        if (this.baseCardImagePaths == null || type >= this.baseCardImagePaths.length || which >= this.baseCardImagePaths[type].length) {
            System.out.println("** Failed to find base card image type " + type + " value " + which + ".");
            return null;
        }
        return this.baseCardImagePaths[type][which];
    }
    
    public void clear() {
        this.baseCardImagePaths = null;
    }
    
    public boolean areLoaded() {
        return this.baseCardImagePaths != null;
    }
    
    public void loadImages(final int cardSizeInput) {
        this.baseCardImagePaths = new String[4][15];
        this.cardSize = cardSizeInput;
        this.baseCardImagePaths[0][1] = "/svg/cards/card-suit-club.svg";
        this.baseCardImagePaths[0][2] = "/svg/cards/card-suit-spade.svg";
        this.baseCardImagePaths[0][3] = "/svg/cards/card-suit-diamond.svg";
        this.baseCardImagePaths[0][4] = "/svg/cards/card-suit-heart.svg";
        this.baseCardImagePaths[1][1] = "/svg/cards/card-suit-club.svg";
        this.baseCardImagePaths[1][2] = "/svg/cards/card-suit-spade.svg";
        this.baseCardImagePaths[1][3] = "/svg/cards/card-suit-diamond.svg";
        this.baseCardImagePaths[1][4] = "/svg/cards/card-suit-heart.svg";
        this.baseCardImagePaths[2][11] = "/svg/cards/card-jack.svg";
        this.baseCardImagePaths[2][12] = "/svg/cards/card-queen.svg";
        this.baseCardImagePaths[2][13] = "/svg/cards/card-king.svg";
        this.baseCardImagePaths[3][11] = "/svg/cards/card-jack.svg";
        this.baseCardImagePaths[3][12] = "/svg/cards/card-queen.svg";
        this.baseCardImagePaths[3][13] = "/svg/cards/card-king.svg";
    }
}
