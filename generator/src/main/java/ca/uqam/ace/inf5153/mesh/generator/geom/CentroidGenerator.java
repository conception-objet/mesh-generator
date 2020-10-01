package ca.uqam.ace.inf5153.mesh.generator.geom;

import org.locationtech.jts.geom.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CentroidGenerator {

    private final int height;
    private final int width;

    public CentroidGenerator(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public Set<Coordinate> generate(int howMany, int smoothingLevel) {
        Set<Coordinate> result = new HashSet<>();
        for(int i = 0; i < howMany; i++) {
            double x = ThreadLocalRandom.current().nextDouble(0, width);
            double y = ThreadLocalRandom.current().nextDouble(0, height);
            result.add(new Coordinate(x,y));
        }
        return this.relax(result, smoothingLevel);
    }

    /**
     * Relax the distribution of coordinates using Lloyd's algorithm
     * Compute the voronoi tesselation based on the given coordinates, and then shift each coordinate to the centroid
     * of its associated tile. Rinse and repeat (smoothing level) to approximate a Centroidal Voronoi Tesselation.
     */
    private Set<Coordinate> relax(Set<Coordinate> data, int smoothingLevel) {
        if (smoothingLevel <= 0)
            return data;
        Geometry tiles = (new VoronoiTesselation(width, height, true)).build(data);
        Set<Coordinate> local = new HashSet<>();
        for(int i = 0; i < tiles.getNumGeometries(); i++) {
            Geometry t = tiles.getGeometryN(i);
            local.add(t.getCentroid().getCoordinate());
        }
        return this.relax(local, smoothingLevel-1);
    }
}
