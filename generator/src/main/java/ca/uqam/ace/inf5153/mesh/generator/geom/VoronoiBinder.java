package ca.uqam.ace.inf5153.mesh.generator.geom;

import org.locationtech.jts.geom.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoronoiBinder {

    private final int width;
    private final int height;

    public VoronoiBinder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Map<Coordinate, Polygon> bind(Set<Coordinate> sites) {
        Geometry tiles = new VoronoiTesselation(width, height).build(sites);
        PolygonBinder binder = new PolygonBinder();
        tiles.apply(binder);
        return binder.getCollected();
    }

    private static class PolygonBinder implements GeometryFilter {

        private final Map<Coordinate, Polygon> collected = new HashMap<>();

        @Override
        public void filter(Geometry geometry) {
            if (geometry instanceof Polygon) {
                collect((Polygon) geometry);
            }
        }

        private void collect(Polygon polygon) {
            Point key = polygon.getCentroid();
            collected.put(new Coordinate(key.getX(), key.getY()), polygon);
        }

        public Map<Coordinate, Polygon> getCollected() {
            return collected;
        }
    }

}
