package ca.uqam.ace.inf5153.mesh.generator.exporter;

import ca.uqam.ace.inf5153.mesh.io.MeshWriter;
import ca.uqam.ace.inf5153.mesh.io.Structs;
import ca.uqam.ace.inf5153.mesh.io.Structs.Mesh;
import ca.uqam.ace.inf5153.mesh.io.Structs.Property;
import ca.uqam.ace.inf5153.mesh.io.Structs.Point;
import org.locationtech.jts.geom.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MeshExporter {

    private final Map<Coordinate, Polygon> tesselation;
    private final Set<Set<Coordinate>> neighbors;

    private Map<Coordinate,Integer> pointRegistry = new HashMap<>();
    private Map<Set<Integer>,Integer> edgeRegistry = new HashMap<>();


    public MeshExporter(Map<Coordinate, Polygon> tesselation, Set<Set<Coordinate>> neighbors) {
        this.neighbors = neighbors;
        this.tesselation = tesselation;
    }

    public void serialize(String outputFile, int width, int height) throws IOException {
        Mesh.Builder builder = Mesh.newBuilder();
        builder.addProperties(Property.newBuilder().setKey("width").setValue(""+width).build());
        builder.addProperties(Property.newBuilder().setKey("height").setValue(""+height).build());
        // Collecting all the points : centroids + tile borders
        addPoints(builder, tesselation.keySet());
        addPoints(builder, tesselation.values().stream()
                .flatMap(p -> { return Arrays.stream(p.getCoordinates()); })
                .collect(Collectors.toSet()));
        // Processing each polygon (edges + tile + neighborhood) in a single pass ;
        tesselation.forEach( (c,p) -> { process(c, p, builder); });
        write(outputFile, builder);
    }

    private void process(Coordinate centroid, Polygon p, Mesh.Builder builder) {
        LinearRing border = (LinearRing) p.convexHull().getBoundary();
        List<Integer> edgeIdxs = addEdges(builder, Arrays.asList(border.getCoordinates()));
        Structs.Polygon poly = Structs.Polygon.newBuilder()
                .setCentroidIdx(pointRegistry.get(centroid))
                .addAllSegmentIdx(edgeIdxs)
                .addAllNeighbors(extractNeighbors(centroid))
                .build();
        builder.addPolygons(poly);
    }

    private Set<Integer> extractNeighbors(Coordinate centroid) {
        Integer myself = pointRegistry.get(centroid);
        return neighbors.stream()
                .filter(  p -> { return p.contains(centroid); })
                .flatMap(Collection::stream)
                .map( p -> { return pointRegistry.get(p); })
                        .filter( i -> { return !i.equals(myself) ; })
                        .collect(Collectors.toSet());
    }

    private void write(String outputFile, Mesh.Builder builder) throws IOException {
        Mesh m = builder.build();
        MeshWriter writer = new MeshWriter();
        writer.writeToFile(m, outputFile);
    }

    private List<Integer> addEdges(Mesh.Builder builder, List<Coordinate> path) {
        if(path.size() < 2)
            return new LinkedList<>();
        int start = pointRegistry.get(path.get(0));
        int end = pointRegistry.get(path.get(1));
        Set<Integer> key = new HashSet<>(Arrays.asList(start,end));
        if(!edgeRegistry.containsKey(key)) {
            int idx = edgeRegistry.size();
            builder.addSegments(Structs.Segment.newBuilder().setV1Idx(start).setV2Idx(end).build());
            edgeRegistry.put(key, idx);
        }
        List<Integer> result = addEdges(builder, path.subList(1, path.size()));
        result.add(edgeRegistry.get(key));
        return result;
    }

    private void addPoints(Mesh.Builder builder, Set<Coordinate> points) {
        points.forEach(c -> {
            int idx = pointRegistry.size();
            pointRegistry.put(c,idx);
            Point p = Point.newBuilder()
                           .setX(Double.valueOf(c.x).floatValue())
                           .setY(Double.valueOf(c.y).floatValue()).build();
            builder.addPoints(p);
        });
    }

}
