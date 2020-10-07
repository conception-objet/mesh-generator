package ca.uqam.ace.inf5153.mesh.generator.geom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;

import java.util.*;
import java.util.stream.Collectors;

public class NeighboroodBorderCleaner {

    private final int width;
    private final int height;

    public NeighboroodBorderCleaner(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Set<Set<Coordinate>> clean(Map<Coordinate, Polygon> tesselation, Set<Set<Coordinate>> neighbors) {
        Set<Coordinate> borderCentroids = tesselation.entrySet().stream()
                .filter(entry -> isBorderPolygon(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Set<Set<Coordinate>> borderNeighbors = neighbors.stream()
                .filter( n -> {
                    Coordinate[] tmp = n.toArray(new Coordinate[0]);
                    return borderCentroids.contains(tmp[0]) && borderCentroids.contains(tmp[1]);
                }).collect(Collectors.toSet());
        Set<Set<Coordinate>> result = new HashSet<>(neighbors);
        result.removeAll(borderNeighbors);
        enrich(result, borderNeighbors.stream().flatMap(Collection::stream).collect(Collectors.toSet()));
        return result;
    }

    private void enrich(Set<Set<Coordinate>> result, Set<Coordinate> borderNeighbors) {
        borderNeighbors.forEach(centroid -> {
            Map<Coordinate, Double> distances = new HashMap<>();
            for(Coordinate other: borderNeighbors) {
                if(other == centroid)
                    continue;
                distances.put(other, other.distance(centroid));
            }
            List<Coordinate> sorted = distances.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            result.add(new HashSet<>(Arrays.asList(centroid,sorted.get(0))));
            result.add(new HashSet<>(Arrays.asList(centroid,sorted.get(1))));
        });
    }



    private boolean isBorderPolygon(Polygon p) {
        Optional<Coordinate> border = Arrays.stream(p.getCoordinates())
                .filter(c -> c.getX() <= 0 || c.getX() >= width || c.getY() <= 0 || c.getY() >= height )
                .findFirst();
        return border.isPresent();
    }


}
