// 
// Decompiled by Procyon v0.5.36
// 

package game.util.graph;

import game.types.board.SiteType;
import main.math.MathRoutines;
import main.math.RCL;
import main.math.RCLType;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public abstract class MeasureGraph
{
    public static void measure(final Graph graph, final boolean boardless) {
        graph.clearProperties();
        if (!boardless) {
            measurePivot(graph);
            measurePerimeter(graph);
            measureInnerOuter(graph);
            measureExtremes(graph);
            measureMajorMinor(graph);
            measureCorners(graph);
            measureSides(graph);
            measurePhase(graph);
            measureEdgeOrientation(graph);
            measureSituation(graph);
        }
        measureCentre(graph);
    }
    
    public static void measurePivot(final Graph graph) {
        for (final Vertex vertex : graph.vertices()) {
            if (vertex.pivot() != null) {
                vertex.pivot().properties().add(64L);
            }
        }
    }
    
    public static void measurePerimeter(final Graph graph) {
        graph.clearPerimeters();
        final BitSet covered = new BitSet();
        while (true) {
            final Perimeter perimeter = createNextPerimeter(graph, covered);
            if (perimeter == null) {
                break;
            }
            graph.addPerimeter(perimeter);
        }
        for (int pa = graph.perimeters().size() - 1; pa >= 0; --pa) {
            final Point2D.Double ptA = (Point2D.Double)graph.perimeters().get(pa).startPoint();
            boolean isInside = false;
            for (int pb = 0; pb < graph.perimeters().size(); ++pb) {
                if (pa != pb) {
                    if (MathRoutines.pointInPolygon(ptA, graph.perimeters().get(pb).positions())) {
                        isInside = true;
                        break;
                    }
                }
            }
            if (isInside) {
                graph.removePerimeter(pa);
            }
        }
        for (final Perimeter perimeter2 : graph.perimeters()) {
            for (int numVerts = perimeter2.elements().size(), n = 0; n < numVerts; ++n) {
                final Vertex vertexA = (Vertex) perimeter2.elements().get(n);
                final Vertex vertexB = (Vertex) perimeter2.elements().get((n + 1) % numVerts);
                vertexA.properties().set(4L);
                vertexB.properties().set(4L);
                final Edge edge = vertexA.incidentEdge(vertexB);
                if (edge != null) {
                    edge.properties().set(4L);
                    if (edge.left() != null) {
                        edge.left().properties().set(4L);
                    }
                    else if (edge.right() != null) {
                        edge.right().properties().set(4L);
                    }
                }
            }
        }
    }
    
    private static Perimeter createNextPerimeter(final Graph graph, final BitSet covered) {
        Vertex start = null;
        double minX = 1000000.0;
        for (final Vertex vertex : graph.vertices()) {
            if (!covered.get(vertex.id()) && vertex.pt().x() < minX) {
                start = vertex;
                minX = vertex.pt().x();
            }
        }
        if (start == null) {
            return null;
        }
        final Perimeter perimeter = followPerimeterClockwise(start);
        if (perimeter == null) {
            return null;
        }
        for (final GraphElement ge : perimeter.elements()) {
            covered.set(ge.id(), true);
        }
        final List<Point2D.Double> polygon2D = perimeter.positions();
        for (final Vertex vertex2 : graph.vertices()) {
            if (!covered.get(vertex2.id()) && MathRoutines.pointInPolygon(vertex2.pt2D(), polygon2D)) {
                if (!perimeter.on().get(vertex2.id())) {
                    perimeter.addInside(vertex2);
                }
                covered.set(vertex2.id(), true);
            }
        }
        return perimeter;
    }
    
    private static Perimeter followPerimeterClockwise(final Vertex from) {
        final Perimeter perimeter = new Perimeter();
        if (from.edges().isEmpty()) {
            perimeter.add(from);
            return perimeter;
        }
        Edge bestEdge = null;
        double bestAngle = 1000000.0;
        for (final Edge edge : from.edges()) {
            final Vertex to = edge.otherVertex(from);
            final double dx = to.pt().x() - from.pt().x();
            final double dy = to.pt().y() - from.pt().y();
            final double angle = Math.atan2(dx, -dy);
            if (angle < bestAngle) {
                bestEdge = edge;
                bestAngle = angle;
            }
        }
        Vertex vertex = from;
        Vertex prev = from;
        Edge edge2 = bestEdge;
        do {
            perimeter.add(vertex);
            prev = vertex;
            vertex = edge2.otherVertex(vertex);
            final int i = vertex.edgePosition(edge2);
            for (int n = 1; n < vertex.edges().size(); ++n) {
                edge2 = vertex.edges().get((i + n) % vertex.edges().size());
                if (edge2.otherVertex(vertex).id() != prev.id()) {
                    break;
                }
            }
        } while (vertex.id() != from.id());
        return perimeter;
    }
    
    public static void measureInnerOuter(final Graph graph) {
        for (int st = 0; st < Graph.siteTypes.length; ++st) {
            for (final GraphElement ge : graph.elements(Graph.siteTypes[st])) {
                if (ge.properties().get(4L)) {
                    ge.properties().set(2L);
                }
            }
        }
        for (final Face face : graph.faces()) {
            for (final Vertex vertex : face.vertices()) {
                if (vertex.properties().get(4L)) {
                    face.properties().set(2L);
                    face.properties().set(4L);
                }
            }
        }
        for (final Edge edge : graph.edges()) {
            if (edge.vertexA().properties().get(4L) && edge.vertexB().properties().get(4L) && (edge.left() == null || edge.right() == null)) {
                edge.properties().set(2L);
                edge.properties().set(4L);
            }
        }
        for (final Face face : graph.faces()) {
            for (final Edge edge2 : face.edges()) {
                if (edge2.left() == null || edge2.right() == null) {
                    face.properties().set(256L);
                }
            }
        }
        for (int st = 0; st < Graph.siteTypes.length; ++st) {
            for (final GraphElement ge : graph.elements(Graph.siteTypes[st])) {
                if (!ge.properties().get(2L)) {
                    ge.properties().set(1L);
                }
            }
        }
    }
    
    public static void measureExtremes(final Graph graph) {
        final double tolerance = 0.01;
        for (int st = 0; st < Graph.siteTypes.length; ++st) {
            double minX = 1000000.0;
            double minY = 1000000.0;
            double maxX = -1000000.0;
            double maxY = -1000000.0;
            for (final GraphElement ge : graph.elements(Graph.siteTypes[st])) {
                if (ge.pt().x() < minX) {
                    minX = ge.pt().x();
                }
                if (ge.pt().y() < minY) {
                    minY = ge.pt().y();
                }
                if (ge.pt().x() > maxX) {
                    maxX = ge.pt().x();
                }
                if (ge.pt().y() > maxY) {
                    maxY = ge.pt().y();
                }
            }
            for (final GraphElement ge : graph.elements(Graph.siteTypes[st])) {
                if (ge.pt().x() - minX < 0.01) {
                    ge.properties().set(33554432L);
                }
                if (ge.pt().y() - minY < 0.01) {
                    ge.properties().set(268435456L);
                }
                if (maxX - ge.pt().x() < 0.01) {
                    ge.properties().set(67108864L);
                }
                if (maxY - ge.pt().y() < 0.01) {
                    ge.properties().set(134217728L);
                }
            }
        }
    }
    
    public static void measureMajorMinor(final Graph graph) {
        int maxSides = 0;
        for (final Face face : graph.faces()) {
            if (face.vertices().size() > maxSides) {
                maxSides = face.vertices().size();
            }
        }
        for (final Face face : graph.faces()) {
            if (face.vertices().size() == maxSides) {
                face.properties().set(16L);
            }
            else {
                face.properties().set(32L);
            }
        }
    }
    
    public static void measureCorners(final Graph graph) {
        cornersFromPerimeters(graph);
        for (final Vertex vertex : graph.vertices()) {
            if (!vertex.properties().get(1024L)) {
                continue;
            }
            final boolean isConcave = vertex.properties().get(4096L);
            boolean singleEdge = false;
            for (final Edge edge : vertex.edges()) {
                final Vertex other = edge.otherVertex(vertex);
                if (other.properties().get(1024L)) {
                    edge.properties().set(1024L);
                    if (isConcave) {
                        edge.properties().set(4096L);
                    }
                    else {
                        edge.properties().set(2048L);
                    }
                    final Face faceL = edge.left();
                    final Face faceR = edge.right();
                    if (faceL != null) {
                        faceL.properties().set(1024L);
                        if (isConcave) {
                            faceL.properties().set(4096L);
                        }
                        else {
                            faceL.properties().set(2048L);
                        }
                    }
                    else if (faceR != null) {
                        faceR.properties().set(1024L);
                        if (isConcave) {
                            faceR.properties().set(4096L);
                        }
                        else {
                            faceR.properties().set(2048L);
                        }
                    }
                    singleEdge = true;
                }
            }
            if (singleEdge) {
                continue;
            }
            for (final Edge edge : vertex.edges()) {
                if (!edge.properties().get(4L)) {
                    continue;
                }
                edge.properties().set(1024L);
                if (isConcave) {
                    edge.properties().set(4096L);
                }
                else {
                    edge.properties().set(2048L);
                }
                if (isConcave) {
                    for (final Face face : vertex.faces()) {
                        if (numPerimeterVertices(face) == 1) {
                            face.properties().set(1024L);
                            if (isConcave) {
                                face.properties().set(4096L);
                            }
                            else {
                                face.properties().set(2048L);
                            }
                        }
                    }
                }
                else {
                    final Face faceL2 = edge.left();
                    final Face faceR2 = edge.right();
                    if (faceL2 != null) {
                        faceL2.properties().set(1024L);
                        faceL2.properties().set(2048L);
                    }
                    else {
                        if (faceR2 == null) {
                            continue;
                        }
                        faceR2.properties().set(1024L);
                        faceR2.properties().set(2048L);
                    }
                }
            }
        }
    }
    
    static int numPerimeterVertices(final Face face) {
        int num = 0;
        for (final Vertex vertex : face.vertices()) {
            if (vertex.properties().get(4L)) {
                ++num;
            }
        }
        return num;
    }
    
    static void cornersFromPerimeters(final Graph graph) {
        for (final Perimeter perimeter : graph.perimeters()) {
            cornersFromPerimeter(graph, perimeter);
        }
    }
    
    static void cornersFromPerimeter(final Graph graph, final Perimeter perimeter) {
        final double tolerance = 0.001;
        if (perimeter.elements().size() < 6) {
            for (final GraphElement ge : perimeter.elements()) {
                ge.properties().set(1024L);
                ge.properties().set(2048L);
            }
            return;
        }
        final int num = perimeter.elements().size();
        final List<Point2D.Double> polygon2D = perimeter.positions();
        final double[] scores = new double[num];
        final int numK = 4;
        for (int n = 0; n < num; ++n) {
            final Point2D.Double pt = polygon2D.get(n);
            double score = 0.0;
            for (int k = 1; k < 4; ++k) {
                final Point2D.Double ptA = polygon2D.get((n - k + num) % num);
                final Point2D.Double ptB = polygon2D.get((n + k) % num);
                double dist = MathRoutines.distanceToLine(pt, ptA, ptB);
                if (MathRoutines.clockwise(ptA, pt, ptB)) {
                    dist = -dist;
                }
                score += dist / k;
            }
            scores[n] = score;
        }
        final int numSmoothingPasses = 1;
        final double[] temp = new double[num];
        for (int pass = 0; pass < 1; ++pass) {
            for (int n2 = 0; n2 < num; ++n2) {
                temp[n2] = (4.0 * scores[n2] + scores[(n2 + 1) % num] + scores[(n2 - 1 + num) % num]) / 6.0;
            }
            for (int n2 = 0; n2 < num; ++n2) {
                scores[n2] = temp[n2];
            }
        }
        final BitSet keep = new BitSet();
        keep.set(0, num, true);
        for (int n2 = 0; n2 < num; ++n2) {
            if (scores[n2] < 0.32 || scores[n2] < scores[(n2 - 1 + num) % num] - 0.001 || scores[n2] < scores[(n2 + 1) % num] - 0.001) {
                keep.set(n2, false);
            }
        }
        final double similar = 0.95;
        for (int n3 = keep.nextSetBit(0); n3 >= 0; n3 = keep.nextSetBit(n3 + 1)) {
            if (scores[(n3 - 1 + num) % num] >= 0.95 * scores[n3]) {
                keep.set((n3 - 1 + num) % num, true);
            }
            if (scores[(n3 + 1) % num] >= 0.95 * scores[n3]) {
                keep.set((n3 + 1) % num, true);
            }
        }
        for (int n3 = keep.nextSetBit(0); n3 >= 0; n3 = keep.nextSetBit(n3 + 1)) {
            perimeter.elements().get(n3).properties().set(1024L);
            perimeter.elements().get(n3).properties().set(2048L);
        }
        keep.set(0, num, true);
        for (int n3 = 0; n3 < num; ++n3) {
            if (scores[n3] > -0.25 || scores[n3] > scores[(n3 - 1 + num) % num] + 0.001 || scores[n3] > scores[(n3 + 1) % num] + 0.001) {
                keep.set(n3, false);
            }
        }
        for (int n3 = keep.nextSetBit(0); n3 >= 0; n3 = keep.nextSetBit(n3 + 1)) {
            perimeter.elements().get(n3).properties().set(1024L);
            perimeter.elements().get(n3).properties().set(4096L);
        }
    }
    
    public static void measureSides(final Graph graph) {
        final Point2D mid = graph.centroid();
        for (final Perimeter perimeter : graph.perimeters()) {
            findSides(graph, mid, perimeter);
        }
        final long[] sides = { 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L };
        for (final Vertex vertex : graph.vertices()) {
            if (!vertex.properties().get(1024L)) {
                continue;
            }
            for (int side = 0; side < sides.length; ++side) {
                final long sideCode = sides[side];
                if (vertex.properties().get(sideCode)) {
                    for (final Edge edge : vertex.edges()) {
                        final Vertex other = edge.otherVertex(vertex);
                        if (other.properties().get(sideCode)) {
                            edge.properties().set(sideCode);
                        }
                    }
                    for (final Face face : vertex.faces()) {
                        face.properties().set(sideCode);
                    }
                }
            }
        }
        final long sidesMask = 280375465082880L;
        for (final Vertex vertex2 : graph.vertices()) {
            for (final Edge edge2 : vertex2.edges()) {
                edge2.properties().add(vertex2.properties().get() & 0xFF0000000000L);
            }
            for (final Face face2 : vertex2.faces()) {
                face2.properties().add(vertex2.properties().get() & 0xFF0000000000L);
            }
        }
    }
    
    static void findSides(final Graph graph, final Point2D mid, final Perimeter perimeter) {
        for (int num = perimeter.elements().size(), from = 0; from < num; ++from) {
            final GraphElement geFrom = perimeter.elements().get(from);
            if (geFrom.properties().get(1024L)) {
                int to = from;
                GraphElement geTo;
                do {
                    geTo = perimeter.elements().get(++to % num);
                } while (!geTo.properties().get(1024L));
                final int runLength = to - from;
                sideFromRun(graph, perimeter, mid, from, runLength);
            }
        }
    }
    
    static void sideFromRun(final Graph graph, final Perimeter perimeter, final Point2D mid, final int from, final int runLength) {
        final int numDirections = 16;
        final int num = perimeter.elements().size();
        final GraphElement geFrom = perimeter.elements().get(from);
        double avgX = geFrom.pt().x();
        double avgY = geFrom.pt().y();
        GraphElement ge = null;
        for (int r = 0; r < runLength; ++r) {
            ge = perimeter.elements().get((from + 1 + r) % num);
            avgX += ge.pt().x();
            avgY += ge.pt().y();
        }
        avgX /= runLength;
        avgY /= runLength;
        final int dirn = discreteDirection(mid, new Point2D.Double(avgX, avgY), 16);
        long property = 0L;
        if (dirn == 0) {
            property = 2199023255552L;
        }
        else if (dirn == 4) {
            property = 1099511627776L;
        }
        else if (dirn == 8) {
            property = 8796093022208L;
        }
        else if (dirn == 12) {
            property = 4398046511104L;
        }
        else if (dirn > 0 && dirn < 4) {
            property = 17592186044416L;
        }
        else if (dirn > dirn / 4 && dirn < 8) {
            property = 140737488355328L;
        }
        else if (dirn > dirn / 2 && dirn < 12) {
            property = 70368744177664L;
        }
        else {
            property = 35184372088832L;
        }
        for (int r2 = 0; r2 < runLength + 1; ++r2) {
            ge = perimeter.elements().get((from + r2) % num);
            ge.properties().set(property);
        }
    }
    
    public static void measureCentre(final Graph graph) {
        final Rectangle2D bounds = Graph.bounds(graph.elements(SiteType.Vertex));
        final Point2D mid = new Point2D.Double(bounds.getX() + bounds.getWidth() / 2.0, bounds.getY() + bounds.getHeight() / 2.0);
        for (int st = 0; st < Graph.siteTypes.length; ++st) {
            measureGeometricCentre(graph, mid, Graph.siteTypes[st]);
        }
    }
    
    private static void measureGeometricCentre(final Graph graph, final Point2D mid, final SiteType type) {
        final double tolerance = 1.0E-4;
        final List<? extends GraphElement> list = graph.elements(type);
        final List<GraphElement> perimeterGE = new ArrayList<>();
        if (list.size() < 100) {
            for (final GraphElement ge : list) {
                if (ge.properties().get(4L)) {
                    perimeterGE.add(ge);
                }
            }
        }
        final List<GraphElement> cornersGE = new ArrayList<>();
        for (final GraphElement ge2 : list) {
            if (ge2.properties().get(1024L)) {
                cornersGE.add(ge2);
            }
        }
        final double[] distances = new double[list.size()];
        for (final GraphElement geA : list) {
            double acc = 0.0;
            for (final GraphElement geB : perimeterGE) {
                acc += MathRoutines.distanceSquared(geA.pt2D(), geB.pt2D());
            }
            for (final GraphElement geB : cornersGE) {
                acc += MathRoutines.distanceSquared(geA.pt2D(), geB.pt2D());
            }
            distances[geA.id()] = acc;
        }
        double minDistance = 1000000.0;
        for (int n = 0; n < distances.length; ++n) {
            if (distances[n] < minDistance) {
                minDistance = distances[n];
            }
        }
        for (int n = 0; n < distances.length; ++n) {
            if (Math.abs(distances[n] - minDistance) < 1.0E-4) {
                list.get(n).properties().set(8L);
            }
        }
    }
    
    public static void measurePhase(final Graph graph) {
        for (int st = 0; st < Graph.siteTypes.length; ++st) {
            measurePhase(graph, Graph.siteTypes[st]);
        }
    }
    
    public static void measurePhase(final Graph graph, final SiteType type) {
        final List<? extends GraphElement> elements = graph.elements(type);
        if (elements.isEmpty()) {
            return;
        }
        while (true) {
            GraphElement start = null;
            for (final GraphElement ge : elements) {
                if (ge.properties().phase() == -1) {
                    start = ge;
                    break;
                }
            }
            if (start == null) {
                break;
            }
            final Deque<GraphElement> queue = new ArrayDeque<>();
            final BitSet visited = new BitSet();
            start.properties().set(1048576L);
            queue.add(start);
            while (!queue.isEmpty()) {
                final GraphElement ge2 = queue.removeFirst();
                if (visited.get(ge2.id())) {
                    continue;
                }
                final List<GraphElement> nbors = ge2.nbors();
                BitSet nborPhases;
                int phase;
                for (nborPhases = nborPhases(nbors), phase = 0; phase < 4 && nborPhases.get(phase); ++phase) {}
                if (phase == 0) {
                    ge2.properties().set(1048576L);
                }
                else if (phase == 1) {
                    ge2.properties().set(2097152L);
                }
                else if (phase == 2) {
                    ge2.properties().set(4194304L);
                }
                else if (phase == 3) {
                    ge2.properties().set(8388608L);
                }
                visited.set(ge2.id(), true);
                for (final GraphElement nbor : nbors) {
                    if (visited.get(nbor.id())) {
                        continue;
                    }
                    final List<GraphElement> nborNbors = nbor.nbors();
                    final BitSet nborNborPhases = nborPhases(nborNbors);
                    final int numNborNborPhases = nborNborPhases.cardinality();
                    if (numNborNborPhases > 1) {
                        queue.addFirst(nbor);
                    }
                    else {
                        queue.addLast(nbor);
                    }
                }
            }
        }
    }
    
    static BitSet nborPhases(final List<GraphElement> nbors) {
        final BitSet phases = new BitSet();
        for (final GraphElement nbor : nbors) {
            if (nbor.properties().get(1048576L)) {
                phases.set(0, true);
            }
            else if (nbor.properties().get(2097152L)) {
                phases.set(1, true);
            }
            else if (nbor.properties().get(4194304L)) {
                phases.set(2, true);
            }
            else {
                if (!nbor.properties().get(8388608L)) {
                    continue;
                }
                phases.set(3, true);
            }
        }
        return phases;
    }
    
    public static void measureEdgeOrientation(final Graph graph) {
        final int numDirections = 16;
        for (final Edge edge : graph.edges()) {
            final Vertex va = edge.vertexA();
            final Vertex vb = edge.vertexB();
            final int direction = discreteDirection(va.pt2D(), vb.pt2D(), 16);
            if (direction == 0 || direction == 8) {
                edge.properties().set(1073741824L);
                edge.properties().set(2147483648L);
            }
            else if (direction == 4 || direction == 12) {
                edge.properties().set(1073741824L);
                edge.properties().set(4294967296L);
            }
            else if ((direction > 0 && direction < 4) || (direction > 8 && direction < 12)) {
                edge.properties().set(8589934592L);
                edge.properties().set(17179869184L);
            }
            else {
                edge.properties().set(8589934592L);
                edge.properties().set(34359738368L);
            }
        }
    }
    
    public static int discreteDirection(final double angleIn, final int numDirections) {
        final double arc = 6.283185307179586 / numDirections;
        final double off = arc / 2.0;
        double angle;
        for (angle = angleIn; angle < 0.0; angle += 6.283185307179586) {}
        while (angle > 6.283185307179586) {
            angle -= 6.283185307179586;
        }
        return ((int)((angle + off) / arc) + numDirections) % numDirections;
    }
    
    public static int discreteDirection(final Point2D ptA, final Point2D ptB, final int numDirections) {
        final double angle = Math.atan2(ptB.getY() - ptA.getY(), ptB.getX() - ptA.getX());
        return discreteDirection(angle, numDirections);
    }
    
    public static void measureSituation(final Graph graph) {
        final double[] bestVertexThetas = { 0.0, 0.5 };
        for (final SiteType siteType : SiteType.values()) {
            final List<? extends GraphElement> elements = graph.elements(siteType);
            if (!elements.isEmpty()) {
                final Rectangle2D bounds = Graph.bounds(elements);
                final double unit = (bounds.getWidth() + bounds.getHeight()) / 2.0 / Math.sqrt(elements.size());
                clusterByRowAndColumn(elements, unit, bestVertexThetas, siteType == SiteType.Edge);
                clusterByDimension(elements, RCLType.Layer, unit, 0.0);
                setCoordinateLabels(graph, siteType, elements);
            }
        }
    }
    
    public static void clusterByRowAndColumn(final List<? extends GraphElement> elements, final double unit, final double[] bestThetas, final boolean useBestThetas) {
        if (useBestThetas) {
            clusterByDimension(elements, RCLType.Row, unit, bestThetas[0]);
            clusterByDimension(elements, RCLType.Column, unit, bestThetas[1]);
            return;
        }
        double bestError = 1000.0;
        bestThetas[0] = 0.0;
        for (int angle = 0; angle <= 60; angle += 15) {
            final double theta = angle / 180.0 * 3.141592653589793;
            final double error = clusterByDimension(elements, RCLType.Row, unit, theta);
            if (error < bestError) {
                bestError = error;
                bestThetas[0] = theta;
                if (error < 0.01) {
                    break;
                }
            }
        }
        clusterByDimension(elements, RCLType.Row, unit, bestThetas[0]);
        bestError = 1000.0;
        bestThetas[1] = 0.0;
        for (int angle = 90; angle <= 120; angle += 15) {
            final double theta = bestThetas[0] + angle / 180.0 * 3.141592653589793;
            final double error = clusterByDimension(elements, RCLType.Column, unit, theta);
            if (error < bestError) {
                bestError = error;
                bestThetas[1] = theta;
                if (error < 0.01) {
                    break;
                }
            }
        }
        clusterByDimension(elements, RCLType.Column, unit, bestThetas[1]);
    }
    
    public static double clusterByDimension(final List<? extends GraphElement> elements, final RCLType rclType, final double unit, final double theta) {
        Point2D refA = null;
        Point2D refB = null;
        final Rectangle2D bounds = Graph.bounds(elements);
        if (rclType == RCLType.Row) {
            refA = new Point2D.Double(bounds.getX() + bounds.getWidth() / 2.0, bounds.getY() - bounds.getHeight());
            refB = new Point2D.Double(refA.getX() + bounds.getWidth() * Math.cos(theta), refA.getY() + bounds.getWidth() * Math.sin(theta));
        }
        else if (rclType == RCLType.Column) {
            refA = new Point2D.Double(bounds.getX() - bounds.getWidth(), bounds.getY() + bounds.getHeight() / 2.0);
            refB = new Point2D.Double(refA.getX() + bounds.getWidth() * Math.cos(theta), refA.getY() + bounds.getWidth() * Math.sin(theta));
        }
        final List<ItemScore> rank = new ArrayList<ItemScore>();
        final double margin = 0.6 * unit;
        for (int n = 0; n < elements.size(); ++n) {
            final double dist = (rclType == RCLType.Layer) ? elements.get(n).pt().z() : MathRoutines.distanceToLine(elements.get(n).pt2D(), refA, refB);
            rank.add(new ItemScore(n, dist));
        }
        Collections.sort(rank);
        final List<Bucket> buckets = new ArrayList<>();
        Bucket bucket = null;
        for (final ItemScore item : rank) {
            if (bucket == null || Math.abs(item.score() - bucket.mean()) > margin) {
                bucket = new Bucket();
                buckets.add(bucket);
            }
            bucket.addItem(item);
        }
        for (int bid = 0; bid < buckets.size(); ++bid) {
            for (final ItemScore item2 : buckets.get(bid).items()) {
                final RCL rcl = elements.get(item2.id()).situation().rcl();
                switch (rclType) {
                    case Row: {
                        rcl.setRow(bid);
                        continue;
                    }
                    case Column: {
                        rcl.setColumn(bid);
                        continue;
                    }
                    case Layer: {
                        rcl.setLayer(bid);
                        continue;
                    }
                }
            }
        }
        double error = 0.0;
        for (final Bucket bkt : buckets) {
            double acc = 0.0;
            for (final ItemScore item3 : bkt.items()) {
                acc = (bkt.mean() - item3.score()) * (bkt.mean() - item3.score());
            }
            error += acc / bkt.items().size();
        }
        error += 0.01 * buckets.size();
        return error;
    }
    
    public static void setCoordinateLabels(final Graph graph, final SiteType siteType, final List<? extends GraphElement> elements) {
        boolean distinctLayers = false;
        for (int eid = 0; eid < elements.size() - 1; ++eid) {
            if (elements.get(eid).situation().rcl().layer() != elements.get(eid + 1).situation().rcl().layer()) {
                distinctLayers = true;
                break;
            }
        }
        final Map<String, GraphElement> map = new HashMap<>();
        map.clear();
        for (final GraphElement element : elements) {
            int column;
            String label;
            for (column = element.situation().rcl().column(), label = "" + (char)(65 + column % 26); column >= 26; column /= 26, label = (char)(65 + column % 26 - 1) + label) {}
            label += element.situation().rcl().row() + 1;
            if (distinctLayers) {
                label = label + "/" + element.situation().rcl().layer();
            }
            if (map.get(label) != null) {
                graph.setDuplicateCoordinates(siteType);
            }
            else {
                map.put(label, element);
            }
            element.situation().setLabel(label);
        }
    }
}
