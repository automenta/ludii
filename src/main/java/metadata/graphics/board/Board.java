// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.board;

import annotations.Name;
import annotations.Opt;
import game.types.board.ShapeType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.board.Boolean.BoardCheckered;
import metadata.graphics.board.colour.BoardColour;
import metadata.graphics.board.ground.BoardBackground;
import metadata.graphics.board.ground.BoardForeground;
import metadata.graphics.board.shape.BoardShape;
import metadata.graphics.board.style.BoardStyle;
import metadata.graphics.board.styleThickness.BoardStyleThickness;
import metadata.graphics.piece.PieceGroundType;
import metadata.graphics.util.BoardGraphicsType;
import metadata.graphics.util.ContainerStyleType;
import metadata.graphics.util.colour.Colour;

public class Board implements GraphicsItem
{
    public static GraphicsItem construct(final BoardStyleType boardType, final ContainerStyleType containerStyleType) {
        switch (boardType) {
            case Style -> {
                return new BoardStyle(containerStyleType, false);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardStyleType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final BoardStyleType boardType, final BoardStylePenAndPaperType containerStyleType, @Name final Boolean onlyEdges) {
        switch (boardType) {
            case Style -> {
                return new BoardStyle(ContainerStyleType.PenAndPaper, onlyEdges);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardStyleType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final BoardStyleThicknessType boardType, final BoardGraphicsType boardGraphicsType, final Float thickness) {
        switch (boardType) {
            case StyleThickness -> {
                return new BoardStyleThickness(boardGraphicsType, thickness);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardStyleThicknessType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final BoardBooleanType boardType, @Opt final Boolean value) {
        switch (boardType) {
            case Checkered -> {
                return new BoardCheckered(value);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardBooleanType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final PieceGroundType boardType, @Name final String image, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale, @Opt @Name final Integer rotation, @Opt @Name final Float offsetX, @Opt @Name final Float offsetY) {
        switch (boardType) {
            case Background -> {
                return new BoardBackground(image, fillColour, edgeColour, scale, rotation, offsetX, offsetY);
            }
            case Foreground -> {
                return new BoardForeground(image, fillColour, edgeColour, scale, rotation, offsetX, offsetY);
            }
            default -> throw new IllegalArgumentException("Piece(): A PieceGroundType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final BoardColourType boardType, final BoardGraphicsType boardGraphicsType, final Colour colour) {
        switch (boardType) {
            case Colour -> {
                return new BoardColour(boardGraphicsType, colour);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardColourType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final BoardShapeType boardType, final ShapeType shape) {
        switch (boardType) {
            case Shape -> {
                return new BoardShape(shape);
            }
            default -> throw new IllegalArgumentException("Board(): A BoardShapeType is not implemented.");
        }
    }
    
    private Board() {
    }
}
