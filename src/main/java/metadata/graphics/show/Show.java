// 
// Decompiled by Procyon v0.5.36
// 

package metadata.graphics.show;

import annotations.Name;
import annotations.Opt;
import annotations.Or;
import game.functions.region.RegionFunction;
import game.types.board.RelationType;
import game.types.board.ShapeType;
import game.types.board.SiteType;
import game.types.play.RoleType;
import metadata.graphics.GraphicsItem;
import metadata.graphics.show.Boolean.*;
import metadata.graphics.show.check.ShowCheck;
import metadata.graphics.show.component.ShowPieceState;
import metadata.graphics.show.component.ShowPieceValue;
import metadata.graphics.show.edges.ShowEdges;
import metadata.graphics.show.line.ShowLine;
import metadata.graphics.show.score.ShowScore;
import metadata.graphics.show.sites.ShowSitesAsHoles;
import metadata.graphics.show.sites.ShowSitesShape;
import metadata.graphics.show.symbol.ShowSymbol;
import metadata.graphics.util.*;
import metadata.graphics.util.colour.Colour;

public class Show implements GraphicsItem
{
    public static GraphicsItem construct(final ShowSiteType showType, final ShowSiteDataType showDataType, @Opt final ShapeType shape, @Opt final Boolean value) {
        switch (showType) {
            case Cell: {
                switch (showDataType) {
                    case Shape -> {
                        return new ShowSitesShape(shape);
                    }
                    case AsHoles -> {
                        return new ShowSitesAsHoles(value);
                    }
                }
            }
            case Sites: {
                switch (showDataType) {
                    case Shape -> {
                        return new ShowSitesShape(shape);
                    }
                    case AsHoles -> {
                        return new ShowSitesAsHoles(value);
                    }
                }
            }
        }

        throw new IllegalArgumentException("Show(): A ShowSiteDataType is not implemented.");
    }
    
    public static GraphicsItem construct(final ShowSymbolType showType, final String imageName, @Opt final String region, @Opt final RoleType roleType, @Opt final SiteType graphElementType, @Opt @Or final Integer[] sites, @Opt @Or final Integer site, @Opt final RegionFunction regionFunction, @Opt final BoardGraphicsType boardGraphicsType, @Opt @Name final Colour fillColour, @Opt @Name final Colour edgeColour, @Opt @Name final Float scale, @Opt @Name final Integer rotation) {
        switch (showType) {
            case Symbol -> {
                return new ShowSymbol(imageName, region, roleType, graphElementType, sites, site, regionFunction, boardGraphicsType, fillColour, edgeColour, scale, rotation);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowSymbolType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowLineType showType, final Integer[][] lines, @Opt final Colour colour, @Opt @Name final Float scale, @Opt @Name final Float[] curve) {
        switch (showType) {
            case Line -> {
                return new ShowLine(lines, colour, scale, curve);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowLineType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowEdgeType showType, @Opt final EdgeType type, @Opt final RelationType relationType, @Opt @Name final Boolean connection, @Opt final LineStyle style, @Opt final Colour colour) {
        switch (showType) {
            case Edges -> {
                return new ShowEdges(type, relationType, connection, style, colour);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowEdgeType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowBooleanType showType, @Opt final Boolean value) {
        switch (showType) {
            case Pits -> {
                return new ShowPits(value);
            }
            case PlayerHoles -> {
                return new ShowPlayerHoles(value);
            }
            case RegionOwner -> {
                return new ShowRegionOwner(value);
            }
            case Cost -> {
                return new ShowCost(value);
            }
            case EdgeDirections -> {
                return new ShowEdgeDirections(value);
            }
            case PossibleMoves -> {
                return new ShowPossibleMoves(value);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowBooleanType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowComponentType showType, final ShowComponentDataType showDataType, @Opt final RoleType roleType, @Opt final String pieceName, @Opt final ValueLocationType location) {
        switch (showType) {
            case Piece -> {
                switch (showDataType) {
                    case State -> {
                        return new ShowPieceState(roleType, pieceName, location);
                    }
                    case Value -> {
                        return new ShowPieceValue(roleType, pieceName, location);
                    }
                    default -> throw new IllegalArgumentException("Show(): A ShowComponentDataType is not implemented for the Piece type.");
                }
            }
            default -> throw new IllegalArgumentException("Show(): A ShowComponentType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowCheckType showType, @Opt final RoleType roleType, @Opt final String pieceName) {
        switch (showType) {
            case Check -> {
                return new ShowCheck(roleType, pieceName);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowCheckType is not implemented.");
        }
    }
    
    public static GraphicsItem construct(final ShowScoreType showType, @Opt final WhenScoreType whenScore) {
        switch (showType) {
            case Score -> {
                return new ShowScore(whenScore);
            }
            default -> throw new IllegalArgumentException("Show(): A ShowScoreType is not implemented.");
        }
    }
    
    private Show() {
    }
}
