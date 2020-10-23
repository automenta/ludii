// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.piece;

import annotations.Name;
import annotations.Opt;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.piece.colour.PieceColour;
import metadata.graphics.piece.families.PieceFamilies;
import metadata.graphics.piece.ground.PieceBackground;
import metadata.graphics.piece.ground.PieceForeground;
import metadata.graphics.piece.name.PieceAddStateToName;
import metadata.graphics.piece.name.PieceExtendName;
import metadata.graphics.piece.name.PieceRename;
import metadata.graphics.piece.reflect.PieceReflect;
import metadata.graphics.piece.rotate.PieceRotate;
import metadata.graphics.piece.scale.PieceScale;
import metadata.graphics.piece.scale.PieceScaleByValue;
import metadata.graphics.piece.style.PieceStyle;
import metadata.graphics.util.ComponentStyleType;
import metadata.graphics.util.colour.Colour;

public class Piece implements GraphicsItem
{
    public static GraphicsItem construct(final PieceStyleType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, final ComponentStyleType componentStyleType) {
        switch (pieceType) {
            case Style: {
                return new PieceStyle(roleType, pieceName, componentStyleType);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceStyleType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceNameType pieceType, @Opt final RoleType roleType, @Opt @Name final String piece, @Opt @Name final Integer state, @Opt final String name) {
        switch (pieceType) {
            case ExtendName: {
                return new PieceExtendName(roleType, piece, state, (name == null) ? "" : name);
            }
            case Rename: {
                return new PieceRename(roleType, piece, state, (name == null) ? "" : name);
            }
            case AddStateToName: {
                return new PieceAddStateToName(roleType, piece, state);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceNameType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceFamiliesType pieceType, final String[] pieceFamilies) {
        switch (pieceType) {
            case Families: {
                return new PieceFamilies(pieceFamilies);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceFamiliesType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceGroundType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Integer state, @Name final String image, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale) {
        switch (pieceType) {
            case Background: {
                return new PieceBackground(roleType, pieceName, state, image, fillColour, edgeColour, scale);
            }
            case Foreground: {
                return new PieceForeground(roleType, pieceName, state, image, fillColour, edgeColour, scale);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceGroundType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceColourType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Integer state, @Opt @Name final Colour fillColour, @Opt @Name final Colour strokeColour, @Opt @Name final Colour secondaryColour) {
        switch (pieceType) {
            case Colour: {
                return new PieceColour(roleType, pieceName, state, fillColour, strokeColour, secondaryColour);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceColourType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceReflectType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, @Opt @Name final Boolean vertical, @Opt @Name final Boolean horizontal) {
        switch (pieceType) {
            case Reflect: {
                return new PieceReflect(roleType, pieceName, vertical, horizontal);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceReflectType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceRotateType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, @Name final Integer degrees) {
        switch (pieceType) {
            case Rotate: {
                return new PieceRotate(roleType, pieceName, degrees);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceRotateType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceScaleType pieceType, @Opt final RoleType roleType, @Opt final String pieceName, final Float scale) {
        switch (pieceType) {
            case Scale: {
                return new PieceScale(roleType, pieceName, scale);
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceScaleType is not implemented.");
            }
        }
    }
    
    public static GraphicsItem construct(final PieceScaleType pieceType, final PieceScaleByType scaleByType, @Opt final Boolean value) {
        switch (pieceType) {
            case Scale: {
                switch (scaleByType) {
                    case ByValue: {
                        return new PieceScaleByValue(value);
                    }
                    default: {
                        throw new IllegalArgumentException("Piece(): A PieceScaleByType is not implemented.");
                    }
                }
            }
            default: {
                throw new IllegalArgumentException("Piece(): A PieceScaleType is not implemented.");
            }
        }
    }
    
    private Piece() {
    }
}
