package ca.uqam.ace.inf5153.mesh.generator.geom;

import ca.uqam.ace.inf5153.mesh.generator.PrecisionModel;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DelaunayTriangulation {

    public Set<Set<Coordinate>> process(Set<Coordinate> sites) {
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(sites);
        Geometry triangles = builder.getTriangles(PrecisionModel.getFactory());
        TriangleExtractor extractor = new TriangleExtractor();
        triangles.apply(extractor);
        return extractor.getCollected();
    }

    private static class TriangleExtractor implements GeometryFilter {

        private final Set<Set<Coordinate>> collected = new HashSet<>();

        @Override
        public void filter(Geometry geometry) {
            if (geometry instanceof Polygon) {
                collect((Polygon) geometry);
            }
        }

        private void collect(Polygon polygon) {
            Coordinate[] points = polygon.getCoordinates();
            Coordinate[] triangle = Arrays.copyOfRange(points, 0, points.length-1);
            for(int i = 0; i < triangle.length; i++)
                for(int j = i+1; i < triangle.length; i++) {
                    Set<Coordinate> pair = new HashSet<>(Arrays.asList(triangle[i],triangle[j]));
                    collected.add(pair);
                }
        }

        public Set<Set<Coordinate>> getCollected() {
            return collected;
        }
    }

}
